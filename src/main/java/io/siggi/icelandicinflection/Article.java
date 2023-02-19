package io.siggi.icelandicinflection;

import java.util.function.Predicate;

public enum Article implements Predicate<InflectionTagI>, InflectionTagAttribute {
    NO_ARTICLE("no article","án greinis"),
    DEFINITE_ARTICLE("with definite article","með greinis");

    private final String englishName;
    private final String icelandicName;

    private Article(String englishName, String icelandicName) {
        this.englishName = englishName;
        this.icelandicName = icelandicName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIcelandicName() {
        return icelandicName;
    }

    public static Article of(InflectionTagI tag) {
        return tag.getArticle();
    }

    @Override
    public boolean test(InflectionTagI tag) {
        return this == of(tag);
    }
}
