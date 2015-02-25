package com.mxmariner.andxtidelib;


public enum StationType {
    STATION_TYPE_TIDE("tide"),
    STATION_TYPE_CURRENT("current");

    private String typeStr;

    StationType(String type) {
        typeStr = type;
    }

    public static StationType typeWithString(String type) {
        for (StationType stationType : StationType.values()) {
            if (stationType.typeStr.equalsIgnoreCase(type))
                return stationType;
        }

        return null;
    }

    public String getTypeStr() {
        return typeStr;
    }
}
