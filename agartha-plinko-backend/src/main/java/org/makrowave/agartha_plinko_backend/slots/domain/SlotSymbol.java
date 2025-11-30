package org.makrowave.agartha_plinko_backend.slots.domain;

import lombok.Getter;

@Getter
public enum SlotSymbol {
    CHERRY(2.0),
    LEMON(3.0),
    ORANGE(5.0),
    PLUM(10.0),
    BELL(20.0),
    BAR(50.0),
    SEVEN(100.0),
    DIAMOND(500.0);

    private final double multiplier;

    SlotSymbol(double multiplier) {
        this.multiplier = multiplier;
    }

    public static SlotSymbol fromString(String str) {
        try {
            return SlotSymbol.valueOf(str);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown symbol: " + str);
        }
    }
}