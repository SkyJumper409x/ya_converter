package skySky;

public class CHKeys {
        private CHKeys() {
        }

        // I know these arent really constants because array but shhh
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
        /*
         * Note to self:
         * 
         * note_kick_sp_phrase is same as note_kick
         * note_anim_kick_sp_phrase is same as note_anim_kick
         * note_overlay_kick_sp_phrase is normalSp - 0x282828
         * note_kick_sp_active is normalSp - 0x707070
         */
}
