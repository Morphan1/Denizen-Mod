package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CONSTANT_MethodType_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.9">section 4.4.9</a>
 */
public class ConstantMethodTypeInfo extends ConstantInfo {
    public final int descriptorIndex;

    public ConstantMethodTypeInfo(int descriptorIndex) {
        super(ConstantTag.CONSTANT_MethodType);
        this.descriptorIndex = descriptorIndex;
    }

    public ConstantMethodTypeInfo(DataInputStream data) throws IOException {
        this(data.readUnsignedShort());
    }

    @Override
    protected void writeInfo(DataOutputStream data) throws IOException {
        data.writeShort(descriptorIndex);
    }
}
