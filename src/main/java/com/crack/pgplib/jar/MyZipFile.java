package com.crack.pgplib.jar;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class MyZipFile {

	public void zipAll(String targetJarFile, String tmp) throws Exception {
		ZipFile zipFile = new ZipFile(targetJarFile);
		// Initiate Zip Parameters which define various properties such
		// as compression method, etc.
		ZipParameters parameters = new ZipParameters();
		// set compression method to store compression
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		// Set the compression level
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

		parameters.setIncludeRootFolder(false);
		// Add folder to the zip file
		zipFile.addFolder(tmp, parameters);
	}

	public void unZipAll(String targetJarFile, String tmp) throws Exception {
		ZipFile zipFile = new ZipFile(targetJarFile);
		zipFile.extractAll(tmp);
	}

}
