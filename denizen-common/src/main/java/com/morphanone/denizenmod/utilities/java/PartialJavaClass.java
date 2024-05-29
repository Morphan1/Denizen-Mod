package com.morphanone.denizenmod.utilities.java;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Conforms to the JVM specs for Java SE 19 class files, as described in Chapter 4
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html">(link)</a>
 *
 * <p> Primarily used to get early information without dragging the entire class file into memory.
 */
public final class PartialJavaClass {
    /**
     * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.1">Chapter 4.1</a>
     */
    public static final int CLASS_FILE_MAGIC = 0xCAFEBABE;

    private static final int ALL_BYTES_INITIAL_CAPACITY = 1024 * 2;

    private JavaVersion version;

    private JavaConstantPool constantPool;

    private final DataInputStream data;

    public PartialJavaClass(DataInputStream data) {
        this.data = data;
    }

    public byte[] getAllBytes() throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(ALL_BYTES_INITIAL_CAPACITY);
              DataOutputStream dataOut = new DataOutputStream(out)) {
            dataOut.writeInt(CLASS_FILE_MAGIC);
            if (version == null) {
                return remaining(out);
            }
            dataOut.writeShort(version.minor());
            dataOut.writeShort(version.major());
            if (constantPool == null) {
                return remaining(out);
            }
            constantPool.writeTo(dataOut);
            return remaining(out);
        }
    }

    private byte[] remaining(ByteArrayOutputStream out) throws IOException {
        out.write(data.readAllBytes());
        return out.toByteArray();
    }

    public JavaVersion getVersion() throws IOException, ClassReadException {
        readHeader();
        return version;
    }

    public JavaConstantPool getConstantPool() throws IOException, ClassReadException {
        readConstantPool();
        return constantPool;
    }

    private void readHeader() throws IOException, ClassReadException {
        if (version != null) {
            return;
        }
        int magic = data.readInt();
        if (magic != CLASS_FILE_MAGIC) {
            throw new ClassReadException("Invalid Java .class file format");
        }
        int minorVersion = data.readUnsignedShort();
        int majorVersion = data.readUnsignedShort();
        JavaVersion version = new JavaVersion(majorVersion, minorVersion);
        if (!version.isValid()) {
            throw new ClassReadException("Invalid Java minor version specified in .class file");
        }
        this.version = version;
    }

    private void readConstantPool() throws IOException, ClassReadException {
        if (constantPool != null) {
            return;
        }
        readHeader();
        constantPool = new JavaConstantPool(data);
    }
}
