package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CONSTANT_Integer_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.4">section 4.4.4</a>,
 * except the info is stored as an {@code int} directly rather than bytes representing an {@code int}
 */
public class ConstantIntegerInfo extends ConstantInfo {
    public final int value;

    public ConstantIntegerInfo(int value) {
        super(ConstantTag.CONSTANT_Integer);
        this.value = value;
    }

    public ConstantIntegerInfo(DataInputStream data) throws IOException {
        this(data.readInt());
    }

    @Override
    protected void writeInfo(DataOutputStream data) throws IOException {
        data.writeInt(value);
    }
}
