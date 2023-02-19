package io.siggi.icelandicinflection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public final class InflectionTagPredicate implements Predicate<InflectionTagI> {
    public InflectionTagPredicate() {
    }

    private final Map<Class<? extends InflectionTagAttribute>, InflectionTagAttribute> conditions = new HashMap<>();

    public InflectionTagPredicate require(InflectionTagAttribute attribute) {
        if (attribute == null) return this;
        conditions.put(attribute.getClass(), attribute);
        return this;
    }

    public InflectionTagPredicate removeRequirement(InflectionTagAttribute attribute) {
        conditions.remove(attribute.getClass());
        return this;
    }

    public InflectionTagPredicate removeRequirement(Class<? extends InflectionTagAttribute> attribute) {
        conditions.remove(attribute);
        return this;
    }

    @Override
    public boolean test(InflectionTagI inflectionTag) {
        if (inflectionTag.isUninflected()) {
            return true;
        }
        for (Map.Entry<Class<? extends InflectionTagAttribute>, InflectionTagAttribute> entry : conditions.entrySet()) {
            InflectionTagAttribute value = entry.getValue();
            if (!value.test(inflectionTag)) {
                return false;
            }
        }
        return true;
    }
}
