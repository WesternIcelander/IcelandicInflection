package io.siggi.icelandicinflection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static io.siggi.icelandicinflection.util.Util.ICELANDIC;

public enum WordClass implements Predicate<WordI>, InflectionTagAttribute {
    /**
     * Nouns
     */
    NOUN("noun", "nafnorð", "kk", "kvk", "hk"),
    /**
     * Reflexive pronouns - sig, sér, sín
     */
    REFLEXIVE_PRONOUN("reflexive pronoun", "afturbeygt fornafn", "afn"),
    /**
     * Personal pronouns - hann, hún, það
     */
    PERSONAL_PRONOUN("personal pronoun", "persónufornafn", "pfn"),
    /**
     * Other pronouns - sá
     */
    OTHER_PRONOUN("pronoun", "fornafn", "fn"),
    /**
     * Adjectives
     */
    ADJECTIVE("adjective", "lýsingarorð", "lo"),
    /**
     * Verbs
     */
    VERB("verb", "sagnorð", "so"),
    /**
     * Adverbs
     */
    ADVERB("adverb", "atviksorð", "ao"),
    /**
     * Prepositions - um, frá, til
     */
    PREPOSITION("preposition", "forsetning", "fs"),
    /**
     * Conjunctions - og, eða, þegar, ef
     */
    CONJUNCTION("conjunction", "samtenging", "st"),
    /**
     * Numbers - einn, tveir, þrír, fjórir
     */
    NUMBER("number", "töluorð", "to"),
    /**
     * Ordinals - fyrsti, annar, þriðji, fjórði
     */
    ORDINAL("ordinal", "raðtala", "rt"),
    /**
     * Exclamations - Hæ, bless, ókey, jæja
     */
    EXCLAMATION("exclamation", "upphrópun", "uh"),
    /**
     * Standalone definite article - hinn
     */
    DEFINITE_ARTICLE("definite article", "greinir", "gr"),
    /**
     * Infinitive marker - að
     */
    INFINITVE_MARKER("infinitive marker", "nafnháttarmerki", "nhm");

    private final String englishName;
    private final String icelandicName;
    private final String[] codes;

    private WordClass(String englishName, String icelandicName,
                      String... codes) {
        this.englishName = englishName;
        this.icelandicName = icelandicName;
        this.codes = codes;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIcelandicName() {
        return icelandicName;
    }

    public String[] getCodes() {
        return Arrays.copyOf(codes, codes.length);
    }

    private static final Map<String, WordClass> byCode = new HashMap<>();

    static {
        for (WordClass wordClass : values()) {
            for (String code : wordClass.codes) {
                byCode.put(code.toLowerCase(ICELANDIC), wordClass);
                byCode.put(code.toUpperCase(ICELANDIC), wordClass);
            }
        }
    }

    public static WordClass getByCode(String code) {
        return byCode.get(code);
    }

    public static WordClass of(WordI word) {
        return word.getWordClass();
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
