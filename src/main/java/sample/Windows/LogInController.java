package sample.Windows;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import sample.AuthorizationResponse;
import sample.CurrentUserInfo;
import sample.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LogInController {


    @FXML
    private TextField login_field;

    @FXML
    private Button login_btn;

    @FXML
    private Button signUp_btn;

    @FXML
    private PasswordField password_field;


    private static final Logger log = Logger.getLogger(LogInController.class);

    @FXML
    void initialize()   {

        login_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    if(login_field.getText().length() == 0 || password_field.getText().length() == 0){
                        return;
                    }

                    StringBuffer url = new StringBuffer();
                    url.append("http://localhost:8080/Login?login=");
                    url.append(login_field.getText());
                    url.append("&password=");
                    url.append(password_field.getText());

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
                    User user = new User("" , login_field.getText() , "");
                    AuthorizationResponse response1 = gson.fromJson(response.toString(), AuthorizationResponse.class);
                    CurrentUserInfo.Init(user , response1.getResponseMessage());

                    if(response1.getResponseID() == 0) {
                        log.info("User logined");
                        //Открываем главное окно
                        Stage applStage = new Stage();
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/appl.fxml"));
                        Parent root = loader.load();
                        applStage.setScene(new Scene(root, 620, 680));
                        applStage.show();

                        //Закрываем текущее окно
                        Stage currentStageToClose = (Stage) signUp_btn.getScene().getWindow();
                        currentStageToClose.close();
                    }

                } catch (Exception e) {
                    log.info(e.getMessage());
                    System.out.println(e.getMessage());
                }
            }
        });

        signUp_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Stage signUp = new Stage();
                Parent signUpSceneRoot = null;
                try {
                    signUpSceneRoot = FXMLLoader.load(LogInController.this.getClass().getResource("/logup.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                signUp.setScene(new Scene(signUpSceneRoot, 620, 680));
                signUp.show();
            }
        });
    }
}

