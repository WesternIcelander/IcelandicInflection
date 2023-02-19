package io.siggi.icelandicinflection;

import java.util.function.Predicate;

public enum Plurality implements Predicate<InflectionTagI>, InflectionTagAttribute {
    SINGULAR("singular","eintala"),
    PLURAL("plural","fleirtala"),
    CLIPPED("clipped","stýfður");

    private final String englishName;
    private final String icelandicName;

    private Plurality(String englishName, String icelandicName) {
        this.englishName = englishName;
        this.icelandicName = icelandicName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIcelandicName() {
        return icelandicName;
    }

    public static Plurality of(InflectionTagI tag) {
        return tag.getPlurality();
    }

    @Override
    public boolean test(InflectionTagI tag) {
        return this == of(tag);
    }
}
