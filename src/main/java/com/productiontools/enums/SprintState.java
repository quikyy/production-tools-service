package com.productiontools.enums;

import lombok.Getter;

@Getter
public enum SprintState {
    ACTIVE("active"),
    CLOSED("closed"),
    FUTURE("future");

    private final String state;

    SprintState(String state) {
        this.state = state;
    }
}
