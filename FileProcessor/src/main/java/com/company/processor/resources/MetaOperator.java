package com.company.processor.resources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.company.processor.constant.AppConstants;
import com.company.processor.utils.PathUtils;

public class MetaOperator {
	
	@Autowired
	Environment env;
	public  void opearateMtd(Path p) {
		Path metaFilePath = Paths.get(PathUtils.newPath(p, AppConstants.NEW_AGGREGATED_FILE_EXTENSION));
		try {
			Files.write(metaFilePath, PathUtils.getFileContent(p), StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.err.println("Failed to write meta information in the file: " + metaFilePath);
			e.printStackTrace();
		}
	}
	
	public  void opearateSmtd(Path p) {
		String sortBy;
		if((sortBy = env.getProperty(AppConstants.SORT_BY)) == null) {
			return;
		}
		
		// apply sort logic..
		Path metaFilePath = Paths.get(PathUtils.newPath(p, AppConstants.NEW_S_META_FILE_EXTENSION));
		try {
			Files.write(metaFilePath, PathUtils.getFileContent(p), StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.err.println("Failed to write meta information in the file: " + metaFilePath);
			e.printStackTrace();
		}
	}
}
