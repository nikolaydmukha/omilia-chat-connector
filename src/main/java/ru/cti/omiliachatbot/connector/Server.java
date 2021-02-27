package ru.cti.omiliachatbot.connector;

import com.google.gson.JsonObject;
import ru.cti.omiliachatbot.actions.Request;
import ru.cti.omiliachatbot.config.AppConfig;
import ru.cti.omiliachatbot.utils.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    static AppConfig appConfig = new AppConfig();

    public static void main(String[] args) throws Exception {
        while (true) {
            try (ServerSocket server = new ServerSocket(appConfig.getConnectorPort())) {
                Socket socket = server.accept();

                Thread t = new ServerThread(socket);
                // Invoking the start() method
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
