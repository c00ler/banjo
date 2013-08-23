package com.gmail.avenderov.api;

import java.util.Date;

/**
 * User: avenderov
 */
public interface Config<T> {

    String getId();

    long getRevision();

    String getName();

    Date getCreatedAt();

    Date getLastModifiedAt();

    ConfigType getConfigType();

    T getContent();

}
