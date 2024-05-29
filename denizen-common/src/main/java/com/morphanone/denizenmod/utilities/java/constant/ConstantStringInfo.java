package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CONSTANT_String_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.3">section 4.4.3</a>
 */
public class ConstantStringInfo extends ConstantInfo {
    public final int stringIndex;

    public ConstantStringInfo(int stringIndex) {
        super(ConstantTag.CONSTANT_String);
        this.stringIndex = stringIndex;
    }

    public ConstantStringInfo(DataInputStream data) throws IOException {
        this(data.readUnsignedShort());
    }

    @Override
    protected void writeInfo(DataOutputStream data) throws IOException {
        data.writeShort(stringIndex);
    }
}
