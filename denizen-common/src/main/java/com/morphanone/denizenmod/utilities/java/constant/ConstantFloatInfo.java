package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CONSTANT_Float_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.4">section 4.4.4</a>,
 * except the info is stored as a {@code float} directly rather than bytes representing a {@code float}
 */
public class ConstantFloatInfo extends ConstantInfo {
    public final float value;

    public ConstantFloatInfo(float value) {
        super(ConstantTag.CONSTANT_Float);
        this.value = value;
    }

    public ConstantFloatInfo(DataInputStream data) throws IOException {
        this(data.readFloat());
    }

    @Override
    protected void writeInfo(DataOutputStream data) throws IOException {
        data.writeFloat(value);
    }
}
