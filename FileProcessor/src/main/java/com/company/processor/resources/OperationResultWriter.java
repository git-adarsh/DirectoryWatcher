package com.company.processor.resources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OperationResultWriter {

	public static void write(OperationResult result, String writeAt) {
		Path path = Paths.get(writeAt);
        try {
			Files.writeString(path, result.toString());
		} catch (IOException e) {
			System.err.println("Failed to write deatils to file at: " + path);
			e.printStackTrace();
		}
	}
}
