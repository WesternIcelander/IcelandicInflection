package io.siggi.icelandicinflection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static io.siggi.icelandicinflection.util.Util.ICELANDIC;

public enum Gender implements Predicate<WordI>, InflectionTagAttribute {
    MASCULINE("masculine", "karlkyn", "kk"),
    FEMININE("feminine", "kvenkyn", "kvk"),
    NEUTER("neuter", "hvorugkyn", "hk"),
    SPECIAL("special", "sérstætt", "serst");

    private final String englishName;
    private final String icelandicName;
    private final String code;

    private Gender(String englishName, String icelandicName, String code) {
        this.englishName = englishName;
        this.icelandicName = icelandicName;
        this.code = code;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIcelandicName() {
        return icelandicName;
    }

    public String getCode() {
        return code;
    }

    private static final Map<String, Gender> byCode = new HashMap<>();

    static {
        for (Gender gender : values()) {
            byCode.put(gender.code.toLowerCase(ICELANDIC), gender);
            byCode.put(gender.code.toUpperCase(ICELANDIC), gender);
        }
    }

    public static Gender getByCode(String code) {
        return byCode.get(code);
    }

    public static Gender of(WordI word) {
        return word.getGender();
    }

    @Override
    public boolean test(WordI word) {
        return this == of(word);
    }

    @Override
    public boolean test(InflectionTagI tag) {
        return this == of(tag);
    }
}
