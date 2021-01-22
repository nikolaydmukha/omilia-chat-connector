package ru.cti.omiliachatbot.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetURL {

    public GetURL() {
    }

    public String getURL(String urlIdentificator, String propFileName) throws IOException {
        FileInputStream fis = null;
        Properties property = new Properties();
        String url = null;
        File f = new File("message.properties");
//        System.out.println(f.getAbsolutePath());
        //for CONSOLE
        try {
            fis = new FileInputStream("src/main/resources/app.properties" );
        } catch (FileNotFoundException fileNotFoundException) {
            url = fileNotFoundException.getMessage();
            return url;
        }
        //for JAR
//        String classpath = System.getProperty("java.class.path");
//        System.out.println(("CLASSPATH: " + classpath));
//        try (InputStream is = GetURL.class.getClassLoader().getResourceAsStream("app.properties")) {
//            Properties properties = new Properties();
//            properties.load(is);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            property.load(fis);
            if (property.containsKey(urlIdentificator))
                return property.getProperty(urlIdentificator);
            else
                return "Key " + urlIdentificator + " not found in " + propFileName;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            url = ioException.getMessage();
            return url;
        }
//        return url;
    }
}
