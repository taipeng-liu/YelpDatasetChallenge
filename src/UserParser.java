package src;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;


public class UserParser extends MyJSONParser {
    /*
    Example of one row:

     {"yelping_since": "2011-09", 
     "votes": {"funny": 9, "useful": 57, "cool": 16}, 
     "review_count": 99, 
     "name": "Todd", 
     "user_id": "FmwWPEQMcxCGI5K1yqAXaA", 
     "friends": ["8dbRf1UsWp2ktXHZ6Zv06w", "PIH92hE32-AMbI6MpLEgbg", "NhbkTLXX2srey5kPtVOAcw"], 
     "fans": 0, 
     "average_stars": 3.71, 
     "type": "user", 
     "compliments": {"hot": 2, "cool": 1}, 
     "elite": [2012, 2013, 2014]}
    */
    public String yelping_since;
    public HashMap<String, Integer> votes;
    public int review_count;
    public String name;
    public String user_id;
    public ArrayList<String> friends;
    public int fans;
    public float average_stars;
    public String type;
    public HashMap<String, Integer> compliments;
    public ArrayList<Integer> elite;
    
    public UserParser(String filename) {
        super(filename);
    }

    public class Yelp_user {
        public String yelping_since;
        public String name;
        public String user_id;
        public int review_count;
        public int friend_count;
        public float average_star;
        public int num_votes;

        public Yelp_user(String yelping_since, String name, String user_id, int review_count, int friend_count, float average_star, int num_votes) {
            this.yelping_since = yelping_since;
            this.name = name;
            this.user_id = user_id;
            this.review_count = review_count;
            this.friend_count = friend_count;
            this.average_star = average_star;
            this.num_votes = num_votes;
        }
    }

    public Yelp_user newYelpUserInstance() {
        int num_votes = 0;
        for (String key : this.votes.keySet()) {
            num_votes += this.votes.get(key);
        }
        return new Yelp_user(this.yelping_since, this.name, this.user_id, this.review_count, this.friends.size(), this.average_stars, num_votes);
    }

    public class Friend {
        public String user_id;
        public String friend_id;

        public Friend(String user_id, String friend_id) {
            this.user_id = user_id;
            this.friend_id = friend_id;
        }
    }

    public ArrayList<Friend> newFriendInstances() {
        ArrayList<Friend> friendList = new ArrayList<>();

        for (String friend_id : this.friends) {
            friendList.add(new Friend(this.user_id, friend_id));
        }

        return friendList;
    }



    @Override
    public void initializeAll() {
        this.yelping_since = "";
        this.votes = new HashMap<String, Integer>();
        this.review_count = 0;
        this.name = "";
        this.user_id = "";
        this.friends = new ArrayList<String>();
        this.fans = 0;
        this.average_stars = 0;
        this.type = "";
        this.compliments = new HashMap<String, Integer>();
        this.elite = new ArrayList<Integer>();
    }

    @Override
    public void parseLine(String line) {
        initializeAll();

        try {
            Object obj = jsonParser.parse(line);
            JSONObject user = (JSONObject) obj;

            this.yelping_since = sqlString(user.get("yelping_since").toString());
            
            JSONObject votes = (JSONObject) user.get("votes");
            for (Object key:votes.keySet()) {
                this.votes.put(sqlString(key.toString()), Integer.parseInt(votes.get(key).toString()));
            }
            
            this.review_count = Integer.parseInt(user.get("review_count").toString());
            
            this.name = sqlString(user.get("name").toString());
            
            this.user_id = sqlString(user.get("user_id").toString());
            
            JSONArray friends = (JSONArray) user.get("friends");
            for (Object friend : friends) {
                this.friends.add(sqlString(friend.toString()));
            }
            
            this.fans = Integer.parseInt(user.get("fans").toString());

            this.average_stars = Float.parseFloat(user.get("average_stars").toString());

            this.type = sqlString(user.get("type").toString());

            JSONObject compliments = (JSONObject) user.get("compliments");
            for (Object key : compliments.keySet()) {
                this.compliments.put(sqlString(key.toString()), Integer.parseInt(compliments.get(key).toString()));
            }

            JSONArray elite = (JSONArray) user.get("elite");
            for (Object year : elite) {
                this.elite.add(Integer.parseInt(year.toString()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
