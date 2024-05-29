package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CONSTANT_Class_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.1">section 4.4.1</a>
 */
public class ConstantClassInfo extends ConstantInfo {
    public final int nameIndex;

    public ConstantClassInfo(int nameIndex) {
        super(ConstantTag.CONSTANT_Class);
        this.nameIndex = nameIndex;
    }

    public ConstantClassInfo(DataInputStream data) throws IOException {
        this(data.readUnsignedShort());
    }

    @Override
    public void writeInfo(DataOutputStream data) throws IOException {
        data.writeShort(nameIndex);
    }
}
