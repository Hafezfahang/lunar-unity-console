//
//  StringUtils.java
//
//  Lunar Unity Mobile Console
//  https://github.com/SpaceMadness/lunar-unity-console
//
//  Copyright 2015-2021 Alex Lementuev, SpaceMadness.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//


package spacemadness.com.lunarconsole.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StringUtils {
    // Force floating point numbers to '.' format
    private static final NumberFormat FLOATING_POINT_FORMAT = new DecimalFormat(
            "0.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    public static boolean isValidInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidFloat(String str) {
        return parseFloat(str) != null;
    }

    public static Integer parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int parseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Float parseFloat(String str) {
        try {
            final Number number = FLOATING_POINT_FORMAT.parse(str);
            return number != null ? number.floatValue() : null;
        } catch (NumberFormatException | ParseException e) {
            return null;
        }
    }

    public static float parseFloat(String str, float defaultValue) {
        final Float number = parseFloat(str);
        return number != null ? number : defaultValue;
    }

    public static int length(String str) {
        return str != null ? str.length() : 0;
    }

    public static boolean contains(String str, CharSequence cs) {
        return str != null && cs != null && str.contains(cs);
    }

    public static boolean containsIgnoreCase(String str, String cs) {
        return str != null && cs != null &&
                str.length() >= cs.length() &&
                str.toLowerCase().contains(cs.toLowerCase());
    }

    public static boolean hasPrefix(String str, String prefix) {
        return str != null && prefix != null && str.startsWith(prefix);
    }

    //region Transformations

    public static String camelCaseToWords(String string) {
        if (IsNullOrEmpty(string)) return string;

        StringBuilder result = new StringBuilder(string.length());
        result.append(Character.toUpperCase(string.charAt(0)));

        for (int i = 1; i < string.length(); ++i) {
            char chr = string.charAt(i);
            if (Character.isUpperCase(chr)) {
                result.append(' ');
            }
            result.append(chr);
        }

        return result.toString();
    }

    //endregion

    //region Nullability

    public static boolean IsNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    //endregion

    public static String toString(Object value) {
        return value != null ? value.toString() : "null";
    }

    public static String toString(float value) {
        return FLOATING_POINT_FORMAT.format(value);
    }

    public static String toString(double value) {
        return FLOATING_POINT_FORMAT.format(value);
    }

    public static <T> String Join(List<T> list) {
        return Join(list, ",");
    }

    public static <T> String Join(List<T> list, String separator) {
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (T e : list) {
            builder.append(e);
            if (++i < list.size()) builder.append(separator);
        }
        return builder.toString();
    }

    public static <T> String Join(T[] array) {
        return Join(array, ",");
    }

    public static <T> String Join(T[] array, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            builder.append(array[i]);
            if (i < array.length - 1) builder.append(separator);
        }
        return builder.toString();
    }

    public static String Join(boolean[] array) {
        return Join(array, ",");
    }

    public static String Join(boolean[] array, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            builder.append(array[i]);
            if (i < array.length - 1) builder.append(separator);
        }
        return builder.toString();
    }

    public static String Join(int[] array) {
        return Join(array, ",");
    }

    public static String Join(int[] array, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            builder.append(array[i]);
            if (i < array.length - 1) builder.append(separator);
        }
        return builder.toString();
    }

    public static String Join(float[] array) {
        return Join(array, ",");
    }

    public static String Join(float[] array, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            builder.append(array[i]);
            if (i < array.length - 1) builder.append(separator);
        }
        return builder.toString();
    }

    public static String format(String format, Object... args) {
        if (format != null && args != null && args.length > 0) {
            try {
                return String.format(format, args);
            } catch (Exception e) {
                android.util.Log.e("Lunar", "Error while formatting String: " + e.getMessage()); // FIXME: better system logging
            }
        }

        return format;
    }

    public static String serializeToString(Map<String, ?> data) {
        StringBuilder result = new StringBuilder();
        int index = 0;
        for (Map.Entry<String, ?> e : data.entrySet()) {
            String key = e.getKey();
            String value = toString(e.getValue());
            value = value.replace("\n", "\\n"); // we use new lines as separators
            result.append(key);
            result.append(':');
            result.append(value);

            if (++index < data.size()) {
                result.append("\n");
            }
        }

        return result.toString();
    }
}
