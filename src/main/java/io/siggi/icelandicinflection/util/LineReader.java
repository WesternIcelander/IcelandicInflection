package io.siggi.icelandicinflection.util;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class LineReader implements Closeable {

    private final InputStream in;
    private boolean wasJustCR = false;
    private long filePointer = 0L;
    private long beginningOfLastLine = 0L;
    private byte[] buffer = new byte[256];

    public LineReader(InputStream in) {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        this.in = in;
    }

    public String readLine() throws IOException {
        int bufferPointer = 0;
        while (true) {
            int value = in.read();
            if (value == -1) {
                if (bufferPointer == 0) {
                    return null;
                }
            }
            if (value == 0x0A) {
                filePointer++;
                if (wasJustCR) continue;
                else break;
            }
            wasJustCR = false;
            if (value == 0x0D) {
                filePointer++;
                wasJustCR = true;
                break;
            }
            if (bufferPointer >= buffer.length) {
                int newLength = buffer.length * 2;
                if (newLength < 0 || newLength < buffer.length || newLength >= Integer.MAX_VALUE - 0x10)
                    newLength = Integer.MAX_VALUE - 0x10;
                buffer = Arrays.copyOf(buffer, newLength);
            }
            if (bufferPointer == 0) beginningOfLastLine = filePointer;
            buffer[bufferPointer++] = (byte) value;
            filePointer++;
        }
        return new String(buffer, 0, bufferPointer, StandardCharsets.UTF_8);
    }

    public long getBeginningOfLastLine() {
        return beginningOfLastLine;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
