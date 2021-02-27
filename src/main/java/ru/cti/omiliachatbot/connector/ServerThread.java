package ru.cti.omiliachatbot.connector;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import ru.cti.omiliachatbot.actions.Request;
import ru.cti.omiliachatbot.config.AppConfig;
import ru.cti.omiliachatbot.utils.Log;
import ru.cti.omiliachatbot.utils.Prompt;
import ru.cti.omiliachatbot.utils.PromptMapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread {

    private Socket socket;
    static AppConfig appConfig = new AppConfig();
    static String connectionURL = appConfig.getOmiliaConnectionURL();
    static String dialogURL = appConfig.getOmiliaDialogURL();
    static JsonObject response;
    static Request request = new Request();
    static Log log = new Log();

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @SneakyThrows
    @Override
    public void run() {
        try {
            // канал записи в сокет
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            // канал чтения из сокета
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            String utterance = null;
            String dialogId = null;
            //Сделать запрос "Start new dialog"
            //Вывести на экран сообщения бота после Start new dialog - приветствие
            response = request.makeRequest(connectionURL, utterance, dialogId);
            dialogId = getDialogId(response);
            //Пересылаем приветственный ролик
            ArrayList<String> welcomePrompts = showBotMessages(response, dialogId);
            log.loggingMessage("Connection accepted. DialogID: " + dialogId + "Welcome message: >>> " + welcomePrompts.get(2) + "\n");
            dos.writeUTF(welcomePrompts.get(0));
            dos.flush();

            // начинаем диалог с подключенным клиентом в цикле, пока сокет не закрыт
            while (!socket.isClosed()) {
                // ждем получения данных клиента
                String entry = dis.readUTF();
                // логгируем сообщение клиента
                log.loggingMessage("DialogID: " + dialogId + " User utterance >>>" + entry + "\n");
                response = request.makeRequest(dialogURL, entry, dialogId);
                ArrayList<String> answer = showBotMessages(response, dialogId);
                log.loggingMessage("DialogID: " + dialogId + " Omilia answer >>> " + answer.get(2) + "\n");
                // если условие окончания работы не верно - продолжаем работу - отправляем эхо-ответ  обратно клиенту
                if (answer.get(0).equals("TRANSFER")) {
                    dos.writeUTF(answer.get(0));
                    dos.flush();
                    System.exit(11);
                }
                dos.writeUTF(answer.get(0));
                dos.flush();
            }
            // если условие выхода - верно выключаем соединения
            // закрываем сначала каналы сокета !
            dis.close();
            dos.close();
            // потом закрываем сам сокет общения на стороне сервера!
            socket.close();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private static String getDialogId(JsonObject response) {
        return response.get("dialogId").toString();
    }

    private static ArrayList<String> showBotMessages(JsonObject response, String dialogId) throws Exception {

        ArrayList<String> parsedAnswer = new ArrayList<>();
        JsonElement actionType = response.getAsJsonObject("action");
        ArrayList<Prompt> prompts = getPromptsFromOmiliaReply(response);
//        System.out.println(getAnswerType(actionType));
        ArrayList<Prompt> promptsAfterPing = new ArrayList<>();
        //если Omilia сначала говорит announce, а потом должна сделать ask, но без "пинка" молчит
        if (getAnswerType(actionType).equals("ANNOUNCEMENT")) {
            try {
                promptsAfterPing = checkForASK(dialogId);
                System.out.println((promptsAfterPing.get(0).getContent()));
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        prompts.addAll(promptsAfterPing);
        parsedAnswer.add(preparePromptsOutput(prompts));
        parsedAnswer.add(getAnswerType(actionType));
        parsedAnswer.add(actionType.toString());
        return parsedAnswer;
    }

    private static ArrayList<Prompt> getPromptsFromOmiliaReply(JsonObject response) throws IOException {
        ArrayList<Prompt> promptList = new ArrayList<>();
        ArrayList<Prompt> prompts = PromptMapper.convert(response.getAsJsonObject("action").getAsJsonObject("message").getAsJsonArray("prompts").toString());
        prompts.stream().forEach(item -> promptList.add(item));
        return promptList;
    }

    private static String preparePromptsOutput(ArrayList<Prompt> prompts) {
        StringBuilder output = new StringBuilder();
        for (Prompt prompt : prompts) {
            output.append(prompt.getContent());
        }
        return output.toString();
    }

    private static String getAnswerType(JsonElement answerType) {
        return answerType.getAsJsonObject().get("type").toString().replaceAll("\"", "");
    }

    private static ArrayList<Prompt> checkForASK(String dialogId) throws Exception {
        response = request.makeRequest(dialogURL, "[noinput]", dialogId);
        return getPromptsFromOmiliaReply(response);
    }
}
