package sample;

public class User {
    private static int NextID;
    private int ID;
    private String Name;
    private String Login;
    private String Password;
    private boolean isBanned;

    public User(String name, String login, String password) {
        Name = name;
        Login = login;
        Password = password;
        this.isBanned = false;
    }

    public static int getNextID() {
        NextID += 1;
        return NextID;
    }

    public static void setNextID(int nextID) {
        NextID = nextID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }
}
