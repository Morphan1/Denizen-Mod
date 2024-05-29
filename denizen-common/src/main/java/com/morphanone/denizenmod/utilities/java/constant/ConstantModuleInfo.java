package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CONSTANT_Module_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.11">section 4.4.11</a>
 */
public class ConstantModuleInfo extends ConstantInfo {
    public final int nameIndex;

    public ConstantModuleInfo(int nameIndex) {
        super(ConstantTag.CONSTANT_Module);
        this.nameIndex = nameIndex;
    }

    public ConstantModuleInfo(DataInputStream data) throws IOException {
        this(data.readUnsignedShort());
    }

    @Override
    protected void writeInfo(DataOutputStream data) throws IOException {
        data.writeShort(nameIndex);
    }
}
