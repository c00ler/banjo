package com.gmail.avenderov.mongo.data;

import com.gmail.avenderov.api.Config;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Alexey Venderov
 */
public class PropertyConfigFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNewPropertyConfigWithBlankName() {
        PropertyConfigFactory.newPropertyConfig("", null, ImmutableMap.of("key1", "value1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNewPropertyConfigWithoutProperties() {
        PropertyConfigFactory.newPropertyConfig("test", null, ImmutableMap.<String, String>of());
    }

    @Test
    public void testBlankParentsAreNotAllowed() {
        final PropertyConfig propertyConfig = PropertyConfigFactory.newPropertyConfig("test",
                Sets.newHashSet("", "parent"), ImmutableMap.of("key1", "value1"));

        assertThat("Wrong number of parents in config", propertyConfig.getParents(), hasSize(1));
        assertThat("Wrong parent config value", Iterables.getOnlyElement(propertyConfig.getParents()),
                is(equalTo("parent")));
    }

    @Test
    public void testBlankPropertyKeysAndValuesAreNotAllowed() {
        final PropertyConfig propertyConfig = PropertyConfigFactory.newPropertyConfig("test", null,
                ImmutableMap.of("key1", "value1", "", "value2", "key3", ""));

        assertThat("Wrong number of properties in config", propertyConfig.getContent().entrySet(), hasSize(1));
        assertThat("Wrong properties in config", propertyConfig.getContent(), hasEntry("key1", "value1"));
    }

    @Test
    public void testCreateNewPropertyConfig() {
        final PropertyConfig propertyConfig = PropertyConfigFactory.newPropertyConfig("test", null,
                ImmutableMap.of("key1", "value1"));

        assertThat("Wrong config revision", propertyConfig.getRevision(), is(1));
        assertThat("Created and modified dates should be equal", propertyConfig.getCreatedAt(),
                is(equalTo(propertyConfig.getLastModifiedAt())));
        assertThat("Wrong config type", propertyConfig.getConfigType(), is(equalTo(Config.Type.TEXT)));
        assertThat("New config shouldn't have children", propertyConfig.getChildren(), hasSize(0));
    }

}
