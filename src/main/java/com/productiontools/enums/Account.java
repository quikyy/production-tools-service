package com.productiontools.enums;
import lombok.Getter;

@Getter
public enum Account {
    APP("app"),
    ATLASSIAN("atlassian"),
    ACTIVE("true");

    private final String accountType;

    Account(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountType() {
        return accountType;
    }
}
