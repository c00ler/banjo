package com.gmail.avenderov.mongo.data;

import com.gmail.avenderov.api.Config;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.collections.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * User: avenderov
 */
@Document(collection = "props")
public class PropertyConfig {

    @Id
    private final String name;

    private final int revision;

    private final Date createdAt;

    private final Date lastModifiedAt;

    private final Set<String> parents;

    private final Map<String, String> properties;

    public PropertyConfig(final String name, final int revision, final Date createdAt, final Date lastModifiedAt,
                          final Set<String> parents, final Map<String, String> properties) {
        checkArgument(isNotBlank(name), "name must not be null");
        checkArgument(revision > 0, "revision must be greater than zero");
        checkNotNull(createdAt, "createdAt must not be null");
        checkNotNull(lastModifiedAt, "lastModifiedAt must not be null");
        checkArgument(isNotEmpty(properties), "properties must no be empty");

        this.name = name;
        this.revision = revision;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.parents = CollectionUtils.isNotEmpty(parents) ? ImmutableSet.copyOf(parents) : ImmutableSet.<String>of();
        this.properties = ImmutableMap.copyOf(properties);
    }

    public String getName() {
        return name;
    }

    public int getRevision() {
        return revision;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getLastModifiedAt() {
        return lastModifiedAt;
    }

    public Config.Type getConfigType() {
        return Config.Type.TEXT;
    }

    public Set<String> getParents() {
        return parents;
    }

    public Map<String, String> getContent() {
        return properties;
    }

}
