package com.productiontools.enums;

public enum Customfield {
    START_DATE("customfield_10015"),
    TEAM("customfield_10001"),
    SPRINT("customfield_10020"),
    APPROVERS("customfield_10003");

    private final String fieldName;

    Customfield(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
