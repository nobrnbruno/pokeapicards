package com.example.pokeapicards;

import com.example.pokeapicards.Card;
import com.example.pokeapicards.FavoriteCard;
import com.example.pokeapicards.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8080") // Permitir requisições do seu cliente web
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // --- Endpoints GET ---

    // GET /api/cards?name=Pikachu (Pesquisar cartas na TCGdex API)
    @GetMapping("/cards")
    public Mono<List<Card>> searchCards(@RequestParam(required = false) String name) {
        if (name == null || name.isEmpty()) {
            return Mono.just(List.of()); // Retorna vazio se não houver nome para pesquisa
        }
        return cardService.searchCards(name);
    }

    // GET /api/favorites (Obter todas as cartas favoritas)
    @GetMapping("/favorites")
    public Flux<FavoriteCard> getAllFavorites() {
        return cardService.getAllFavoriteCards();
    }

    // --- Endpoints POST ---

    // POST /api/favorites/add (Adicionar uma carta aos favoritos)
    @PostMapping("/favorites/add")
    public Mono<ResponseEntity<FavoriteCard>> addFavorite(@RequestBody String cardId) { // Recebe apenas o ID da carta
        return cardService.addFavoriteCard(cardId)
                .map(favoriteCard -> new ResponseEntity<>(favoriteCard, HttpStatus.CREATED))
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST)));
    }

    // --- Endpoints DELETE ---

    // DELETE /api/favorites/{cardId} (Remover uma carta dos favoritos)
    @DeleteMapping("/favorites/{cardId}")
    public Mono<ResponseEntity<Void>> removeFavorite(@PathVariable String cardId) {
        return cardService.removeFavoriteCard(cardId)
                .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)))
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }
}