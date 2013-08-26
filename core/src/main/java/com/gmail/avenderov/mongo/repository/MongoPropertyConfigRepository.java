package com.gmail.avenderov.mongo.repository;

import com.gmail.avenderov.api.repository.PropertyConfigRepository;
import com.gmail.avenderov.mongo.data.PropertyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author Alexey Venderov
 */
@Repository
public class MongoPropertyConfigRepository implements PropertyConfigRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoPropertyConfigRepository(final MongoTemplate mongoTemplate) {
        checkNotNull(mongoTemplate, "mongoTemplate must not be null");
        this.mongoTemplate = mongoTemplate;
    }

    public PropertyConfig insert(final PropertyConfig propertyConfig) {
        checkNotNull(propertyConfig, "propertyConfig must not be null");
        checkState(propertyConfig.getChildren().isEmpty(), "new config shouldn't have children");

        mongoTemplate.save(propertyConfig);
        return propertyConfig;
    }

}
