package src;

import java.io.FileReader;
 
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;


public abstract class MyJSONParser {
    String filename;
    JSONParser jsonParser;
    BufferedReader reader;
    String line;

    public MyJSONParser(String filename) {
        try {
            this.filename = filename;
            jsonParser = new JSONParser();
            reader = new BufferedReader(new FileReader(filename));
            line = reader.readLine();
            parseLine(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void parseLine(String line);
    public abstract void initializeAll();

    public boolean next() {
        line = null;
        
        try {
            line = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (line != null) {
            parseLine(line);
            return true;
        }
        return false;
    }

    public static String sqlString(String s) {
        return s.replace("'", "''").replace("&", "&' ||'");
    }

    public static String normalString(String s) {
        return s.replace("''", "'").replace("&' ||'", "&");
    }
};