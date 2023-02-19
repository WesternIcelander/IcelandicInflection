package io.siggi.icelandicinflection;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.siggi.icelandicinflection.util.Util.compareBooleans;
import static io.siggi.icelandicinflection.util.Util.compareNullableEnum;
import static io.siggi.icelandicinflection.util.Util.firstNonZeroValue;

public final class InflectionTag implements Comparable<InflectionTag>, InflectionTagI {
    private final String toString;
    private final String toEnglishString;
    String tag;
    int alternativeId = 0;
    WordClass wordClass;
    boolean uninflected = false;
    QuestionWord questionWord;
    Article article;
    Inflection inflection;
    Case grammaticalCase;
    Degree degree;
    Gender gender;
    Mood mood;
    Perspective perspective;
    Plurality plurality;
    Tense tense;
    Voice voice;

    private final Pattern casePluralityAndArticle = Pattern.compile("(NF|ÞF|ÞGF|EF)(ET|FT)(gr|)");

    InflectionTag(WordClass wordClass, Gender gender, String tag, Consumer<String> unknownTagHandler) {
        if (tag == null) {
            throw new NullPointerException("tag cannot be null");
        }
        if (wordClass == WordClass.NOUN && gender == null) {
            throw new NullPointerException("gender cannot be null when wordClass is NOUN");
        }
        boolean genderDefined = gender != null;
        this.tag = tag;
        char lastCharacter = tag.charAt(tag.length() - 1);
        if (lastCharacter >= '2' && lastCharacter <= '9') {
            tag = tag.substring(0, tag.length() - 1);
            alternativeId = (lastCharacter - '1');
        }
        this.wordClass = wordClass;
        this.gender = gender;
        if (tag.equals("OBEYGJANLEGT")) {
            uninflected = true;
        } else {
            String[] tagParts = tag.split("-");
            for (String tagPart : tagParts) {
                Matcher caseAndPluralityMatcher = casePluralityAndArticle.matcher(tagPart);
                if (caseAndPluralityMatcher.matches()) {
                    switch (caseAndPluralityMatcher.group(1)) {
                        case "NF":
                            grammaticalCase = Case.NOMINATIVE;
                            break;
                        case "ÞF":
                            grammaticalCase = Case.ACCUSATIVE;
                            break;
                        case "ÞGF":
                            grammaticalCase = Case.DATIVE;
                            break;
                        case "EF":
                            grammaticalCase = Case.GENITIVE;
                            break;
                        default:
                            System.err.println("Unhandled tag " + tagPart);
                            break;
                    }
                    switch (caseAndPluralityMatcher.group(2)) {
                        case "ET":
                            plurality = Plurality.SINGULAR;
                            break;
                        case "FT":
                            plurality = Plurality.PLURAL;
                            break;
                        default:
                            System.err.println("Unhandled tag " + tagPart);
                            break;
                    }
                    if (caseAndPluralityMatcher.group(3).equals("gr")) {
                        article = Article.DEFINITE_ARTICLE;
                    } else if (wordClass == WordClass.NOUN) {
                        article = Article.NO_ARTICLE;
                    }
                    continue;
                }
                switch (tagPart) {
                    case "NF":
                        grammaticalCase = Case.NOMINATIVE;
                        break;
                    case "ÞF":
                        grammaticalCase = Case.ACCUSATIVE;
                        break;
                    case "ÞGF":
                        grammaticalCase = Case.DATIVE;
                        break;
                    case "EF":
                        grammaticalCase = Case.GENITIVE;
                        break;
                    case "það":
                        grammaticalCase = Case.DUMMY_SUBJECT;
                        break;
                    case "OP":
                        // other than not report as an unknown tag,
                        // do nothing as this tag is redundant as it
                        // only and always appears if the subject is
                        // in a case other than nominative when the
                        // mood is indicative or subjunctive.
                        break;
                    case "ET":
                        plurality = Plurality.SINGULAR;
                        break;
                    case "FT":
                        plurality = Plurality.PLURAL;
                        break;
                    case "ST":
                        plurality = Plurality.CLIPPED;
                        break;
                    case "KK":
                        gender = Gender.MASCULINE;
                        break;
                    case "KVK":
                        gender = Gender.FEMININE;
                        break;
                    case "HK":
                        gender = Gender.NEUTER;
                        break;
                    case "SERST":
                        gender = Gender.SPECIAL;
                        break;
                    case "1P":
                        perspective = Perspective.FIRST_PERSON;
                        break;
                    case "2P":
                        perspective = Perspective.SECOND_PERSON;
                        break;
                    case "3P":
                        perspective = Perspective.THIRD_PERSON;
                        break;
                    case "NT":
                        tense = Tense.PRESENT;
                        break;
                    case "ÞT":
                        tense = Tense.PAST;
                        break;
                    case "NH":
                        mood = Mood.INFINITIVE;
                        break;
                    case "FH":
                        mood = Mood.INDICATIVE;
                        break;
                    case "VH":
                        mood = Mood.SUBJUNCTIVE;
                        break;
                    case "BH":
                        mood = Mood.IMPERATIVE;
                        break;
                    case "SAGNB":
                        mood = Mood.SUPINE;
                        break;
                    case "LHNT":
                        mood = Mood.PRESENT_PARTICIPLE;
                        break;
                    case "LHÞT":
                        mood = Mood.PAST_PARTICIPLE;
                        break;
                    case "GM":
                        voice = Voice.ACTIVE;
                        break;
                    case "MM":
                        voice = Voice.MIDDLE;
                        break;
                    case "SP":
                        questionWord = QuestionWord.QUESTION_WORD;
                        break;
                    case "FST": // adverbs only
                        degree = Degree.POSITIVE;
                        break;
                    case "FSB": // adjectives only
                        degree = Degree.POSITIVE;
                        inflection = Inflection.STRONG;
                        break;
                    case "FVB": // adjectives only
                        degree = Degree.POSITIVE;
                        inflection = Inflection.WEAK;
                        break;
                    case "MST": // both adjectives and adverbs
                        if (wordClass != WordClass.ADVERB) {
                            inflection = Inflection.WEAK;
                        }
                        degree = Degree.COMPARATIVE;
                        break;
                    case "EST": // adverbs only
                        degree = Degree.SUPERLATIVE;
                        break;
                    case "ESB": // adjectives only
                        degree = Degree.SUPERLATIVE;
                        inflection = Inflection.STRONG;
                        break;
                    case "EVB": // adjectives only
                        degree = Degree.SUPERLATIVE;
                        inflection = Inflection.WEAK;
                        break;
                    case "SB": // appears in adjective form of verbs
                        inflection = Inflection.STRONG;
                        break;
                    case "VB": // appears in adjective form of verbs
                        inflection = Inflection.WEAK;
                        break;
                    default:
                        if (unknownTagHandler != null)
                            unknownTagHandler.accept(tagPart);
                        break;
                }
            }
        }
        this.gender = gender;
        if (wordClass == WordClass.VERB) {
            if (questionWord == null) {
                questionWord = QuestionWord.NOT_QUESTION_WORD;
            }
            if (grammaticalCase == null
                    && getQuestionWord() != QuestionWord.QUESTION_WORD
                    && (mood == Mood.INDICATIVE || mood == Mood.SUBJUNCTIVE)){
                grammaticalCase = Case.NOMINATIVE;
            }
        }
        if (uninflected) {
            toString = wordClass.getIcelandicName() + " óbeygjanlegt";
            toEnglishString = wordClass.getEnglishName() + " uninflected";
        } else {
            StringBuilder icelandicBuilder = new StringBuilder();
            StringBuilder englishBuilder = new StringBuilder();

            BiConsumer<String, String> stringAppender = (icelandic, english) -> {
                icelandicBuilder.append(", ").append(icelandic);
                englishBuilder.append(", ").append(english);
            };

            Consumer<InflectionTagAttribute> appender = (nameable) -> {
                if (nameable == null || nameable == QuestionWord.NOT_QUESTION_WORD) return;
                stringAppender.accept(nameable.getIcelandicName(), nameable.getEnglishName());
            };

            icelandicBuilder.append(wordClass.getIcelandicName());
            englishBuilder.append(wordClass.getEnglishName());

            if (wordClass == WordClass.NOUN) {
                icelandicBuilder.append(" (").append(gender.getIcelandicName()).append(")");
                englishBuilder.append(" (").append(gender.getEnglishName()).append(")");
            }
            icelandicBuilder.append(": ");
            englishBuilder.append(": ");
            if (wordClass == WordClass.VERB && mood != Mood.PAST_PARTICIPLE) {
                appender.accept(mood);
                appender.accept(voice);
                appender.accept(grammaticalCase);
                appender.accept(tense);
                appender.accept(plurality);
                appender.accept(perspective);
                appender.accept(questionWord);
            } else {
                appender.accept(inflection);
                appender.accept(degree);
                appender.accept(plurality);
                appender.accept(grammaticalCase);
                if (wordClass != WordClass.NOUN) appender.accept(gender);
                appender.accept(article);
            }

            toString = icelandicBuilder.toString().replaceFirst(": ,", ":");
            toEnglishString = englishBuilder.toString().replaceFirst(": ,", ":");
        }
    }

    public InflectionTag getInflectionTag() {
        return this;
    }

    public String getBinTag() {
        return tag;
    }

    public int getAlternativeId() {
        return alternativeId;
    }

    public WordClass getWordClass() {
        return wordClass;
    }

    public boolean isUninflected() {
        return uninflected;
    }

    public QuestionWord getQuestionWord() {
        return questionWord;
    }

    public Article getArticle() {
        return article;
    }

    public Inflection getInflection() {
        return inflection;
    }

    public Case getCase() {
        return grammaticalCase;
    }

    public Degree getDegree() {
        return degree;
    }

    public Gender getGender() {
        return gender;
    }

    public Mood getMood() {
        return mood;
    }

    public Perspective getPerspective() {
        return perspective;
    }

    public Plurality getPlurality() {
        return plurality;
    }

    public Tense getTense() {
        return tense;
    }

    public Voice getVoice() {
        return voice;
    }

    @Override
    public String toString() {
        return toString;
    }

    public String toEnglishString() {
        return toEnglishString;
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordClass, gender, tag);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof InflectionTag)) {
            return false;
        }
        InflectionTag o = ((InflectionTag) other);
        return Objects.equals(wordClass, o.wordClass)
                && Objects.equals(gender, o.gender)
                && tag.equals(o.tag);
    }

    @Override
    public int compareTo(InflectionTag o) {
        int wordClassComparison = compareNullableEnum(wordClass, o.wordClass, false);
        if (wordClassComparison != 0) return wordClassComparison;
        assert wordClass == o.wordClass : "wordClass doesn't match";
        if (wordClass == WordClass.VERB) {
            return firstNonZeroValue(
                    compareBooleans(uninflected, o.uninflected),
                    compareNullableEnum(questionWord, o.questionWord, false),
                    compareNullableEnum(mood, o.mood, false),
                    compareNullableEnum(degree, o.degree, true),
                    compareNullableEnum(voice, o.voice, true),
                    compareNullableEnum(grammaticalCase, o.grammaticalCase, false),
                    compareNullableEnum(inflection, o.inflection, false),
                    compareNullableEnum(tense, o.tense, false),
                    compareNullableEnum(plurality, o.plurality, false),
                    compareNullableEnum(perspective, o.perspective, false),
                    compareNullableEnum(gender, o.gender, false),
                    compareNullableEnum(article, o.article, true),
                    alternativeId - o.alternativeId
            );
        }
        return firstNonZeroValue(
                compareBooleans(uninflected, o.uninflected),
                compareNullableEnum(questionWord, o.questionWord, false),
                compareNullableEnum(mood, o.mood, false),
                compareNullableEnum(degree, o.degree, true),
                compareNullableEnum(voice, o.voice, true),
                compareNullableEnum(inflection, o.inflection, false),
                compareNullableEnum(tense, o.tense, false),
                compareNullableEnum(plurality, o.plurality, false),
                compareNullableEnum(grammaticalCase, o.grammaticalCase, false),
                compareNullableEnum(perspective, o.perspective, false),
                compareNullableEnum(gender, o.gender, false),
                compareNullableEnum(article, o.article, true),
                alternativeId - o.alternativeId
        );
    }

    public InflectionTagPredicate predicate() {
        return new InflectionTagPredicate()
                .require(questionWord)
                .require(article)
                .require(inflection)
                .require(grammaticalCase)
                .require(degree)
                .require(gender)
                .require(mood)
                .require(perspective)
                .require(plurality)
                .require(tense)
                .require(voice);
    }
}
