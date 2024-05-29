package com.morphanone.denizenmod.utilities.java.constant;

import com.morphanone.denizenmod.utilities.java.JavaClassNode;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Base for {@link com.morphanone.denizenmod.utilities.java.JavaConstantPool constant_pool} entries.
 */
public abstract class ConstantInfo implements JavaClassNode {
    public final ConstantTag tag;

    public ConstantInfo(ConstantTag tag) {
        this.tag = tag;
    }

    public void writeTo(DataOutputStream data) throws IOException {
        data.writeByte(tag.value);
        writeInfo(data);
    }

    protected abstract void writeInfo(DataOutputStream data) throws IOException;
}
