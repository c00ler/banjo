package com.gmail.avenderov.api.repository;

import com.gmail.avenderov.mongo.data.PropertyConfig;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Alexey Venderov
 */
public interface PropertyConfigRepository {

    MongoTemplate getMongoTemplate();

    PropertyConfig insert(PropertyConfig propertyConfig);

}
