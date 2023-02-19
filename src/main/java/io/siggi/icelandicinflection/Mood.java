package io.siggi.icelandicinflection;

import java.util.function.Predicate;

public enum Mood implements Predicate<InflectionTagI>, InflectionTagAttribute {
    INFINITIVE("infinitive","nafnháttur"),
    INDICATIVE("indicative","framsöguháttur"),
    SUBJUNCTIVE("subjunctive","viðtengingarháttur"),
    IMPERATIVE("imperative","boðháttur"),
    SUPINE("supine","sagnbót"),
    PRESENT_PARTICIPLE("present participle","lýsingarháttur nútíð"),
    PAST_PARTICIPLE("past participle","lýsingarháttur þátíð");

    private final String englishName;
    private final String icelandicName;

    private Mood(String englishName, String icelandicName) {
        this.englishName = englishName;
        this.icelandicName = icelandicName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIcelandicName() {
        return icelandicName;
    }

    public static Mood of(InflectionTagI tag) {
        return tag.getMood();
    }

    @Override
    public boolean test(InflectionTagI tag) {
        return this == of(tag);
    }
}
