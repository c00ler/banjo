package com.gmail.avenderov.mongo.repository;

import com.gmail.avenderov.api.repository.PropertyConfigRepository;
import com.gmail.avenderov.mongo.data.PropertyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

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
        final long numberOfParentsInDatabase = mongoTemplate.count(query(where("_id").in(parents)),
                PropertyConfig.class);
        if (numberOfParentsInDatabase != parents.size()) {
            throw new DataIntegrityViolationException(format("Can't insert new config '%1$s', " +
                    "because some of the parents are missing in database", propertyConfig.getName()));
        }
    }

}
