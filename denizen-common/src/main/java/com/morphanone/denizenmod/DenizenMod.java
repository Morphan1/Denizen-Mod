package com.morphanone.denizenmod;

import com.morphanone.denizenmod.config.DenizenModConfig;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class DenizenMod {
    public static final String MOD_ID = "denizen";

    public static DenizenModImplementation instance;

    public static DenizenCoreBridge coreBridge;

    private static class ClassLoaderWrapper extends ClassLoader {
        public ClassLoaderWrapper(ClassLoader parent) {
            super(parent);
        }

        public boolean isLoaded(String name) {
            return findLoadedClass(name) != null;
        }
    }

    private static class DenizenModClassLoader extends ClassLoader {
        private final ClassLoaderWrapper parent;

        private final Set<String> validPaths = new HashSet<>();

        private static String toClassPath(String className) {
            return className.replace('.', '/').concat(".class");
        }

        private static final String DENIZEN_CORE_PATH = toClassPath(com.denizenscript.denizencore.DenizenCore.class.getName());

        private static final String DENIZEN_MOD_PATH = toClassPath(DenizenMod.class.getName());

        private void addValidPaths(String... paths) {
            for (String path : paths) {
                String fullPath = Objects.requireNonNull(parent.getResource(path)).toString();
                validPaths.add(fullPath.substring(0, fullPath.length() - path.length()));
            }
        }

        public DenizenModClassLoader(ClassLoader parent) {
            super(null);
            this.parent = new ClassLoaderWrapper(parent);
            try {
                Class.forName(DenizenModConfig.class.getName(), true, this.parent);
                Class.forName(DenizenCoreBridge.class.getName(), true, this.parent);
                Class.forName(DenizenModImplementation.class.getName(), true, this.parent);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            addValidPaths(DENIZEN_CORE_PATH, DENIZEN_MOD_PATH);
        }

        @Nullable
        @Override
        public URL getResource(String name) {
            URL url = parent.getResource(name);
            if (url == null) {
                return null;
            }
            String urlString = url.toString();
            for (String path : validPaths) {
                if (urlString.startsWith(path)) {
                    return url;
                }
            }
            return null;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (parent.isLoaded(name)) {
                return parent.loadClass(name);
            }
            try (InputStream is = getResourceAsStream(toClassPath(name))) {
                if (is == null) {
                    return parent.loadClass(name);
                }
                byte[] bytes = is.readAllBytes();
                return defineClass(name, bytes, 0, bytes.length);
            }
            catch (ClassFormatError | IOException e) {
                e.printStackTrace();
                throw new ClassNotFoundException();
            }
        }
    }

    public static DenizenCoreBridge establishCoreBridge()  {
        try {
            ClassLoader parentLoader = DenizenMod.class.getClassLoader();
            DenizenModClassLoader loader = new DenizenModClassLoader(parentLoader);
            Class<?> bridgeImpl = loader.loadClass(DenizenCoreBridgeImpl.class.getName());
            return (DenizenCoreBridge) bridgeImpl.getDeclaredConstructor().newInstance();
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static DenizenCoreBridge newCore(DenizenModImplementation instance) {
        DenizenCoreBridge bridge = establishCoreBridge();
        if (bridge.getClass().getClassLoader().equals(DenizenMod.class.getClassLoader())) {
            throw new IllegalStateException("Something went catastrophically wrong: DenizenCore loaded with default ClassLoader!");
        }
        bridge.init(instance);
        return bridge;
    }

    public static ResourceLocation resource(String id) {
        return new ResourceLocation(DenizenMod.MOD_ID, id);
    }
}
