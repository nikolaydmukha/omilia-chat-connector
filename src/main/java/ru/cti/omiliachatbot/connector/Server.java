package ru.cti.omiliachatbot.connector;

import ru.cti.omiliachatbot.config.AppConfig;
import ru.cti.omiliachatbot.utils.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    static AppConfig appConfig = new AppConfig();
    static Log log = new Log();

    public static void main(String[] args) {
        while (true) {
            try (ServerSocket server = new ServerSocket(appConfig.getConnectorPort())
            ) {
                //акцептим подключения
                Socket socket = server.accept();
                // канал записи в сокет
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                // канал чтения из сокета
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                Thread t = new ServerThread(socket, dos, dis, log);
                t.start();
                System.out.println(t.getName());
            } catch (IOException e) {
                log.loggingMessage(e.getMessage());
            }
        }
    }
}
