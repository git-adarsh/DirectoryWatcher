package com.company.processor.executor;

import java.nio.file.Path;

public interface ITaskHandler {
	
	void put(Path p);
	
	Path get() throws InterruptedException;
}
