package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * CONSTANT_Utf8_info structure as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.7">section 4.4.7</a>
 */
public class ConstantUtf8Info extends ConstantInfo {
    private final byte[] bytes;

    public ConstantUtf8Info(byte[] bytes) {
        super(ConstantTag.CONSTANT_Utf8);
        this.bytes = bytes;
    }

    public ConstantUtf8Info(DataInputStream data) throws IOException {
        this(data.readNBytes(data.readUnsignedShort()));
    }

    public String getString() {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    protected void writeInfo(DataOutputStream data) throws IOException {
        data.writeShort(bytes.length);
        data.write(bytes);
    }
}
