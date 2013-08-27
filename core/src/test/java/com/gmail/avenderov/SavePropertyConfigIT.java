package com.gmail.avenderov;

import com.gmail.avenderov.api.repository.PropertyConfigRepository;
import com.gmail.avenderov.mongo.data.PropertyConfig;
import com.gmail.avenderov.mongo.data.PropertyConfigFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Alexey Venderov
 */
public class SavePropertyConfigIT {

    private ConfigurableApplicationContext applicationContext;

    private PropertyConfigRepository propertyConfigRepository;

    @Before
    public void setUp() {
        applicationContext = new ClassPathXmlApplicationContext("META-INF/spring/banjo-context.xml");
        propertyConfigRepository = applicationContext.getBean(PropertyConfigRepository.class);
        dropCollectionIfNecessary();
    }

    private void dropCollectionIfNecessary() {
        final MongoTemplate mongoTemplate = propertyConfigRepository.getMongoTemplate();
        if (mongoTemplate.collectionExists(PropertyConfig.class)) {
            mongoTemplate.dropCollection(PropertyConfig.class);
        }
    }

    @Test(expected = DuplicateKeyException.class)
    public void testSaveObjectThatAlreadyExistInDatabase() {
        final PropertyConfig propertyConfigToSave = PropertyConfigFactory.newPropertyConfig(
                this.getClass().getSimpleName() + "_" + Long.toString(System.currentTimeMillis()), null,
                ImmutableMap.of("key1", "value1", "key2", "value2", "key3", "value3"));

        propertyConfigRepository.insert(propertyConfigToSave);
        final MongoTemplate mongoTemplate = propertyConfigRepository.getMongoTemplate();
        assertThat("Wrong number of config files in collection", mongoTemplate.count(null, PropertyConfig.class),
                is(1L));

        propertyConfigRepository.insert(propertyConfigToSave);
        fail("Exception should be thrown if object already exist in database");
    }

    @Test
    public void testSavePropertyConfig() {
        final PropertyConfig propertyConfigToSave = PropertyConfigFactory.newPropertyConfig(
                this.getClass().getSimpleName() + "_" + Long.toString(System.currentTimeMillis()), null,
                ImmutableMap.of("key1", "value1", "key2", "value2", "key3", "value3"));

        propertyConfigRepository.insert(propertyConfigToSave);

        final MongoTemplate mongoTemplate = propertyConfigRepository.getMongoTemplate();
        assertThat("Collection for property config should exist", mongoTemplate.collectionExists(PropertyConfig.class),
                is(true));
        final List<PropertyConfig> configs = mongoTemplate.findAll(PropertyConfig.class);
        assertThat("There should be one config in collection", configs, hasSize(1));

        final PropertyConfig loadedPropertyConfig = Iterables.getOnlyElement(configs);
        assertThat("Loaded config has wrong name", loadedPropertyConfig.getName(),
                is(equalTo(propertyConfigToSave.getName())));
        assertThat("There should be no parents in loaded config", loadedPropertyConfig.getParents(), hasSize(0));
        assertThat("Loaded config has wrong revision", loadedPropertyConfig.getRevision(), is(1));
        assertThat("Loaded config has wrong createAt value", loadedPropertyConfig.getCreatedAt(),
                is(equalTo(propertyConfigToSave.getCreatedAt())));
        assertThat("Loaded config has wrong lastModifiedAt value", loadedPropertyConfig.getLastModifiedAt(),
                is(equalTo(propertyConfigToSave.getLastModifiedAt())));
        assertThat("Loaded config has wrong properties", loadedPropertyConfig.getContent(), allOf(hasEntry("key1",
                "value1"), hasEntry("key2", "value2"), hasEntry("key3", "value3")));
    }

    @After
    public void tearDown() {
        dropCollectionIfNecessary();
        applicationContext.close();
    }

}
