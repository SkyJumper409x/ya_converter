package skySky;

import java.util.*;
import java.io.*;
import java.awt.Color;
import static skySky.Utils.CHKeys.*;

public class Yargconvert {
    static String[] colors = new String[] { "Open", "Green", "Red", "Yellow", "Blue", "Orange" };
    static String[] drumColors = new String[] { "Kick", "Red", "Yellow", "Blue", "Green" };
    static String[] drumFiveLaneColors = new String[] { "Kick", "Red", "Yellow", "Blue", "Orange", "Green" };
    static String[] sfColors = new String[] {
            "sf_note_open",
            "sf_note_black_left", "sf_note_black_mid", "sf_note_black_right",
            "sf_note_white_left", "sf_note_white_mid", "sf_note_white_right"
    };
    static String baseColorChoice = "#FFFFFF";
    static String openHeadLightChoice = "innerFret"; // innerfret, noteComplement, sameAsNote, or some custom color
    // 5fret: Fret, FretInner, Particles, Note, NoteStarPower
    // Drums: Fret, FretInner, Particles, Drum, DrumStarPower

    public static void main(String[] args) {

    }

    static void yargToCh(File yargFile, File chFile) {
        HashMap<String, HashMap<String, String>> theMap = readYargFile(yargFile);
        HashMap<String, String> guitarMap = theMap.get("FiveFretGuitar");
        HashMap<String, String> guitarOutputMap = yargToCh(guitarMap, PlasticInstrument.Guitar);
    }

    static HashMap<String, String> yargToCh(HashMap<String, String> yargMap, PlasticInstrument instrument) {
        HashMap<String, String> outputMap = new HashMap<>();

        int colorCount = colors.length;
        switch (instrument) {
            case Drums:
                colorCount = drumColors.length;
                break;
            case FiveLaneDrums:
                colorCount = drumFiveLaneColors.length;
                break;
            case Guitar:
                colorCount = colors.length;
                break;
            // case GuitarSF:
            // lastColor = sfColors.length;
            // break;
            default:
                break;
        }
        String[] sps = new String[colorCount];
        for (int i = 0; i < colorCount; i++) { // no 0 cuz most stuff doesnt have opens
            String color = null;
            switch (instrument) {
                case Drums:
                    color = drumColors[i];
                    break;
                case FiveLaneDrums:
                    color = drumFiveLaneColors[i];
                    break;
                case Guitar:
                    color = colors[i];
                    break;
                // case GuitarSF:
                // color = sfColors[i];
                // break;
                default:
                    break;
            }
            if (instrument == PlasticInstrument.FiveLaneDrums && color.equals("Orange")) {
                continue;
            }
            boolean isguitar = (instrument == PlasticInstrument.Guitar);

            String noteKey = color;
            String spKey = color;

            String strikerPrefix = "striker_";
            String notePrefix = "note_";

            if (!isguitar) {
                strikerPrefix = "drums_" + strikerPrefix;
                if (i != 0) {
                    // !color.equals("Kick")
                    noteKey += "Drum";
                    spKey += "NoteStarPower";
                    notePrefix = "tom_";
                } else {
                    // color.equals("Kick")
                    noteKey += "Note";
                    spKey += "StarPower";
                }
            }

            String note = yargMap.get(noteKey);
            sps[i] = yargMap.get(spKey);
            String innerFret = yargMap.get(color + "FretInner");
            String chColor = color.toLowerCase();
            outputMap.put(notePrefix + chColor, ytc(note));
            outputMap.put(notePrefix + "anim_" + chColor, color.equals("Open") ? "#FFFFFF" : chAnim(note));

            if (i != 0) {
                if (instrument == PlasticInstrument.Drums) {
                    String cymbal = yargMap.get(color + "Cymbal");
                    outputMap.put("cym_" + chColor, ytc(cymbal));
                    outputMap.put("cym_anim_" + chColor, chAnim(cymbal));
                }
                String fret = yargMap.get(color + "Fret");
                outputMap.put(strikerPrefix + "cover_" + chColor, ytc(fret));
                outputMap.put(strikerPrefix + "head_cover_" + chColor, ytc(fret));
                outputMap.put(strikerPrefix + "head_light_" + chColor, ytc(innerFret));
                outputMap.put(strikerPrefix + "base_" + chColor, baseColorChoice);

            } else {
                outputMap.put(strikerPrefix
                        + "head_light_" + chColor,
                        findOpenHeadLightColor(innerFret, note));
            }
        }
        // even if the yarg sps arent equal, these can be added cuz they are separate in
        // ch for some reason
        outputMap.put("note_kick_sp_phrase", ytc(sps[0]));
        outputMap.put("note_anim_kick_sp_phrase", chAnim(sps[0]));
        boolean spsEqual = true;
        String previousSp = sps[0];
        for (String string : sps) {
            spsEqual = spsEqual && previousSp.equals(string);
            previousSp = string;
        }
        if (spsEqual) {
            outputMap.putAll(createChSpKeyVals(previousSp, instrument));
        }
        return outputMap;
    }

    static HashMap<String, String> createChSpKeyVals(String yargSp, PlasticInstrument instrument) {
        String[] normal = null;
        String[] normalAnim = null;
        String[] white = null;
        HashMap<String, String> result = new HashMap<>();
        if (instrument == PlasticInstrument.Drums) {
            normal = normalSpKeysDrum;
            normalAnim = normalAnimSpKeysDrum;
            white = whiteSpKeysDrum;
            result.put("note_overlay_kick_sp_phrase", ytc(Utils.betterBrighter(yargSp, -0x028)));
            result.put("note_kick_sp_active", ytc(Utils.betterBrighter(yargSp, -0x070)));
            String cymSp = chCymSp(yargSp);
            for (String key : cymbalSpKeys) {
                result.put(key, cymSp);
            }
        } else {
            normal = normalSpKeys;
            normalAnim = normalAnimSpKeys;
            white = whiteSpKeys;
        }

        String chSp = ytc(yargSp);
        for (String key : normal) {
            result.put(key, chSp);
        }

        String anim = Utils.betterBrighter(yargSp, 0x051);
        for (String key : normalAnim) {
            result.put(key, anim);
        }
        for (String key : white) {
            result.put(key, "#FFFFFF");
        }
        return result;
    }

    static String chCymSp(String normalYargSp) {
        Color c = new Color(Integer.parseUnsignedInt(normalYargSp) >>> 8, true);
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return "#" + Utils.formatHex(true, 6,
                Color.HSBtoRGB((hsb[0] * 360f - 19f) / 360f, hsb[1] - 0.49f, hsb[2])
                        & 0x00ffffff);
    }

    static String findOpenHeadLightColor(String innerFret, String note) {
        switch (openHeadLightChoice) {
            case "innerFret":
                return innerFret;
            case "noteComplement":
                /*
                 * lil bit of bitmagic: ~ inverts all bits, and 0 is all zeros, so ~0 is all 1sm
                 * which is the rgba color (255,255,255,255)
                 * now i subtract c's rgb from it (which, for the default clone hero green fret,
                 * would be 00FF00, so (0, 255, 0, 255)) to get the complementary color
                 * (255, 0, 255, 0). yes, i subtract both alphas, but the color constructor
                 * accepts an *rgb* int, ***not*** an rbga int, so the alpha defaults to 255.
                 * Then i use the Utils Method i made for formatting hex numbers, because
                 * Integer.toHexString() doesnt zero-pad.
                 */
                Color complementary = new Color(~Integer.parseUnsignedInt(removeAlpha(note), 16));
                return Utils.formatHex(true, Utils.bitRotateLeft(complementary.getRGB(), 8));
            case "sameAsNote":
                return note;
            default: // treat openHeadLightChoice as a custom color
                return openHeadLightChoice;
        }
    }

    static String chAnim(String colString) {
        return ytc(Utils.betterBrighter(colString, 0x08b));
    }

    static String ytc(String colString) { // shorthand
        return yargToChColor(colString);
    }

    static String yargToChColor(String colString) {
        return "#" + removeAlpha(colString);
    }

    static String removeAlpha(String colString) {
        return colString.substring(0, 6);
    }

    static HashMap<String, HashMap<String, String>> readYargFile(File file) {
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line = null;

            HashMap<String, HashMap<String, String>> theMap = new HashMap<>();

            HashMap<String, String> currentSection = null;
            while ((line = in.readLine()) != null) {
                if (isSection(line)) {
                    currentSection = new HashMap<>();
                    theMap.put(parseSectionName(line), currentSection);
                } else if (isColor(line)) {
                    currentSection.put(getKey(line), getValue(line));
                }
            }
            return theMap;
        } catch (FileNotFoundException fnfex) {
            fnfex.printStackTrace();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
        return null;
    }

    static boolean isSection(String line) {
        return line.trim().matches("\".*?\" *: *\\{ *");
    }

    static String parseSectionName(String line) {
        return line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
    }

    static boolean isColor(String line) {
        return line.trim().matches("\".*?\" *: *\".*?\".*");
    }

    static String getKey(String line) {
        return line.substring(line.indexOf("\"") + 1, line.indexOf("\"", line.indexOf("\"") + 1));
    }

    static String getValue(String line) {
        return line.substring(line.lastIndexOf("\"", line.lastIndexOf("\"") - 1) + 1, line.lastIndexOf("\""));
    }

    static long d; // reset on generateYargUUID() call

    static synchronized String generateYargUUID() {
        d = new Date().getTime();
        String s = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx";
        char[] chars = s.toCharArray();
        String uuid = "";
        for (char c : chars) {
            if (c == 'x' || c == 'y') {
                uuid += randomThing(c);
            } else {
                s += c;
            }
        }
        return uuid;
    }

    static synchronized String randomThing(char c) {
        int result = (int) ((d + Math.random() * 16) % 16);
        d = (long) Math.floor(d / 16.0);
        return Long.toHexString(c == 'x' ? result : ((result & 0x3) | 0x8));
    }
}
