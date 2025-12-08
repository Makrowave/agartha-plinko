package org.makrowave.agartha_plinko_backend.shared.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.NonNull;

@AllArgsConstructor
public abstract class AbstractCard implements Comparable<AbstractCard> {

    protected final CardSuit suit;

    @Getter
    protected final CardRank rank;

    public abstract int getValue();

    @Override
    public abstract int compareTo(@NonNull AbstractCard other);

    @Override
    public String toString() {
        return suit.toString() + rank.toString();
    }
}


