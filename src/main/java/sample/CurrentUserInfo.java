package sample;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.control.Alert;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class CurrentUserInfo {
    private static User currentUser;
    private static String currentKey;
    public static Thread ourThread ;
    public static String currentChat;
    private static final org.apache.log4j.Logger log = Logger.getLogger(CurrentUserInfo.class);

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentKey() {
        return currentKey;
    }

    public static void Init(User user , String key){
       currentUser = user;
       currentKey = key;
    }

    public static void LogOut(){
        currentKey = "";
        currentUser = null;
    }

}
