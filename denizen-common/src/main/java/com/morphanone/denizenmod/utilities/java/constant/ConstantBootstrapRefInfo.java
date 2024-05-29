package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Base for {@link ConstantDynamicInfo CONSTANT_Dynamic_info} and {@link ConstantInvokeDynamicInfo CONSTANT_InvokeDynamic_info} structures
 */
public abstract class ConstantBootstrapRefInfo extends ConstantInfo {
    public final int bootstrapMethodAttrIndex;

    public final int nameAndTypeIndex;

    public ConstantBootstrapRefInfo(ConstantTag tag, int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
        super(tag);
        this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    public ConstantBootstrapRefInfo(ConstantTag tag, DataInputStream data) throws IOException {
        this(tag, data.readUnsignedShort(), data.readUnsignedShort());
    }

    @Override
    protected void writeInfo(DataOutputStream data) throws IOException {
        data.writeShort(bootstrapMethodAttrIndex);
        data.writeShort(nameAndTypeIndex);
    }
}
