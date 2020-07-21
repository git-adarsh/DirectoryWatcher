package com.company.processor.executor;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

@Component
public class DefaultTaskHandler implements ITaskHandler {
	private BlockingQueue<Path> tasks = new LinkedBlockingQueue<>(10);

	@Override
	public void put(Path p) {
		System.out.println("Ading to task path: " + p);
		if (p != null)
			tasks.add(p);
	}

	@Override
	public Path get() throws InterruptedException {
		return tasks.poll();
	}

}
