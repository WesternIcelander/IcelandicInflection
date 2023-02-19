package io.siggi.icelandicinflection;

import java.util.function.Predicate;

public enum Tense implements Predicate<InflectionTagI>, InflectionTagAttribute {
    PRESENT("present tense", "nútíð"),
    PAST("past tense", "þátíð");

    private final String englishName;
    private final String icelandicName;

    private Tense(String englishName, String icelandicName) {
        this.englishName = englishName;
        this.icelandicName = icelandicName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIcelandicName() {
        return icelandicName;
    }

    public static Tense of(InflectionTagI tag) {
        return tag.getTense();
    }

    @Override
    public boolean test(InflectionTagI tag) {
        return this == of(tag);
    }
}
