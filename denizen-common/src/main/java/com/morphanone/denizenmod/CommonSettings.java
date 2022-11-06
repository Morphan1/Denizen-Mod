package com.morphanone.denizenmod;

public class CommonSettings {
    public static boolean enforceLocale() {
        return DenizenMod.coreBridge.config().getBoolean("Enforce Locale", true);
    }

    public static boolean debugGeneral() {
        return DenizenMod.coreBridge.config().getBoolean("Debug.General", true);
    }
}
