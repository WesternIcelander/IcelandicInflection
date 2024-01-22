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
     * Pronouns that don't fit in the more specific categories - sá
     */
    PRONOUN("pronoun", "fornafn", NOUN, "fn"),
    /**
     * Reflexive pronouns - sig, sér, sín
     */
    REFLEXIVE_PRONOUN("reflexive pronoun", "afturbeygt fornafn", PRONOUN, "afn"),
    /**
     * Personal pronouns - hann, hún, það
     */
    PERSONAL_PRONOUN("personal pronoun", "persónufornafn", PRONOUN, "pfn"),
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
    private final WordClass superclass;
    private final String[] codes;

    WordClass(String englishName, String icelandicName,
                      String... codes) {
        this(englishName, icelandicName, null, codes);
    }

    WordClass(String englishName, String icelandicName, WordClass superclass,
                      String... codes) {
        this.englishName = englishName;
        this.icelandicName = icelandicName;
        this.superclass = superclass;
        this.codes = codes;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIcelandicName() {
        return icelandicName;
    }

    public WordClass getSuperclass() {
        return superclass;
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
        return test(of(word));
    }

    @Override
    public boolean test(InflectionTagI tag) {
        return test(of(tag));
    }

    /**
     * Test if the specified word class is the same as this word class or a subclass of this word class.
     *
     * @param wordClass the word class to test
     * @return true if the word class is the same as this word class or a subclass of this word class.
     */
    public boolean test(WordClass wordClass) {
        if (wordClass == null) return false;
        if (wordClass == this) return true;
        if (wordClass.superclass != null) return test(wordClass.superclass);
        return false;
    }
}
