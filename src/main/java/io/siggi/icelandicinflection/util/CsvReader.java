package io.siggi.icelandicinflection.util;

import java.io.Closeable;
import java.io.IOException;

public final class CsvReader implements Closeable {
    private final String separator;
    private final LineReader reader;

    public CsvReader(LineReader reader, String separator) {
        this.separator = separator;
        this.reader = reader;
    }

    public String[] readLine() throws IOException {
        String line;
        do {
            line = reader.readLine();
            if (line == null) return null;
        } while (line.isEmpty());
        return line.split(separator, Integer.MAX_VALUE);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
