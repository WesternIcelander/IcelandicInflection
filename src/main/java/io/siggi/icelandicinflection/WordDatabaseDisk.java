package io.siggi.icelandicinflection;

import io.siggi.icelandicinflection.util.CsvReader;
import io.siggi.icelandicinflection.util.FileIndexReader;
import io.siggi.icelandicinflection.util.LineReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static io.siggi.icelandicinflection.util.Util.ICELANDIC;

public final class WordDatabaseDisk implements WordDatabase {
    private final File csvFile;
    private final File indexDirectory;
    private final File csvIndexFile;
    private final File wordsFile;
    private final File wordsIndexFile;
    private final InflectionTags tags;

    public WordDatabaseDisk(File csvFile, File indexDirectory) {
        this.csvFile = csvFile;
        this.indexDirectory = indexDirectory;
        this.csvIndexFile = new File(indexDirectory, "csv-idx");
        this.wordsFile = new File(indexDirectory, "words");
        this.wordsIndexFile = new File(indexDirectory, "words-idx");
        this.tags = new InflectionTags(true);
    }

    @Override
    public Collection<Word> getByInflectedForm(String inflectedForm) {
        inflectedForm = inflectedForm.toLowerCase(ICELANDIC);
        try {
            long startingPoint = FileIndexReader.findStringAddress(wordsIndexFile, inflectedForm);
            if (startingPoint == -1L) {
                return Collections.EMPTY_SET;
            }
            try (FileInputStream in = new FileInputStream(wordsFile);
                 LineReader reader = new LineReader(in)) {
                in.skip(startingPoint);
                String line;
                while ((line = reader.readLine()) != null) {
                    int equalPos = line.indexOf("=");
                    if (equalPos == -1) continue;
                    String key = line.substring(0, equalPos);
                    if (!key.equals(inflectedForm)) continue;
                    String[] values = line.substring(equalPos + 1).split(",");
                    Set<Word> words = new HashSet<>();
                    for (String value : values) {
                        Word word = getByBinId(Integer.parseInt(value));
                        if (word != null) words.add(word);
                    }
                    return words.isEmpty() ? Collections.EMPTY_SET : words;
                }
                return Collections.EMPTY_SET;
            }
        } catch (IOException e) {
            throw new WordDatabaseException(e);
        }
    }

    @Override
    public Collection<Word> getByInflectedFormPrefix(String inflectedFormPrefix) {
        List<Word> words = new LinkedList<>();
        inflectedFormPrefix = inflectedFormPrefix.toLowerCase(ICELANDIC);
        try {
            long startingPoint = FileIndexReader.findStringAddress(wordsIndexFile, inflectedFormPrefix);
            if (startingPoint == -1L) {
                return null;
            }
            try (FileInputStream in = new FileInputStream(wordsFile);
                 LineReader reader = new LineReader(in)) {
                in.skip(startingPoint);
                String line;
                while ((line = reader.readLine()) != null) {
                    int equalPos = line.indexOf("=");
                    if (equalPos == -1) continue;
                    String key = line.substring(0, equalPos);
                    if (!key.startsWith(inflectedFormPrefix)) {
                        if (key.compareTo(inflectedFormPrefix) > 0)
                            break;
                        else
                            continue;
                    }
                    String[] values = line.substring(equalPos + 1).split(",");
                    for (String value : values) {
                        Word word = getByBinId(Integer.parseInt(value));
                        if (word != null) words.add(word);
                    }
                }
            }
        } catch (IOException e) {
            throw new WordDatabaseException(e);
        }
        return words;
    }

    @Override
    public Word getByBinId(int binId) {
        try {
            long startingPoint = FileIndexReader.findIntAddress(csvIndexFile, binId);
            if (startingPoint == -1L) {
                return null;
            }
            Word word = null;
            try (FileInputStream in = new FileInputStream(csvFile);
                 CsvReader csvReader = new CsvReader(new LineReader(in), ";")) {
                in.skip(startingPoint);
                String[] line;
                while ((line = csvReader.readLine()) != null) {
                    String headword = line[0];
                    int binIdInFile = Integer.parseInt(line[1]);
                    if (binId != binIdInFile) {
                        // while words are in alphabetical order, different words with the exact
                        // same headword may be fragmented amongst themselves in CSVs from BIN.
                        if (word == null) continue;
                        if (!headword.equals(word.getHeadword())) break;
                        continue;
                    }
                    String wordClassString = line[2];
                    String wordCategory = line[3];
                    String inflectedForm = line[9];

                    WordClass wordClass = WordClass.getByCode(wordClassString);
                    Gender gender = Gender.getByCode(wordClassString);

                    if (word == null) {
                        word = new Word(headword, binId, wordClass, gender, wordCategory);
                    }

                    InflectionTag inflectionTag = tags.get(wordClass, gender, line[10]);

                    word.addInflectedForm(inflectionTag, inflectedForm);
                }
            }
            return word;
        } catch (IOException e) {
            throw new WordDatabaseException(e);
        }
    }

    @Override
    public Collection<InflectionTag> getAllInflectionTags() {
        return tags.getAllTags();
    }
}
