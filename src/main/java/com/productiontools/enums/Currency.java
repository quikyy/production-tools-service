package com.productiontools.enums;

import lombok.Getter;

@Getter
public enum Currency {
    USD("usd"),
    GBP("gbp"),
    EUR("eur"),
    CAD("cad"),
    PLN("pln");

    private final String name;

    Currency(String name) {
        this.name = name;
    }
}
