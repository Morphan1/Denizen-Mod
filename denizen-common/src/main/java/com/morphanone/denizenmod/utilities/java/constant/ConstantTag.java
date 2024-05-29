package com.morphanone.denizenmod.utilities.java.constant;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class ConstantTag<T extends ConstantInfo> {
    private static final Map<Integer, ConstantTag<?>> byId = new HashMap<>();

    /** Entry tag for {@link ConstantUtf8Info} */
    public static final ConstantTag<ConstantUtf8Info> CONSTANT_Utf8 = register(new ConstantTag<>(1, ConstantUtf8Info::new));

    /** Entry tag for {@link ConstantIntegerInfo} */
    public static final ConstantTag<ConstantIntegerInfo> CONSTANT_Integer = register(new ConstantTag<>(3, ConstantIntegerInfo::new));

    /** Entry tag for {@link ConstantFloatInfo} */
    public static final ConstantTag<ConstantFloatInfo> CONSTANT_Float = register(new ConstantTag<>(4, ConstantFloatInfo::new));

    /** Entry tag for {@link ConstantLongInfo} */
    public static final ConstantTag<ConstantLongInfo> CONSTANT_Long = register(new ConstantTag<>(5, ConstantLongInfo::new, 2));

    /** Entry tag for {@link ConstantDoubleInfo} */
    public static final ConstantTag<ConstantDoubleInfo> CONSTANT_Double = register(new ConstantTag<>(6, ConstantDoubleInfo::new, 2));

    /** Entry tag for {@link ConstantClassInfo} */
    public static final ConstantTag<ConstantClassInfo> CONSTANT_Class = register(new ConstantTag<>(7, ConstantClassInfo::new));

    /** Entry tag for {@link ConstantStringInfo} */
    public static final ConstantTag<ConstantStringInfo> CONSTANT_String = register(new ConstantTag<>(8, ConstantStringInfo::new));

    /** Entry tag for {@link ConstantFieldRefInfo} */
    public static final ConstantTag<ConstantFieldRefInfo> CONSTANT_Fieldref = register(new ConstantTag<>(9, ConstantFieldRefInfo::new));

    /** Entry tag for {@link ConstantMethodRefInfo} */
    public static final ConstantTag<ConstantMethodRefInfo> CONSTANT_Methodref = register(new ConstantTag<>(10, ConstantMethodRefInfo::new));

    /** Entry tag for {@link ConstantInterfaceMethodRefInfo} */
    public static final ConstantTag<ConstantInterfaceMethodRefInfo> CONSTANT_InterfaceMethodref = register(new ConstantTag<>(11, ConstantInterfaceMethodRefInfo::new));

    /** Entry tag for {@link ConstantNameAndTypeInfo} */
    public static final ConstantTag<ConstantNameAndTypeInfo> CONSTANT_NameAndType = register(new ConstantTag<>(12, ConstantNameAndTypeInfo::new));

    /** Entry tag for {@link ConstantMethodHandleInfo} */
    public static final ConstantTag<ConstantMethodHandleInfo> CONSTANT_MethodHandle = register(new ConstantTag<>(15, ConstantMethodHandleInfo::new));

    /** Entry tag for {@link ConstantMethodTypeInfo} */
    public static final ConstantTag<ConstantMethodTypeInfo> CONSTANT_MethodType = register(new ConstantTag<>(16, ConstantMethodTypeInfo::new));

    /** Entry tag for {@link ConstantDynamicInfo} */
    public static final ConstantTag<ConstantDynamicInfo> CONSTANT_Dynamic = register(new ConstantTag<>(17, ConstantDynamicInfo::new));

    /** Entry tag for {@link ConstantInvokeDynamicInfo} */
    public static final ConstantTag<ConstantInvokeDynamicInfo> CONSTANT_InvokeDynamic = register(new ConstantTag<>(18, ConstantInvokeDynamicInfo::new));

    /** Entry tag for {@link ConstantModuleInfo} */
    public static final ConstantTag<ConstantModuleInfo> CONSTANT_Module = register(new ConstantTag<>(19, ConstantModuleInfo::new));

    /** Entry tag for {@link ConstantPackageInfo} */
    public static final ConstantTag<ConstantPackageInfo> CONSTANT_Package = register(new ConstantTag<>(20, ConstantPackageInfo::new));

    public final int value;

    public final InfoCreator<?> infoCreator;

    public final int entryConsumption;

    public ConstantTag(int value, InfoCreator<T> infoCreator) {
        this(value, infoCreator, 1);
    }

    public ConstantTag(int value, InfoCreator<T> infoCreator, int entryConsumption) {
        this.value = value;
        this.infoCreator = infoCreator;
        this.entryConsumption = entryConsumption;
    }

    public static <T extends ConstantTag<?>> T register(T tag) {
        byId.put(tag.value, tag);
        return tag;
    }

    public static ConstantTag<?> fromId(int id) {
        return byId.get(id);
    }

    @FunctionalInterface
    public interface InfoCreator<T extends ConstantInfo> {
        T from(DataInputStream data) throws IOException;
    }
}
