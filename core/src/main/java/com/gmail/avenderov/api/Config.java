package com.gmail.avenderov.api;

import java.util.Date;

/**
 * User: avenderov
 */
public interface Config<T> {

    String getName();

    int getRevision();

    Date getCreatedAt();

    Date getLastModifiedAt();

    Type getConfigType();

    T getContent();

    public enum Type {

        TEXT("text/plain");

        private final String description;

        private Type(final String description) {
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

}
