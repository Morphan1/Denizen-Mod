package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CONSTANT_Package_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.12">section 4.4.12</a>
 */
public class ConstantPackageInfo extends ConstantInfo {
    public final int nameIndex;

    public ConstantPackageInfo(int nameIndex) {
        super(ConstantTag.CONSTANT_Package);
        this.nameIndex = nameIndex;
    }

    public ConstantPackageInfo(DataInputStream data) throws IOException {
        this(data.readUnsignedShort());
    }

    @Override
    protected void writeInfo(DataOutputStream data) throws IOException {
        data.writeShort(nameIndex);
    }
}
