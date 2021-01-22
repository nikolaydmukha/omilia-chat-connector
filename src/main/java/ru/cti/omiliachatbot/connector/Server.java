package ru.cti.omiliachatbot.connector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(9091)) {
            Socket client = server.accept();

            System.out.print("Connection accepted.");

            // канал записи в сокет
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            System.out.println("DataOutputStream  created");

            // канал чтения из сокета
            DataInputStream dis = new DataInputStream(client.getInputStream());
            System.out.println("DataInputStream created");

            // начинаем диалог с подключенным клиентом в цикле, пока сокет не закрыт
            while (!client.isClosed()) {

                System.out.println("Server reading from channel");

                // сервер ждёт в канале чтения (inputstream) получения данных клиента
                String entry = dis.readUTF();

                // после получения данных считывает их
                System.out.println("READ from client message - " + entry);

                // и выводит в консоль
                System.out.println("Server try writing to channel - " + entry);

                // инициализация проверки условия продолжения работы с клиентом по этому сокету по кодовому слову - quit
                if (entry.equalsIgnoreCase("quit")) {
                    System.out.println("Client initialize connections suicide ...");
                    dos.flush();
                    Thread.sleep(3000);
                    break;
                }

                // если условие окончания работы не верно - продолжаем работу - отправляем эхо-ответ  обратно клиенту
                dos.writeUTF(entry.concat("-OK"));
                // освобождаем буфер сетевых сообщений (по умолчанию сообщение не сразу отправляется в сеть, а сначала
                // накапливается в специальном буфере сообщений, размер которого определяется конкретными настройками в
                // системе, а метод  - flush() отправляет сообщение не дожидаясь наполнения буфера согласно настройкам системы
                dos.flush();
                System.out.println("Server Wrote message to client.");
            }
            // если условие выхода - верно выключаем соединения
            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels.");

            // закрываем сначала каналы сокета !
            dis.close();
            dos.close();

            // потом закрываем сам сокет общения на стороне сервера!
            client.close();

            // потом закрываем сокет сервера, который создаёт сокеты общения
            // хотя при многопоточном применении его закрывать не нужно
            // для возможности поставить этот серверный сокет обратно в ожидание нового подключения

            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
