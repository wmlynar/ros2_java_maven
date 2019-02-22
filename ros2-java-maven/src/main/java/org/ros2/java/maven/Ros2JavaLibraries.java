package org.ros2.java.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.ros2.rcljava.common.JNIUtils;
import org.scijava.nativelib.NativeLoader;

import com.google.common.base.Predicate;

public class Ros2JavaLibraries {

    private static final String TMP_FOLDER_NAME = "ros2_java_libs";

    public static void installLibraryLoader() {
    	JNIUtils.setLibraryLoader(libname -> {
			try {
				NativeLoader.loadLibrary(libname);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
    }
    
    public static String unpack() throws Exception {
        Set<String> fileNames = getJniLibraryNames();

        String temporaryDiretcoryName = createTemporaryDirectory().toString();

        copyJniLibrariesToTemporaryDirectory(temporaryDiretcoryName, fileNames);

        System.out.println("\nCopied ros2_java libraries to: " + temporaryDiretcoryName + "\n"
                + "Please add to following line to your .bashrc\n"
                + "export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:" + temporaryDiretcoryName + "\n");

        // addToLibraryPath(temporaryDiretcoryName);
        // EnvironmentModifier.setenv("LD_LIBRARY_PATH", temporaryDiretcoryName);

        return temporaryDiretcoryName;
    }

    private static Set<String> getJniLibraryNames() {
        Set<String> fileNames = new Reflections(".*", new ResourcesScanner()).getResources(new Predicate<String>() {
            public boolean apply(String input) {
                return input.contains("lib");
            }
        });
        return fileNames;
    }

    private static File createTemporaryDirectory() throws IOException {
        File temporaryDir = createTempDirectory(TMP_FOLDER_NAME);
        temporaryDir.deleteOnExit(); // TODO: does not work
        return temporaryDir;
    }

    private static void copyJniLibrariesToTemporaryDirectory(String temporaryDiretcoryName, Set<String> fileNames)
            throws Exception {
        Ros2JavaLibraries object = new Ros2JavaLibraries();
        for (String fileName : fileNames) {
            exportResource(temporaryDiretcoryName + "/" + fileName, object.getResourceAsStream(fileName));
        }
    }

    /**
     * When messing around with JNI, one have to set the java.library.path
     * accordingly. Unfortunately the only way is to add a system property before
     * the application is started:
     * 
     * java -Djava.library.path=/path/to/libs
     * 
     * same as
     * 
     * java -Djava.library.path=C:\Java\ljwgl\libs org.mypackage.MyProgram
     * 
     * Changing the system property later doesn’t have any effect, since the
     * property is evaluated very early and cached. But the guys over at jdic
     * discovered a way how to work around it. It is a little bit dirty – but hey,
     * those hacks are the reason we all love Java…
     * 
     * System.setProperty( "java.library.path", "/path/to/libs" ); Field
     * fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
     * fieldSysPath.setAccessible( true ); fieldSysPath.set( null, null );
     * 
     * Explanation
     * 
     * At first the system property is updated with the new value. This might be a
     * relative path – or maybe you want to create that path dynamically.
     * 
     * The Classloader has a static field (sys_paths) that contains the paths. If
     * that field is set to null, it is initialized automatically. Therefore forcing
     * that field to null will result into the reevaluation of the library path as
     * soon as loadLibrary() is called…
     */
    private static void addToLibraryPath(String temporaryDiretcoryName)
            throws NoSuchFieldException, IllegalAccessException {
        System.setProperty("java.library.path", temporaryDiretcoryName);
        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);
    }

    private static File createTempDirectory(String prefix) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File generatedDir = new File(tempDir, prefix);

        if (!generatedDir.exists() && !generatedDir.mkdir()) {
            throw new IOException("Failed to create temp directory " + generatedDir.getName());
        }

        return generatedDir;
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in = getContextClassLoader().getResourceAsStream(resource);
        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private static void exportResource(String where, InputStream stream) throws Exception {
        OutputStream resStreamOut = null;
        try {
            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(where);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }
    }
}
