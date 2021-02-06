package ru.cti.omiliachatbot;

import ru.cti.omiliachatbot.config.AppConfig;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    static AppConfig appConfig = new AppConfig();

    public static void main(String[] args) {
        proxyConnect();
    }

    private static void proxyConnect() {

        // запускаем подключение сокета по известным координатам и инициализируем приём сообщений с консоли клиента
        try (Socket socket = new Socket(appConfig.getConnectorIP(), appConfig.getConnectorPort());
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream());
        ) {
            // проверяем, что нам ответит сервер на сообщение
            printMessage(dis);
            // проверяем живой ли канал и работаем если живой
            while (!socket.isOutputShutdown()) {
                // ждём в консоли клиента на предмет появления в ней данных
                if (br.ready()) {
                    // данные появились - работаем
                    String clientCommand = br.readLine();
                    // пишем данные с консоли в канал сокета для сервера
                    dos.writeUTF(clientCommand);
                    dos.flush();
                    // ждём чтобы сервер успел прочесть сообщение из сокета и ответить
                    // проверяем условие выхода из соединения
                    if (clientCommand.equalsIgnoreCase("quit")) {
                        // если условие выхода достигнуто разъединяемся
                        // смотрим что нам ответил сервер напоследок перед закрытием ресурсов
                        printMessage(dis);
                        // после предварительных приготовлений выходим из цикла записи чтения
                        break;
                    }
                    // если условие разъединения не достигнуто продолжаем работу
                    // проверяем, что нам ответит сервер на сообщение(за предоставленное ему время в паузе он должен был успеть ответить)
                    printMessage(dis);
                }
            }
            // на выходе из цикла общения закрываем свои ресурсы
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printMessage(DataInputStream dis) throws IOException {
        String text = null;
        if (dis.available() > -1) {
            do {
                text = dis.readUTF();
                System.out.println(text);
            } while (dis.available() > 0);
        }
    }

}
