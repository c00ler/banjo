package com.gmail.avenderov.mongo.repository;

import com.gmail.avenderov.api.repository.PropertyConfigRepository;
import com.gmail.avenderov.mongo.data.PropertyConfig;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Alexey Venderov
 */
@Repository
public class MongoPropertyConfigRepository implements PropertyConfigRepository {

    private static final String ID_FIELD = "_id";

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoPropertyConfigRepository(final MongoTemplate mongoTemplate) {
        checkNotNull(mongoTemplate, "mongoTemplate must not be null");
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Override
    public PropertyConfig insert(final PropertyConfig propertyConfig) {
        checkNotNull(propertyConfig, "propertyConfig must not be null");

        if (!propertyConfig.getParents().isEmpty()) {
            checkParentsExistInDatabase(propertyConfig);
        }
        mongoTemplate.insert(propertyConfig);

        return propertyConfig;
    }

    private void checkParentsExistInDatabase(final PropertyConfig propertyConfig) {
        final Set<String> parents = propertyConfig.getParents();
        // In database config name is stored in _id field
        final long numberOfParentsInDatabase = mongoTemplate.count(query(where(ID_FIELD).in(parents)),
                PropertyConfig.class);
        if (numberOfParentsInDatabase != parents.size()) {
            throw new DataIntegrityViolationException(format("Can't insert new config '%1$s', " +
                    "because some of the parents are missing in database", propertyConfig.getName()));
        }
    }

    @Override
    public boolean checkConfigExist(final String name) {
        checkArgument(isNotBlank(name), "name must not be blank");
        final long numberOfObjectsInDatabase = mongoTemplate.count(query(where(ID_FIELD).is(name)),
                PropertyConfig.class);
        return numberOfObjectsInDatabase == 1;
    }

    @Override
    public Optional<PropertyConfig> findConfig(final String name) {
        checkArgument(isNotBlank(name), "name must not be blank");

        return Optional.fromNullable(mongoTemplate.findOne(query(where(ID_FIELD).is(name)), PropertyConfig.class));
    }

    @Override
    public Map<String, PropertyConfig> findConfigWithParents(final String name) {
        final Optional<PropertyConfig> optionalPropertyConfig = findConfig(name);
        if (optionalPropertyConfig.isPresent()) {
            final PropertyConfig propertyConfig = optionalPropertyConfig.get();
            if (!propertyConfig.getParents().isEmpty()) {
                final List<PropertyConfig> parents = mongoTemplate.find(query(where(ID_FIELD).in(propertyConfig
                        .getParents())), PropertyConfig.class);
                // This check should always be true, because we are not going to delete config files
                checkState(parents.size() == propertyConfig.getParents().size(),
                        format("Number of parents in database is not equal to number of parents in '%1$s' config", name));
                final ImmutableMap.Builder<String, PropertyConfig> resultBuilder =
                        ImmutableMap.<String, PropertyConfig>builder().put(propertyConfig.getName(), propertyConfig);
                for (final PropertyConfig parent : parents) {
                    resultBuilder.put(parent.getName(), parent);
                }
                return resultBuilder.build();
            }
            return Collections.singletonMap(propertyConfig.getName(), propertyConfig);
        }
        return Collections.emptyMap();
    }

}
