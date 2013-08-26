package com.gmail.avenderov.mongo.data;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Alexey Venderov
 */
public class PropertyConfigTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testPropertiesAreImmutable() {
        final Date now = Calendar.getInstance().getTime();
        final PropertyConfig propertyConfig = new PropertyConfig("test", 1, now, now, null, null,
                new HashMap<String, String>() {{
                    put("key1", "value1");
                }});

        final Map<String, String> properties = propertyConfig.getContent();
        assertThat("Wrong number of properties", properties.entrySet(), hasSize(1));
        properties.put("key2", "value2");
        fail("Returned map has to be immutable");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testParentsAreImmutable() {
        final Date now = Calendar.getInstance().getTime();
        final PropertyConfig propertyConfig = new PropertyConfig("test", 1, now, now, new HashSet<String>() {{
            add("parent");
        }}, null, ImmutableMap.of("key1", "value1"));

        final Set<String> parents = propertyConfig.getParents();
        assertThat("Wrong number of parents in config", parents, hasSize(1));
        parents.add("anotherParent");
        fail("Returned set has to be immutable");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testChildrenAreImmutable() {
        final Date now = Calendar.getInstance().getTime();
        final PropertyConfig propertyConfig = new PropertyConfig("test", 1, now, now, null, new HashSet<String>() {{
            add("child");
        }}, ImmutableMap.of("key1", "value1"));

        final Set<String> children = propertyConfig.getChildren();
        assertThat("Wrong number of children in config", children, hasSize(1));
        children.add("anotherChild");
        fail("Returned set has to be immutable");
    }

}
