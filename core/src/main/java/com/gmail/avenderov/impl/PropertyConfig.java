package com.gmail.avenderov.impl;

import com.gmail.avenderov.api.Config;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: avenderov
 */
@Document(collection = "props")
public class PropertyConfig implements Config<Map<String, String>> {

    @Id
    private String name;

    private int revision;

    private Date createdAt;

    private Date lastModifiedAt;

    private List<String> parents;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getRevision() {
        return revision;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public Date getLastModifiedAt() {
        return lastModifiedAt;
    }

    @Override
    public Type getConfigType() {
        return Type.TEXT;
    }

    @Override
    public Map<String, String> getContent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
