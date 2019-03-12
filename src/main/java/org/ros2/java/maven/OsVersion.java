package org.ros2.java.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Scanner;

public class OsVersion {
	
	public static String getOsName() {
		String name = System.getProperty("os.name");
		if("Linux".equals(name)) {
			return getUbuntuName();
		}
		return "";
	}

	public static String getUbuntuName() {
		try {
			File file = new File("/etc");
			File[] list = file.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.endsWith("-release");
			    }
			});
			for (File f : list) {
				try (Scanner s = new Scanner(new FileInputStream(f))) {
				    while (s.hasNext()) {
				    	String str = s.nextLine();
				    	//System.out.println(str);
				    	if(str.startsWith("UBUNTU_CODENAME=")) {
				    		return str.substring(16);
				    	}
				    }
				}
			}
			return "";
		} catch (FileNotFoundException e) {
			return "";
		}		
	}
	
	public static String getFileNameFromPath(String path) {
		int index = path.lastIndexOf("/");
		return path.substring(index + 1);		
	}

	public static void main(String[] args) {
		System.out.println(OsVersion.getOsName());
	}
}
