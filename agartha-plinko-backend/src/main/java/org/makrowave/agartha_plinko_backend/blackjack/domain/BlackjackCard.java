package org.makrowave.agartha_plinko_backend.blackjack.domain;

import org.makrowave.agartha_plinko_backend.shared.domain.AbstractCard;
import org.makrowave.agartha_plinko_backend.shared.domain.CardRank;
import org.makrowave.agartha_plinko_backend.shared.domain.CardSuit;
import org.springframework.lang.NonNull;

import java.util.Arrays;

public class BlackjackCard extends AbstractCard {
    public BlackjackCard(CardSuit suit, CardRank rank) {
        super(suit, rank);
    }

    @Override
    public int getValue() {
        if (this.rank.equals(CardRank.ACE)) return 11;
        return Math.min(this.rank.getValue(), 10);
    }

    @Override
    public int compareTo(@NonNull AbstractCard other) {
        return 0;
    }

    public static BlackjackCard fromString(String str) {
        if (str == null || str.length() < 2) {
            throw new IllegalArgumentException("Invalid card string: " + str);
        }

        String suitSymbol = str.substring(0, 1);
        String rankSymbol = str.substring(1);

        CardSuit suit = Arrays.stream(CardSuit.values())
                .filter(s -> s.toString().equals(suitSymbol))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown suit: " + suitSymbol));

        CardRank rank = Arrays.stream(CardRank.values())
                .filter(r -> r.toString().equals(rankSymbol))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown rank: " + rankSymbol));

        return new BlackjackCard(suit, rank);
    }


}
