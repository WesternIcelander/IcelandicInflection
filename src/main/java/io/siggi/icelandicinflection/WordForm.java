package io.siggi.icelandicinflection;

import static io.siggi.icelandicinflection.util.Util.firstNonZeroValue;

public final class WordForm implements Comparable<WordForm>, InflectionTagI {
    private transient final Word word;
    private final InflectionTag inflectionTag;
    private final String inflectedForm;

    WordForm(Word word, InflectionTag inflectionTag, String inflectedForm) {
        if (word == null || inflectionTag == null || inflectedForm == null)
            throw new NullPointerException();
        this.word = word;
        this.inflectionTag = inflectionTag;
        this.inflectedForm = inflectedForm;
    }

    public Word getWord() {
        return word;
    }

    @Override
    public InflectionTag getInflectionTag() {
        return inflectionTag;
    }

    public String getInflectedForm() {
        return inflectedForm;
    }

    @Override
    public int compareTo(WordForm o) {
        if (this == o) return 0;
        return firstNonZeroValue(
                word.compareTo(o.word),
                inflectionTag.compareTo(o.inflectionTag)
        );
    }
}
