package com.morphanone.denizenfabric;

import com.denizenscript.denizencore.utilities.CoreUtilities;

public class Settings {
    public static boolean enforceLocale() {
        return getBoolean("Enforce Locale", true);
    }

    public static boolean debugGeneral() {
        return getBoolean("Debug.General", true);
    }

    private static boolean getBoolean(String path, boolean def) {
        return CoreUtilities.toLowerCase(Denizen.instance.config.getString(path, def ? "true" : "false")).equals("true");
    }
}
