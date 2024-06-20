package skySky;

import java.util.*;
import java.io.*;
import static skySky.CHKeys.*;
import static skySky.Utils.complain;
import static skySky.Utils.die;

public class Yargconvert {
    static final List<String> colors;
    static final List<String> drumColors;
    static final List<String> drumFiveLaneColors;
    static final List<String> sfColors;
    private static List<String> colorsThing, drumColorsThing, drumFiveLaneColorsThing, sfColorsThing;
    static String baseColorChoice = "#FFFFFF";
    // 5fret: Fret, FretInner, Particles, Note, NoteStarPower
    // Drums: Fret, FretInner, Particles, Drum, DrumStarPower

    static String openHeadLightChoice = "innerFret"; // innerfret, noteComplement, sameAsNote, or some custom color
    // openHeadLightChoice will be set by the ui once i actually make it lol

    private static final Map<String, String> chOtherConstants;
    static {
        /*
         * the reason these are not directly assigned is that compiler complains that
         * they may not have been assigned if i try to do so, aswell as complaining in
         * createColorConstants that they cannot be assigned.
         */
        createColorConstants();
        colors = colorsThing;
        drumColors = drumColorsThing;
        drumFiveLaneColors = drumFiveLaneColorsThing;
        sfColors = sfColorsThing;

        HashMap<String, String> thing = new HashMap<String, String>();
        thing.put("combo_sp_active_glow", "#FFFFFF");
        thing.put("combo_four_glow", "#E8B1FF");
        thing.put("combo_three_glow", "#F0FFF0");
        thing.put("combo_two_glow", "#FFFF00");
        thing.put("combo_four", "#874E9E");
        thing.put("combo_three", "#00FF00");
        thing.put("combo_two", "#D55800");
        thing.put("combo_one", "#FFDD00");
        thing.put("striker_hit_particles", "#FF5000");
        thing.put("striker_hit_flame", "#FFB76D");
        thing.put("sp_bar_elec", "#B2B2B2");
        thing.put("general_sp_active", "#FFFFFF");
        chOtherConstants = Collections.unmodifiableMap(thing);
    }

    private static void createColorConstants() {
        colorsThing = Collections.unmodifiableList(Arrays.asList(new String[] {
                "Open", "Green", "Red", "Yellow", "Blue", "Orange"
        }));
        drumColorsThing = Collections.unmodifiableList(Arrays.asList(new String[] {
                "Kick", "Red", "Yellow", "Blue", "Green"
        }));
        drumFiveLaneColorsThing = Collections.unmodifiableList(Arrays.asList(new String[] {
                "Kick", "Red", "Yellow", "Blue", "Orange", "Green"
        }));
        sfColorsThing = Collections.unmodifiableList(Arrays.asList(new String[] {
                "open",
                "black_left", "black_mid", "black_right",
                "white_left", "white_mid", "white_right"
        }));
    }

    public static void main(String[] args) {
        String jarname = new java.io.File(Yargconvert.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath()).getName();
        String usage = String.format("Usage: (java -jar %s) <Path to source file> [path to target file]", jarname);
        if (args.length > 2) {
            complain("Too many arguments were provided.\n" + usage, 65);
        } else if (args.length < 1) {
            complain("Please provide the path to the source file.\n" + usage, 65);
        }
        if (args[0].equals("--help") || args[0].equals("-h") || args[0].equals("-help")) {
            System.out.println(usage);
            System.exit(0);
        }
        String inPath = args[0];
        String outPath = args[0].substring(0, args[0].lastIndexOf(".")) + "_converted.ini";
        if (args.length == 2) {
            outPath = args[1];
        }
        yargToCh(new File(inPath),
                new File(outPath));
    }

    static void yargToCh(File yargFile, File chFile) {
        if (!yargFile.exists()) {
            complain("Could not find source file.", 65);
        }
        if (!yargFile.isFile()) {
            complain("Source is not a file.", 65);
        }
        if (chFile.exists()) {
            System.out.print("Target file already exists");
            if (!chFile.isFile()) {
                complain(" and is not a File.", 65);
            }
            System.out.println(". Overwrite? (y/n)");
            Scanner sc = new Scanner(System.in);
            String response = sc.next();
            sc.close();
            if (response.equals("n")) {
                System.out.println("Stopping");
                System.exit(0);
            }
        }
        try {
            if (!chFile.getParentFile().exists()) {
                boolean dirCreated = chFile.getParentFile().mkdirs();
                if (!dirCreated) {
                    die("Could not create Target file's parent directories: " + chFile.getAbsolutePath(), 64);
                }
            }
            HashMap<String, HashMap<String, String>> theMap = readYargFile(yargFile);
            HashMap<String, String> guitarMap = theMap.get("FiveFretGuitar");
            HashMap<String, String> guitarOutputMap = yargToCh(guitarMap, PlasticInstrument.Guitar);
            HashMap<String, String> drumMap = theMap.get("FourLaneDrums");
            HashMap<String, String> drumOutputMap = yargToCh(drumMap, PlasticInstrument.Drums);
            HashMap<String, String> otherMap = otherCh(guitarOutputMap, drumOutputMap);
            PrintWriter p = new PrintWriter(chFile);

            p.println("[other]");
            Set<String> otherKeys = otherMap.keySet();
            for (String key : otherKeys) {
                p.println(String.format("%s = %s", key, otherMap.get(key)));
            }
            p.println();

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
            p.println();

            // sixfret
            ClassLoader classLoader = Yargconvert.class.getClassLoader();
            InputStream sfInputStream = classLoader.getResourceAsStream("sf.txt");
            if (sfInputStream != null) {
                InputStreamReader isr = new InputStreamReader(sfInputStream);
                int c = 0;
                while ((c = isr.read()) != -1) {
                    p.write(c);
                }
                p.println();
            }
            p.close();
        } catch (SecurityException securityex) {
            String msg = securityex.getLocalizedMessage();
            securityex.printStackTrace(System.err);
            if (msg != null) {
                die(msg);
            }
            die("Some read/write permissions might be missing.");
        } catch (Exception ex) {
            ex.printStackTrace();
            die();
        }
    }

    private static HashMap<String, String> otherCh(HashMap<String, String> gtrMap, HashMap<String, String> drumMap) {
        HashMap<String, String> result = new HashMap<String, String>();
        result.putAll(chOtherConstants);

        String normalSp = gtrMap.get("note_sp_phrase");
        if (normalSp == null) {
            die("\"Wait, didn't I fix that?\" - me when I see this, probably\nAt otherCh: normalSp is null, gtrMap.size(): "
                    + gtrMap.size());
        }
        // combo sp active glow is in constants
        // combo four-two glow are in constants
        result.put("combo_sp_active", Utils.betterBrighter(normalSp, -0x022));
        // combo four-one are in constants
        result.put("striker_hold_spark_sp_active", complementaryChColor(normalSp.substring(1) + "FF"));
        result.put("striker_hold_spark", complementaryChColor(normalSp.substring(1) + "FF"));
        result.put("striker_hit_particles_sp_active", normalSp);
        // striker hit particles is in constants
        result.put("striker_hit_flame_sp_active", normalSp);
        // striker hit flame is in constants
        result.put("striker_hit_flame_kick", Utils.hueShiftCh(drumMap.get("note_kick"), 26));
        result.put("striker_hit_flame_open", gtrMap.get("note_open"));
        result.put("sp_bar_arrow", Utils.betterBrighter(normalSp, 0x07F));
        // sp bar elec is in constants
        result.put("sp_bar_color", Utils.betterBrighter(normalSp, -0x0A7));
        result.put("sp_act_animation", Utils.hsvShiftCh(normalSp, -9, 0, -10));
        result.put("sp_act_flash", Utils.hsvShiftCh(normalSp, 47, 0, -25));
        // general sp active is in constants
        result.put("general_sp", normalSp);
        return result;
    }

    private static HashMap<String, String> yargToCh(HashMap<String, String> yargMap, PlasticInstrument instrument) {
        HashMap<String, String> outputMap = new HashMap<>();

        int colorCount = colors.size();
        switch (instrument) {
            case Drums:
                colorCount = drumColors.size();
                break;
            case FiveLaneDrums:
                colorCount = drumFiveLaneColors.size();
                break;
            case Guitar:
                colorCount = colors.size();
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
                    color = drumColors.get(i);
                    break;
                case FiveLaneDrums:
                    color = drumFiveLaneColors.get(i);
                    break;
                case Guitar:
                    color = colors.get(i);
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
                die("Broken spKey: " + spKey);
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
        } else {
            outputMap.putAll(createChSpKeyVals("00FFFFFF", instrument)); // default to 00ffff
        }
        return outputMap;
    }

    private static HashMap<String, String> createChSpKeyVals(String yargSp, PlasticInstrument instrument) {
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

    private static String chCymSp(String normalYargSp) {
        return Utils.hsvShiftCh(ytc(normalYargSp), -19, -49, 0);
    }

    private static String complementaryChColor(String yargColor) {
        int colorInt = Integer.parseUnsignedInt(removeAlpha(yargColor), 16);
        return "#" + Utils.formatHex(true, 6, (~colorInt) & 0x00FFFFFF);
    }

    private static String chAnim(String colString) {
        return ytc(Utils.betterBrighter(colString, 0x08b));
    }

    static String ytc(String yargColor) { // shorthand cuz yes
        return yargToChColor(yargColor);
    }

    static String yargToChColor(String colString) {
        return "#" + removeAlpha(colString);
    }

    private static String removeAlpha(String colString) {
        return colString.substring(0, 6);
    }

    static HashMap<String, HashMap<String, String>> readYargFile(File yargFile) {
        try (BufferedReader in = new BufferedReader(new FileReader(yargFile))) {
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

    private static boolean isSection(String line) {
        return line.trim().matches("\".*?\" *: *\\{ *");
    }

    private static String parseSectionName(String line) {
        return line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
    }

    private static boolean isColor(String line) {
        return line.trim().matches("\".*?\" *: *\".*?\".*");
    }

    private static String getKey(String line) {
        return line.substring(line.indexOf("\"") + 1, line.indexOf("\"", line.indexOf("\"") + 1));
    }

    private static String getValue(String line) {
        return line.substring(line.lastIndexOf("\"", line.lastIndexOf("\"") - 1) + 1, line.lastIndexOf("\""));
    }
}
