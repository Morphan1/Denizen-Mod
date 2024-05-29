package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Base for {@link ConstantFieldRefInfo CONSTANT_Fieldref_info}, {@link ConstantMethodRefInfo CONSTANT_Methodref_info},
 * and {@link ConstantInterfaceMethodRefInfo CONSTANT_InterfaceMethodref_info} structures
 */
public abstract class ConstantRefInfo extends ConstantInfo {
    public final int classIndex;

    public final int nameAndTypeIndex;

    public ConstantRefInfo(ConstantTag tag, int classIndex, int nameAndTypeIndex) {
        super(tag);
        this.classIndex = classIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    public ConstantRefInfo(ConstantTag tag, DataInputStream data) throws IOException {
        this(tag, data.readUnsignedShort(), data.readUnsignedShort());
    }

    @Override
    protected void writeInfo(DataOutputStream data) throws IOException {
        data.writeShort(classIndex);
        data.writeShort(nameAndTypeIndex);
    }
}
