package com.gmail.avenderov.mongo.repository;

import com.gmail.avenderov.api.repository.PropertyConfigRepository;
import com.gmail.avenderov.mongo.data.PropertyConfig;
import com.gmail.avenderov.mongo.data.PropertyConfigFactory;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Alexey Venderov
 */
public class MongoPropertyConfigRepositoryIT {

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

    private static String randomName() {
        return MongoPropertyConfigRepositoryIT.class.getSimpleName() + "_" + RandomStringUtils.randomAlphanumeric(20);
    }

    @Test
    public void testInsertConfigWithParents() {
        final PropertyConfig parent1PropertyConfig = PropertyConfigFactory.newPropertyConfig(randomName(), null,
                ImmutableMap.of("key1", "value1"));
        propertyConfigRepository.insert(parent1PropertyConfig);
        assertThat("Wrong number of config files in collection", countConfigFilesInCollection(), is(1L));

        final PropertyConfig parent2PropertyConfig = PropertyConfigFactory.newPropertyConfig(randomName(), null,
                ImmutableMap.of("key2", "value2"));
        propertyConfigRepository.insert(parent2PropertyConfig);
        assertThat("Wrong number of config files in collection", countConfigFilesInCollection(), is(2L));

        final PropertyConfig propertyConfigToSave = PropertyConfigFactory.newPropertyConfig(randomName(),
                ImmutableSet.of(parent1PropertyConfig.getName(), parent2PropertyConfig.getName()),
                ImmutableMap.of("key3", "value3"));
        propertyConfigRepository.insert(propertyConfigToSave);
        assertThat("Wrong number of config files in collection", countConfigFilesInCollection(), is(3L));
        final Optional<PropertyConfig> optionalLoadedPropertyConfig =
                propertyConfigRepository.findConfig(propertyConfigToSave.getName());
        assertThat("Config was not found in database", optionalLoadedPropertyConfig.isPresent(), is(true));
        final PropertyConfig loadedPropertyConfig = optionalLoadedPropertyConfig.get();
        assertThat("Wrong parents in loaded config", loadedPropertyConfig.getParents(),
                contains(parent1PropertyConfig.getName(), parent2PropertyConfig.getName()));
        assertThat("Loaded config has wrong revision", loadedPropertyConfig.getRevision(), is(1));
    }

    private long countConfigFilesInCollection() {
        return propertyConfigRepository.getMongoTemplate().count(null, PropertyConfig.class);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testInsertConfigWhenNotAllParentsExistInDatabase() {
        final PropertyConfig parentPropertyConfig = PropertyConfigFactory.newPropertyConfig(randomName(), null,
                ImmutableMap.of("key1", "value1"));

        propertyConfigRepository.insert(parentPropertyConfig);
        assertThat("Wrong number of config files in collection", countConfigFilesInCollection(), is(1L));

        final PropertyConfig propertyConfigToSave = PropertyConfigFactory.newPropertyConfig(randomName(),
                ImmutableSet.of(parentPropertyConfig.getName(), "parent"),
                ImmutableMap.of("key2", "value2", "key3", "value3"));

        propertyConfigRepository.insert(propertyConfigToSave);
        fail("Exception should be thrown if not all parents exist in database");
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testInsertConfigWhenParentDoesNotExistInDatabase() {
        final PropertyConfig propertyConfigToSave = PropertyConfigFactory.newPropertyConfig(randomName(),
                ImmutableSet.of("parent"),
                ImmutableMap.of("key1", "value1", "key2", "value2", "key3", "value3"));

        propertyConfigRepository.insert(propertyConfigToSave);
        fail("Exception should be thrown if parent doesn't exist in database");
    }

    @Test(expected = DuplicateKeyException.class)
    public void testInsertObjectThatAlreadyExistInDatabase() {
        final PropertyConfig propertyConfigToSave = PropertyConfigFactory.newPropertyConfig(randomName(), null,
                ImmutableMap.of("key1", "value1", "key2", "value2", "key3", "value3"));

        propertyConfigRepository.insert(propertyConfigToSave);
        assertThat("Wrong number of config files in collection", countConfigFilesInCollection(), is(1L));

        propertyConfigRepository.insert(propertyConfigToSave);
        fail("Exception should be thrown if object already exist in database");
    }

    @Test
    public void testInsertPropertyConfig() {
        final PropertyConfig propertyConfigToSave = PropertyConfigFactory.newPropertyConfig(randomName(), null,
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

    @Test
    public void testExist() {
        final String name = randomName();
        assertThat("Config shouldn't exist in database", propertyConfigRepository.checkConfigExist(name), is(false));

        final PropertyConfig propertyConfigToSave = PropertyConfigFactory.newPropertyConfig(name, null,
                ImmutableMap.of("key1", "value1"));
        propertyConfigRepository.insert(propertyConfigToSave);

        assertThat("Config should exist in database after it has been inserted",
                propertyConfigRepository.checkConfigExist(name), is(true));
    }

    @Test
    public void testFindConfigWithParentsWhenThereAreNoParentsInConfig() {
        final String name = randomName();
        assertThat("Empty map has to be returned if there is no such config in database",
                propertyConfigRepository.findConfigWithParents(name).entrySet(), hasSize(0));

        final PropertyConfig propertyConfigToSave = PropertyConfigFactory.newPropertyConfig(name, null,
                ImmutableMap.of("key1", "value1"));
        propertyConfigRepository.insert(propertyConfigToSave);
        final Map<String, PropertyConfig> configs = propertyConfigRepository.findConfigWithParents(name);

        assertThat("Only one config has to be in the map", configs.entrySet(), hasSize(1));
        assertThat("Config name was not found in the map", configs, hasKey(name));
    }

    @Test
    public void testFindConfigWithParents() {
        final PropertyConfig parent1PropertyConfig = PropertyConfigFactory.newPropertyConfig(randomName(), null,
                ImmutableMap.of("key1", "value1"));
        propertyConfigRepository.insert(parent1PropertyConfig);
        assertThat("Parent config 1 was not found in database",
                propertyConfigRepository.checkConfigExist(parent1PropertyConfig.getName()), is(true));

        final PropertyConfig parent2PropertyConfig = PropertyConfigFactory.newPropertyConfig(randomName(), null,
                ImmutableMap.of("key2", "value2"));
        propertyConfigRepository.insert(parent2PropertyConfig);
        assertThat("Parent config 2 was not found in database",
                propertyConfigRepository.checkConfigExist(parent2PropertyConfig.getName()), is(true));

        final PropertyConfig anotherPropertyConfig = PropertyConfigFactory.newPropertyConfig(randomName(),
                ImmutableSet.of(parent1PropertyConfig.getName()),
                ImmutableMap.of("key2", "value2"));
        propertyConfigRepository.insert(anotherPropertyConfig);
        assertThat("Config was not found in database",
                propertyConfigRepository.checkConfigExist(anotherPropertyConfig.getName()), is(true));

        final PropertyConfig propertyConfigToSave = PropertyConfigFactory.newPropertyConfig(randomName(),
                ImmutableSet.of(parent1PropertyConfig.getName(), parent2PropertyConfig.getName()),
                ImmutableMap.of("key3", "value3"));
        propertyConfigRepository.insert(propertyConfigToSave);

        final Map<String, PropertyConfig> configs =
                propertyConfigRepository.findConfigWithParents(propertyConfigToSave.getName());
        assertThat("Wrong number of config files in returned map", configs.entrySet(), hasSize(3));
        assertThat("Returned map contains wrong config files", configs, allOf(hasKey(propertyConfigToSave.getName()),
                hasKey(parent1PropertyConfig.getName()), hasKey(parent2PropertyConfig.getName())));
    }

    @After
    public void tearDown() {
        dropCollectionIfNecessary();
        applicationContext.close();
    }

}
