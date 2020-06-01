package sample.Windows;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.javafx.tk.Toolkit;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import sample.*;


public class ApplController implements Initializable {

    @FXML
    private Label current_user_name_lbl;

    @FXML
    private Button logout_btn;

    @FXML
    private Button send_btn;

    @FXML
    private ListView<String> users_listview;

    @FXML
    private ListView<String> chat_listview;

    @FXML
    private TextField send_message_field;

    @FXML
    private TextField find_user_login;

    @FXML
    private Button find_user_btn;

    private static final Logger log = Logger.getLogger(ApplController.class);


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        logout_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                LogOut();
            }
        });

        find_user_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FindUser();
            }
        });

        send_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Send();
            }
        });

        users_listview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                UsersListViewChanged(newValue);
            }
        });

        BindThreadCheckNewMessages();

        LoadUserChats();

        SetCurrentUserNameToWindow();
    }

    private void UsersListViewChanged(String newValue){
        UpdateChatForUser(newValue);
    }

    private void Send(){
        if (users_listview.getSelectionModel().isEmpty() == true){
            log.info("Send function call : user is empty");
            return;
        }
        else{
            String selected_user = users_listview.getSelectionModel().getSelectedItem();
            if(send_message_field.getText().length() > 0){
                boolean isExistsOnlyOfSpace = true;

                for(Character symbol : send_message_field.getText().toCharArray()){
                    if(!symbol.equals(' ')){
                        isExistsOnlyOfSpace = false;
                        break;
                    }
                }

                if(isExistsOnlyOfSpace){
                    log.info("entered message text consist only of space");
                    return;
                }else{
                    try {
                        log.info("Staring send 'sendMessage' to server");
                        StringBuffer url = new StringBuffer();
                        url.append("http://localhost:8080/sendMessage?senderLogin=");
                        url.append(CurrentUserInfo.getCurrentUser().getLogin());
                        url.append("&senderKey=");
                        url.append(CurrentUserInfo.getCurrentKey());
                        url.append("&receiverLogin=");
                        url.append(users_listview.getSelectionModel().getSelectedItem());
                        url.append("&message=");
                        String mesg = send_message_field.getText().replaceAll(" " , "%20");
                        url.append(mesg);

                        URL obj = new URL(url.toString());
                        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

                        connection.setRequestMethod("GET");

                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        log.info("request 'sendMessage' sended" );

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        DateFormat formatter = new SimpleDateFormat("HH:mm");
                        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                        String dateFormatted = formatter.format(System.currentTimeMillis());
                        chat_listview.getItems().add(dateFormatted + " " + CurrentUserInfo.getCurrentUser().getLogin() + " : " + send_message_field.getText());

                        int index = chat_listview.getItems().size() - 1;
                        chat_listview.scrollTo(index);

                        send_message_field.setText("");

                    }catch (Exception e){
                        log.warn(e.getMessage());
                        System.out.println(e.getMessage());
                    }

                }

            }else{
                return;
            }
            log.info("Entered message text less than 0 symbols");
        }
    }

    private void FindUser(){
        if(find_user_login.getText().length() == 0){
            return;
        }

        try {
            log.info("start send 'findUser' to server");
            StringBuffer url = new StringBuffer();
            url.append("http://localhost:8080/isUserExists?senderLogin=");
            url.append(CurrentUserInfo.getCurrentUser().getLogin());
            url.append("&senderKey=");
            url.append(CurrentUserInfo.getCurrentKey());
            url.append("&findUserLogin=");
            url.append(find_user_login.getText());

            URL obj = new URL(url.toString());
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            log.info("request sended");
            Gson gson = new Gson();

            AuthorizationResponse response1 = gson.fromJson(response.toString(), AuthorizationResponse.class);

            if(response1.getResponseID() == 0){
                log.info("response 0 from server");
                users_listview.getItems().add(find_user_login.getText());
                users_listview.refresh();
            }else{
                log.warn("response not 0 from server : " + response1.getResponseMessage());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("User not found");
                alert.show();
            }
        }catch (Exception e){
            log.warn(e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private void SetCurrentUserNameToWindow(){
        String text = "Вы вошли под логином : " + CurrentUserInfo.getCurrentUser().getLogin();
        current_user_name_lbl.setText(text);
    }

    private void BindThreadCheckNewMessages(){
        Task task = new Task() {
            @Override
            protected Void call() throws InterruptedException {
                do {
                    try {
                        StringBuffer url = new StringBuffer();
                        url.append("http://localhost:8080/haveNewMessages?senderLogin=");
                        url.append(CurrentUserInfo.getCurrentUser().getLogin());
                        url.append("&senderKey=");
                        url.append(CurrentUserInfo.getCurrentKey());

                        URL obj = new URL(url.toString());
                        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

                        connection.setRequestMethod("GET");

                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        Gson gson = new Gson();

                        AuthorizationResponse response1 = gson.fromJson(response.toString(), AuthorizationResponse.class);


                        if (response1.getResponseID() == 0) {
                            Type listType = new TypeToken<Set<String>>() {
                            }.getType();
                            Set<String> users = gson.fromJson(response1.getResponseMessage(), listType);
                        }

                        Type listType = new TypeToken<ArrayList<String>>(){}.getType();
                        ArrayList<String> usersChatUpdated = gson.fromJson(response1.getResponseMessage() , listType);

                        if(usersChatUpdated.size() != 0) {

                            ObservableList<String> arrusers = users_listview.getItems();

                            usersChatUpdated.forEach(item -> {
                                if (CurrentUserInfo.currentChat.equals(item)) {
                                    Platform.runLater(() -> {
                                        UpdateChatForUser(item);
                                    });
                                }
                            });

                            // TODO : доделать обновление для других пользователей
                        }
                        Thread.sleep(2000);
                    }
                    catch (ConnectException exc){
                        CurrentUserInfo.ourThread.destroy();
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }while(true);
            }
        };

        log.info("Thread bound");

        CurrentUserInfo.ourThread = new Thread(task);
        CurrentUserInfo.ourThread.start();
        log.info("Thread started");
    }

    private void UpdateChatForUser(String login){
        try {
            log.info("sending 'updatechatforuser' request to server");
            StringBuffer url = new StringBuffer();
            url.append("http://localhost:8080/getChat?senderLogin=");
            url.append(CurrentUserInfo.getCurrentUser().getLogin());
            url.append("&senderKey=");
            url.append(CurrentUserInfo.getCurrentKey());
            url.append("&companionLogin=");
            url.append(login);

            URL obj = new URL(url.toString());
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            log.info("request was sent");
            Gson gson = new Gson();

            AuthorizationResponse response1 = gson.fromJson(response.toString(), AuthorizationResponse.class);

            Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
            ArrayList<Message> messages = gson.fromJson(response1.getResponseMessage() , listType);


            chat_listview.getItems().clear();


            for(Message msg : messages){
                DateFormat formatter = new SimpleDateFormat("HH:mm");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String dateFormatted = formatter.format(msg.getDate().getTime());
                chat_listview.getItems().add(dateFormatted + " " + msg.getSender() + " : " + msg.getMessage());
            }
            chat_listview.refresh();
            CurrentUserInfo.currentChat = login;

            int index = chat_listview.getItems().size() - 1;
            chat_listview.scrollTo(index);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void LoadUserChats(){
        try {
            log.info("request 'loaduserchat' configuration");
            StringBuffer url = new StringBuffer();
            url.append("http://localhost:8080/GetUserChats?login=");
            url.append(CurrentUserInfo.getCurrentUser().getLogin());
            url.append("&key=");
            url.append(CurrentUserInfo.getCurrentKey());

            URL obj = new URL(url.toString());
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            log.info("request was sent");
            Gson gson = new Gson();

            AuthorizationResponse response1 = gson.fromJson(response.toString(), AuthorizationResponse.class);

            Type listType = new TypeToken<Set<String>>(){}.getType();
            Set<String> currentUsersChat = gson.fromJson(response1.getResponseMessage() , listType);
            currentUsersChat.remove(CurrentUserInfo.getCurrentUser().getLogin());
            users_listview.getItems().addAll(currentUsersChat);
            users_listview.refresh();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void LogOut(){
        log.info("logout command");
        CurrentUserInfo.LogOut();
        CurrentUserInfo.ourThread.stop();
        log.info("thread was stopped");

        Stage stageToClose = (Stage) logout_btn.getScene().getWindow();
        stageToClose.close();

        Stage mainStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/logIn.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainStage.setScene(new Scene(root, 620, 680));
        mainStage.show();
        log.info("opened login.fxml");
    }
}



