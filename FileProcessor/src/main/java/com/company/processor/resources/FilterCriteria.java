package com.company.processor.resources;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Utitliy class More criteria methods can be added here and supplied to Filter
 * interface
 */
@Component
public class FilterCriteria {

	private @Autowired ExpiringCache cache;

	public boolean isProcessed(Path path) {
		return path == null ? false : cache.checkAndStore(path.getFileName().toString());

	}

	public boolean isProcessed(int duration, TimeUnit timeunit) {
		return false;
	}
}
