package io.siggi.icelandicinflection;

import io.siggi.icelandicinflection.util.CsvReader;
import io.siggi.icelandicinflection.util.LineReader;
import io.siggi.icelandicinflection.util.StringDeduplicator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import static io.siggi.icelandicinflection.util.Util.ICELANDIC;

public final class WordDatabaseMemory implements WordDatabase {
    private final InflectionTags tags;
    private final NavigableMap<String, Set<Word>> byHeadword = new TreeMap<>();
    private final Map<String,Set<Word>> byHeadwordImmutableSets = new HashMap<>();
    private final NavigableMap<String, Set<Word>> byInflectedForm = new TreeMap<>();
    private final Map<String,Set<Word>> byInflectedFormImmutableSets = new HashMap<>();
    private final Map<Integer, Word> byBinId = new HashMap<>();
    private final Map<Integer, Long> csvIndex;
    private int highestBinId = 0;

    public WordDatabaseMemory(InputStream in) throws IOException {
        this(in, false);
    }

    public WordDatabaseMemory(InputStream in, boolean indexCsv) throws IOException {
        csvIndex = indexCsv ? new HashMap<>() : null;
        LineReader lineReader = new LineReader(in);
        CsvReader reader = new CsvReader(lineReader, ";");
        tags = new InflectionTags(false);
        String[] line;
        StringDeduplicator deduplicator = new StringDeduplicator();
        while ((line = reader.readLine()) != null) {
            addCsvLine(deduplicator, line, lineReader.getBeginningOfLastLine());
        }
        for (Map.Entry<String,Set<Word>> entry : byHeadword.entrySet()) {
            byHeadwordImmutableSets.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
        }
        for (Map.Entry<String,Set<Word>> entry : byInflectedForm.entrySet()) {
            byInflectedFormImmutableSets.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
        }
    }

    @Override
    public Collection<Word> getByHeadword(String headword) {
        return byHeadwordImmutableSets.getOrDefault(headword.toLowerCase(ICELANDIC), (Set<Word>) Collections.EMPTY_SET);
    }

    @Override
    public Collection<Word> getByInflectedForm(String inflectedForm) {
        return byInflectedFormImmutableSets.getOrDefault(inflectedForm.toLowerCase(ICELANDIC), (Set<Word>) Collections.EMPTY_SET);
    }

    @Override
    public Collection<Word> getByInflectedFormPrefix(String inflectedFormPrefix) {
        List<Word> words = new LinkedList<>();
        inflectedFormPrefix = inflectedFormPrefix.toLowerCase(ICELANDIC);
        for (Map.Entry<String, Set<Word>> entry : byInflectedForm.tailMap(inflectedFormPrefix).entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(inflectedFormPrefix)) break;
            words.addAll(entry.getValue());
        }
        return words;
    }

    @Override
    public Word getByBinId(int binId) {
        return byBinId.get(binId);
    }

    private void addCsvLine(StringDeduplicator deduplicator, String[] line, long fileLocation) {
        String headword = line[0];
        int binId = Integer.parseInt(line[1]);
        String wordClassString = line[2];
        String wordCategory = line[3];
        highestBinId = Math.max(binId, highestBinId);

        WordClass wordClass = WordClass.getByCode(wordClassString);
        Gender gender = Gender.getByCode(wordClassString);

        Word word = byBinId.get(binId);
        if (word == null) {
            byBinId.put(binId, word = new Word(deduplicator.deduplicate(headword), binId, wordClass, gender, deduplicator.deduplicate(wordCategory)));

            Set<Word> words = byHeadword.computeIfAbsent(headword.toLowerCase(ICELANDIC), k -> new TreeSet<>());
            words.add(word);

            words = byInflectedForm.computeIfAbsent(headword.toLowerCase(ICELANDIC), k -> new TreeSet<>());
            words.add(word);

            if (csvIndex != null) {
                csvIndex.put(binId, fileLocation);
            }
        }
        InflectionTag inflectionTag = tags.get(wordClass, gender, line[10]);

        String inflectedForm = line[9];

        Set<Word> words = byInflectedForm.computeIfAbsent(inflectedForm.toLowerCase(ICELANDIC), k -> new TreeSet<>());
        words.add(word);

        word.addInflectedForm(inflectionTag, inflectedForm);
    }

    public void createIndex(File directory) throws IOException {
        if (csvIndex == null) {
            throw new IllegalStateException("WordDatabaseMemory was not created with csv index");
        }
        File csvIndexFile = new File(directory, "csv-idx");
        File wordsFile = new File(directory, "words");
        File wordsFileIndex = new File(directory, "words-idx");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try (ByteArrayOutputStream bufferStream = new ByteArrayOutputStream()) {
            for (int i = 0; i <= highestBinId; i++) {
                Long loc = csvIndex.get(i);
                long location = (loc == null ? -1L : loc.longValue());
                bufferStream.write((int) ((location >> 56L) & 0xff));
                bufferStream.write((int) ((location >> 48L) & 0xff));
                bufferStream.write((int) ((location >> 40L) & 0xff));
                bufferStream.write((int) ((location >> 32L) & 0xff));
                bufferStream.write((int) ((location >> 24L) & 0xff));
                bufferStream.write((int) ((location >> 16L) & 0xff));
                bufferStream.write((int) ((location >> 8L) & 0xff));
                bufferStream.write((int) (location & 0xff));
            }
            try (FileOutputStream out = new FileOutputStream(csvIndexFile)) {
                bufferStream.writeTo(out);
            }
        }
        try (FileOutputStream out = new FileOutputStream(wordsFile);
             FileOutputStream indexOut = new FileOutputStream(wordsFileIndex);
             ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
             OutputStreamWriter bufferWriter = new OutputStreamWriter(bufferStream, StandardCharsets.UTF_8)) {
            long filePointer = 0L;
            int arrayPointer = 0;
            SortedSet<String> inflectedForms = new TreeSet<>();
            inflectedForms.addAll(byInflectedForm.keySet());
            for (String inflectedForm : inflectedForms) {
                Set<Word> words = byInflectedForm.get(inflectedForm);
                if (words.isEmpty()) continue;
                if (arrayPointer % 512 == 0) {
                    bufferStream.reset();
                    bufferWriter.write(inflectedForm);
                    bufferWriter.write("=");
                    bufferWriter.write(Long.toString(filePointer));
                    bufferWriter.write("\n");
                    bufferWriter.flush();
                    bufferStream.writeTo(indexOut);
                }
                bufferStream.reset();
                bufferWriter.write(inflectedForm + "=");
                boolean skipComma = true;
                for (Word word : words) {
                    if (skipComma) skipComma = false;
                    else bufferWriter.write(",");
                    bufferWriter.write(Integer.toString(word.getBinId()));
                }
                bufferWriter.write("\n");
                bufferWriter.flush();
                bufferStream.writeTo(out);
                arrayPointer += 1;
                filePointer += bufferStream.size();
            }
        }
    }

    @Override
    public Collection<InflectionTag> getAllInflectionTags() {
        return tags.getAllTags();
    }
}
