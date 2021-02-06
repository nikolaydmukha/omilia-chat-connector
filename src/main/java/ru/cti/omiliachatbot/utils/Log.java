package ru.cti.omiliachatbot.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    public void loggingMessage(String mes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy ");
        try (FileWriter writer = new FileWriter(getFileName(), true)) {
            writer.write(LocalDateTime.now().format(formatter) + " " + mes);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getFileName() {
        String path = "src/main/java/ru/cti/log/log.txt";
        return path;
    }

}
