package io.metadew.iesi.connection.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import io.metadew.iesi.framework.execution.FrameworkControl;

public final class FileTools {

	public static void delete(String fileName) {	
		File f = new File(fileName);
		try {
			if (f.exists()) {
				if (!f.delete()) {
					throw new RuntimeException("Unable to delete file " + f.getAbsolutePath());
				};
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean exists(String fileName) {
		File f = new File(fileName);
		return f.exists();
	}

	public static void appendToFile(String fileName, String header, String record) {
		try {
			PrintWriter out = null;
			File f = new File(fileName);
			if (!f.exists()) {
				out = new PrintWriter(new BufferedWriter(new java.io.FileWriter(fileName, true)));
				if (!header.equals(""))
					out.println(header);
			} else {
				out = new PrintWriter(new BufferedWriter(new java.io.FileWriter(fileName, true)));
			}
			out.println(record);
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printToFile(ResultSet rs, String fileName, String delimiter) throws IOException {
		try {
			OutputStream out = new FileOutputStream(fileName);
			PrintStream ps = new PrintStream(out);
			String temp = "";

			// Get result set meta data
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();

			// Get the column names; column indices start from 1
			for (int i = 1; i < cols + 1; i++) {
				ps.print(rsmd.getColumnName(i));
				if (i != cols) {
					ps.print(delimiter);
				}
			}
			ps.println();

			int rsType = rs.getType();
			if (rsType != java.sql.ResultSet.TYPE_FORWARD_ONLY) {
				rs.beforeFirst();
			}
			while (rs.next()) {
				for (int i = 1; i < cols + 1; i++) {
					temp = rs.getString(i);
					if (temp != null && !temp.isEmpty()) {
						// Remove the CRLF from the db to allow importing into
						// an excel file
						temp = temp.replaceAll("[\n\r]", "");
						// Remove the delimiter from the string to allow
						// importing into an excel file
						temp = temp.replaceAll("[" + delimiter + "]", "");
						ps.print(temp);
					}
					if (i != cols) {
						ps.print(delimiter);
					}
				}
				ps.println();
			}
			ps.close();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// Convert to inputStream
	public static InputStream getInputStream(File file) {
		String output = "";
		try {
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String readLine = "";
			while ((readLine = bufferedReader.readLine()) != null) {
				output += readLine;
				output += "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));
	}

	
	// Convert to inputsteeam and resolve configuration
	public static InputStream convertToInputStream(File file, FrameworkControl frameworkControl) {
		String output = "";
		try {
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String readLine = "";
			while ((readLine = bufferedReader.readLine()) != null) {
				output += frameworkControl.resolveConfiguration(readLine);
				output += "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));
	}

	public static InputStream convertToInputStream(String input, FrameworkControl frameworkControl) {
		String output = "";
		try {
			Reader inputString = new StringReader(input);
			BufferedReader bufferedReader = new BufferedReader(inputString);
			String readLine = "";
			while ((readLine = bufferedReader.readLine()) != null) {
				output += frameworkControl.resolveConfiguration(readLine);
				output += "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));
	}
	// Copy operations
	@SuppressWarnings("resource")
	public static void copyFromFileToFile(String sourceFile, String targetFile) {
		File src_file_nm = new File(sourceFile);
		File tgt_file_nm = new File(targetFile);
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(src_file_nm).getChannel();
			outputChannel = new FileOutputStream(tgt_file_nm).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				inputChannel.close();
				outputChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
    public static String getFileExtension(File file) {
        String extension = "";
 
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf(".") + 1);
            }
        } catch (Exception e) {
            extension = "";
        }
 
        return extension;
 
    }

}