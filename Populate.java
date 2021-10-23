import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;

import config.Settings;
import src.*;

public class Populate {
    private static DbHelper db = new DbHelper();
    private static Connection connection;
    // Entity tables
    private static PreparedStatement table_yelp_user;
    private static PreparedStatement table_business;
    private static PreparedStatement table_review;
    private static PreparedStatement table_checkinInfo;
    // Relation tables
    private static PreparedStatement table_friend;
    private static PreparedStatement table_neighborhood;
    private static PreparedStatement table_voteOn;
    private static PreparedStatement table_operationTime;
    private static PreparedStatement table_categoryHasCategory;
    private static PreparedStatement table_businessInCategory;
    private static PreparedStatement table_attribute;

    public static void main(String[] args) {
        db.initializeConnection();
        connection = db.getConnection();
        initializePreparedStatements();
        populateData();
        closePreparedStatements();
        db.closeConnection();
    }

    public static void populateData() {
        Instant start = Instant.now();

        populateUser("data/yelp_user.json");
        populateBusiness("data/yelp_business.json");
        populateCheckin("data/yelp_checkin.json");
        populateReview("data/yelp_review.json");

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("populateDate() finishes in " + timeElapsed+" ms");
    }

    // 2
    public static void populateReview(String filename) {
        ReviewParser review = new ReviewParser(filename);
        int count = 0;
        ArrayList<ReviewParser.Review> reviewList = new ArrayList<>();
        ArrayList<ReviewParser.VoteOn> voteOnList = new ArrayList<>();

        if(Settings.RELOAD_ALL || Settings.RELOAD_REVIEW) {db.deleteDataInTable("Review");}
        if(Settings.RELOAD_ALL || Settings.RELOAD_VOTEON) {db.deleteDataInTable("VoteOn");}
        do {
            reviewList.add(review.newReviewInstance());
            voteOnList.addAll(review.newVoteOnInstances());
            count += 1;
            if (count >= Settings.BUFFER_SIZE) {
                reviewInserts(reviewList);
                voteOnInserts(voteOnList);
                
                reviewList = new ArrayList<ReviewParser.Review>();
                voteOnList = new ArrayList<ReviewParser.VoteOn>();
                count = 0;
                if (Settings.TEST_MODE) {
                    return;
                }
            }
        } while (review.next());

        if (count > 0) {
            reviewInserts(reviewList);
            voteOnInserts(voteOnList);
        }
    }

    // 1
    public static void populateCheckin(String filename) {
        CheckinParser checkin = new CheckinParser(filename);
        int count = 0;
        ArrayList<CheckinParser.CheckinInfo> checkinInfoList = new ArrayList<>();

        if(Settings.RELOAD_CHECKININFO) {db.deleteDataInTable("CheckinInfo");}
        do {
            checkinInfoList.addAll(checkin.newCheckinInfoInstances());
            count += 1;
            if (count >= Settings.BUFFER_SIZE) {
                checkinInserts(checkinInfoList);

                checkinInfoList = new ArrayList<CheckinParser.CheckinInfo> ();
                count = 0;
                if (Settings.TEST_MODE) {
                    break;
                }
            }
        } while (checkin.next());

        if (count > 0) {
            checkinInserts(checkinInfoList);
        }
    }

    // 6
    public static void populateBusiness(String filename) {
        BusinessParser business = new BusinessParser(filename);
        int count = 0;
        ArrayList<BusinessParser.Business> businessList = new ArrayList<>();
        ArrayList<BusinessParser.Neighborhood> neighborhoodList = new ArrayList<>();
        ArrayList<BusinessParser.OperationTime> operationTimeList = new ArrayList<>();
        ArrayList<BusinessParser.BusinessInCategory> businessInCategoryList = new ArrayList<>();
        ArrayList<BusinessParser.Attribute> attributeList = new ArrayList<>();
        HashSet<String> categoryHasCategorySet = new HashSet<>();

        if(Settings.RELOAD_ALL || Settings.RELOAD_BUSINESS) {db.deleteDataInTable("Business");}
        if(Settings.RELOAD_ALL || Settings.RELOAD_NEIGHBORHOOD) {db.deleteDataInTable("Neighborhood");} 
        if(Settings.RELOAD_ALL || Settings.RELOAD_OPERATIONTIME) {db.deleteDataInTable("OperationTime");} 
        if(Settings.RELOAD_ALL || Settings.RELOAD_CATEGORYHASCATEGORY) {db.deleteDataInTable("CategoryHasCategory");} 
        if(Settings.RELOAD_ALL || Settings.RELOAD_BUSINESSINCATEGORY) {db.deleteDataInTable("BusinessInCategory");} 
        if(Settings.RELOAD_ALL || Settings.RELOAD_ATTRIBUTE) {db.deleteDataInTable("Attribute");}
        do {
            businessList.add(business.newBusinessInstance());
            neighborhoodList.addAll(business.newNeighborhoodInstances());
            operationTimeList.addAll(business.newOperationTimeInstances());
            attributeList.addAll(business.newAttributeInstances());
            businessInCategoryList.addAll(business.newBusinessInCategoryInstances());
            business.addNewCategoryHasCategory(categoryHasCategorySet);
            count += 1;
            if (count >= Settings.BUFFER_SIZE) {
                businessInserts(businessList);
                neighborhoodInserts(neighborhoodList);
                operationTimeInserts(operationTimeList);
                businessInCategoryInserts(businessInCategoryList);
                attributeInserts(attributeList);

                businessList = new ArrayList<BusinessParser.Business>();
                neighborhoodList = new ArrayList<BusinessParser.Neighborhood>();
                operationTimeList = new ArrayList<BusinessParser.OperationTime>();
                businessInCategoryList = new ArrayList<BusinessParser.BusinessInCategory>();
                attributeList = new ArrayList<BusinessParser.Attribute>();
                count = 0;
                if (Settings.TEST_MODE) {break;}
            }
        } while (business.next());

        if (count > 0) {
            businessInserts(businessList);
            neighborhoodInserts(neighborhoodList);
            operationTimeInserts(operationTimeList);
            businessInCategoryInserts(businessInCategoryList);
            attributeInserts(attributeList);
        }
        categoryHasCategoryInserts(categoryHasCategorySet);
    }

    // 2
    public static void populateUser(String filename) {
        UserParser user = new UserParser(filename);
        int count = 0;
        ArrayList<UserParser.Yelp_user> yelp_users = new ArrayList<UserParser.Yelp_user>();
        ArrayList<UserParser.Friend> friendList = new ArrayList<>();

        if(Settings.RELOAD_ALL || Settings.RELOAD_YELP_USER) {db.deleteDataInTable("Yelp_user");}
        if(Settings.RELOAD_ALL || Settings.RELOAD_FRIEND) {db.deleteDataInTable("Friend");} 

        do {
            yelp_users.add(user.newYelpUserInstance());
            friendList.addAll(user.newFriendInstances());
            count += 1;
            if (count >= Settings.BUFFER_SIZE) {
                userInserts(yelp_users);
                friendInserts(friendList);

                yelp_users = new ArrayList<UserParser.Yelp_user>();
                friendList = new ArrayList<UserParser.Friend>();
                count = 0;
                if (Settings.TEST_MODE) {break;}
            }
        } while (user.next());

        if (count > 0) {
            userInserts(yelp_users);
            friendInserts(friendList);
        }
    }

    public static void voteOnInserts(ArrayList<ReviewParser.VoteOn> voteOnList) {
        if (!(Settings.RELOAD_ALL || Settings.RELOAD_VOTEON)) {return;}

        try {
            for (ReviewParser.VoteOn voteOn : voteOnList) {
                table_voteOn.setString(1, voteOn.review_id);
                table_voteOn.setString(2, voteOn.vote_type);
                table_voteOn.addBatch();
            }
            table_voteOn.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void neighborhoodInserts(ArrayList<BusinessParser.Neighborhood> neighborhoodList) {
        if(!(Settings.RELOAD_ALL || Settings.RELOAD_NEIGHBORHOOD)) {return;}

        try {
            for (BusinessParser.Neighborhood neighborhood : neighborhoodList) {
                table_neighborhood.setString(1, neighborhood.business_id);
                table_neighborhood.setString(2, neighborhood.hood);
                table_neighborhood.addBatch();
            }
            table_neighborhood.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void operationTimeInserts(ArrayList<BusinessParser.OperationTime> operationTimeList) {
        if(!(Settings.RELOAD_ALL || Settings.RELOAD_OPERATIONTIME)) {return;}

        try {
            for (BusinessParser.OperationTime operationTime : operationTimeList) {
                table_operationTime.setString(1, operationTime.business_id);
                table_operationTime.setInt(2, operationTime.day);
                table_operationTime.setString(3, operationTime.close_hour);
                table_operationTime.setString(4, operationTime.open_hour);
                table_operationTime.addBatch();
            }
            table_operationTime.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void categoryHasCategoryInserts(HashSet<String> categoryHasCategorySet) {
        if(!(Settings.RELOAD_ALL || Settings.RELOAD_CATEGORYHASCATEGORY)) {return;}
        try {
            for (String categoryHasCategory : categoryHasCategorySet) {
                String[] mainAndSub = categoryHasCategory.split(":");
                table_categoryHasCategory.setString(1, mainAndSub[0]);
                table_categoryHasCategory.setString(2, mainAndSub[1]);
                table_categoryHasCategory.addBatch();
            }
            table_categoryHasCategory.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void businessInCategoryInserts(ArrayList<BusinessParser.BusinessInCategory> businessInCategoryList) {
        if(!(Settings.RELOAD_ALL || Settings.RELOAD_BUSINESSINCATEGORY)) {return;}
        try {
            for (BusinessParser.BusinessInCategory businessInCategory : businessInCategoryList) {
                table_businessInCategory.setString(1, businessInCategory.business_id);
                table_businessInCategory.setString(2, businessInCategory.category);
                table_businessInCategory.addBatch();
            }
            table_businessInCategory.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void attributeInserts(ArrayList<BusinessParser.Attribute> attributeList) {
        if(!(Settings.RELOAD_ALL || Settings.RELOAD_ATTRIBUTE)) {return;}

        try {
            for (BusinessParser.Attribute attribute : attributeList) {
                table_attribute.setString(1, attribute.business_id);
                table_attribute.setString(2, attribute.key);
                table_attribute.addBatch();
            }
            table_attribute.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void friendInserts(ArrayList<UserParser.Friend> friendList) {
        if(!(Settings.RELOAD_ALL || Settings.RELOAD_FRIEND)) {return;}

        try {
            for (UserParser.Friend friend : friendList) {
                table_friend.setString(1, friend.user_id);
                table_friend.setString(2, friend.friend_id);
                table_friend.addBatch();
            }
            table_friend.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reviewInserts(ArrayList<ReviewParser.Review> reviewList) {
        if(!(Settings.RELOAD_ALL || Settings.RELOAD_REVIEW)) {return;}
        
        try {
            for (ReviewParser.Review review : reviewList) {
                table_review.setString(1, review.review_id);
                table_review.setString(2, review.user_id);
                table_review.setString(3, review.date);
                table_review.setInt(4, review.stars);
                table_review.setString(5, review.business_id);
                table_review.setString(6, review.text);
                table_review.addBatch();;
            }
            table_review.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkinInserts(ArrayList<CheckinParser.CheckinInfo> checkinInfoList) {
        if(!(Settings.RELOAD_ALL || Settings.RELOAD_CHECKININFO)) {return;}
        
        try {
            for (CheckinParser.CheckinInfo checkinInfo : checkinInfoList) {
                table_checkinInfo.setString(1, checkinInfo.business_id);
                table_checkinInfo.setInt(2, checkinInfo.hour);
                table_checkinInfo.setInt(3, checkinInfo.day);
                table_checkinInfo.addBatch();
            }
            table_checkinInfo.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void businessInserts(ArrayList<BusinessParser.Business> businessList) {
        if(!(Settings.RELOAD_ALL || Settings.RELOAD_BUSINESS)) {return;}
        
        try {
            for (BusinessParser.Business business : businessList) {
                table_business.setString(1, business.business_id);
                table_business.setString(2, business.full_address);
                table_business.setString(3, business.open.substring(0, 1).toUpperCase());
                table_business.setString(4, business.city);
                table_business.setString(5, business.state);
                table_business.setFloat(6, business.latitude);
                table_business.setFloat(7, business.longitude);
                table_business.setString(8, business.name);
                table_business.addBatch();
            }
            table_business.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void userInserts(ArrayList<UserParser.Yelp_user> yelp_users){
        if(!(Settings.RELOAD_ALL || Settings.RELOAD_YELP_USER)) {return;}

        try {
            for (UserParser.Yelp_user yelp_user: yelp_users) {
                table_yelp_user.setString(1, yelp_user.yelping_since);
                table_yelp_user.setString(2, yelp_user.name);
                table_yelp_user.setString(3, yelp_user.user_id);
                table_yelp_user.setInt(4, yelp_user.review_count);
                table_yelp_user.setInt(5, yelp_user.friend_count);
                table_yelp_user.setFloat(6, yelp_user.average_star);
                table_yelp_user.setInt(7, yelp_user.num_votes);
                table_yelp_user.addBatch();;
            }
            table_yelp_user.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initializePreparedStatements() {
        try {
            table_yelp_user = connection.prepareStatement("INSERT INTO Yelp_user VALUES(TO_DATE(?,'yyyy-mm'),?,?,?,?,?,?)");
            table_business = connection.prepareStatement("INSERT INTO Business VALUES(?,?,?,?,?,?,?,?)");
            table_checkinInfo = connection.prepareStatement("INSERT INTO CheckinInfo VALUES(?,?,?)");
            table_review = connection.prepareStatement("INSERT INTO Review VALUES(?,?,TO_DATE(?,'yyyy-mm-dd'),?,?,?)");
            
            table_friend = connection.prepareStatement("INSERT INTO Friend VALUES(?,?)");
            table_neighborhood = connection.prepareStatement("INSERT INTO Neighborhood VALUES(?,?)");
            table_voteOn = connection.prepareStatement("INSERT INTO VoteOn VALUES(?,?)");
            table_operationTime = connection.prepareStatement("INSERT INTO OperationTime VALUES(?,?,TO_DATE(?,'hh24:mi'),TO_DATE(?,'hh24,mi'))");
            table_categoryHasCategory = connection.prepareStatement("INSERT INTO CategoryHasCategory VALUES(?,?)");
            table_businessInCategory = connection.prepareStatement("INSERT INTO BusinessInCategory VALUES(?,?)");
            table_attribute = connection.prepareStatement("INSERT INTO Attribute VALUES(?,?)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closePreparedStatements() {
        try {
            table_yelp_user.close();
            table_business.close();
            table_checkinInfo.close();
            table_review.close();

            table_friend.close();
            table_neighborhood.close();
            table_voteOn.close();
            table_operationTime.close();
            table_categoryHasCategory.close();
            table_businessInCategory.close();
            table_attribute.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
