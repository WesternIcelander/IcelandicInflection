package io.siggi.icelandicinflection;

public interface InflectionTagAttribute {
    String getEnglishName();

    String getIcelandicName();

    boolean test(InflectionTagI tag);
}
