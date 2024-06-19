package skySky;

import java.util.*;
import java.io.*;
import java.awt.Color;
import static skySky.CHKeys.*;

public class Yargconvert {
    static String[] colors = new String[] { "Open", "Green", "Red", "Yellow", "Blue", "Orange" };
    static String[] drumColors = new String[] { "Kick", "Red", "Yellow", "Blue", "Green" };
    static String[] drumFiveLaneColors = new String[] { "Kick", "Red", "Yellow", "Blue", "Orange", "Green" };
    static String[] sfColors = new String[] {
            "open",
            "black_left", "black_mid", "black_right",
            "white_left", "white_mid", "white_right"
    };
    static String baseColorChoice = "#FFFFFF";
    // 5fret: Fret, FretInner, Particles, Note, NoteStarPower
    // Drums: Fret, FretInner, Particles, Drum, DrumStarPower

    static String openHeadLightChoice = "innerFret"; // innerfret, noteComplement, sameAsNote, or some custom color
    // openHeadLightChoice will be set by the ui once i actually make it lol

    public static void main(String[] args) {
        // just for testing, not the final contents of the main method
        // put the filepath to the yarg color preset in the empty string
        Yargconvert.yargToCh(new File(""),
                new File("ya_converter_out.ini"));
    }

    static void yargToCh(File yargFile, File chFile) {
        HashMap<String, HashMap<String, String>> theMap = readYargFile(yargFile);
        HashMap<String, String> guitarMap = theMap.get("FiveFretGuitar");
        HashMap<String, String> guitarOutputMap = yargToCh(guitarMap, PlasticInstrument.Guitar);
        HashMap<String, String> drumMap = theMap.get("FourLaneDrums");
        HashMap<String, String> drumOutputMap = yargToCh(drumMap, PlasticInstrument.Drums);
        try {
            PrintWriter p = new PrintWriter(chFile);
            p.println("[guitar]");
            Set<String> guitarKeys = guitarOutputMap.keySet();
            for (String key : guitarKeys) {
                p.println(String.format("%s = %s", key, guitarOutputMap.get(key)));
            }
            p.println();
            p.println("[drums]");
            Set<String> drumKeys = drumOutputMap.keySet();
            for (String key : drumKeys) {
                p.println(String.format("%s = %s", key, drumOutputMap.get(key)));
            }
            p.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        for (int i = 0; i < colorCount; i++) {
            String color = null;
            boolean isguitar = (instrument == PlasticInstrument.Guitar);
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
            String chColor = color.toLowerCase();

            String noteKey = color + "Note";
            String spKey = color;

            String strikerPrefix = "";
            String notePrefix = "note_";

            if (!isguitar) {
                strikerPrefix = "drums_";
                if (i != 0) {
                    // !color.equals("Kick")
                    String cymbal = yargMap.get(color + "Cymbal");
                    outputMap.put("cym_" + chColor, ytc(cymbal));
                    outputMap.put("cym_anim_" + chColor, chAnim(cymbal));

                    noteKey = color + "Drum";
                    spKey += "Drum";
                    notePrefix = "tom_";
                } else {
                    // color.equals("Kick")
                    // so we have to use neither Drum nor Note even though Drum is used for all
                    // other drums;
                }
                spKey += "Starpower";
            } else {
                spKey += "NoteStarPower"; // yes, the difference in capitalization is like that.
            }
            sps[i] = yargMap.get(spKey);
            if (sps[i] == null) {
                System.out.println("broken key is " + spKey);
                System.exit(1);
            }
            String yargNote = yargMap.get(noteKey);
            String animValue = chAnim(yargNote);
            if (color.equals("Open")) {
                animValue = "#FFFFFF";
            }
            outputMap.put(notePrefix + chColor, ytc(yargNote));
            outputMap.put(notePrefix + "anim_" + chColor, animValue);

            String innerFret = ytc(yargMap.get(color + "FretInner"));
            if (isguitar) {
                outputMap.put("sustain_" + chColor, ytc(Utils.betterBrighter(yargNote, 0x030)));
            }
            if (i != 0) {
                // not in the further up if(i != 0) cuz these are instrument independent
                // altho strikerprefix was set depending on isGuitar earlier but shhh
                String fret = yargMap.get(color + "Fret");
                outputMap.put(strikerPrefix + "striker_cover_" + chColor, ytc(fret));
                outputMap.put(strikerPrefix + "striker_head_cover_" + chColor, ytc(fret));
                outputMap.put(strikerPrefix + "striker_head_light_" + chColor, innerFret);
                outputMap.put(strikerPrefix + "striker_base_" + chColor, baseColorChoice);

            } else {
                // all these start with open but it also works and is intended to run for kick
                String openHeadLightColor = null;

                switch (openHeadLightChoice) {
                    case "innerFret":
                        openHeadLightColor = innerFret;
                        break;
                    case "noteComplement":
                        openHeadLightColor = complementaryChColor(yargNote);
                        break;
                    case "sameAsNote":
                        openHeadLightColor = ytc(yargNote);
                        break;
                    default: // treat openHeadLightChoice as a custom color (which is what it will be set to
                             // if user picks custom color)
                        openHeadLightColor = openHeadLightChoice;
                        break;
                }
                outputMap.put(strikerPrefix
                        + "striker_head_light_" + chColor, openHeadLightColor);
            }
        }
        // sp stuff
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
            // even if the yarg sps arent equal, these can be added cuz they are separate in
            // ch for some reason
            // but i dont so its all in 1 place
            result.put("note_kick_sp_phrase", ytc(yargSp));
            result.put("note_anim_kick_sp_phrase", chAnim(yargSp));
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

        String anim = ytc(Utils.betterBrighter(yargSp, 0x051));
        for (String key : normalAnim) {
            result.put(key, anim);
        }
        for (String key : white) {
            result.put(key, "#FFFFFF");
        }
        return result;
    }

    static String chCymSp(String normalYargSp) {
        Color c = new Color(Integer.parseUnsignedInt(normalYargSp, 16) >>> 8);
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return "#" + Utils.formatHex(true, 6,
                Color.HSBtoRGB((hsb[0] * 360f - 19f) / 360f, hsb[1] - 0.49f, hsb[2])
                        & 0x00ffffff);
    }

    static String complementaryChColor(String yargColor) {
        int colorInt = Integer.parseUnsignedInt(removeAlpha(yargColor), 16);
        return "#" + Utils.formatHex(true, 6, (~colorInt) & 0x00FFFFFF);
    }

    static String chAnim(String colString) {
        return ytc(Utils.betterBrighter(colString, 0x08b));
    }

    static String ytc(String colString) { // shorthand cuz yes
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
