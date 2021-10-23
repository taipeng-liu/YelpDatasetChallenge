package src;

import config.Settings;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;

public class DbHelper {
    public static String seperator = "</s>";
    public static String newLine = "</l>";
    private Connection connection;
    
    public DbHelper() {
        initializeConnection();
    }
    public String[] getAllTables() {
        return executeQuery("SELECT table_name FROM user_tables").split(newLine, 0);
    }

    public String getDBMetaData() {
        String result = "";
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            String productName = dbMetaData.getDatabaseProductName();
            result += "Database: " + productName;
            String productVersion = dbMetaData.getDatabaseProductVersion();
            result += "\nVersion: " + productVersion;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Connection getConnection() {
        return connection;
    }

    public void deleteAllData() {
        if(!Settings.RELOAD_ALL) {return;}
        
        Instant start = Instant.now();

        String[] tables = getAllTables();
        for (int i = 0; i < tables.length; i++) {
            deleteDataInTable(tables[i]);
        }

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("deleteAllData() finishes in " + timeElapsed+" ms");
    }

    public void deleteDataInTable(String table_name) {
        executeUpdate("DELETE FROM "+ table_name);
    }

    public int executeUpdate(String sql) {
        int updateCount = 0;
        try {
            Statement statement = connection.createStatement();
            updateCount = statement.executeUpdate(sql);
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return updateCount;
    }

    public String executeQuery(String sql) {
        String ret = "";
        String colNames = "";
        String colContents = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            int nCols = resultSet.getMetaData().getColumnCount();
            
            for (int i = 1; i <= nCols; i++) {
                if (colNames.length() > 0) {
                    colNames += seperator;
                }
                colNames += resultSet.getMetaData().getColumnName(i);
            }

            while (resultSet.next()) {
                if (colContents.length() > 0) {
                    colContents += newLine;
                }
                
                for (int i = 1; i <= nCols; i++) {
                    if (i < nCols) {
                        colContents += resultSet.getString(i) + seperator;
                    } else {
                        colContents += resultSet.getString(i);
                    }
                }
            }

            ret = colNames + newLine + colContents;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void initializeConnection() {
        String oracleURL = Settings.getOracleConnectionURL();

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = 
            DriverManager.getConnection(oracleURL, Settings.getUsername(), Settings.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
