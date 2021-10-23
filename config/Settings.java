package config;

public class Settings {
    

    private static final String host = "localhost";
    private static final String dbName = "xe";
    private static final int port = 1521;
    private static final String systemUsername = "system";
    private static final String systemPassword = "oracle";
    private static final String username = "taipeng";
    private static final String password = "840608";

    public static final int BUFFER_SIZE = 5000;
    public static final boolean TEST_MODE = false;

    public static final boolean RELOAD_ALL = true;

    public static final boolean RELOAD_YELP_USER = false;
    public static final boolean RELOAD_BUSINESS = false;
    public static final boolean RELOAD_CHECKININFO = false;
    public static final boolean RELOAD_REVIEW = false;

    public static final boolean RELOAD_FRIEND = false;
    public static final boolean RELOAD_NEIGHBORHOOD = false;
    public static final boolean RELOAD_VOTEON = false;
    public static final boolean RELOAD_OPERATIONTIME = false;
    public static final boolean RELOAD_CATEGORYHASCATEGORY = false;
    public static final boolean RELOAD_BUSINESSINCATEGORY = false;
    public static final boolean RELOAD_ATTRIBUTE = false;

    

    public static String getOracleConnectionURL() {
        return "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName;
    }

    public static String getSystemUsername() {
        return systemUsername;
    }

    public static String getSystemPassword() {
        return systemPassword;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }
}
