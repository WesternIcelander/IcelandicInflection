package io.siggi.icelandicinflection;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import static io.siggi.icelandicinflection.util.Util.compareNullableEnum;
import static io.siggi.icelandicinflection.util.Util.firstNonZeroValue;

public final class Word implements Comparable<Word>, WordI {
    private final String headword;
    private final int binId;
    private final WordClass wordClass;
    private final Gender gender;
    private final String category;
    private final NavigableSet<WordForm> inflections = new TreeSet<>();
    private final transient NavigableSet<WordForm> inflectionsImmutable = Collections.unmodifiableNavigableSet(inflections);
    private final transient String toString;

    public String getHeadword() {
        return headword;
    }

    public int getBinId() {
        return binId;
    }

    public WordClass getWordClass() {
        return wordClass;
    }

    public Gender getGender() {
        return gender;
    }

    public String getCategory() {
        return category;
    }

    public Set<WordForm> getInflections() {
        return inflectionsImmutable;
    }

    Word(String headword, int binId, WordClass wordClass, Gender gender, String category) {
        this.headword = headword;
        this.binId = binId;
        this.wordClass = wordClass;
        this.gender = gender;
        this.category = category;

        StringBuilder toStringBuilder = new StringBuilder();
        toStringBuilder.append(wordClass.getIcelandicName());
        if (wordClass == WordClass.NOUN) {
            toStringBuilder.append(" (").append(gender.getIcelandicName()).append(")");
        }
        toStringBuilder.append(": ").append(headword);
        this.toString = toStringBuilder.toString();
    }

    @Override
    public int compareTo(Word o) {
        if (this == o) return 0;
        return firstNonZeroValue(
                headword.compareTo(o.headword),
                compareNullableEnum(wordClass, o.wordClass, false),
                compareNullableEnum(gender, o.gender, false),
                binId - o.binId
        );
    }

    void addInflectedForm(InflectionTag inflectionTag, String inflectedForm) {
        inflections.add(new WordForm(this, inflectionTag, inflectedForm));
    }

    @Override
    public String toString() {
        return toString;
    }
}
