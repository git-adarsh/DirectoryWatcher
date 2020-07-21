package com.company.processor.resources;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.company.processor.constant.AppConstants;

@Component
public class ExpiringCache {

	@Autowired
	Environment env;

	private int lastExpValue;

	/** Use a thread scheduler to clear the outdated entries*/
	private Map<String, Long> fileNameToLastAccessedMap = new ConcurrentHashMap<>();

	/**
	 * This method takes fileName and takes action based on conditions:
	 * 
	 * @returns {@code true} If fileName not in map or timeExpired. {@code false} If
	 *          fileName is in map and within expiration range
	 * 
	 *          If the the value of expire at changes at any time during app
	 *          lifecycle, all entries are invalidated and fresh entries are made
	 * 
	 */
	public boolean checkAndStore(String fileName) {
		checkAndSetExpirationTime();
		
		if (fileNameToLastAccessedMap.containsKey(fileName)) {
			// check if key has expired
			if ((fileNameToLastAccessedMap.get(fileName) + lastExpValue) <= System.currentTimeMillis()) {
				// key is expired
				// reset insertion time
				fileNameToLastAccessedMap.put(fileName, System.currentTimeMillis());
				return true;
			}
			// file received within time threshold
			return false;
		} else {
			fileNameToLastAccessedMap.put(fileName, System.currentTimeMillis());
			return true;
		}

	}

	private void checkAndSetExpirationTime() {
		/* if the lastExpValue has changed since last, clear the map */
		int t;
		if ((t = Integer.parseInt(env.getProperty(AppConstants.FILE_PROCESSING_EXPIRES_AT))) != lastExpValue) {
			lastExpValue = t;
			fileNameToLastAccessedMap.clear();
		}
	}

	@PostConstruct
	private void initLastEnvValue() {
		lastExpValue = Integer.parseInt(env.getProperty(AppConstants.FILE_PROCESSING_EXPIRES_AT));
	}
}
