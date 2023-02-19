package io.siggi.icelandicinflection.util;

import java.util.HashMap;
import java.util.Map;

public final class StringDeduplicator {
    private final Map<String, String> internedStrings = new HashMap<>();

    public String deduplicate(String string) {
        return internedStrings.computeIfAbsent(string, k -> k);
    }
}
