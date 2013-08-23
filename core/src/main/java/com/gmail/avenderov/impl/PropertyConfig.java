package com.gmail.avenderov.impl;

import com.gmail.avenderov.api.Config;
import com.gmail.avenderov.api.ConfigType;

import java.util.Date;
import java.util.Map;

/**
 * User: avenderov
 */
public class PropertyConfig implements Config<Map<String, String>> {

    @Override
    public String getId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getRevision() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getCreatedAt() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getLastModifiedAt() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ConfigType getConfigType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, String> getContent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
