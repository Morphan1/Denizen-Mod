package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * CONSTANT_Fieldref_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.2">section 4.4.2</a>
 */
public class ConstantFieldRefInfo extends ConstantRefInfo {
    public ConstantFieldRefInfo(int classIndex, int nameAndTypeIndex) {
        super(ConstantTag.CONSTANT_Fieldref, classIndex, nameAndTypeIndex);
    }

    public ConstantFieldRefInfo(DataInputStream data) throws IOException {
        super(ConstantTag.CONSTANT_Fieldref, data);
    }
}
