package com.morphanone.denizenmod.utilities.java;

import com.morphanone.denizenmod.utilities.java.constant.ConstantClassInfo;
import com.morphanone.denizenmod.utilities.java.constant.ConstantInfo;
import com.morphanone.denizenmod.utilities.java.constant.ConstantTag;
import com.morphanone.denizenmod.utilities.java.constant.ConstantUtf8Info;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Class file {@code constant_pool} as described by
 * <a href="https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4">Chapter 4.4</a>
 */
public class JavaConstantPool implements JavaClassNode, Iterable<ConstantInfo> {
    private final ConstantInfo[] entries;

    public JavaConstantPool(ConstantInfo[] entries) {
        this.entries = Arrays.copyOf(entries, entries.length);
    }

    public JavaConstantPool(DataInputStream data) throws IOException, ClassReadException {
        int count = data.readUnsignedShort();
        ConstantInfo[] entries = new ConstantInfo[count];
        /*
          Relevant notes:

          Chapter 4.1 https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.1
          The constant_pool table is indexed from 1 to constant_pool_count - 1.
          (The first entry is unused)

          Chapter 4.4.5 https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.4.5
          All 8-byte constants take up two entries in the constant_pool table of the class file.
          (We handle entry consumption through the ConstantTag - though, most are a single entry)
         */
        int i = 1;
        while (i < count) {
            int tagId = data.readUnsignedByte();
            ConstantTag<?> tag = ConstantTag.fromId(tagId);
            if (tag == null) {
                throw new ClassReadException("Unknown tag specified in class file constant pool: " + tagId + " at index " + i);
            }
            ConstantInfo info = tag.infoCreator.from(data);
            entries[i] = info;
            i += tag.entryConsumption;
        }
        this.entries = entries;
    }

    @SuppressWarnings("unchecked")
    public <T extends ConstantInfo> List<T> gatherInfoFor(ConstantTag<T> tag) {
        List<T> list = new ArrayList<>();
        for (ConstantInfo info : this) {
            if (info.tag == tag) {
                list.add((T) info);
            }
        }
        return list;
    }

    public List<String> getClassReferences() throws ClassReadException {
        List<String> result = new ArrayList<>();
        List<ConstantClassInfo> classReferences = gatherInfoFor(ConstantTag.CONSTANT_Class);
        for (ConstantClassInfo classReference : classReferences) {
            ConstantInfo classNameEntry = entries[classReference.nameIndex];
            if (!(classNameEntry instanceof ConstantUtf8Info className)) {
                throw new ClassReadException("Invalid class name index specified in .class file");
            }
            result.add(className.getString());
        }
        return result;
    }

    @Override
    public void writeTo(DataOutputStream data) throws IOException {
        data.writeShort(entries.length);
        for (ConstantInfo info : this) {
            info.writeTo(data);
        }
    }

    @Override
    public Iterator<ConstantInfo> iterator() {
        return new ConstantIterator();
    }

    private class ConstantIterator implements Iterator<ConstantInfo> {
        private int currentIndex = 1;

        @Override
        public boolean hasNext() {
            return currentIndex < entries.length && entries[currentIndex] != null;
        }

        @Override
        public ConstantInfo next() {
            ConstantInfo info = entries[currentIndex];
            currentIndex += info.tag.entryConsumption;
            return info;
        }
    }
}
