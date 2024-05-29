package com.morphanone.denizenmod.utilities.java;

/**
 * Java version codes as specified by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.1-200-B.2">Table 4.1-A</a>
 */
public record JavaVersion(int major, int minor) {
    public boolean isValid() {
        if (major < JavaVersion.MAJOR_12) {
            // minorVersion is allowed to be any valid unsigned short
            return true;
        }
        // minorVersion must validly indicate whether the class uses preview features or not
        return minor == JavaVersion.DEFAULT_MINOR || minor == JavaVersion.PREVIEW_MINOR;
    }

    public boolean supportsPreviewFeatures() {
        if (major < JavaVersion.MAJOR_12) {
            // preview features didn't exist
            return false;
        }
        return minor == JavaVersion.PREVIEW_MINOR;
    }

    /**
     * Java SE 1.1 (or 1.0.2, which is indistinguishable by class header alone)
     * @see #MINOR_1_1
     */
    public static final int MAJOR_1_1 = 45;

    /**
     * {@link #MAJOR_1_1 Java SE 1.1} must have a minor version less than or equal to this value.
     * <p> In practice, the specs only require a valid number between 0 and 65535 inclusive for major versions 45
     * {@link #MAJOR_1_1 (SE 1.1)} through 55 {@link #MAJOR_11 (SE 11)}.
     * <p> Source: <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.1-200-B.2">Historical note below table 4.1-A</a>
     */
    public static final int MINOR_1_1 = 65535;

    /**
     * Java SE 1.2
     * @see #MINOR_1_2
     */
    public static final int MAJOR_1_2 = 46;

    /**
     * {@link #MAJOR_1_2 Java SE 1.2} must have a minor version that is equal to this value.
     * <p> In practice, the specs only require a valid number between 0 and 65535 inclusive for major versions 45
     * {@link #MAJOR_1_1 (SE 1.1)} through 55 {@link #MAJOR_11 (SE 11)}.
     * <p> Source: <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.1-200-B.2">Historical note below table 4.1-A</a>
     */
    public static final int MINOR_1_2 = 0;

    /**
     * Java SE versions between {@link #MAJOR_1_3 1.3} and {@link #MAJOR_11 11} inclusive only support a minor version equal to this value.
     * <p> In practice, the specs only require a valid number between 0 and 65535 inclusive for major versions 45
     * {@link #MAJOR_1_1 (SE 1.1)} through 55 {@link #MAJOR_11 (SE 11)}.
     * <p> After {@link #MAJOR_12 Java SE 12}, this value indicates that there is no dependency on preview features for a given version
     * and the only other valid value {@link #PREVIEW_MINOR implies the opposite}.
     * <p> Source: <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.1-200-B.2">Note below table 4.1-A</a>
     */
    public static final int DEFAULT_MINOR = 0;

    /**
     * After {@link #MAJOR_12 Java SE 12}, this value indicates that there is a dependency on preview features for a given version
     * and the only other valid value {@link #DEFAULT_MINOR implies the opposite}.
     * <p> Source: <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.1-200-B.2">Note below table 4.1-A</a>
     */
    public static final int PREVIEW_MINOR = 65535;

    /**
     * Java SE 1.3
     * @see #DEFAULT_MINOR
     */
    public static final int MAJOR_1_3 = 47;

    /**
     * Java SE 1.4
     * @see #DEFAULT_MINOR
     */
    public static final int MAJOR_1_4 = 48;

    /**
     * Java SE 5.0
     * @see #DEFAULT_MINOR
     */
    public static final int MAJOR_5_0 = 49;

    /**
     * Java SE 6
     * @see #DEFAULT_MINOR
     */
    public static final int MAJOR_6 = 50;

    /**
     * Java SE 7
     * @see #DEFAULT_MINOR
     */
    public static final int MAJOR_7 = 51;

    /**
     * Java SE 8
     * @see #DEFAULT_MINOR
     */
    public static final int MAJOR_8 = 52;

    /**
     * Java SE 9
     * @see #DEFAULT_MINOR
     */
    public static final int MAJOR_9 = 53;

    /**
     * Java SE 10
     * @see #DEFAULT_MINOR
     */
    public static final int MAJOR_10 = 54;

    /**
     * Java SE 11
     * @see #DEFAULT_MINOR
     */
    public static final int MAJOR_11 = 55;

    /**
     * Java SE 12
     * @see #DEFAULT_MINOR
     * @see #PREVIEW_MINOR
     */
    public static final int MAJOR_12 = 56;

    /**
     * Java SE 13
     * @see #DEFAULT_MINOR
     * @see #PREVIEW_MINOR
     */
    public static final int MAJOR_13 = 57;

    /**
     * Java SE 14
     * @see #DEFAULT_MINOR
     * @see #PREVIEW_MINOR
     */
    public static final int MAJOR_14 = 58;

    /**
     * Java SE 15
     * @see #DEFAULT_MINOR
     * @see #PREVIEW_MINOR
     */
    public static final int MAJOR_15 = 59;

    /**
     * Java SE 16
     * @see #DEFAULT_MINOR
     * @see #PREVIEW_MINOR
     */
    public static final int MAJOR_16 = 60;

    /**
     * Java SE 17
     * @see #DEFAULT_MINOR
     * @see #PREVIEW_MINOR
     */
    public static final int MAJOR_17 = 61;

    /**
     * Java SE 18
     * @see #DEFAULT_MINOR
     * @see #PREVIEW_MINOR
     */
    public static final int MAJOR_18 = 62;

    /**
     * Java SE 19
     * @see #DEFAULT_MINOR
     * @see #PREVIEW_MINOR
     */
    public static final int MAJOR_19 = 63;
}
