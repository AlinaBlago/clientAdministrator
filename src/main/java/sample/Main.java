package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.apache.log4j.Logger;

public class Main extends Application {
    private static final Logger log = Logger.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception{

        log.info("Application start");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/logIn.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 620, 680));
        primaryStage.show();
        log.info("login.xml loaded");


    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        log.info("Program finalized");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
