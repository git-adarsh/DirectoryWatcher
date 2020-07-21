package com.company.processor.executor.fileprocessor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface IOperations {

	Set<Character> VOWELS = new HashSet<>(Arrays.asList('a', 'e', 'i', 'o', 'u'));
	Set<Character> SPECIAL_CHARACTERS = new HashSet<>(Arrays.asList('@', '#', '$', '*'));

	void operate();
	
	void stop();
}
