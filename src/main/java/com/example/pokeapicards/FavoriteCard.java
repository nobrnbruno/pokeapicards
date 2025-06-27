package com.example.pokeapicards;

import java.util.Objects;

public class FavoriteCard {
    private String cardId;
    private String cardName;
    private String imageUrl; // Pode ser útil para exibir no frontend

    public FavoriteCard() {}

    public FavoriteCard(String cardId, String cardName, String imageUrl) {
        this.cardId = cardId;
        this.cardName = cardName;
        this.imageUrl = imageUrl;
    }

    // Getters e Setters
    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteCard that = (FavoriteCard) o;
        return Objects.equals(cardId, that.cardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId);
    }
}