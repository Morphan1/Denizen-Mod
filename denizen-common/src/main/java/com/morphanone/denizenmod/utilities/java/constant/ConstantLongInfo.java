package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CONSTANT_Long_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.5">section 4.4.5</a>,
 * except the info is stored as a {@code long} directly rather than bytes representing a {@code long}
 */
public class ConstantLongInfo extends ConstantInfo {
    public final long value;

    public ConstantLongInfo(long value) {
        super(ConstantTag.CONSTANT_Long);
        this.value = value;
    }

    public ConstantLongInfo(DataInputStream data) throws IOException {
        this(data.readLong());
    }

    @Override
    protected void writeInfo(DataOutputStream data) throws IOException {
        data.writeLong(value);
    }
}
