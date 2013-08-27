package com.gmail.avenderov.api.repository;

import com.gmail.avenderov.mongo.data.PropertyConfig;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Alexey Venderov
 */
public interface PropertyConfigRepository {

    /**
     * Returns {@link MongoTemplate} that is used for database interaction.
     *
     * @return {@link MongoTemplate} instance
     */
    MongoTemplate getMongoTemplate();

    /**
     * Inserts a new config into database.
     *
     * @param propertyConfig config to insert into database
     * @return The same object that was passed to it
     */
    PropertyConfig insert(PropertyConfig propertyConfig);

    /**
     * Checks if config with specified name checkConfigExist in database.
     *
     * @param name config name to check
     * @return {@code true} if config checkConfigExist, {@code false} otherwise
     */
    boolean checkConfigExist(String name);

}
