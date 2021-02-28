package ru.cti.omiliachatbot.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private Properties props = new Properties();

    public AppConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("app.properties")) {
            props.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error during loading app.properties! " + ex.getMessage());
        }
    }

    public int getConnectorPort() {
        return Integer.parseInt(getParam("server.port"));
    }

    public String getConnectorIP() {
        return getParam("server.ip");
    }

    public String getOmiliaConnectionURL() {
        return getParam("omiliaAPI.connection.url");
    }

    public String getOmiliaDialogURL() {
        return getParam("omiliaAPI.dialog.url");
    }

    public String getOmiliaAppName() {
        return getParam("omiliaAPI.appname");
    }

    private String getParam(String param) {
        return props.getProperty(param);
    }


}