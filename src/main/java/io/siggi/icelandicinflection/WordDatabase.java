package io.siggi.icelandicinflection;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static io.siggi.icelandicinflection.util.Util.ICELANDIC;

public interface WordDatabase {

    default Collection<Word> getByHeadword(String headword) {
        String lowercaseHeadword = headword.toLowerCase(ICELANDIC);
        Collection<Word> words = getByInflectedForm(headword);
        try {
            words.removeIf(word -> !word.getHeadword().toLowerCase(ICELANDIC).equals(lowercaseHeadword));
            return words;
        } catch (UnsupportedOperationException e) {
            // this may occur if the collection is immutable
            Set<Word> newWords = new HashSet<>(words);
            newWords.removeIf(word -> !word.getHeadword().toLowerCase(ICELANDIC).equals(lowercaseHeadword));
            return newWords;
        }
    }

    Collection<Word> getByInflectedForm(String inflectedForm);

    default Collection<WordForm> getWordFormsByInflectedForm(String inflectedForm) {
        List<WordForm> forms = new LinkedList<>();
        Collection<Word> words = getByInflectedForm(inflectedForm);
        inflectedForm = inflectedForm.toLowerCase(ICELANDIC);
        for (Word word : words) {
            for (WordForm form : word.getInflections()) {
                if (form.getInflectedForm().toLowerCase(ICELANDIC).equals(inflectedForm)) {
                    forms.add(form);
                }
            }
        }
        return forms;
    }

    Collection<Word> getByInflectedFormPrefix(String inflectedFormPrefix);

    default Collection<WordForm> getWordFormsByInflectedFormPrefix(String inflectedFormPrefix) {
        List<WordForm> forms = new LinkedList<>();
        Collection<Word> words = getByInflectedFormPrefix(inflectedFormPrefix);
        inflectedFormPrefix = inflectedFormPrefix.toLowerCase(ICELANDIC);
        for (Word word : words) {
            for (WordForm form : word.getInflections()) {
                if (form.getInflectedForm().toLowerCase(ICELANDIC).startsWith(inflectedFormPrefix)) {
                    forms.add(form);
                }
            }
        }
        return forms;
    }

    Word getByBinId(int binId);

    Collection<InflectionTag> getAllInflectionTags();
}
