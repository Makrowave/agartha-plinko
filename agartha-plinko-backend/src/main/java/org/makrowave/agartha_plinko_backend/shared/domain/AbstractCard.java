package org.makrowave.agartha_plinko_backend.shared.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.NonNull;

@AllArgsConstructor
public abstract class AbstractCard implements Comparable<AbstractCard> {

    protected final CardSuit suit;
    protected final CardRank rank;

    public abstract int getValue();

    public abstract int getSuitValue();

    public CardRank getRank() {
        return rank;
    }

    public CardSuit getSuit() {
        return suit;
    }

    @Override
    public abstract int compareTo(@NonNull AbstractCard other);

    @Override
    public String toString() {
        return suit.toString() + rank.toString();
    }


    public static AbstractCard fromString(String str) {
        throw new UnsupportedOperationException("Implement in subclass");
    }
}


