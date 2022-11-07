package com.morphanone.denizenmod;

import com.morphanone.denizenmod.config.DenizenModConfig;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This class is given a new context for:
 * <ul>
 *     <li>The mod implementation</li>
 *     <li>The client-side DenizenCore, if applicable</li>
 *     <li>The server-side DenizenCore, if applicable</li>
 *     <li>Any other DenizenCore instances that might be created</li>
 * </ul>
 * For example, data set here from a Core is not accessible directly from the mod implementation and vice versa.
 * Likewise, Cores cannot access data here that was set by another Core.
 */
public final class DenizenMod {
    public static final String MOD_ID = "denizen";

    /**
     * Not accessible outside the internals.
     */
    public static DenizenModImplementation instance;

    /**
     * Not accessible outside the internals.
     */
    public static DenizenCoreBridge coreBridge;

    /**
     * Accessible only outside the internals.
     */
    private static List<DenizenCoreBridge> allActiveCores;

    /**
     * Accessible only outside the internals.
     */
    private static List<DenizenModClassLoader> classLoaders;

    /**
     * Intermediary ClassLoader for load and checking our force-loaded classes.
     */
    private static class ClassLoaderWrapper extends ClassLoader {
        public ClassLoaderWrapper(ClassLoader parent) {
            super(parent);
            forceLoadApi();
        }

        /**
         * This loads interfaces that should be freely available to code both within and without the sandbox.
         */
        private void forceLoadApi() {
            try {
                Class.forName(DenizenModConfig.class.getName(), true, this);
                Class.forName(DenizenCoreBridge.class.getName(), true, this);
                Class.forName(DenizenModImplementation.class.getName(), true, this);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean isLoaded(String name) {
            return findLoadedClass(name) != null;
        }
    }

    /**
     * Each DenizenCore is loaded through a separate instance of this ClassLoader.
     * This effectively creates a sandbox for all classes contained within the DenizenCore itself,
     * but also means that any class referencing the core must also be loaded through here.
     *
     * <p> For example, tags such as {@link com.morphanone.denizenmod.objects.PlayerTag PlayerTag} rely on
     * {@link com.denizenscript.denizencore.objects.ObjectTag ObjectTag} and consequentially much more.
     * Thus, it cannot be loaded externally without risking the sandbox being compromised due to loading
     * the DenizenCore (and its static contents) in a parent ClassLoader.
     *
     * <p> Unfortunately, this leads the system to a clear drawback: each project must be very careful with
     * the classes it loads. New Tag and Command implementations must be separated from the rest of the project,
     * and must be carefully loaded through the DenizenMod API.
     */
    private static class DenizenModClassLoader extends ClassLoader {
        private final ClassLoaderWrapper parent;

        private final Set<String> validPaths = new HashSet<>();

        private static String toClassPath(String className) {
            return className.replace('.', '/').concat(".class");
        }

        private static final String DENIZEN_CORE_PATH = toClassPath(com.denizenscript.denizencore.DenizenCore.class.getName());

        private static final String DENIZEN_MOD_PATH = toClassPath(DenizenMod.class.getName());

        private void addValidPath(String path) {
            System.out.println("ADDING PATH: " + path);
            String fullPath = Objects.requireNonNull(parent.getResource(path)).toString();
            validPaths.add(fullPath.substring(0, fullPath.length() - path.length()));
        }

        public void addValidClass(String className) {
            String path = toClassPath(className);
            if (validPaths.contains(path)) {
                return;
            }
            addValidPath(path);
        }

        public DenizenModClassLoader(ClassLoader parent) {
            super(null);
            this.parent = new ClassLoaderWrapper(parent);
            addValidPath(DENIZEN_CORE_PATH);
            addValidPath(DENIZEN_MOD_PATH);
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

    /**
     * Creates a new DenizenCore sandbox and returns a bridge to it.
     */
    private static DenizenCoreBridge establishCoreBridge()  {
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

    /**
     * Creates a new DenizenCore sandbox for a DenizenMod implementation and returns the bridge to it.
     */
    public static DenizenCoreBridge newCore(DenizenModImplementation instance) {
        if (allActiveCores == null) {
            allActiveCores = new ArrayList<>();
            classLoaders = new ArrayList<>();
        }
        DenizenCoreBridge coreBridge = establishCoreBridge();
        ClassLoader classLoader = coreBridge.getClass().getClassLoader();
        if (classLoader.equals(DenizenMod.class.getClassLoader())) {
            throw new IllegalStateException("Something went catastrophically wrong: DenizenCore loaded with default ClassLoader!");
        }
        coreBridge.init(instance);
        allActiveCores.add(coreBridge);
        classLoaders.add((DenizenModClassLoader) classLoader);
        return coreBridge;
    }

    public static void registerTagExtension(String objectTag, String extension) {
        for (int i = 0; i < allActiveCores.size(); i++) {
            classLoaders.get(i).addValidClass(extension);
            allActiveCores.get(i).registerTagExtension(objectTag, extension);
        }
    }

    public static void registerTagFactory(String factory) {
        for (int i = 0; i < allActiveCores.size(); i++) {
            classLoaders.get(i).addValidClass(factory);
            allActiveCores.get(i).registerTagFactory(factory);
        }
    }

    public static ResourceLocation resource(String id) {
        return new ResourceLocation(DenizenMod.MOD_ID, id);
    }
}
