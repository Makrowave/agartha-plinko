package org.makrowave.agartha_plinko_backend.blackjack.domain;

import org.makrowave.agartha_plinko_backend.shared.domain.AbstractCard;
import org.makrowave.agartha_plinko_backend.shared.domain.CardRank;
import org.makrowave.agartha_plinko_backend.shared.domain.CardSuit;
import org.springframework.lang.NonNull;

public class BlackjackCard extends AbstractCard {
    public BlackjackCard(CardSuit suit, CardRank rank) {
        super(suit, rank);
    }

    @Override
    public int getSuitValue() {
        return 0;
    }

    @Override
    public int getValue() {
        if(this.rank.equals(CardRank.ACE)) return 11;
        return Math.min(this.rank.getValue(), 10);
    }

    @Override
    public int compareTo(@NonNull AbstractCard other) {
        return 0;
    }
}
