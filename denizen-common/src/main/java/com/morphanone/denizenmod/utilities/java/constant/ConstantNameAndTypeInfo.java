package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CONSTANT_NameAndType_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.6">section 4.4.6</a>
 */
public class ConstantNameAndTypeInfo extends ConstantInfo {
    public final int nameIndex;

    public final int descriptorIndex;

    public ConstantNameAndTypeInfo(int nameIndex, int descriptorIndex) {
        super(ConstantTag.CONSTANT_NameAndType);
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
    }

    public ConstantNameAndTypeInfo(DataInputStream data) throws IOException {
        this(data.readUnsignedShort(), data.readUnsignedShort());
    }

    @Override
    protected void writeInfo(DataOutputStream data) throws IOException {
        data.writeShort(nameIndex);
        data.writeShort(descriptorIndex);
    }
}
