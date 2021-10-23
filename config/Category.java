package config;
import src.MyJSONParser;

public class Category {
    public static final String[] MAIN_CATEGORIES = 
    {
        "Active Life",
        "Arts & Entertainment",
        "Automotive",
        "Car Rental",
        "Cafes",
        "Beauty & Spas",
        "Convenience Stores",
        "Dentists",
        "Doctors",
        "Drugstores",
        "Department Stores",
        "Education",
        "Event Planning & Services",
        "Flowers & Gifts",
        "Food",
        "Health & Medical",
        "Home Services",
        "Home & Garden",
        "Hospitals",
        "Hotels & Travel",
        "Hardware Stores",
        "Grocery",
        "Medical Centers",
        "Nurseries & Gardening",
        "Nightlife",
        "Restaurants",
        "Shopping",
        "Transportation"
    };

    public static boolean isMainCategory(String category) {
        for (String mainCategory : MAIN_CATEGORIES) {
            if (MyJSONParser.sqlString(mainCategory).compareTo(MyJSONParser.sqlString(category)) == 0) {
                return true;
            }
        }
        return false;
    }
}