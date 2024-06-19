package skySky;

import java.awt.Color;
import java.util.*;

public class Utils {

    private Utils() {
    }

    public static String[] trimMany(String[] s) {
        String[] trimmedStrings = new String[s.length];
        for (int i = 0; i < s.length; i++) {
            trimmedStrings[i] = s[i].trim();
        }
        return trimmedStrings;
    }

    public static int sign(int n) {
        return ((n >> 31) << 1) + 1;
    }

    public static String formatArray(Object[] array, String regex) {
        if (regex.equals(", ")) {
            return Arrays.toString(array);
        }
        String result = "[";
        for (Object object : array) {
            result = result + object.toString() + regex;
        }
        result = result.substring(0, result.lastIndexOf(regex)) + "]";
        return result;
    }

    public static String formatArray(Object[] array) {
        return formatArray(array, ", ");
    }

    public static int csharpRound(float f) {
        return sign((int) f) * Math.round(Math.abs(f));
    }

    public static int[] parseInts(String[] s) throws NumberFormatException {
        int[] ints = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            ints[i] = Integer.parseInt(s[i]);
        }
        return ints;
    }

    public static short[] parseShorts(String[] s) throws NumberFormatException {
        short[] shorts = new short[s.length];
        for (int i = 0; i < s.length; i++) {
            shorts[i] = Short.parseShort(s[i]);
        }
        return shorts;
    }

    public static byte[] parseBytes(String[] s) throws NumberFormatException {
        byte[] bytes = new byte[s.length];
        for (int i = 0; i < s.length; i++) {
            bytes[i] = Byte.parseByte(s[i]);
        }
        return bytes;
    }

    public static String formatBinary(boolean zeroPadded, int i) {
        String result = String.format("%32s", Integer.toBinaryString(i));
        if (zeroPadded) {
            return result.replace(' ', '0');
        }
        return result;
    }

    public static String formatBinary(boolean zeroPadded, byte b) {
        return formatBinary(zeroPadded, Byte.toUnsignedInt(b)).substring(24, 32);
    }

    public static String formatBinary(int i) {
        return formatBinary(false, i);
    }

    public static String formatBinary(byte b) {
        return formatBinary(false, b);
    }

    public static String[] formatBinaryMany(boolean zeroPadded, int... args) {
        String[] result = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = String.format("%32s", Integer.toBinaryString(args[i]));
            if (zeroPadded) {
                result[i] = result[i].replace(' ', '0');
            }
        }
        return result;
    }

    public static String[] formatBinaryMany(boolean zeroPadded, byte... args) {
        String[] result = formatBinaryMany(zeroPadded, toUnsignedIntMany(args));
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i].substring(24, 32);

        }
        return result;
    }

    public static String[] formatBinaryMany(int... args) {
        return formatBinaryMany(false, args);
    }

    public static String[] formatBinaryMany(byte... args) {
        return formatBinaryMany(false, args);
    }

    public static int[] toUnsignedIntMany(byte... args) {
        int[] result = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = Byte.toUnsignedInt(args[i]);
        }
        return result;
    }

    public static String[] toBinaryStringMany(int... args) {
        String[] result = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = Integer.toBinaryString(args[i]);
        }
        return result;
    }

    public static String[] equalLengthMany(int l, String... s) {
        String[] result = new String[s.length];
        for (int i = 0; i < s.length; i++) {
            result[i] = String.format("%" + l + "s", s[i]);
        }
        return result;
    }

    public static String[] equalLengthSpacedMany(int divisor, String seperator, int l, String... s) {
        String[] result = equalLengthMany(l, s);
        for (int i = 0; i < result.length; i++) {
            StringBuilder sb = new StringBuilder("");
            char[] cs = result[i].toCharArray();
            for (int j = 0; j < cs.length; j++) {
                if (j % divisor == 0) {
                    sb = sb.append(seperator);
                }
                sb = sb.append(cs[j]);
            }
            result[i] = sb.toString();
        }
        return result;
    }

    public static String[] equalLengthSpacedMany(int divisor, int l, String... s) {
        return equalLengthSpacedMany(divisor, " ", l, s);
    }

    public static String[] equalLengthSpacedMany(String seperator, int l, String... s) {
        return equalLengthSpacedMany(4, seperator, l, s);
    }

    public static String[] equalLengthSpacedMany(int divisor, String... s) {
        return equalLengthSpacedMany(divisor, " ", maxLength(s), s);
    }

    public static String equalLengthSpaced(int divisor, String seperator, int l, String s) {
        return equalLengthSpacedMany(divisor, seperator, s.length(), s)[0];
    }

    public static String equalLengthSpaced(int divisor, String s) {
        return equalLengthSpaced(divisor, " ", s.length(), s);
    }

    public static String equalLengthSpaced(int divisor, int l, String s) {
        return equalLengthSpaced(divisor, " ", l, s);
    }

    public static String equalLengthSpaced(String s) {
        return equalLengthSpaced(Math.max(4, Math.abs(Math.min(1, s.length() % 4) - 1) * (s.length() / 4)), " ",
                s.length(),
                s);
    }

    public static int maxLength(String... args) {
        int result = 0;
        for (String string : args) {
            result = Math.max(result, string.length());
        }
        return result;
    }

    public static String[] toStringMany(byte... args) {
        String[] result = new String[args.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = String.valueOf(args[i]);
        }
        return result;
    }

    public static String[] toStringMany(short... args) {
        String[] result = new String[args.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = String.valueOf(args[i]);
        }
        return result;
    }

    public static String[] toStringMany(int... args) {
        String[] result = new String[args.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = String.valueOf(args[i]);
        }
        return result;
    }

    public static String[] toStringMany(long... args) {
        String[] result = new String[args.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = String.valueOf(args[i]);
        }
        return result;
    }

    public static boolean areEmpty(String... args) {
        if (!areNoneNull((Object[]) args)) {
            return false;
        }
        boolean result = true;
        for (String string : args) {
            result = result && string.isEmpty();
        }
        return result;
    }

    public static boolean areBlank(String... args) {
        if (!areNoneNull((Object[]) args)) {
            return false;
        }
        boolean result = true;
        for (String string : args) {
            result = result && isBlank(string);
        }
        return result;
    }

    // the difference between !areEmpty() and areNoneEmpty() is that !areEmpty() is
    // false only if all arguments are empty,
    // while areNoneEmpty() is false if at least one argument is empty.
    public static boolean areNoneEmpty(String... args) {
        if (!areNoneNull((Object[]) args)) {
            return false;
        }
        boolean result = false;
        for (String string : args) {
            result = result || !string.isEmpty();
        }
        return result;
    }

    public static boolean areNoneBlank(String... args) {
        if (!areNoneNull((Object[]) args)) {
            // System.out.println("hai none null");
            return false;
        }
        boolean result = false;
        for (String string : args) {
            result = result || !isBlank(string);
        }
        return result;
    }

    public static boolean areNoneNull(Object... args) {
        boolean result = true;
        for (Object o : args) {
            result = result && (o != null);
        }
        return result;
    }

    // from
    // https://stackoverflow.com/questions/5711084/java-runtime-getruntime-getting-output-from-executing-a-command-line-program
    public static String execCmd(String cmd) throws java.io.IOException {
        java.util.Scanner sc = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream())
                .useDelimiter("\\A");
        // return s.hasNext() ? s.next() : "";
        // my code:
        String result = sc.hasNext() ? sc.next() : "";
        sc.close();
        return result;
    }

    // yoinked from https://stackoverflow.com/a/31976060
    public static final char[] FILENAME_FORBIDDEN_ASCII_CHARACTERS = new char[] { '/', '<', '>', ':', '"', '\\', '|',
            '?', '*' };

    public static int[] allIndexesOf(String arg0, char c) {
        return allIndexesOf(arg0, String.valueOf(c));
    }

    public static int[] allIndexesOf(String arg0, String c) {
        LinkedList<Integer> result = new LinkedList<Integer>();
        int tmp = -1;
        while ((tmp = arg0.indexOf(c, tmp + 1)) != -1) {
            result.add(tmp);
        }
        return intValueOfMany(result.toArray(new Integer[0]));
    }

    public static int[] intValueOfMany(Integer... args) {
        int[] result = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = args[i].intValue();
        }
        return result;
    }

    public static boolean isBlank(String s) {
        return s.trim().isEmpty();
    }

    public static int randomInt(int min, int max) { // result is including min but excluding max
        if (min == max) {
            return min;
        }
        int range = Math.abs(max - min);
        if (range == 1) {
            return Math.min(min, max);
        }
        int random = (int) (Math.random() * range);
        return Math.min(min, max) + random;
    }

    public static int randomInt(int max) {
        return randomInt(0, max);
    }

    public static String formatHex(boolean zeroPadded, int digitCount, int i) {
        String result = String.format("%" + digitCount + "x", i);
        if (zeroPadded) {
            return result.replace(' ', '0');
        }
        return result.toUpperCase();
    }

    public static String formatHex(boolean zeroPadded, int i) {
        return formatHex(zeroPadded, 8, i);
    }

    public static String formatHex(boolean zeroPadded, byte b) {
        return formatHex(zeroPadded, 2, Byte.toUnsignedInt(b));
    }

    public static String formatHex(int i) {
        return formatHex(false, i);
    }

    public static String formatHex(byte b) {
        return formatHex(false, b);
    }

    public static String toString(Color c) {
        return String.format("%3d, %3d, %3d, %3d", c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    private static int addThis = 0x08b;

    public static Color betterBrighter(Color c) {
        return betterBrighter(c, addThis);
    }

    public static Color betterBrighter(Color c, int i) {

        return new Color(
                Math.max(0, Math.min(c.getRed() + i, 255)),
                Math.max(0, Math.min(c.getGreen() + i, 255)),
                Math.max(0, Math.min(c.getBlue() + i, 255)),
                c.getAlpha());
    }

    public static final int redBits = 0xFF000000, greenBits = 0x00FF0000, blueBits = 0x0000FF00, alphaBits = 0x000000FF;
    public static final int redBitsShift = 24, greenBitsShift = 16, blueBitsShift = 8;

    public static String betterBrighter(String s) {
        return betterBrighter(s, addThis);
    }

    public static String betterBrighter(String s, int i) {
        int colorInt = 0;
        if (isParseIntAble(s, 16)) {
            colorInt = Integer.parseUnsignedInt(s, 16);
        } else {
            colorInt = decodeUnsigned(s);
        }
        int newRed = Math.max(0, Math.min(((colorInt & redBits) >>> redBitsShift) + i, 255));
        int newGreen = Math.max(0, Math.min(((colorInt & greenBits) >>> greenBitsShift) + i, 255));
        int newBlue = Math.max(0, Math.min(((colorInt & blueBits) >>> blueBitsShift) + i, 255));
        int alpha = colorInt & alphaBits;
        int newColorInt = (newRed << redBitsShift) | (newGreen << greenBitsShift) | (newBlue << blueBitsShift) | alpha;
        return formatHex(true, newColorInt);
    }

    private static boolean isParseIntAble(String s, int radix) {
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (Character.digit(c, radix) < 0) {
                return false;
            }
        }
        return true;
    }

    public static int decodeUnsigned(String s) {
        return (int) Long.decode(s).longValue();
    }

    static int bitRotateLeft(int number, int bitCount) {
        bitCount = bitCount % 32;
        if (bitCount == 0)
            return number;
        else if (bitCount < 0) {
            bitCount += 32;
        }
        int result = (number << bitCount);
        int cutOff = number >>> (32 - bitCount);
        result |= cutOff;
        return result;
    }

    static int bitRotateRight(int number, int bitCount) {
        bitCount = bitCount % 32;
        if (bitCount == 0)
            return number;
        else if (bitCount < 0) {
            bitCount += 32;
        }
        return bitRotateLeft(number, 32 - bitCount);
    }

    public static final class CHKeys {

        // note_kick_sp_phrase is same as note_kick
        // note_anim_kick_sp_phrase is same as note_anim_kick
        // note_overlay_kick_sp_phrase is normalSp - 0x282828
        // note_kick_sp_active is normalSp - 0x707070
        public static final String[] normalSpKeys = { "note_sp_active", "note_sp_phrase", "note_sp_phrase_active",
                "sustain_sp_phrase",
                "sustain_sp_phrase_active", "sustain_sp_active" };
        public static final String[] normalAnimSpKeys = { "note_anim_sp_active", "note_anim_sp_phrase" };
        public static final String[] whiteSpKeys = { "note_anim_sp_phrase_active" };

        public static final String[] normalSpKeysDrum = { "tom_sp_phrase", "tom_sp_phrase_active", "tom_sp_active",
                "note_anim_kick_sp_active" };
        public static final String[] normalAnimSpKeysDrum = { "tom_anim_sp_active", "tom_anim_sp_phrase" };
        public static final String[] whiteSpKeysDrum = { "tom_anim_sp_phrase_active", "cym_anim_sp_active",
                "note_anim_kick_sp_phrase_active", "note_kick_sp_phrase_active" };
        public static final String[] cymbalSpKeys = { "cym_sp_active", "cym_sp_phrase", "cym_sp_phrase_active",
                "cym_anim_sp_active",
                "cym_anim_sp_phrase" };
    }
}
