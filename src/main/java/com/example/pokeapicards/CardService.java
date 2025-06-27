package com.example.pokeapicards;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CardService {

    private static final Logger log = LoggerFactory.getLogger(CardService.class);

    private final WebClient webClient;
    private final FavoriteCardRepository favoriteCardRepository;
    private final ObjectMapper objectMapper;

    public CardService(WebClient.Builder webClientBuilder, FavoriteCardRepository favoriteCardRepository) {
        this.webClient = webClientBuilder.baseUrl("https://api.tcgdex.net/v2").build();
        this.favoriteCardRepository = favoriteCardRepository;
        this.objectMapper = new ObjectMapper();
    }

    // --- GET cards by name ---
    public Object searchCards(String name) {
        log.info("Buscando cartas com nome: {}", name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/en/cards").queryParam("name", name).build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnNext(json -> log.debug("Resposta JSON (searchCards): {}", json.toPrettyString()))
                .map(jsonNode -> {
                    JsonNode dataNode = jsonNode.get("data");
                    if (dataNode != null && dataNode.isArray()) {
                        return StreamSupport.stream(dataNode.spliterator(), false)
                                .map(node -> {
                                    String id = node.path("id").asText();
                                    String cardName = node.path("name").asText();
                                    String imageUrl = node.path("image").path("url").asText();
                                    return new Card(id, cardName, imageUrl);
                                })
                                .collect(Collectors.toList());
                    }
                    log.warn("Nenhum array 'data' encontrado para nome: {}", name);
                    return Collections.emptyList();
                })
                .defaultIfEmpty(Collections.emptyList())
                .doOnError(e -> log.error("Erro ao buscar cartas: {}", e.getMessage(), e));
    }

    // --- GET card by ID ---
    public Mono<Card> getCardById(String id) {
        log.info("Buscando carta por ID: {}", id);

        return webClient.get()
                .uri("/en/cards/{id}", id)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnNext(json -> log.debug("Resposta JSON (getCardById): {}", json.toPrettyString()))
                .flatMap(jsonNode -> {
                    JsonNode dataNode = jsonNode.get("data");
                    if (dataNode != null && dataNode.isObject()) {
                        String cardId = dataNode.path("id").asText();
                        String cardName = dataNode.path("name").asText();
                        String imageUrl = dataNode.path("image").path("url").asText();
                        return Mono.just(new Card(cardId, cardName, imageUrl));
                    }
                    log.warn("Carta não encontrada para ID: {}", id);
                    return Mono.empty();
                })
                .doOnError(e -> log.error("Erro ao buscar carta por ID: {}", e.getMessage(), e));
    }

    // --- GET favoritos ---
    public Flux<FavoriteCard> getAllFavoriteCards() {
        List<FavoriteCard> allFavorites = (List<FavoriteCard>) favoriteCardRepository.findAll();
        return Flux.fromIterable(allFavorites)
                .doOnComplete(() -> log.info("Carregadas {} cartas favoritas.", allFavorites.size()))
                .doOnError(e -> log.error("Erro ao carregar cartas favoritas: {}", e.getMessage(), e));
    }

    // --- POST favoritos ---
    public Mono<FavoriteCard> addFavoriteCard(String cardId) {
        if (favoriteCardRepository.existsById(cardId)) {
            log.warn("Carta já está nos favoritos: {}", cardId);
            return Mono.error(new IllegalArgumentException("Card already in favorites"));
        }

        return getCardById(cardId)
                .flatMap(card -> {
                    FavoriteCard newFavorite = new FavoriteCard(card.getId(), card.getName(), card.getImageUrl());
                    FavoriteCard savedCard = favoriteCardRepository.save(newFavorite);
                    log.info("Adicionada aos favoritos: {} ({})", savedCard.getCardName(), savedCard.getCardId());
                    return Mono.just(savedCard);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Card not found in TCGdex API: " + cardId)))
                .doOnError(e -> log.error("Erro ao adicionar carta {}: {}", cardId, e.getMessage(), e));
    }

    // --- DELETE favoritos ---
    public Mono<Void> removeFavoriteCard(String cardId) {
        if (!favoriteCardRepository.existsById(cardId)) {
            log.warn("Tentativa de remover carta inexistente: {}", cardId);
            return Mono.error(new IllegalArgumentException("Card not found in favorites: " + cardId));
        }

        favoriteCardRepository.deleteById(cardId);
        log.info("Carta com ID {} removida dos favoritos.", cardId);
        return Mono.empty();
    }
}
