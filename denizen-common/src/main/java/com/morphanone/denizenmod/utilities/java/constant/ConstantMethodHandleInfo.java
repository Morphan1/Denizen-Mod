package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CONSTANT_MethodHandle_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.8">section 4.4.8</a>
 */
public class ConstantMethodHandleInfo extends ConstantInfo {
    public final int referenceKind;

    public final int referenceIndex;

    public ConstantMethodHandleInfo(int referenceKind, int referenceIndex) {
        super(ConstantTag.CONSTANT_MethodHandle);
        this.referenceKind = referenceKind;
        this.referenceIndex = referenceIndex;
    }

    public ConstantMethodHandleInfo(DataInputStream data) throws IOException {
        this(data.readUnsignedByte(), data.readUnsignedShort());
    }

    @Override
    protected void writeInfo(DataOutputStream data) throws IOException {
        data.writeByte(referenceKind);
        data.writeShort(referenceIndex);
    }
}
