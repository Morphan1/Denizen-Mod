package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * CONSTANT_InvokeDynamic_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.10">section 4.4.10</a>
 */
public class ConstantInvokeDynamicInfo extends ConstantBootstrapRefInfo {
    public ConstantInvokeDynamicInfo(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
        super(ConstantTag.CONSTANT_InvokeDynamic, bootstrapMethodAttrIndex, nameAndTypeIndex);
    }

    public ConstantInvokeDynamicInfo(DataInputStream data) throws IOException {
        super(ConstantTag.CONSTANT_InvokeDynamic, data);
    }
}
