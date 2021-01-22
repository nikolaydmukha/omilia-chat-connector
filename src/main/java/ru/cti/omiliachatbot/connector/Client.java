package ru.cti.omiliachatbot.connector;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) {

        // запускаем подключение сокета по известным координатам и инициализируем приём сообщений с консоли клиента
        try (Socket socket = new Socket("127.0.0.1", 9091);
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             ) {

            System.out.println("Client connected to socket.");
            System.out.println();
            System.out.println("Client writing channel = oos & reading channel = ois initialized.");

            // проверяем живой ли канал и работаем если живой
            while (!socket.isOutputShutdown()) {

                // ждём в консоли клиента на предмет появления в ней данных
                if (br.ready()) {

                    // данные появились - работаем
                    System.out.println("Client start writing in channel...");
                    Thread.sleep(1000);
                    String clientCommand = br.readLine();

                    // пишем данные с консоли в канал сокета для сервера
                    dos.writeUTF(clientCommand);
                    dos.flush();
                    System.out.println("Client sent message " + clientCommand + " to server.");
                    Thread.sleep(2000);
                    // ждём чтобы сервер успел прочесть сообщение из сокета и ответить

                    // проверяем условие выхода из соединения
                    if (clientCommand.equalsIgnoreCase("quit")) {
                        // если условие выхода достигнуто разъединяемся
                        System.out.println("Client kill connections");
                        Thread.sleep(2000);
                        // смотрим что нам ответил сервер напоследок перед закрытием ресурсов
                        if (dis.read() > -1) {
                            System.out.println("reading...");
                            String in = dis.readUTF();
                            System.out.println(in);
                        }
                        // после предварительных приготовлений выходим из цикла записи чтения
                        break;
                    }

                    // если условие разъединения не достигнуто продолжаем работу
                    System.out.println("Client sent message & start waiting for data from server...");
                    Thread.sleep(2000);
                    String name = null;
                    // проверяем, что нам ответит сервер на сообщение(за предоставленное ему время в паузе он должен был успеть ответить)
                    if (dis.available() > -1) {
                        do {
                            name = dis.readUTF();
                            System.out.println(name);
                        } while (dis.available() > 0);
                    }
                }
            }
            // на выходе из цикла общения закрываем свои ресурсы
            System.out.println("Closing connections & channels on clientSide - DONE.");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
