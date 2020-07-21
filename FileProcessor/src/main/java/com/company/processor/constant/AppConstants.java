package com.company.processor.constant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AppConstants {
	public static final String READ_DIRECTORY_PATH = "read_from";
	public static final String WRITE_DIRECTORY_PATH = "write_at";

	public static final String FILE_PROCESSING_EXPIRES_AT = "cache_expire_at";

	public static final String FILE_EXTENSIONS_TEXT = "txt";
	public static final String FILE_EXTENSIONS_CSV = "csv";

	private final static Set<String> ALLOWED_FILE_TYPES = loadAllowedFileTypes();
	public static final String NEW_EXTENSION_FOR_INDIVIDUAL_FILE = ".mtd";

	public static final String NEW_AGGREGATED_FILE_EXTENSION = ".dmtd";
	public static final String NEW_S_META_FILE_EXTENSION = ".smtd";

	public static final String SORT_BY = "sort_files_by";

	private static final Map<String, String> FILE_EXT_TO_DELIMETER_MAP = loadfileExtToDelimMap();

	private static Set<String> loadAllowedFileTypes() {
		Set<String> set = new HashSet<>();
		set.add(FILE_EXTENSIONS_TEXT);
		set.add(FILE_EXTENSIONS_CSV);
		return set;
	}

	private static Map<String, String> loadfileExtToDelimMap() {
		Map<String, String> map = new HashMap<>();
		map.put(FILE_EXTENSIONS_TEXT, " ");
		map.put(FILE_EXTENSIONS_CSV, ",");

		return map;
	}

	public static boolean isFileTypeAllowed(String fileType) {
		return AppConstants.ALLOWED_FILE_TYPES.contains(fileType);
	}

	public static String getDelimiterFor(String fileExtension) {
		return FILE_EXT_TO_DELIMETER_MAP.get(fileExtension);
	}
}
