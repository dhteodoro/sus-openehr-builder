package br.uerj.lampada.openehr.susbuilder.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

public class FileHandler {

	private String error;
	private String file;

	public void appendString(String str) {
		file += str;
	}

	public void copyDirectory(File srcPath, File dstPath) throws IOException {
		if (srcPath.isDirectory()) {
			if (!dstPath.exists()) {
				dstPath.mkdir();
			}

			String files[] = srcPath.list();
			for (int i = 0; i < files.length; i++) {
				copyDirectory(new File(srcPath, files[i]), new File(dstPath,
						files[i]));
			}
		} else {
			if (!srcPath.exists()) {
				setError(srcPath.getAbsolutePath() + " does not exist.");
			} else {
				InputStream in = new FileInputStream(srcPath);
				OutputStream out = new FileOutputStream(dstPath);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		}
	}

	public void copyDirectory(String source, String destinantion)
			throws IOException {
		File srcPath = new File(source);
		File dstPath = new File(destinantion);
		copyDirectory(srcPath, dstPath);
	}

	public void copyFile(String outputfile) throws FileNotFoundException,
			IOException {
		File aFile = new File(outputfile);
		aFile.createNewFile();

		if (!aFile.isFile()) {
			throw new IllegalArgumentException("Should not be a directory: "
					+ aFile);
		}
		if (!aFile.canWrite()) {
			throw new IllegalArgumentException("File cannot be written: "
					+ aFile);
		}

		Writer output = new BufferedWriter(new FileWriter(aFile));
		try {
			output.write(file);
		} catch (IOException e) {
			setError("IOException Message: " + e.getMessage());
		} finally {
			output.close();
		}
	}

	public void createDirectory(String dir) {
		File srcPath = new File(dir);
		if (!srcPath.exists()) {
			srcPath.mkdirs();
		}
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	public String getFile() {
		return file;
	}

	public void load(String filePath) throws FileNotFoundException, IOException {
		BufferedReader reader = read(filePath);

		StringBuffer fileData = new StringBuffer(1000);
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		file = fileData.toString();
	}

	public BufferedReader read(String filePath) throws FileNotFoundException,
			IOException {
		if (filePath == null)
			throw new IllegalArgumentException("null input: filePath");

		File f = new File(".");
		String h = "";
		try {
			h = f.getCanonicalPath();
		} catch (IOException e) {
			setError("IOException Message: " + e.getMessage());
		}

		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		return reader;
	}

	public void removeTag(String bind) {
		String regEx = bind + "\"><value>:\\w+</value>";
		file = file.replaceAll(regEx, bind + "\"><null/>");
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setError(String _error) {
		error = _error;
	}

	public void setFile(String _file) {
		file = _file;
	}

	public void substituteString(String bind, String value) {
		file = file.replaceAll(bind, value);
	}

}

// class CopyFile{
// private static String error;
// public static void copyFile(String srFile, String dtFile) {
// try{
// File f1 = new File(srFile);
// File f2 = new File(dtFile);
// InputStream in = new FileInputStream(f1);
//
// //For Append the file.
// // OutputStream out = new FileOutputStream(f2,true);
//
// //For Overwrite the file.
// OutputStream out = new FileOutputStream(f2);
//
// byte[] buf = new byte[1024];
// int len;
// while ((len = in.read(buf)) > 0){
// out.write(buf, 0, len);
// }
// in.close();
// out.close();
// System.out.println("File copied.");
// }
// catch(FileNotFoundException e){
// setError(e.getMessage());
// }
// catch(IOException e){
// setError(e.getMessage());
// }
// }
// /**
// * @param error the error to set
// */
// public static void setError(String error) {
// CopyFile.error = error;
// }
// /**
// * @return the error
// */
// public static String getError() {
// return error;
// }
// }