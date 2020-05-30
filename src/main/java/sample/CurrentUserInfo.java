package sample;

public class CurrentUserInfo {

   private static User currentUser;
   private static String currentKey;

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
