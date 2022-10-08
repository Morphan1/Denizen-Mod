package com.morphanone.denizenmod;

import com.denizenscript.denizencore.utilities.CoreUtilities;

public class CommonSettings {
    public static boolean enforceLocale() {
        return getBoolean("Enforce Locale", true);
    }

    public static boolean debugGeneral() {
        return getBoolean("Debug.General", true);
    }

    private static boolean getBoolean(String path, boolean def) {
        return CoreUtilities.toLowerCase(DenizenMod.config.getString(path, def ? "true" : "false")).equals("true");
    }
}
