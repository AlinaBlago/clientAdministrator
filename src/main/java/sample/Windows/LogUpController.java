package sample.Windows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.sun.deploy.cache.JarSigningData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.AuthorizationResponse;

public class LogUpController {

    @FXML
    private TextField name_field;

    @FXML
    private TextField login_field;

    @FXML
    private Button signUp_btn;

    @FXML
    private PasswordField password_field;

    @FXML
    private PasswordField repeat_password_field;


    @FXML
    void initialize() {
        signUp_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    if (name_field.getText().length() < 1) throw new Exception("Please enter name more than 1 symbol!");
                    if (login_field.getText().length() < 2) throw new Exception("Please enter login more than 2 symbols!");
                    if (password_field.getText().length() < 4)
                        throw new Exception("Password cannot be less than 4 symbols!");
                    if (!password_field.getText().equals(repeat_password_field.getText()))
                        throw new Exception("Passwords are not equal!");
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("WARNING");
                    alert.setHeaderText("Wrong data");
                    alert.setContentText(e.getMessage());
                    alert.show();
                    return;
                }

                StringBuffer url = new StringBuffer();
                url.append("http://localhost:8080/SignUp?name=");
                url.append(name_field.getText());
                url.append("&login=");
                url.append(login_field.getText());
                url.append("&password=");
                url.append(password_field.getText());
                URL obj = null;
                try {
                    obj = new URL(url.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) obj.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    connection.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }

                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String inputLine = null;
                StringBuffer response = new StringBuffer();

                while (true) {
                    try {
                        if (!((inputLine = in.readLine()) != null)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    response.append(inputLine);
                }
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Gson gson = new Gson();

                AuthorizationResponse response1 = gson.fromJson(response.toString() , AuthorizationResponse.class);
                if(response1.getResponseID() == 0){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Answer");
                    alert.setHeaderText("Results:");
                    alert.setContentText(response1.getResponseMessage());
                    alert.show();
                    Stage stage = (Stage) signUp_btn.getScene().getWindow();
                    stage.close();
                    return;
                }
                if(response1.getResponseID() == 2){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(response1.getResponseMessage());
                    alert.setHeaderText("Results:");
                    alert.setContentText("Такой пользователь уже существует");
                    alert.show();
                    login_field.setText("");
                    name_field.setText("");
                    password_field.setText("");
                    repeat_password_field.setText("");
                }
            }
        });
    }
}
