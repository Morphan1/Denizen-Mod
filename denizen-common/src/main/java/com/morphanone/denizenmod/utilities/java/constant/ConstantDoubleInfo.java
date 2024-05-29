package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CONSTANT_Double_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.5">section 4.4.5</a>,
 * except the info is stored as a {@code double} directly rather than bytes representing a {@code double}
 */
public class ConstantDoubleInfo extends ConstantInfo {
    public final double value;

    public ConstantDoubleInfo(double value) {
        super(ConstantTag.CONSTANT_Double);
        this.value = value;
    }

    public ConstantDoubleInfo(DataInputStream data) throws IOException {
        this(data.readDouble());
    }

    @Override
    protected void writeInfo(DataOutputStream data) throws IOException {
        data.writeDouble(value);
    }
}
