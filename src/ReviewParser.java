package src;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class ReviewParser extends MyJSONParser {
        /*
    Example of one row
    {"votes": {"funny": 6, 
                "useful": 0,
                "cool": 0}, 
    "user_id": "ZYaumz29bl9qHpu-KVtMGA", 
    "review_id": "ow1c4Lcl3ObWxDC2yurwjQ", 
    "stars": 4, 
    "date": "2009-05-04", 
    "text": "If you like lot lizards, you'll love the Pine Cone!", 
    "type": "review", 
    "business_id": "JwUE5GmEO-sH1FuwJgKBlQ"}
    */

    public HashMap<String, Integer> votes;
    public String user_id;
    public String review_id;
    public int stars;
    public String date;
    public String text;
    public String type;
    public String business_id;

    public ReviewParser(String filename) {
        super(filename);
    }

    public class Review {
        public String review_id;
        public String user_id;
        public String date;
        public int stars;
        public String business_id;
        public String text;

    public Review(String review_id,
                  String user_id,
                  String date,
                  int stars,
                  String business_id,
                  String text) {
            this.review_id = review_id;
            this.user_id = user_id;
            this.date = date;
            this.stars = stars;
            this.business_id = business_id;
            this.text = text;
        }
    }

    public Review newReviewInstance() {
        return new Review(this.review_id,
                          this.user_id,
                          this.date,
                          this.stars,
                          this.business_id,
                          this.text);
    }

    public class VoteOn {
        public String review_id;
        public String vote_type;

        public VoteOn(String review_id, String vote_type) {
            this.review_id = review_id;
            this.vote_type = vote_type;
        }
    }

    public ArrayList<VoteOn> newVoteOnInstances() {
        ArrayList<VoteOn> voteOnList = new ArrayList<>();
        int nVote = 0;

        for (String vote_type : this.votes.keySet()) {
            nVote = this.votes.get(vote_type);
            for (int i = 0; i < nVote; i++) {
                voteOnList.add(new VoteOn(this.review_id, vote_type));
            }
        }

        return voteOnList;
    }


    @Override
    public void initializeAll() {
        this.votes = new HashMap<String, Integer>();
        this.user_id = "";
        this.review_id = "";
        this.stars = 0;
        this.date = "";
        this.text = "";
        this.type = "";
        this.business_id = "";
    }

    @Override
    public void parseLine(String line) {
        initializeAll();

        try {
            Object obj = jsonParser.parse(line);
            JSONObject review = (JSONObject) obj;

            JSONObject votes = (JSONObject) review.get("votes");
            for (Object key:votes.keySet()) {
                this.votes.put(sqlString(key.toString()), Integer.parseInt(votes.get(key).toString()));
            }

            this.user_id = sqlString(review.get("user_id").toString());

            this.review_id = sqlString(review.get("review_id").toString());

            this.stars = Integer.parseInt(review.get("stars").toString());

            this.date = review.get("date").toString();

            this.text = sqlString(review.get("text").toString());

            this.type = review.get("type").toString();

            this.business_id = sqlString(review.get("business_id").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
