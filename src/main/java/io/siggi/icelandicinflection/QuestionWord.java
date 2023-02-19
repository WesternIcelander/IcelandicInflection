package io.siggi.icelandicinflection;

import java.util.function.Predicate;

public enum QuestionWord implements Predicate<InflectionTagI>, InflectionTagAttribute {
    NOT_QUESTION_WORD("not question form","ekki spurnarmyndir"),
    QUESTION_WORD("question form","spurnarmyndir");

    private final String englishName;
    private final String icelandicName;

    private QuestionWord(String englishName, String icelandicName) {
        this.englishName = englishName;
        this.icelandicName = icelandicName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIcelandicName() {
        return icelandicName;
    }

    public static QuestionWord of(InflectionTagI tag) {
        return tag.getQuestionWord();
    }

    @Override
    public boolean test(InflectionTagI tag) {
        return this == of(tag);
    }
}
