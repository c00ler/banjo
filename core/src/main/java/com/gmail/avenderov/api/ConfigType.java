package com.gmail.avenderov.api;

/**
 * User: avenderov
 */
public enum ConfigType {

    TEXT("text/plain");

    private final String description;

    private ConfigType(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }

}
