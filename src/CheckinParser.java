package src;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class CheckinParser extends MyJSONParser {
    /*
    Example of one row
    {"checkin_info": {"3-4": 1, 
                        "13-5": 1, 
                        "6-6": 1, 
                        "14-5": 1, 
                        "14-6": 1, 
                        "14-2": 1, 
                        "14-3": 1, 
                        "19-0": 1, 
                        "11-5": 1, 
                        "13-2": 1, 
                        "11-6": 2, 
                        "11-3": 1, 
                        "12-6": 1, 
                        "6-5": 1, 
                        "5-5": 1, 
                        "9-2": 1, 
                        "9-5": 1, 
                        "9-6": 1, 
                        "5-2": 1, 
                        "7-6": 1, 
                        "7-5": 1, 
                        "7-4": 1, 
                        "17-5": 1, 
                        "8-5": 1, 
                        "10-2": 1, 
                        "10-5": 1, 
                        "10-6": 1}, 
        "type": "checkin", 
        "business_id": "JwUE5GmEO-sH1FuwJgKBlQ"}
    */

    public HashMap<String, Integer> checkin_info;
    public String type;
    public String business_id;

    public class CheckinInfo {
        public String business_id;
        public int hour;
        public int day;

        public CheckinInfo(String business_id, int hour, int day) {
            this.business_id = business_id;
            this.hour = hour;
            this.day = day;
        }
    }

    public CheckinParser(String filename) {
        super(filename);
    }

    public ArrayList<CheckinInfo> newCheckinInfoInstances() {
        ArrayList<CheckinInfo> checkInfoList = new ArrayList<>();

        for (String key : this.checkin_info.keySet()) {
            String[] hour_day = key.split("-");
            int hour = Integer.parseInt(hour_day[0]);
            int day = Integer.parseInt(hour_day[1]);
            int checkinCount = this.checkin_info.get(key);

            for (int i = 0; i < checkinCount; i++) {
                checkInfoList.add(new CheckinInfo(this.business_id, hour, day));
            }
        }

        return checkInfoList;
    }

    @Override
    public void initializeAll() {
        this.checkin_info = new HashMap<String, Integer>();
        this.type = "";
        this.business_id = "";
    }

    @Override
    public void parseLine(String line) {
        initializeAll();

        try {
            Object obj = jsonParser.parse(line);
            JSONObject checkin = (JSONObject) obj;

            JSONObject checkin_info = (JSONObject) checkin.get("checkin_info");
            for (Object key: checkin_info.keySet()) {
                this.checkin_info.put(sqlString(key.toString()), Integer.parseInt(checkin_info.get(key).toString()));
            }

            this.type = checkin.get("type").toString();

            this.business_id = checkin.get("business_id").toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
