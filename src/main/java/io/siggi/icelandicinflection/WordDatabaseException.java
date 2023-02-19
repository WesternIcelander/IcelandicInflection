package io.siggi.icelandicinflection;

public class WordDatabaseException extends RuntimeException {
    public WordDatabaseException(String message) {
        super(message);
    }
    public WordDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    public WordDatabaseException(Throwable cause) {
        super(cause);
    }
}
