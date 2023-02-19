package io.siggi.icelandicinflection;

public interface InflectionTagI extends WordI {

    InflectionTagI getInflectionTag();

    default String getBinTag() {
        return getInflectionTag().getBinTag();
    }

    default int getAlternativeId() {
        return getInflectionTag().getAlternativeId();
    }

    default WordClass getWordClass() {
        return getInflectionTag().getWordClass();
    }

    default boolean isUninflected() {
        return getInflectionTag().isUninflected();
    }

    default QuestionWord getQuestionWord() {
        return getInflectionTag().getQuestionWord();
    }

    default Article getArticle() {
        return getInflectionTag().getArticle();
    }

    default Inflection getInflection() {
        return getInflectionTag().getInflection();
    }

    default Case getCase() {
        return getInflectionTag().getCase();
    }

    default Degree getDegree() {
        return getInflectionTag().getDegree();
    }

    default Gender getGender() {
        return getInflectionTag().getGender();
    }

    default Mood getMood() {
        return getInflectionTag().getMood();
    }

    default Perspective getPerspective() {
        return getInflectionTag().getPerspective();
    }

    default Plurality getPlurality() {
        return getInflectionTag().getPlurality();
    }

    default Tense getTense() {
        return getInflectionTag().getTense();
    }

    default Voice getVoice() {
        return getInflectionTag().getVoice();
    }

    default InflectionTagPredicate predicate() {
        return getInflectionTag().predicate();
    }
}
