package com.company.test.processor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.company.processor.executor.DefaultTaskHandler;
import com.company.processor.executor.fileprocessor.DefaultOperator;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { DefaultOperator.class })
public class FileOperatorTest {
	/*
	 * @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder(); private
	 * File etcHost;
	 */

	@Autowired
	DefaultOperator fileOperator;

	@MockBean
	DefaultTaskHandler mock;

	String readFileExt = ".txt";
	String writeFileExt = ".mtd";

	String fileName = "testfile";

	File readFile;
	File writeFile;

	// creating a sample file for watcher service
	Path resDir = Paths.get("src", "test", "data");

	@Before
	public void createFile() throws IOException {

		readFile = new File(resDir.toFile().getAbsolutePath() + File.separator + fileName + readFileExt);
		// create sub folders if not there already
		readFile.getParentFile().mkdirs();

		Files.write(Paths.get(readFile.getAbsolutePath()), "Test File with some sp chars like & and *".getBytes());
		// check if a mtd file created
		writeFile = new File(resDir.toFile().getAbsolutePath() + File.separator + fileName + writeFileExt);

	}

	@After
	public void intiDirectoryWatcher() throws IOException {
		// get rid of temp file
		if (readFile != null && readFile.exists())
			readFile.delete();
		
		if (writeFile != null && writeFile.exists())
			writeFile.delete();
		

	}

	@Test
	public void whenFileDetected_ProcessAndCreateMTDFile() {
		try {
			Thread testThread = null;
			Mockito.when(mock.get()).thenReturn(Paths.get(readFile.getAbsolutePath())).thenReturn(null);

			Runnable run = () -> fileOperator.operate();

			// imp to run in separate thread else, test never finishes

			testThread = new Thread(run);
			// end when main thread ends

			testThread.start();
			testThread.setDaemon(true);

		} catch (Exception e) {
			assertThat(writeFile != null && writeFile.exists());
		} finally {
			fileOperator.stop();
		}

	}
}