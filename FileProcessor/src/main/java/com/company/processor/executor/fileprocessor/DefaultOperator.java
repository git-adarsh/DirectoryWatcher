package com.company.processor.executor.fileprocessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.processor.constant.AppConstants;
import com.company.processor.executor.ITaskHandler;
import com.company.processor.resources.OperationResult;
import com.company.processor.resources.OperationResultWriter;
import com.company.processor.utils.PathUtils;

@Component
public class DefaultOperator implements IOperations {
	private ExecutorService WORKER_POOL;

	@Autowired
	ITaskHandler taskHandler;

	@Override
	public void operate() {
		if (WORKER_POOL == null)
			WORKER_POOL = Executors.newCachedThreadPool();

		for (;;) {
			try {
				Path processFileAt = taskHandler.get();
				// empty queue, park thread
				if (processFileAt == null)
					Thread.sleep(2000);
				else {
					String fileExtension = PathUtils.getFileExtension(processFileAt);
					String delim = AppConstants.getDelimiterFor(fileExtension);
					Task task = new DefaultOperator.Task(processFileAt, delim);
					WORKER_POOL.submit(task.run);
				}

			} catch (InterruptedException e) {
				System.err.println("Thread Interrupted..");
				e.printStackTrace();
				break;

			}
		}
	}

	@Override
	public void stop() {
		if(WORKER_POOL != null)
			WORKER_POOL.shutdownNow();
	}

	private class Task {
		Path p;

		long totalWords;
		long vowels = 0;
		long spChars = 0;

		String delimiter;

		Task(Path p, String delimiter) {
			this.p = p;
			this.delimiter = delimiter;
		};

		Runnable run = () -> {
			// read file line by line. This is to avoid loading all of content to file and a
			// possible out of memory error
			try (Stream<String> lines = Files.lines(p)) {
				lines.forEach(this::processLine);
			} catch (IOException e) {
				System.err.println("Failed to process file at: " + p);
				e.printStackTrace();
			}

			write();

		};

		void processLine(String line) {
			String[] words = line.split(delimiter);
			totalWords = words.length;

			Arrays.stream(words).forEach(this::processWord);
		}

		void processWord(String word) {

			for (int i = 0; i < word.length(); i++) {
				if (VOWELS.contains(word.charAt(i)))
					vowels++;
				else if (SPECIAL_CHARACTERS.contains(word.charAt(i)))
					spChars++;
			}
		}

		private void write() {
			// write details to another file with same name
			OperationResult res = OperationResult.builder().totalWords(totalWords).vowels(vowels)
					.specialCharacters(spChars).build();
			OperationResultWriter.write(res, PathUtils.newPath(p, AppConstants.NEW_EXTENSION_FOR_INDIVIDUAL_FILE));

		}

	}

}
