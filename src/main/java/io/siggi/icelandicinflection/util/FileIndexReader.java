package io.siggi.icelandicinflection.util;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class FileIndexReader {
    private FileIndexReader() {
    }

    public static long findIntAddress(File index, int key) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(index, "r")) {
            long pos = (long) key * 8L;
            if (pos > raf.length())
                return -1L;
            raf.seek(pos);
            return raf.readLong();
        } catch (EOFException eof) {
            return -1L;
        }
    }

    public static long findStringAddress(File index, String key) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(index))) {
            String line;
            long lastValue = 0L;
            while ((line = reader.readLine()) != null) {
                int equalPos = line.indexOf("=");
                if (equalPos == -1) continue;
                String keyInFile = line.substring(0, equalPos);
                long value = Long.parseLong(line.substring(equalPos + 1));
                int comparison = key.compareTo(keyInFile);
                if (comparison == 0) {
                    return value;
                } else if (comparison < 0) {
                    return lastValue;
                }
                lastValue = value;
            }
            return lastValue;
        }
    }
}
