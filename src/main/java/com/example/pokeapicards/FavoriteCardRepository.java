package com.example.pokeapicards;

import com.example.pokeapicards.FavoriteCard;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class FavoriteCardRepository {

    private final Map<String, FavoriteCard> favorites = new HashMap<>();

    public Collection<FavoriteCard> findAll() {
        return favorites.values();
    }

    public Optional<FavoriteCard> findById(String id) {
        return Optional.ofNullable(favorites.get(id));
    }

    public FavoriteCard save(FavoriteCard favoriteCard) {
        favorites.put(favoriteCard.getCardId(), favoriteCard);
        return favoriteCard;
    }

    public void deleteById(String id) {
        favorites.remove(id);
    }

    public boolean existsById(String id) {
        return favorites.containsKey(id);
    }
}