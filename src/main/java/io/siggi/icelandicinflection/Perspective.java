package io.siggi.icelandicinflection;

import java.util.function.Predicate;

public enum Perspective implements Predicate<InflectionTagI>, InflectionTagAttribute {
    FIRST_PERSON("1st person","1. persóna"),
    SECOND_PERSON("2nd person","2. persóna"),
    THIRD_PERSON("3rd person","3. persóna");

    private final String englishName;
    private final String icelandicName;

    private Perspective(String englishName, String icelandicName) {
        this.englishName = englishName;
        this.icelandicName = icelandicName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIcelandicName() {
        return icelandicName;
    }

    public static Perspective of(InflectionTagI tag) {
        return tag.getPerspective();
    }

    @Override
    public boolean test(InflectionTagI tag) {
        return this == of(tag);
    }
}
