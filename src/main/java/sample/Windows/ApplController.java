package sample.Windows;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.*;
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
import sample.AuthorizationResponse;
import sample.CurrentUserInfo;
import sample.Main;
import sample.User;


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
    private ListView<?> chat_listview;

    @FXML
    private TextField send_message_field;

    @FXML
    private TextField find_user_login;

    @FXML
    private Button find_user_btn;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        logout_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                LogOut();
            }
        });

        find_user_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if(find_user_login.getText().length() == 0){
                    return;
                }

                try {
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
                    Gson gson = new Gson();

                    AuthorizationResponse response1 = gson.fromJson(response.toString(), AuthorizationResponse.class);

                    if(response1.getResponseID() == 0){
                        users_listview.getItems().add(find_user_login.getText());
                        users_listview.refresh();
                    }else{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("User not found");
                        alert.show();
                    }
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        });

        send_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (users_listview.getSelectionModel().isEmpty() == true){
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
                            return;
                        }else{


                            try {
                                StringBuffer url = new StringBuffer();
                                url.append("http://localhost:8080/sendMessage?senderLogin=");
                                url.append(CurrentUserInfo.getCurrentUser().getLogin());
                                url.append("&senderKey=");
                                url.append(CurrentUserInfo.getCurrentKey());
                                url.append("&receiverLogin=");
                                url.append(users_listview.getSelectionModel().getSelectedItem());
                                url.append("&message=");
                                url.append(send_message_field.getText());

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

                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }

                        }

                    }else{
                        return;
                    }
                }
            }
        });

        users_listview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                
            }
        });

        BindThreadCheckNewMessages();

        LoadUserChats();

        SetCurrentUserNameToWindow();
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

                        System.out.println(response1.getResponseMessage());
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

        CurrentUserInfo.ourThread = new Thread(task);
        CurrentUserInfo.ourThread.start();
    }

    private void LoadUserChats(){
        try {

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
            Gson gson = new Gson();

            AuthorizationResponse response1 = gson.fromJson(response.toString(), AuthorizationResponse.class);

            Type listType = new TypeToken<Set<String>>(){}.getType();
            Set<String> currentUsersChat = gson.fromJson(response1.getResponseMessage() , listType);
            users_listview.getItems().addAll(currentUsersChat);
            users_listview.refresh();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void LogOut(){
        CurrentUserInfo.LogOut();
        CurrentUserInfo.ourThread.stop();

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
    }
}



