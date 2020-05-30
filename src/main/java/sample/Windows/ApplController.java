package sample.Windows;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.AuthorizationResponse;
import sample.CurrentUserInfo;
import sample.User;


public class ApplController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label current_user_name_lbl;

    @FXML
    private Button logout_btn;

    @FXML
    private Button send_btn;

    @FXML
    private TextField send_message_field;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String text = "Вы вошли под логином : " + CurrentUserInfo.getCurrentUser().getLogin();
        current_user_name_lbl.setText(text);

        logout_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
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

            }
        });


        try {
            //TODO:сделать проверки логина и пароля

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

            //TODO: массив пока что пустой , сделать нахождение пользователей и отправку сообещний

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        
    }
}



