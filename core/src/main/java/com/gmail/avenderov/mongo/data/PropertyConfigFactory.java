package com.gmail.avenderov.mongo.data;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.collections.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Alexey Venderov
 */
public class PropertyConfigFactory {

    private PropertyConfigFactory() {
    }

    /**
     * Creates new properties config for insert into database.
     *
     * @param name
     * @param parents
     * @param properties
     * @return
     */
    public static PropertyConfig newPropertyConfig(final String name, final Set<String> parents,
                                                   final Map<String, String> properties) {
        checkArgument(isNotBlank(name), "name must not be blank");
        checkArgument(isNotEmpty(properties), "properties must not be empty");

        final Date now = Calendar.getInstance().getTime();

        return new PropertyConfig(name, 1, now, now, removeNullAndBlankValue(parents), Maps.filterEntries(properties,
                new Predicate<Map.Entry<String, String>>() {

                    @Override
                    public boolean apply(final Map.Entry<String, String> input) {
                        return input != null && isNotBlank(input.getKey()) && isNotBlank(input.getValue());
                    }

                }));
    }

    private static Set<String> removeNullAndBlankValue(final Set<String> set) {
        if (CollectionUtils.isEmpty(set)) {
            return Collections.emptySet();
        }
        return Sets.filter(set, new Predicate<String>() {

            @Override
            public boolean apply(final String input) {
                return isNotBlank(input);
            }

        });
    }

}
