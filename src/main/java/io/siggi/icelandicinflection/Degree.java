package io.siggi.icelandicinflection;

import java.util.function.Predicate;

public enum Degree implements Predicate<InflectionTagI>, InflectionTagAttribute {
    POSITIVE("positive","frumstig"),
    COMPARATIVE("comparative","miðstig"),
    SUPERLATIVE("superlative","efstastig");

    private final String englishName;
    private final String icelandicName;

    private Degree(String englishName, String icelandicName) {
        this.englishName = englishName;
        this.icelandicName = icelandicName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIcelandicName() {
        return icelandicName;
    }

    public static Degree of(InflectionTagI tag) {
        return tag.getDegree();
    }

    @Override
    public boolean test(InflectionTagI tag) {
        return this == of(tag);
    }
}
