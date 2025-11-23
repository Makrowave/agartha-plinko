package org.makrowave.agartha_plinko_backend.shared.util;

import org.makrowave.agartha_plinko_backend.shared.domain.AbstractCard;

import java.util.List;
import java.util.function.Function;

public class CardUtil {
    public static List<String> cardsToStrings(List<? extends AbstractCard> cards) {
        return cards.stream()
                .map(AbstractCard::toString)
                .toList();
    }

    public static <T extends AbstractCard> List<T> stringsToCards(
            List<String> strings,
            Function<String, T> factory
    ) {
        return strings.stream()
                .map(factory)
                .toList();
    }

}
