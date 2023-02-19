package io.siggi.icelandicinflection;

import java.util.function.Predicate;

public enum Inflection implements Predicate<InflectionTagI>, InflectionTagAttribute {
    STRONG("strong inflection", "sterk beyging"),
    WEAK("weak inflection", "veik beyging");

    private final String englishName;
    private final String icelandicName;

    private Inflection(String englishName, String icelandicName) {
        this.englishName = englishName;
        this.icelandicName = icelandicName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIcelandicName() {
        return icelandicName;
    }

    public static Inflection of(InflectionTagI tag) {
        return tag.getInflection();
    }

    @Override
    public boolean test(InflectionTagI tag) {
        return this == of(tag);
    }
}
