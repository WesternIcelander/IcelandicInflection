package io.siggi.icelandicinflection;

import java.util.function.Predicate;

public enum Voice implements Predicate<InflectionTagI>, InflectionTagAttribute {
    ACTIVE("active voice", "germynd"),
    MIDDLE("middle voice", "miðmynd");

    private final String englishName;
    private final String icelandicName;

    private Voice(String englishName, String icelandicName) {
        this.englishName = englishName;
        this.icelandicName = icelandicName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIcelandicName() {
        return icelandicName;
    }

    public static Voice of(InflectionTagI tag) {
        return tag.getVoice();
    }

    @Override
    public boolean test(InflectionTagI tag) {
        return this == of(tag);
    }
}
