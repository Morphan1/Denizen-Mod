package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * CONSTANT_Dynamic_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.10">section 4.4.10</a>
 */
public class ConstantDynamicInfo extends ConstantBootstrapRefInfo {
    public ConstantDynamicInfo(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
        super(ConstantTag.CONSTANT_Dynamic, bootstrapMethodAttrIndex, nameAndTypeIndex);
    }

    public ConstantDynamicInfo(DataInputStream data) throws IOException {
        super(ConstantTag.CONSTANT_Dynamic, data);
    }
}
