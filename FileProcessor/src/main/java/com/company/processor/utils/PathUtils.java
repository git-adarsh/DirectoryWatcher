package com.company.processor.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

import com.company.processor.constant.AppConstants;

public class PathUtils {

	/** Process only .txt and .csv files */
	public final static Predicate<Path> ALLOWED_FILE_TYPE = p -> {
		return AppConstants.isFileTypeAllowed(getFileExtension(p));
	};

	public static String getFileExtension(Path p) {
		String t = p == null ? "" : p.toString();
		return t.isEmpty() ? "" : t.substring(t.lastIndexOf('.') + 1);
	}

	public static String newPath(Path p, String ext) {
		String t = p == null ? "" : p.toString();
		return t.substring(0, t.lastIndexOf('.')) + ext; 
	}

	public static byte[] getFileContent(Path p) throws IOException {
		return Files.readAllBytes(p);
	}

}
