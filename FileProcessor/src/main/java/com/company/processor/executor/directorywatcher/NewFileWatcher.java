package com.company.processor.executor.directorywatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.company.processor.constant.AppConstants;
import com.company.processor.executor.ITaskHandler;
import com.company.processor.utils.PathUtils;

@Component
public class NewFileWatcher implements IDirectoryWatcher {
	private String readFrom;
	private List<Predicate<Path>> predicates = new ArrayList<>();

	private ExecutorService WATCH_WORKER;
	private WatchService watcher;

	@Autowired
	Environment env;

	@Autowired
	ITaskHandler taskHandler;

	@Override
	public IDirectoryWatcher filter(Predicate<Path> condition) {
		if (condition != null)
			this.predicates.add(condition);
		return this;
	}

	@Override
	public void watch() throws IOException {
		// once initialized, ignore subsequent watch calls
		if (watcher != null)
			return;
		init();

		Task watchTask = new NewFileWatcher.Task();
		WATCH_WORKER.submit(watchTask.watchTask);
	}

	private void init() throws IOException {
		this.readFrom = env.getProperty(AppConstants.READ_DIRECTORY_PATH);
		System.out.println("READ_FROM: " + readFrom);

		// make sure a valid path is provided
		Objects.requireNonNull(readFrom, "Directory path cannot be null");

		Path directoryPath = Paths.get(readFrom);
		watcher = FileSystems.getDefault().newWatchService();

		directoryPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
		WATCH_WORKER = Executors.newSingleThreadExecutor();
	}

	@Override
	public void stop() throws IOException {
		if (watcher == null)
			return;

		watcher.close();
		WATCH_WORKER.shutdownNow();
	}

	/*
	 * private void clean(int waitTill, TimeUnit timeUnit) throws IOException {
	 * watcher.close(); }
	 */
	/*
	 * @Override public void stopNow() { if(watcher == null) return; clean(-1,
	 * TimeUnit.DAYS); }
	 */

	private class Task {
		Runnable watchTask = () -> {
			WatchKey k;
			System.out.println("Beginning to Watch directory: " + readFrom);
			while (true) {
				k = watcher.poll();
				try {
					if (k != null) {
						processKey(k);
					}
				} finally {
					// empty the queue
					if (k != null)
						k.reset();
				}
				// give the thread a breather
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					System.err.println("Watcher thread interuppted..");
					e.printStackTrace();
					break;
				}
			}
		};

		private void processKey(WatchKey k) {
			for (WatchEvent<?> event : k.pollEvents()) {

				if (event.context() != null) {
					/* Path p = Paths.get(readFrom + File.separator + event.context()); */
					Path p = (Path) event.context();
					if (Files.isDirectory(p))
						try {
							processDirectory(p);
						} catch (IOException e) {
							// catch error for this directory and continue processing
							e.printStackTrace();
						}
					else
						processFile(p);

				}
			}

		}

		private void processDirectory(Path path) throws IOException {
			Files.newDirectoryStream(path).forEach(p -> {
				if (Files.isDirectory(p))
					try {
						processDirectory(p);
					} catch (IOException e) {
						e.printStackTrace();
					}
				else
					processFile(p);
			});
		}

		private void processFile(Path p) {
			// if this is an internal file (file with .mtd ext), do internal computations
			if (PathUtils.getFileExtension(p).equals(AppConstants.NEW_EXTENSION_FOR_INDIVIDUAL_FILE))
				internalComputation(Paths.get(readFrom + File.separator + p.toString()));
			else {
				// skip file with extensions that are not allowed
				if (!PathUtils.ALLOWED_FILE_TYPE.test(p))
					return;

				if (applyPredicatesAndGet(predicates, p))
					// submit this path for execution
					taskHandler.put(Paths.get(readFrom + File.separator + p.toString()));
			}
		}
	}
}
