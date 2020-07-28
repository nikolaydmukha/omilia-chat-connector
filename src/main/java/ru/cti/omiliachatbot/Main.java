package main.java.ru.cti.omiliachatbot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import main.java.ru.cti.omiliachatbot.actions.GetURL;
import main.java.ru.cti.omiliachatbot.actions.Request;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        final String CONNECTION_URL = "connection_url";
        final String PROCESS_URL = "process_url";
        final String PROPERTIES_FILE = "app.properties";
        String utterance = null;
        Scanner scanner = new Scanner(System.in);
        String dialogId = null;

        //Прочитать app.properties для получения connection_url
        GetURL appProperties = new GetURL();
        String connectionURL = appProperties.getURL(CONNECTION_URL, PROPERTIES_FILE);
        String processURL = appProperties.getURL(PROCESS_URL, PROPERTIES_FILE);

        //Сделать запрос "Start new dialog"
        Request request = new Request();

        //Вывести на экран сообщения бота после Start new dialog
        JsonObject response = request.makeRequest(connectionURL, utterance, dialogId);
        showBotMessages(response);
        dialogId = getDialogId(response);

//        В цикле общаться с ботом
        while (true) {
            utterance = scanner.nextLine();
            response = request.makeRequest(processURL, utterance, dialogId);
            showBotMessages(response);
        }
    }

    private static String getDialogId(JsonObject response) {
        return response.get("dialogId").toString();
    }

    private static void showBotMessages(JsonObject response) throws IOException {
        String concatMessage = "";
        JsonElement actionType =  response.getAsJsonObject("action");
        System.out.println(actionType.getAsJsonObject().get("type"));
        JsonArray messagesJSONArray = response.getAsJsonObject("action").getAsJsonObject("message").getAsJsonArray("prompts");
        for (int i = 0; i < messagesJSONArray.size(); i++) {
            JsonObject messagePlay = (JsonObject) messagesJSONArray.get(i);
            concatMessage += messagePlay.get("content").toString().replaceAll("\"", "") + " ";
        }
        System.out.println(concatMessage);
        if (actionType.getAsJsonObject().get("type").toString().replaceAll("\"", "").equals("TRANSFER")) {
                        System.exit(11);
        }
    }
}
