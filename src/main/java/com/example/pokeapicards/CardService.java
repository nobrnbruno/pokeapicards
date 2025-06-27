package com.example.pokeapicards;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class CardService {

    private final WebClient webClient;
    private final FavoriteCardRepository favoriteCardRepository;

    public CardService(FavoriteCardRepository favoriteCardRepository) {
        this.favoriteCardRepository = favoriteCardRepository;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.pokemontcg.io/v2")
                .build();
    }

    public Mono<List<Card>> searchCards(String name) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cards")
                        .queryParam("q", "name:" + name + "*")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> cardsData = (List<Map<String, Object>>) response.get("data");
                    if (cardsData == null) {
                        return List.<Card>of();
                    }

                    return cardsData.stream()
                            .map(this::mapToCard)
                            .collect(java.util.stream.Collectors.toList());
                })
                .onErrorReturn(List.of()); // Retorna lista vazia em caso de erro
    }

    private Card mapToCard(Map<String, Object> cardData) {
        String id = (String) cardData.get("id");
        String name = (String) cardData.get("name");

        // Extrai a URL da imagem da API oficial do Pokémon TCG
        String imageUrl = null;
        Map<String, Object> images = (Map<String, Object>) cardData.get("images");
        if (images != null) {
            imageUrl = (String) images.get("small");
            if (imageUrl == null) {
                imageUrl = (String) images.get("large");
            }
        }

        return new Card(id, name, imageUrl);
    }

    public Flux<FavoriteCard> getAllFavoriteCards() {
        return Flux.fromIterable(favoriteCardRepository.findAll());
    }

    public Mono<FavoriteCard> addFavoriteCard(String cardId) {
        // Verifica se já existe nos favoritos
        if (favoriteCardRepository.existsById(cardId)) {
            return Mono.error(new IllegalArgumentException("Card already in favorites"));
        }

        // Busca os detalhes da carta na API
        return webClient.get()
                .uri("/cards/{id}", cardId)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Map<String, Object> cardData = (Map<String, Object>) response.get("data");
                    if (cardData == null) {
                        throw new IllegalArgumentException("Card not found");
                    }

                    String name = (String) cardData.get("name");
                    String imageUrl = null;

                    Map<String, Object> images = (Map<String, Object>) cardData.get("images");
                    if (images != null) {
                        imageUrl = (String) images.get("small");
                        if (imageUrl == null) {
                            imageUrl = (String) images.get("large");
                        }
                    }

                    FavoriteCard favoriteCard = new FavoriteCard(cardId, name, imageUrl);
                    return favoriteCardRepository.save(favoriteCard);
                })
                .onErrorMap(Exception.class, e ->
                        new IllegalArgumentException("Error adding card to favorites: " + e.getMessage()));
    }

    public Mono<Void> removeFavoriteCard(String cardId) {
        if (!favoriteCardRepository.existsById(cardId)) {
            return Mono.error(new IllegalArgumentException("Card not found in favorites"));
        }

        favoriteCardRepository.deleteById(cardId);
        return Mono.empty();
    }
}