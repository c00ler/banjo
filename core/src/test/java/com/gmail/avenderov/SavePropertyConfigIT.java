package com.gmail.avenderov;

import com.gmail.avenderov.mongo.data.PropertyConfig;
import com.gmail.avenderov.mongo.data.PropertyConfigFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Alexey Venderov
 */
public class SavePropertyConfigIT {

    private ConfigurableApplicationContext applicationContext;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        applicationContext = new ClassPathXmlApplicationContext("META-INF/spring/mongo-context.xml");
        mongoTemplate = applicationContext.getBean("mongoTemplate", MongoTemplate.class);
        dropCollectionIfNecessary();
    }

    private void dropCollectionIfNecessary() {
        if (mongoTemplate.collectionExists(PropertyConfig.class)) {
            mongoTemplate.dropCollection(PropertyConfig.class);
        }
    }

    @Test
    public void testSavePropertyConfig() {
        final PropertyConfig propertyConfigToSave = PropertyConfigFactory.newPropertyConfig(
                this.getClass().getSimpleName() + "_" + Long.toString(System.currentTimeMillis()), null, null,
                ImmutableMap.of("key1", "value1", "key2", "value2", "key3", "value3"));

        mongoTemplate.save(propertyConfigToSave);

        assertThat("Collection for property config should exist", mongoTemplate.collectionExists(PropertyConfig.class),
                is(true));
        final List<PropertyConfig> configs = mongoTemplate.findAll(PropertyConfig.class);
        assertThat("There should be one config in collection", configs, hasSize(1));

        final PropertyConfig loadedPropertyConfig = Iterables.getOnlyElement(configs);
        assertThat("Loaded config has wrong name", loadedPropertyConfig.getName(),
                is(equalTo(propertyConfigToSave.getName())));
        assertThat("There should be no parents in loaded config", loadedPropertyConfig.getParents(), hasSize(0));
        assertThat("There should be no children in loaded config", loadedPropertyConfig.getChildren(), hasSize(0));
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
