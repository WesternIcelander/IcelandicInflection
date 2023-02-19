package io.siggi.icelandicinflection;

import java.util.function.Predicate;

public enum Case implements Predicate<InflectionTagI>, InflectionTagAttribute {
    NOMINATIVE("nominative", "nefnifall"),
    ACCUSATIVE("accusative","þolfall"),
    DATIVE("dative","þágufall"),
    GENITIVE("genitive","eignarfall"),
    DUMMY_SUBJECT("dummy subject","gervifrumlag");

    private final String englishName;
    private final String icelandicName;

    private Case(String englishName, String icelandicName) {
        this.englishName = englishName;
        this.icelandicName = icelandicName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIcelandicName() {
        return icelandicName;
    }

    public static Case of(InflectionTagI tag) {
        return tag.getCase();
    }

    @Override
    public boolean test(InflectionTagI tag) {
        return this == of(tag);
    }
}
