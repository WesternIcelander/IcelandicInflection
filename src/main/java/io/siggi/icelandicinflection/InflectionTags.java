package io.siggi.icelandicinflection;

import io.siggi.icelandicinflection.util.NoOpLock;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class InflectionTags {
    InflectionTags(boolean sync) {
        if (sync) {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            readLock = lock.readLock();
            writeLock = lock.writeLock();
        } else {
            readLock = writeLock = new NoOpLock();
        }
    }

    private final Map<String, InflectionTag> tags = new HashMap<>();
    private final SortedSet<InflectionTag> tagSet = new TreeSet<>();
    private final SortedSet<InflectionTag> tagSetImmutable = Collections.unmodifiableSortedSet(tagSet);
    private final Set<String> unhandledTags = new HashSet<>();
    private final Set<String> unhandledTagsImmutable = Collections.unmodifiableSet(unhandledTags);
    private final Lock readLock;
    private final Lock writeLock;

    InflectionTag get(WordClass wordClass, Gender gender, String tag) {
        String internalTag = (wordClass == null ? "" : wordClass.name())
                + (gender == null ? "" : ("-" + gender.name()))
                + tag;
        readLock.lock();
        try {
            InflectionTag inflectionTag = tags.get(internalTag);
            if (inflectionTag != null) return inflectionTag;
        } finally {
            readLock.unlock();
        }
        writeLock.lock();
        try {
            InflectionTag inflectionTag = tags.get(internalTag);
            if (inflectionTag == null) {
                tags.put(internalTag, inflectionTag = new InflectionTag(wordClass, gender, tag, this::addUnhandledTag));
                tagSet.add(inflectionTag);
            }
            return inflectionTag;
        } finally {
            writeLock.unlock();
        }
    }

    private void addUnhandledTag(String tag) {
        unhandledTags.add(tag);
    }

    Set<String> getUnhandledTags() {
        return unhandledTagsImmutable;
    }

    public Collection<InflectionTag> getAllTags() {
        return tagSetImmutable;
    }
}
