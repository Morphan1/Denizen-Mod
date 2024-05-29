package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * CONSTANT_InterfaceMethodref_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.2">section 4.4.2</a>
 */
public class ConstantInterfaceMethodRefInfo extends ConstantRefInfo {
    public ConstantInterfaceMethodRefInfo(int classIndex, int nameAndTypeIndex) {
        super(ConstantTag.CONSTANT_InterfaceMethodref, classIndex, nameAndTypeIndex);
    }

    public ConstantInterfaceMethodRefInfo(DataInputStream data) throws IOException {
        super(ConstantTag.CONSTANT_InterfaceMethodref, data);
    }
}
