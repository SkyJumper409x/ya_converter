package skySky;

public enum PlasticInstrument {

    Guitar("guitar", "FiveFretGuitar"), GuitarSF("sixfret", null),
    Drums("drums", "FourLaneDrums"), FiveLaneDrums("drums", "FiveLaneDrums");

    private String chName;
    private String yargName;

    PlasticInstrument(String chName, String yargName) {
        this.chName = chName;
        this.yargName = yargName;
    }

    public String getChName() {
        return chName;
    }

    public String getYargName() {
        return yargName;
    }
}