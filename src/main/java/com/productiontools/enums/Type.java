package com.productiontools.enums;

import lombok.Getter;

@Getter
public enum Type {
    SUBTASK("Sub-task"),
    TASK("Task"),
    STORY("Story"),
    BUG("Bug"),
    EPIC("Epic");

    private final String name;

    Type(String name) {
        this.name = name;
    }
}
