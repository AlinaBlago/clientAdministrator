package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/logIn.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 620, 680));
        primaryStage.show();

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        CurrentUserInfo.ourThread.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
