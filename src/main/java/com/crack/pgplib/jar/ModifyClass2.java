package com.crack.pgplib.jar;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.gjt.jclasslib.io.ClassFileWriter;
import org.gjt.jclasslib.structures.CPInfo;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.constants.ConstantLongInfo;
import org.gjt.jclasslib.structures.constants.ConstantStringInfo;
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class ModifyClass2 {

	private String readJar = "pgplib-3.1.1.jar"; //原始jar
	private String modifyJar = "pgplib-3.1.1.crack.3999y.jar"; //修改後jar
	private String modifyClassName = "PGPLib.class"; // 要修改的class
	private String modifyClassPackage = "com/didisoft/pgp"; // 要修改class的package

	//---------------------------------------------------------------

	private String relativeTarget = "/modifyjar";
	private String readJarPath;
	private String modifyJarPath;
	private String baseDir;
	private String tmp;
	private String modifyClassFullPath;
	private File readJarFile;
	private File modifyJarFile;

	public void init() throws Exception {

		this.baseDir = System.getProperty("user.dir");
		this.readJarPath = this.baseDir + relativeTarget + "/" + this.readJar;
		this.modifyJarPath = this.baseDir + relativeTarget + "/" + this.modifyJar;
		this.tmp = this.baseDir + "/tmp";
		this.clean();
		// create tmp folder
		FileUtils.forceMkdir(new File(tmp));
		this.readJarFile = new File(readJarPath);
		this.modifyJarFile = new File(modifyJarPath);

		this.modifyClassFullPath = this.tmp + "/" + this.modifyClassPackage + "/" + this.modifyClassName;

	}

	@SuppressWarnings("unchecked")
	public void unZipClass() throws Exception {
		ZipFile zipFile = new ZipFile(readJarFile);
		List<FileHeader> fileHeaders = zipFile.getFileHeaders();
		for (FileHeader fileHeader : fileHeaders) {
			String clazz = modifyClassPackage + "/" + modifyClassName;
			if (clazz.equals(fileHeader.getFileName())) {
				zipFile.extractFile(fileHeader, tmp);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void zipClass() throws Exception {
		FileUtils.copyFile(readJarFile, modifyJarFile);
		ZipFile zipFile = new ZipFile(modifyJarFile);
		ArrayList filesToAdd = new ArrayList();
		filesToAdd.add(new File(this.modifyClassFullPath));
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		parameters.setRootFolderInZip(this.modifyClassPackage);

		zipFile.addFiles(filesToAdd, parameters);
	}

	public void clean() throws Exception {
		FileUtils.deleteDirectory(new File(tmp));
	}

	/**
	 *
	 * <pre>
	 * Method Name : process
	 * Description : process
	 * </pre>
	 *
	 * @param lineNumber
	 * @param info
	 * @throws Exception
	 */
	public void process(Map<Integer, String> changers) throws Exception {

		String filePath = this.modifyClassFullPath;
		FileInputStream fis = new FileInputStream(filePath);

		DataInput di = new DataInputStream(fis);
		ClassFile cf = new ClassFile();
		cf.read(di);
		CPInfo[] infos = cf.getConstantPool();

		int count = infos.length;
		for (int i = 0; i < count; i++) {
			if (infos[i] != null) {

				Iterator<Entry<Integer, String>> iterator = changers.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<Integer, String> entry = iterator.next();
					int lineNumber = entry.getKey();
					String info = entry.getValue();

					if (i == lineNumber) {
						System.out.println("[LOG]lineNumber=" + lineNumber + "  :  info=" + info + "---------");
					}
					if (i == lineNumber) {
						if (infos[i] instanceof ConstantStringInfo) {
						} else if (infos[i] instanceof ConstantLongInfo) {
							ConstantLongInfo uInfo = (ConstantLongInfo) infos[i];

							Long oldInfo = uInfo.getLong();



							System.out.println("[LOG][getHighBytes]" + uInfo.getHighBytes() + " -> [getLowBytes]" + uInfo.getLowBytes());

							//uInfo.setLong(Long.parseLong(info));
							//uInfo.setHighBytes(Integer.parseInt("1"));
							uInfo.setLowBytes(Integer.parseInt(info));
							System.out.println("[LOG][getHighBytes]" + uInfo.getHighBytes() + " -> [getLowBytes]" + uInfo.getLowBytes());

							infos[i] = uInfo;

							System.out.println("[LOG][ConstantLongInfo]" + oldInfo + " -> " + info);

						} else if (infos[i] instanceof ConstantUtf8Info) {
							ConstantUtf8Info uInfo = (ConstantUtf8Info) infos[i];

							String oldInfo = uInfo.getString();
							uInfo.setString(info);
							infos[i] = uInfo;

							System.out.println("[LOG][ConstantUtf8Info]" + oldInfo + " -> " + info);

						} else {
						}

					}
					if (i == lineNumber) {
						System.out.println("[LOG]---------------------------------------");
					}

				}
				CPInfo cpInfo = infos[i];
				System.out.println(i + " [" + infos[i].getVerbose() + "]" + " 							getTagVerbose=> " + infos[i].getTagVerbose()
						+ " CPInfo --> " + cpInfo.getClass().getName() + " " + ToStringBuilder.reflectionToString(infos[i]));

			}
		}
		cf.setConstantPool(infos);
		fis.close();
		File f = new File(filePath);
		ClassFileWriter.writeToFile(f, cf);
	}

	public static void main(String[] args) throws Exception {
		ModifyClass2 modifyClass = new ModifyClass2();
		modifyClass.init();
		modifyClass.unZipClass();

		Map<Integer, String> changers = new HashMap<Integer, String>();
		changers.put(257, "23552");
		changers.put(1254, "05/14/3999");

		modifyClass.process(changers);

		modifyClass.zipClass();
		modifyClass.clean();

	}
}