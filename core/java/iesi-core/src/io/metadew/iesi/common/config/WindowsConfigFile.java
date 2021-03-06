package io.metadew.iesi.common.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import io.metadew.iesi.framework.execution.FrameworkControl;

public class WindowsConfigFile extends ConfigFile {

	public WindowsConfigFile(FrameworkControl frameworkControl, String fileName) {
		super();
		try {
			File file = new File(fileName);
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String readLine = "";
			while ((readLine = bufferedReader.readLine()) != null) {
				if (readLine.trim().toLowerCase().startsWith("set ")) {
					String innerpart = readLine.trim().substring(7);
					int delim = innerpart.indexOf("=");
					if (delim > 0) {
						String key = innerpart.substring(0, delim);
						String value = innerpart.substring(delim + 1);
						this.setProperty(key, frameworkControl.resolveConfiguration(value));
					} else {
						// Not a valid configuration
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}