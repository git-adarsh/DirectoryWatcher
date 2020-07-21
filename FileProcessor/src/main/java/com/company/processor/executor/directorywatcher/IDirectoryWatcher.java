package com.company.processor.executor.directorywatcher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import com.company.processor.resources.FilterCriteria;
import com.company.processor.resources.MetaOperator;

@Component
public interface IDirectoryWatcher {

	MetaOperator metaOperator = new MetaOperator();
	/**
	 * The configuration for the app which is set at the initialization of the app.
	 * These configs cannot be changed dynamically.
	 * 
	 * @param condition A condition to applied on each file. If the condition is
	 *                  met, then only file is processed. Accepts {@code null}. In
	 *                  such a case, every file is processed
	 * 
	 * 
	 * @see {@link FilterCriteria}
	 */
	IDirectoryWatcher filter(Predicate<Path> condition);

	void watch() throws IOException;

	void stop() throws IOException;

	default boolean applyPredicatesAndGet(List<Predicate<Path>> predicates, Path path) {
		if (predicates == null || predicates.isEmpty())
			return true;

		boolean allConditionsMet = true;

		for (Predicate<Path> p : predicates) {
			// break away for the first false condition
			if (!p.test(path)) {
				allConditionsMet = false;
				break;
			}
		}

		return allConditionsMet;
	}

	default void internalComputation(Path p) {
		metaOperator.opearateMtd(p);
		metaOperator.opearateSmtd(p);
	}
}
