package com.company.processor.resources;

import lombok.Builder;

@Builder
public class OperationResult {
	private long totalWords;
	private long vowels;
	private long specialCharacters;
	
	@Override
	public String toString() {
		return "[totalWords=" + totalWords + ", vowels=" + vowels + ", specialCharacters="
				+ specialCharacters + "]";
	}
}
