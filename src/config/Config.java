package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {
	
	public static Properties getPropValues() throws IOException {
		InputStream inputStream = null;
		Properties prop = new Properties();
		try {
			String propFileName = "src/config/app.config";
			inputStream = new FileInputStream(propFileName);
				prop.load(inputStream);
			return prop;
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
		return null;
	}
}
