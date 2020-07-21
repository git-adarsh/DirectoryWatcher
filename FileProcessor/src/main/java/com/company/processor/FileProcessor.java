package com.company.processor;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.company.processor.executor.directorywatcher.IDirectoryWatcher;
import com.company.processor.executor.fileprocessor.IOperations;
import com.company.processor.resources.FilterCriteria;

@Component
class FileProcessor implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	Environment env;

	@Autowired
	IDirectoryWatcher directoryWatcher;

	@Autowired
	FilterCriteria criteria;

	@Autowired
	IOperations onFileOperator;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		try {
			System.out.println("Initialing watch...");
			process();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void process() throws IOException {
		// create a directory watcher
		directoryWatcher.filter(
				// check if file is processed
				criteria::isProcessed).watch();

		// Process each file. No check is applied before processing the file
		/* directoryWatcher.filter(null).watch(); */

		// operate on the files/directories as they are created
		onFileOperator.operate();
	}
}
