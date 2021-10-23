package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import config.Category;

public class BusinessParser extends MyJSONParser {
    /*
    Example of one row
    {"business_id": "G1qUGBNYNS220jKlPAlFsA", 
    "full_address": "7508 Hubbard Ave\nMiddleton, WI 53562", 
    "hours": {"Monday": {"close": "22:30", "open": "10:30"}, 
              "Tuesday": {"close": "22:30", "open": "10:30"},
              "Friday": {"close": "22:30", "open": "10:30"}, 
              "Wednesday": {"close": "22:30", "open": "10:30"}, 
              "Thursday": {"close": "22:30", "open": "10:30"}, 
              "Saturday": {"close": "22:30", "open": "10:30"}}, 
    "open": true, 
    "categories": ["Bars", "American (Traditional)", "Nightlife", "Restaurants"], 
    "city": "Middleton", 
    "review_count": 34, 
    "name": "The Village Green Bar & Grill", 
    "neighborhoods": [], 
    "longitude": -89.5120419, 
    "state": "WI", 
    "stars": 4.0, 
    "latitude": 43.095723999999997, 
    "attributes": {"Alcohol": "full_bar", 
                    "Noise Level": "average",
                    "Music": {"dj": false, 
                                "background_music": true, 
                                "jukebox": false, 
                                "live": false, 
                                "video": false, 
                                "karaoke": false}, 
                    "Attire": "casual", 
                    "Ambience": {"romantic": false, 
                                "intimate": false,
                                "classy": false, 
                                "hipster": false, 
                                "divey": false, 
                                "touristy": false, 
                                "trendy": false, 
                                "upscale": false, 
                                "casual": true}, 
                    "Good for Kids": true, 
                    "Wheelchair Accessible": true, 
                    "Good For Dancing": false, 
                    "Delivery": false, 
                    "Coat Check": false, 
                    "Smoking": "no", 
                    "Accepts Credit Cards": true, 
                    "Take-out": true, 
                    "Price Range": 2, 
                    "Happy Hour": true, 
                    "Outdoor Seating": true, 
                    "Takes Reservations": false, 
                    "Waiter Service": true, 
                    "Wi-Fi": "no", 
                    "Caters": true, 
                    "Good For": {"dessert": false, 
                                "latenight": false, 
                                "lunch": true, 
                                "dinner": true, 
                                "brunch": false, 
                                "breakfast": false}, 
                    "Parking": {"garage": false, 
                                "street": true, 
                                "validated": false, 
                                "lot": false, 
                                "valet": false}, 
                    "Has TV": true, 
                    "Good For Groups": true}, 
    "type": "business"}

    */

    private HashMap<String, Integer> DayNumber;

    public String business_id;
    public String full_address;
    public ArrayList<OperationHour> hours;
    public String open;
    public ArrayList<String> categories;
    public String city;
    public int review_count;
    public String name;
    public ArrayList<String> neighborhoods;
    public float longitude;
    public String state;
    public float stars;
    public float latitude;
    public ArrayList<AttributeItem> attributes;
    public String type;

    public BusinessParser(String filename) {
        super(filename);
        DayNumber = new HashMap<>();
        DayNumber.put("Sunday", 0);
        DayNumber.put("Monday", 1);
        DayNumber.put("Tuesday", 2);
        DayNumber.put("Wednesday", 3);
        DayNumber.put("Thursday", 4);
        DayNumber.put("Friday", 5);
        DayNumber.put("Saturday", 6);
    }

    public class OperationHour {
        public String day;
        public String close;
        public String open;

        public OperationHour(String day, String close, String open) {
            this.day = day;
            this.close = close;
            this.open = open;
        }
    }

    public class AttributeItem {
        public String key;
        public String val;
        public HashMap<String, String> vals;
        public boolean hasMultiVals;

        public AttributeItem(String key, String val) {
            this.key = key;
            this.val = val;
            this.hasMultiVals = false;
        }

        public AttributeItem(String key, HashMap<String, String> vals) {
            this.key = key;
            this.vals = vals;
            this.hasMultiVals = true;
        }
    }

    public class Business {
        public String business_id;
        public String full_address;
        public String open;
        public String city;
        public String state;
        public float latitude;
        public float longitude;
        public String name;

        public Business(String business_id,
                        String full_address,
                        String open,
                        String city,
                        String state,
                        float latitude,
                        float longitude,
                        String name) {
            this.business_id = business_id;
            this.full_address = full_address;
            this.open = open;
            this.city = city;
            this.state = state;
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
        }
    }

    public Business newBusinessInstance() {
        return new Business(this.business_id,
                            this.full_address,
                            this.open,
                            this.city,
                            this.state,
                            this.latitude,
                            this.longitude,
                            this.name);
    }

    public class Neighborhood {
        public String business_id;
        public String hood;

        public Neighborhood(String business_id, String hood) {
            this.business_id = business_id;
            this.hood = hood;
        }
    }

    public ArrayList<Neighborhood> newNeighborhoodInstances() {
        ArrayList<Neighborhood> neighborhoodList = new ArrayList<>();

        for (String neighborhood : this.neighborhoods) {
            neighborhoodList.add(new Neighborhood(this.business_id, neighborhood));
        }

        return neighborhoodList;
    }

    public class OperationTime {
        public String business_id;
        public int day;
        public String close_hour;
        public String open_hour;

        public OperationTime(String business_id, int day, String close_hour, String open_hour) {
            this.business_id = business_id;
            this.day = day;
            this.close_hour = close_hour;
            this.open_hour = open_hour;
        }
    }

    public ArrayList<OperationTime> newOperationTimeInstances() {
        ArrayList<OperationTime> operationTimeList = new ArrayList<>();

        for (OperationHour operationHour : this.hours) {
            operationTimeList.add(new OperationTime(this.business_id, DayNumber.get(operationHour.day), operationHour.close, operationHour.open));
        }

        return operationTimeList;
    }


    public class BusinessInCategory {
        public String business_id;
        public String category;

        public BusinessInCategory(String business_id, String category) {
            this.business_id = business_id;
            this.category = category;
        }
    }

    public ArrayList<BusinessInCategory> newBusinessInCategoryInstances() {
        ArrayList<BusinessInCategory> businessInCategoryList = new ArrayList<>();
        for (String category : this.categories) {
            businessInCategoryList.add(new BusinessInCategory(this.business_id, category));
        }
        return businessInCategoryList;
    }

    public void addNewCategoryHasCategory(HashSet<String> categoryHasCategorySet) {
        ArrayList<String> mainCategoryList = new ArrayList<>();
        ArrayList<String> subCategoryList = new ArrayList<>();
        for (String category : this.categories) {
            if (Category.isMainCategory(category)) {
                mainCategoryList.add(category);
            } else {
                subCategoryList.add(category);
            }
        }
        for (String mainCategory : mainCategoryList) {
            for (String subCategory : subCategoryList) {
                String categoryHasCategory = mainCategory + ":" + subCategory;
                if (!categoryHasCategorySet.contains(categoryHasCategory)) {
                    categoryHasCategorySet.add(categoryHasCategory);
                }
            }
        }
    }

    public class Attribute {
        public String business_id;
        public String key;

        public Attribute(String business_id, String key) {
            this.business_id = business_id;
            this.key = key;
        }
    }

    public ArrayList<Attribute> newAttributeInstances() {
        ArrayList<Attribute> attributeList = new ArrayList<>();

        for (AttributeItem attribute : this.attributes) {
            if (attribute.hasMultiVals) {
                for (String valsKey : attribute.vals.keySet()) {
                    attributeList.add(new Attribute(this.business_id, attribute.key+"_"+valsKey+"_"+attribute.vals.get(valsKey)));
                }
            } else {
                attributeList.add(new Attribute(this.business_id, attribute.key+"_"+attribute.val));
            }
        }

        return attributeList;
    }

    @Override
    public void initializeAll() {
        this.business_id = "";
        this.full_address = "";
        this.hours = new ArrayList<OperationHour>();
        this.open = "";
        this.categories = new ArrayList<String>();
        this.city = "";
        this.review_count = 0;
        this.name = "";
        this.neighborhoods = new ArrayList<String>();
        this.longitude = 0;
        this.state = "";
        this.stars = 0;
        this.latitude = 0;
        this.attributes = new ArrayList<AttributeItem>();
        this.type = "";
    }

    @Override
    public void parseLine(String line) {
        initializeAll();

        try {
            Object obj = jsonParser.parse(line);
            JSONObject business = (JSONObject) obj;

            this.business_id = sqlString(business.get("business_id").toString());

            this.full_address = sqlString(business.get("full_address").toString());

            JSONObject hours = (JSONObject) business.get("hours");
            for (Object item : hours.keySet()) {
                JSONObject hour = (JSONObject) hours.get(item);
                this.hours.add(new OperationHour(item.toString(), hour.get("close").toString(), hour.get("open").toString()));
            }

            this.open = business.get("open").toString();

            JSONArray categories = (JSONArray) business.get("categories");
            for (Object category : categories) {
                this.categories.add(category.toString());
            }

            this.city = sqlString(business.get("city").toString());

            this.review_count = Integer.parseInt(business.get("review_count").toString());

            this.name = sqlString(business.get("name").toString());

            JSONArray neighborhoods = (JSONArray) business.get("neighborhoods");
            for (Object hood : neighborhoods) {
                this.neighborhoods.add(sqlString(hood.toString()));
            }

            this.longitude = Float.parseFloat(business.get("longitude").toString());

            this.state = sqlString(business.get("state").toString());

            this.stars = Float.parseFloat(business.get("stars").toString());

            this.latitude = Float.parseFloat(business.get("latitude").toString());

            JSONObject attributes = (JSONObject) business.get("attributes");
            for (Object objKey : attributes.keySet()) {
                String key = sqlString(objKey.toString());
                Object val = attributes.get(key);
                if (val.toString().contains("{")) {
                    JSONObject objVal = (JSONObject) val;
                    HashMap<String, String> vals = new HashMap<String, String>();
                    for (Object objValKey: objVal.keySet()) {
                        vals.put(sqlString(objValKey.toString()), sqlString(objVal.get(objValKey).toString()));
                    }
                    this.attributes.add(new AttributeItem(key, vals));
                } else {
                    this.attributes.add(new AttributeItem(key, sqlString(val.toString())));
                }
            }
            
            this.type = sqlString(business.get("type").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
