package io.siggi.icelandicinflection.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public final class Util {
    private Util() {
    }

    public static final Locale ICELANDIC;
    public static final Comparator<String> ICELANDIC_SORTER;

    static {
        Locale icelandic = null;
        for (Locale locale : Locale.getAvailableLocales()) {
            if (!locale.getCountry().equals("IS")) continue;
            if (icelandic == null) {
                icelandic = locale;
            }
            if (locale.toString().equals("is_IS")) {
                icelandic = locale;
                break;
            }
        }
        if (icelandic == null) {
            icelandic = Locale.ROOT;
        }
        ICELANDIC = icelandic;
        ICELANDIC_SORTER = Collator.getInstance(ICELANDIC)::compare;
    }

    public static int firstNonZeroValue(int... values) {
        for (int value : values) {
            if (value != 0) return value;
        }
        return 0;
    }

    public static int compareNullableEnum(Enum a, Enum b, boolean nullFirst) {
        if (a == b) return 0;
        if (a == null) return nullFirst ? -1 : 1;
        else if (b == null) return nullFirst ? 1 : -1;
        return a.compareTo(b);
    }

    public static int compareBooleans(boolean a, boolean b) {
        if (a == b) return 0;
        return a ? 1 : -1;
    }
}
