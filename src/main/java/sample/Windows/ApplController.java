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
    private ListView<String> users_listview;

    @FXML
    private Button delete_user_btn;

    @FXML
    private Button block_user_btn;

    @FXML
    private Button unblock_user_btn;

    private static final Logger log = Logger.getLogger(ApplController.class);


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        logout_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                LogOut();
            }
        });

        delete_user_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                deleteUser();
            }
        });

        block_user_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                banUser();
            }
        });

        unblock_user_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                unbanUser();
            }
        });

        /*
        users_listview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //UsersListViewChanged(newValue);
            }
        });*/

        LoadUsers();

        SetCurrentUserNameToWindow();
    }

    private void deleteUser(){
        if (users_listview.getSelectionModel().isEmpty() == true){
            log.info("Delete user function call : user is empty");
            return;
        }
        try {
            log.info("request 'deleteUser' configuration");
            StringBuffer url = new StringBuffer();
            url.append("http://localhost:8080/deleteUser?login=");
            url.append(CurrentUserInfo.getCurrentUser().getLogin());
            url.append("&key=");
            url.append(CurrentUserInfo.getCurrentKey());
            url.append("&userToDeleteLogin=");
            url.append(users_listview.getSelectionModel().getSelectedItem());

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
            users_listview.getItems().clear();
            LoadUsers();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void banUser(){
        if (users_listview.getSelectionModel().isEmpty() == true){
            log.info("banUser function call : user is empty");
            return;
        }
        try {
            log.info("request 'banUser' configuration");
            StringBuffer url = new StringBuffer();
            url.append("http://localhost:8080/banUser?login=");
            url.append(CurrentUserInfo.getCurrentUser().getLogin());
            url.append("&key=");
            url.append(CurrentUserInfo.getCurrentKey());
            url.append("&userToBanLogin=");
            url.append(users_listview.getSelectionModel().getSelectedItem());

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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void unbanUser(){
        if (users_listview.getSelectionModel().isEmpty() == true){
            log.info("unbanUser function call : user is empty");
            return;
        }
        try {
            log.info("request 'unbanUser' configuration");
            StringBuffer url = new StringBuffer();
            url.append("http://localhost:8080/unbanUser?login=");
            url.append(CurrentUserInfo.getCurrentUser().getLogin());
            url.append("&key=");
            url.append(CurrentUserInfo.getCurrentKey());
            url.append("&userToUnbanLogin=");
            url.append(users_listview.getSelectionModel().getSelectedItem());

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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void SetCurrentUserNameToWindow(){
        String text = "Вы вошли под логином : " + CurrentUserInfo.getCurrentUser().getLogin();
        current_user_name_lbl.setText(text);
    }

    private void LoadUsers(){
        try {
            log.info("request 'loaduserchat' configuration");
            StringBuffer url = new StringBuffer();
            url.append("http://localhost:8080/LoadUsersForAdmin?login=");
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
            Set<String> currentUsers = gson.fromJson(response1.getResponseMessage() , listType);
            currentUsers.remove(CurrentUserInfo.getCurrentUser().getLogin());
            users_listview.getItems().addAll(currentUsers);
            users_listview.refresh();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void LogOut(){
        log.info("logout command");
        CurrentUserInfo.LogOut();

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



