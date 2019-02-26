package org.ros2.java.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Set;
import java.util.regex.Pattern;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.ros2.rcljava.common.JNIUtils;
import org.scijava.nativelib.NativeLoader;

public class Ros2JavaLibraries {

	private static final String TMP_FOLDER_NAME = "ros2_java_libs";

	/**
	 * THIS WILL NOT WORK, beacuse ROS2 libraries depend on other libraries. It
	 * seems the only way is indeed to copy libraries into folder where
	 * LD_LIBRARY_PATH is pointing to.
	 */
	public static void installLibraryLoader() {
		JNIUtils.setLibraryLoader(libname -> {
			try {
				NativeLoader.loadLibrary(libname);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	public static void unpack() throws Exception {
		unpack(false);
	}

	public static void unpack(boolean skipUnpackingWhenFolderExists) throws Exception {
		String tempDir = System.getProperty("java.io.tmpdir");
		File temporaryDiretcoryFile = new File(tempDir, TMP_FOLDER_NAME);
		String temporaryDiretcoryName = temporaryDiretcoryFile.getAbsolutePath();

		if (skipUnpackingWhenFolderExists && temporaryDiretcoryFile.exists()) {
			return;
		}

		File tmp = Files.createTempDirectory(new File(tempDir).toPath(), TMP_FOLDER_NAME + "_").toFile();

		Set<String> fileNames = getJniLibraryNames();

		copyJniLibrariesToTemporaryDirectory(tmp.getAbsolutePath(), fileNames);

		tmp.renameTo(temporaryDiretcoryFile);

		System.out.println("\nCopied ros2_java libraries to: " + temporaryDiretcoryName + "\n"
				+ "Please add to following line to your .bashrc\n" + "export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:"
				+ temporaryDiretcoryName + "\n");
	}

	private static Set<String> getJniLibraryNames() {
		return new Reflections("lib*", new ResourcesScanner()).getResources(Pattern.compile("lib.*so.*"));
	}

	private static void copyJniLibrariesToTemporaryDirectory(String temporaryDiretcoryName, Set<String> fileNames)
			throws Exception {
		Ros2JavaLibraries object = new Ros2JavaLibraries();
		for (String fileName : fileNames) {
			exportResource(temporaryDiretcoryName + "/" + fileName, object.getResourceAsStream(fileName));
		}
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
			try {
				stream.close();
			} catch (Exception e) {
			}
			resStreamOut.close();
		}
	}
}
