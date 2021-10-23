import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import config.*;
import src.DateLabelFormatter;
import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import org.jdatepicker.impl.*;
import src.DbHelper;
import src.MyJSONParser;
import java.text.SimpleDateFormat;

class Yelp_GUI{
    // Database
    DbHelper db = new DbHelper();
    // Constant
    final Dimension smallBoxDimension = new Dimension(150,25);
    final Dimension scrollBoxDimension = new Dimension(100, 100);
    final String[] emptyStringArray = {};
    final String[][] emptyString2DArray = {};
    final String[] logicOperators = {"", ">", "=", "<"};
    final String[] searchCriteria = {"AND", "OR"};
    final String[] businessCols = {};
    final String datePattern = "yyyy-MM-dd";
    final SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
    // Abstract components
    JFrame frame = new JFrame("Yelp_GUI");
    JPanel panel_business = new JPanel();
    JPanel panel_result = new JPanel();
    JPanel panel_user = new JPanel();
    JPanel panel_query = new JPanel();
    JFrame resultDetailsFrame = new JFrame("Result Details");
    JPanel panel_resultDetails = new JPanel();
    // Business Search components
    JPanel mainCategoryList = newCheckBoxList(emptyStringArray);
    JPanel subCategoryList = newCheckBoxList(emptyStringArray);
    JPanel attributeList = newCheckBoxList(emptyStringArray);
    JComboBox<String> searchBusinessFor = newStringComboBoxes(searchCriteria);
    // Business containers
    HashSet<String> selectedMainCategories = new HashSet<>();
    HashSet<String> selectedSubCategories = new HashSet<>();
    HashSet<String> selectedAttributes = new HashSet<>();
    String businessRealtion = getRelation(searchBusinessFor);
    // Review Filter components
    JDatePickerImpl fromDate = newDatePicker();
    JDatePickerImpl toDate = newDatePicker();
    JComboBox<String> starOperator = newStringComboBoxes(logicOperators);
    JTextField starValue = new JTextField();
    JComboBox<String> voteOperator = newStringComboBoxes(logicOperators);
    JTextField voteValue = new JTextField();
    JButton applyFilter = new JButton("Apply");
    JButton resetFilter = new JButton("Reset");
    // Review Filter containers
    String reviewWhereArgs = "";
    // User components
    JDatePickerImpl memberSince = newDatePicker();
    JComboBox<String> reviewCountOperator = newStringComboBoxes(logicOperators);
    JTextField reviewCountValue = new JTextField();
    JComboBox<String> numberOfFriendsOperator = newStringComboBoxes(logicOperators);
    JTextField numberOfFriendsValue = new JTextField();
    JComboBox<String> averageStarsOperator = newStringComboBoxes(logicOperators);
    JTextField averageStarsValue = new JTextField();
    JComboBox<String> numberOfVotesOperator = newStringComboBoxes(logicOperators);
    JTextField numberOfVotesValue = new JTextField();
    JComboBox<String> searchUserFor = newStringComboBoxes(searchCriteria);
    JButton searchUser = new JButton("Search User");
    JButton resetUser = new JButton("Reset");
    // User containers
    String userWhereArgs = "";
    String userRelation = searchUserFor.getSelectedItem().toString();
    String friendCountGroupArgs = "";
    String userReviewGroupArgs = "";
    // Result components
    JTable resultTable = newEmptyJTable();
    // Query components
    JTextArea textArea = new JTextArea();
    JButton executeSql = new JButton("Execute Query");
    // Result Details component
    JTable resultDetailsTable = newEmptyJTable();

    Yelp_GUI() {
        drawFrame();
        db.initializeConnection();
        setFunc();
    }

    /* -------------------FUNCTIONALITIES----------------- */
    void setFunc() {
        setBusinessFunc();
        setResultFunc();
        setUserFunc();
        setQueryFunc();
    }

    void setBusinessFunc() {
        updateMainCategory();
        setSearchBusinessFor();
        setReviewFilterButtons();
    }

    void setResultFunc() {
        // Nothing
    }

    void setUserFunc() {
        setSearchUserFor();
        setUserSearchButtons();
    }

    void setQueryFunc() {
        setExecuteButton();
    }

    /* -------------------FUNCTIONALITY HELPERS----------------- */
    void updateMainCategory() {
        mainCategoryList.removeAll();
        selectedMainCategories = new HashSet<>();

        String result = db.executeQuery("SELECT * FROM MainCategory");

        String[] categories = result.split(DbHelper.newLine);
        for (int i = 1; i < categories.length; i++) {
            String category = categories[i];
            if (category.length() == 0) {continue;}
            JCheckBox mainCategoryCheckBox = new JCheckBox(category);
            mainCategoryList.add(mainCategoryCheckBox);
            mainCategoryCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JCheckBox currentCheckBox = (JCheckBox) e.getSource();
                    String text = currentCheckBox.getText();
                    if (currentCheckBox.isSelected()) {
                        selectedMainCategories.add(text);
                    } else {
                        if (selectedMainCategories.contains(text)) {
                            selectedMainCategories.remove(text);
                        }
                    }
                    updateSubCategory();
                    updateAttribute();
                    updateBusinessResult();
                }
            });
        }
        mainCategoryList.updateUI();
    }

    void updateSubCategory() {
        subCategoryList.removeAll();
        selectedSubCategories = new HashSet<>();

        if (selectedMainCategories.isEmpty()) {
            subCategoryList.updateUI();
            return;
        }

        String selection = "SELECT DISTINCT(sub) FROM CategoryHasCategory WHERE super = '";
        String sql = "";
        for (String mainCategory : selectedMainCategories) {
            if (sql.length()>0) {
                sql += businessRealtion;
            }
            sql += selection + MyJSONParser.sqlString(mainCategory) + "'";
        }
        //System.out.println(sql);
        String result = db.executeQuery(sql);

        // Process the result
        String[] subCategories = result.split(DbHelper.newLine);
        for (int i = 1; i < subCategories.length; i++) {
            String subCategory = subCategories[i];
            if (subCategory.length() == 0) {continue;}
            JCheckBox subCategoryCheckBox = new JCheckBox(subCategory);
            subCategoryList.add(subCategoryCheckBox);
            subCategoryCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JCheckBox currentCheckBox = (JCheckBox) e.getSource();
                    String text = currentCheckBox.getText();
                    if (currentCheckBox.isSelected()) {
                        selectedSubCategories.add(text);
                    } else {
                        if (selectedSubCategories.contains(text)) {
                            selectedSubCategories.remove(text);
                        }
                    }
                    updateAttribute();
                    updateBusinessResult();
                }
            });
        }
        subCategoryList.updateUI();
    }

    void updateAttribute() {
        attributeList.removeAll();
        selectedAttributes = new HashSet<>();

        if (selectedSubCategories.isEmpty()) {
            attributeList.updateUI();
            return;
        }

        
        String sql = "SELECT DISTINCT(key) " + 
                            "FROM Attribute " +
                            "WHERE b_id IN (" + generateInArgs() + ")";
        
        //System.out.println(sql);
        String result = db.executeQuery(sql);
        
        // Process the result
        String[] attributes = result.split(DbHelper.newLine);
        for (int i = 1; i < attributes.length; i++) {
            String attribute = attributes[i];
            if (attribute.length()==0) { continue;}
            JCheckBox attributeCheckBox = new JCheckBox(attribute);
            attributeList.add(attributeCheckBox);
            attributeCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JCheckBox currentCheckBox = (JCheckBox) e.getSource();
                    String text = currentCheckBox.getText();
                    if (currentCheckBox.isSelected()) {
                        selectedAttributes.add(text);
                    } else {
                        if (selectedAttributes.contains(text)) {
                            selectedAttributes.remove(text);
                        }
                    }
                    updateBusinessResult();
                }
            });
            
        }
        attributeList.updateUI();
    }

    void updateBusinessResult() {
        Instant start = Instant.now();
        if (selectedMainCategories.isEmpty()) {
            updateResultToTable("", resultTable);
            return;
        }

        String sql = "SELECT B.id AS ID, B.name AS Business, B.city AS City, B.state AS State, AVG(R.star) AS Star " + 
                    "FROM Business B " +
                    "LEFT JOIN Review R ON " +
                    "B.id = R.b_id " +  
                    "WHERE B.id IN (" + generateInArgs() + ") " + reviewWhereArgs +
                    "GROUP BY (B.id, B.name, B.city, B.state)";
        
        String result = db.executeQuery(sql);
        System.out.println(sql);
        updateResultToTable(result, resultTable);

        resultTable.removeColumn(resultTable.getColumn("ID"));

        resultTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = resultTable.getSelectedRow();
                    String b_id = resultTable.getModel().getValueAt(row, 0).toString();
                    String table = "SELECT R.id AS Id, R.publish_date AS Review_Date, R.star AS Stars, R.u_id AS UserID, SUM(case V.vote_type when 'useful' then 1 else 0 end) AS Useful_Votes " +
                                "FROM Review R " +
                                "LEFT JOIN VoteOn V ON " +
                                "R.id = V.r_id " +
                                "WHERE R.b_id = '" + b_id + "' " + reviewWhereArgs +
                                "GROUP BY R.id, R.publish_date, R.star, R.u_id";
                    
                                String sql = "SELECT T.Review_Date AS Review_Date, T.Stars AS Stars, U.name AS Username, R.content AS Review_Text, T.Useful_Votes AS Useful_Votes " +
                                "FROM Review R, (" + table + ") T, Yelp_user U " + 
                                "WHERE R.id = T.Id AND T.UserID=U.id";
                    //System.out.println(b_id);
                    String result = db.executeQuery(sql);
                    updateResultToTable(result, resultDetailsTable);
                    resultDetailsFrame.setVisible(true);
                }
            }
        });
        //businessResultBuffer;

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("updateBusinessResult() finishes in " + timeElapsed+" ms");
    }

    String generateInArgs() {
        String inArgs = "";
        String temp = "";

        for (String mainCategory : selectedMainCategories) {
            if (temp.length()>0) {
                temp += businessRealtion;
            }
            temp += "SELECT BIC1.b_id FROM BusinessInCategory BIC1 WHERE BIC1.c_name = '" + MyJSONParser.sqlString(mainCategory) + "'";
        }

        inArgs += temp;

        temp = "";
        for (String subCategory : selectedSubCategories) {
            if (temp.length()>0) {
                temp += businessRealtion;
            }
            temp += "SELECT BIC2.b_id FROM BusinessInCategory BIC2 WHERE BIC2.c_name = '" + MyJSONParser.sqlString(subCategory) + "'";
        }

        if (temp.length()>0) {
            inArgs = "(" + inArgs + ") INTERSECT (" + temp + ")";
        }

        temp = "";
        for (String attribute : selectedAttributes) {
            if (temp.length() > 0) {
                temp += businessRealtion;
            }
            temp += "SELECT A.b_id FROM Attribute A WHERE A.key = '" + MyJSONParser.sqlString(attribute) + "'";
        }

        if (temp.length()>0) {
            inArgs = "(" + inArgs + ") INTERSECT (" + temp + ")";
        }

        return inArgs;
    }

    void setReviewFilterButtons() {
        // Apply button
        applyFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String args = "";

                Date selectedFromDate = (Date) fromDate.getModel().getValue();
                if (selectedFromDate != null) {
                    args += "AND R.publish_date > date '" + dateFormatter.format(selectedFromDate) + "' ";
                }

                Date selectedToDate = (Date) toDate.getModel().getValue();
                if (selectedToDate != null) {
                    if (selectedFromDate != null && selectedFromDate.compareTo(selectedToDate) > 0) {
                        JOptionPane.showMessageDialog(frame, "Please select a valid duration");
                        return;
                    }
                    args += "AND R.publish_date < date '" + dateFormatter.format(selectedToDate) + "' ";
                }
                
                String starOp = starOperator.getSelectedItem().toString();
                int starVal;
                try {
                    starVal = Integer.parseInt(starValue.getText());
                    if (!starOp.isEmpty()) {
                        args += "AND R.star" + starOp + starVal + " ";
                    }
                } catch (NumberFormatException ex) {
                    // Nothing
                }
                
                String voteOp = voteOperator.getSelectedItem().toString();
                int voteVal;
                try {
                    voteVal = Integer.parseInt(voteValue.getText());
                    if (!voteOp.isEmpty()) {
                        args += "AND (SELECT COUNT(*) FROM VoteOn V1 WHERE V1.r_id=R.id AND V1.vote_type='useful')" + voteOp + voteVal + " ";
                    }
                } catch (NumberFormatException ex) {
                    // Nothing
                }

                if (args.length() == 0) {
                    return;
                }
                reviewWhereArgs = args;
                updateBusinessResult();
            }
        });

        // Reset button
        resetFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearReviewFilter();
                updateBusinessResult();
            }
        });
    }

    void clearReviewFilter() {
        reviewWhereArgs = "";
        fromDate.getModel().setValue(null);;
        toDate.getModel().setValue(null);
        starOperator.setSelectedIndex(0);
        starValue.setText("");
        voteOperator.setSelectedIndex(0);
        voteValue.setText("");
    }

    void setSearchBusinessFor() {
        searchBusinessFor.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    businessRealtion = getRelation(searchBusinessFor);
                    updateMainCategory();
                    updateSubCategory();
                    updateAttribute();
                    updateBusinessResult();
                }
            }
        });
    }

    String getRelation(JComboBox<String> searchFor) {
        if (searchFor.getSelectedItem().toString().compareTo("AND") == 0) {
            return " INTERSECT ";
        } else {
            return " UNION ";
        }
    }

    void updateUserResult() {
        Instant start = Instant.now();

        String sql = "SELECT U.id AS ID, U.name AS Username, U.yelping_since AS Yelp_since, U.friend_count AS nFriends, U.review_count AS nReviews, U.average_star AS avgStar, U.num_votes AS nVotes " + 
                    "FROM Yelp_user U";
        if (userWhereArgs.length() > 0) {
            sql += " WHERE " + userWhereArgs;
        }

        System.out.println(sql);
        String result = db.executeQuery(sql);
        updateResultToTable(result, resultTable);
        
        resultTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = resultTable.getSelectedRow();
                    String u_id = resultTable.getModel().getValueAt(row, 0).toString();
                    String table = "SELECT R.id AS Id, R.publish_date AS Review_Date, R.star AS Stars, R.b_id AS BusinessId, SUM(case V.vote_type when 'useful' then 1 else 0 end) AS Useful_Votes " +
                                "FROM Review R " +
                                "LEFT JOIN VoteOn V ON " +
                                "R.id = V.r_id " +
                                "WHERE R.u_id = '" + u_id + "' " +
                                "GROUP BY R.id, R.publish_date, R.star, R.b_id";
                    
                                String sql = "SELECT T.Review_Date AS Review_Date, T.Stars AS Stars, B.name AS BusinessName, R.content AS Review_Text, T.Useful_Votes AS Useful_Votes " +
                                "FROM Review R, (" + table + ") T, Business B " + 
                                "WHERE R.id = T.Id AND T.BusinessId=B.id";
                    //System.out.println(b_id);
                    String result = db.executeQuery(sql);
                    updateResultToTable(result, resultDetailsTable);
                    resultDetailsFrame.setVisible(true);
                }
            }
        });

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("updateUserResult() finishes in " + timeElapsed+" ms");
    }

    void setSearchUserFor() {
        searchUserFor.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    userRelation = searchUserFor.getSelectedItem().toString();
                }
            }
        });
    }

    void setUserSearchButtons() {
        // Button Search
        searchUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userWhereArgs = "";

                // Member Since
                Date selectedMemberSinceDate = (Date) memberSince.getModel().getValue();
                if (selectedMemberSinceDate != null) {
                    if (userWhereArgs.length() > 0) {userWhereArgs += " " + userRelation + " ";}
                    userWhereArgs += "U.yelping_since > date '" + dateFormatter.format(selectedMemberSinceDate) + "'";
                }

                // Review Count
                String reviewCountOp = reviewCountOperator.getSelectedItem().toString();
                int reviewCountVal;
                try {
                    reviewCountVal = Integer.parseInt(reviewCountValue.getText());
                    if (!reviewCountOp.isEmpty()) {
                        if (userWhereArgs.length() > 0) {userWhereArgs += " " + userRelation + " ";}
                        userWhereArgs += "U.review_count" + reviewCountOp + reviewCountVal;
                    }
                } catch (NumberFormatException ex) {}

                // Number of Friends
                String numberOfFriendsOp = numberOfFriendsOperator.getSelectedItem().toString();
                int numberOfFriendsVal;
                try {
                    numberOfFriendsVal = Integer.parseInt(numberOfFriendsValue.getText());
                    if (!numberOfFriendsOp.isEmpty()) {
                        if (userWhereArgs.length() > 0) {userWhereArgs += " " + userRelation + " ";}
                        userWhereArgs += "U.friend_count" + numberOfFriendsOp + numberOfFriendsVal;
                    }
                } catch (NumberFormatException ex) {}

                // Average stars
                String avgStarsOp = averageStarsOperator.getSelectedItem().toString();
                int avgStarsVal;
                try {
                    avgStarsVal = Integer.parseInt(averageStarsValue.getText());
                    if (!avgStarsOp.isEmpty()) {
                        if (userWhereArgs.length() > 0) {userWhereArgs += " " + userRelation + " ";}
                        userWhereArgs += "U.average_star" + avgStarsOp + avgStarsVal;
                    }
                } catch (NumberFormatException ex) {}

                // Number of Votes
                String nVotesOp = numberOfVotesOperator.getSelectedItem().toString();
                int nVotesVal;
                try {
                    nVotesVal = Integer.parseInt(numberOfVotesValue.getText());
                    if (!nVotesOp.isEmpty()) {
                        if (userWhereArgs.length() > 0) {userWhereArgs += " " + userRelation + " ";}
                        userWhereArgs += "U.num_votes" + nVotesOp + nVotesVal;
                    }
                } catch (NumberFormatException ex) {}

                updateUserResult();
            }
        });

        // Button Reset
        resetUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearUserSearch();
            }
        });
    }

    void clearUserSearch() {
        userWhereArgs = "";

        memberSince.getModel().setValue(null);;
        reviewCountOperator.setSelectedIndex(0);
        reviewCountValue.setText("");
        numberOfFriendsOperator.setSelectedIndex(0);
        numberOfFriendsValue.setText("");
        averageStarsOperator.setSelectedIndex(0);
        averageStarsValue.setText("");
        numberOfVotesOperator.setSelectedIndex(0);
        numberOfVotesValue.setText("");
        searchUserFor.setSelectedIndex(0);
    }

    void updateResultToTable(String results, JTable table) {
        DefaultTableModel model = new DefaultTableModel();

        if (results.length() > 0) {
            String[] resultsArray = MyJSONParser.normalString(results).split(DbHelper.newLine);
            String[] colNames;
            boolean isSingleCol = false;
            if (resultsArray[0].contains(DbHelper.seperator)){
                colNames = resultsArray[0].split(DbHelper.seperator);
            } else {
                isSingleCol = true;
                colNames = new String[]{resultsArray[0]};
            }
            
            for (String colName : colNames) {
                model.addColumn(colName);
            }
            
            for (int i = 1; i < resultsArray.length; i++) {
                if (isSingleCol) {
                    model.addRow(new String[]{resultsArray[i]});
                } else {
                    model.addRow(resultsArray[i].split(DbHelper.seperator));
                }
            }
        }
        table.setModel(model);
        for (MouseListener ml : table.getMouseListeners()) {
            table.removeMouseListener(ml);
        }
        table.updateUI();
    }

    void setExecuteButton() {
        executeSql.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Instant start = Instant.now();

                String sql = textArea.getText().trim();
                if (sql.length() == 0) {
                    return;
                }
                if (sql.contains(";")) {
                    sql = sql.replace(";", "");
                }
                String result = "";
                result = db.executeQuery(sql);
                if (result == "") {
                    JOptionPane.showMessageDialog(frame, "Not a valid sql");
                    return;
                }
                updateResultToTable(result, resultTable);

                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();
                System.out.println("executeQuery() finishes in " + timeElapsed+" ms");
            }
        });
    }

    /* -------------------DRAWERS----------------- */
    void drawFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600,780);
        frame.setLocation(100, 100);
        frame.setResizable(false);

        resultDetailsFrame.setSize(1000,500);
        resultDetailsFrame.setLocation(200, 200);
        resultDetailsFrame.setResizable(false);

        Container contentPane = frame.getContentPane();
        GroupLayout layout = new GroupLayout(contentPane);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        contentPane.setLayout(layout);

        drawBusiness();
        drawResult();
        drawUser();
        drawQuery();
        drawResultDetails();

        layout.setHorizontalGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup()
                                            .addComponent(panel_business,1000,1000,1000)
                                            .addComponent(panel_user,1000,1000,1000))
                                .addGroup(layout.createParallelGroup()
                                            .addComponent(panel_result,500,500,500)
                                            .addComponent(panel_query,500,500,500))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup()
                                            .addComponent(panel_business,500,500,500)
                                            .addComponent(panel_result,475,475,475))
                                .addGroup(layout.createParallelGroup()
                                            .addComponent(panel_user)
                                            .addComponent(panel_query))
        );
        
        resultDetailsFrame.getContentPane().add(panel_resultDetails);

        frame.setVisible(true);
    }

    void drawBusiness() {
        GroupLayout layout = new GroupLayout(panel_business);
        panel_business.setLayout(layout);

        JLabel title = new JLabel("BUSINESS SEARCH");

        JPanel panel_category = newScrollCheckBoxList("Category", mainCategoryList);
        
        JPanel panel_subCategory = newScrollCheckBoxList("Sub-Category", subCategoryList);

        JPanel panel_attribute = newScrollCheckBoxList("Attribute", attributeList);

        JPanel panel_review = newReviewPanel();

        JPanel search_logic = newComponentRow(new Dimension(200,0), newComponentRow(new JLabel("    Search for: "), searchBusinessFor));

        layout.setHorizontalGroup(layout.createParallelGroup()
                                    .addComponent(title)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(panel_category,250,250,250)
                                        .addComponent(panel_subCategory,250,250,250)
                                        .addComponent(panel_attribute,250,250,250)
                                        .addComponent(panel_review,250,250,250))
                                    .addComponent(search_logic)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                                .addComponent(title)
                                .addGroup(layout.createParallelGroup()
                                    .addComponent(panel_category)
                                    .addComponent(panel_subCategory)
                                    .addComponent(panel_attribute)
                                    .addComponent(panel_review))
                                .addComponent(search_logic)
        );
    }

    void drawResult() {
        GroupLayout layout = new GroupLayout(panel_result);
        panel_result.setLayout(layout);

        JLabel title = new JLabel("RESULT");

        JScrollPane results = new JScrollPane(resultTable);
        
        layout.setHorizontalGroup(layout.createParallelGroup()
                                    .addComponent(title)
                                    .addComponent(results)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                                .addComponent(title)
                                .addComponent(results)
        );
    }

    void drawUser() {
        panel_user.setLayout(new BoxLayout(panel_user, BoxLayout.Y_AXIS));;

        JLabel title = new JLabel("USER SEARCH");
        JPanel row1 = newComponentRow(new Dimension(85,0), newFixedLabel("    Member Since", smallBoxDimension), memberSince);
        JPanel row2 = newComponentRow(new Dimension(50,0), newFixedLabel("    Review Count", smallBoxDimension), reviewCountOperator, newLabeledTextField("value", reviewCountValue));
        JPanel row3 = newComponentRow(new Dimension(50,0), newFixedLabel("    Number of Friends", smallBoxDimension), numberOfFriendsOperator, newLabeledTextField("value", numberOfFriendsValue));
        JPanel row4 = newComponentRow(new Dimension(50,0), newFixedLabel("    Average Stars", smallBoxDimension), averageStarsOperator, newLabeledTextField("value", averageStarsValue));
        JPanel row5 = newComponentRow(new Dimension(50,0), newFixedLabel("    Number of Votes", smallBoxDimension), numberOfVotesOperator, newLabeledTextField("value", numberOfVotesValue));
        JPanel search_logic = newComponentRow(new Dimension(50,0), newComponentRow(new JLabel("    Search for: "), searchUserFor), searchUser, resetUser);
        
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);
        row3.setAlignmentX(Component.LEFT_ALIGNMENT);
        row4.setAlignmentX(Component.LEFT_ALIGNMENT);
        row5.setAlignmentX(Component.LEFT_ALIGNMENT);
        search_logic.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel_user.add(Box.createRigidArea(new Dimension(0, 15)));
        panel_user.add(title);
        panel_user.add(row1);
        panel_user.add(Box.createRigidArea(new Dimension(0, 5)));
        panel_user.add(row2);
        panel_user.add(Box.createRigidArea(new Dimension(0, 5)));
        panel_user.add(row3);
        panel_user.add(Box.createRigidArea(new Dimension(0, 5)));
        panel_user.add(row4);
        panel_user.add(Box.createRigidArea(new Dimension(0, 5)));
        panel_user.add(row5);
        panel_user.add(Box.createRigidArea(new Dimension(0, 5)));
        panel_user.add(search_logic);
    }

    void drawQuery() {
        BoxLayout boxLayout = new BoxLayout(panel_query, BoxLayout.Y_AXIS);
        textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        executeSql.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_query.setLayout(boxLayout);
        panel_query.add(textArea);
        panel_query.add(executeSql);
    }

    void drawResultDetails() {
        GroupLayout layout = new GroupLayout(panel_resultDetails);
        panel_resultDetails.setLayout(layout);

        JLabel title = new JLabel("RESULT DETAILS");

        JScrollPane results = new JScrollPane(resultDetailsTable);
        
        layout.setHorizontalGroup(layout.createParallelGroup()
                                    .addComponent(title)
                                    .addComponent(results)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                                .addComponent(title)
                                .addComponent(results)
        );
    }

    /* -------------------DRAWER HELPERS----------------- */
    /*
    JTable newUserResultTable(String[][] data, String[] cols) {
        //String[] cols = {"UserID", "Name", "YelpSince", "nReviews", "nFriends", "nStars", "nVotes"};
        JTable resultTable = new JTable(data, cols);

        resultTable.setDefaultEditor(Object.class, null);
        resultTable.setFillsViewportHeight(true);

        return resultTable;
    }

    void updateBusinessResultTable(String[][] data, String[] cols) {
        resultTable.getColumnModel();
        resultTable.setDefaultEditor(Object.class, null);
        resultTable.setFillsViewportHeight(true);
        resultTable.getColumnModel().getColumn(1).setMaxWidth(100);
        resultTable.getColumnModel().getColumn(2).setMaxWidth(100);
        resultTable.getColumnModel().getColumn(3).setMaxWidth(100);

    }
    */
    JTable newEmptyJTable() {
        JTable resultTable = new JTable(new DefaultTableModel());
        resultTable.setDefaultEditor(Object.class, null);
        resultTable.setFillsViewportHeight(true);
        return resultTable;
    }

    JPanel newReviewPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("REVIEW FILTER");
        JPanel row1 = newComponentRow(new JLabel("from "), fromDate);
        JPanel row2 = newComponentRow(new JLabel("  to   "), toDate);
        JPanel row3 = newComponentRow(new JLabel("#Star"), starOperator);
        JPanel row4 = newLabeledTextField("value", starValue);
        JPanel row5 = newComponentRow(new JLabel("#Vote"), voteOperator);
        JPanel row6 = newLabeledTextField("value", voteValue);

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        row1.setAlignmentX(Component.CENTER_ALIGNMENT);
        row2.setAlignmentX(Component.CENTER_ALIGNMENT);
        row3.setAlignmentX(Component.CENTER_ALIGNMENT);
        row4.setAlignmentX(Component.CENTER_ALIGNMENT);
        row5.setAlignmentX(Component.CENTER_ALIGNMENT);
        row6.setAlignmentX(Component.CENTER_ALIGNMENT);
        applyFilter.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetFilter.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(row1);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(row2);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(row3);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(row4);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(row5);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(row6);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(applyFilter);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(resetFilter);

        //panel.setBorder(BorderFactory.createLineBorder(Color.black));
        return panel;
    }

    JPanel newComponentRow(JComponent...jComponents) {
        return newComponentRow(new Dimension(5,0), jComponents);
    }

    JPanel newComponentRow(Dimension gapDimension, JComponent...jComponents) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        for (JComponent jComponent:jComponents) {
            row.add(jComponent);
            row.add(Box.createRigidArea(gapDimension));
        }

        return row;
    }
    
    JLabel newFixedLabel(String text, Dimension dimension) {
        JLabel textlabel = new JLabel(text);
        textlabel.setMaximumSize(dimension);
        return textlabel;
    }

    JPanel newLabeledTextField(String text, JTextField textField) {
        textField.setMaximumSize(smallBoxDimension);
        return newComponentRow(new JLabel(text), textField);
    } 

    JDatePickerImpl newDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.setMaximumSize(smallBoxDimension);
        return datePicker;
    }

    JComboBox<String> newStringComboBoxes(String[] labels) {
        JComboBox<String> myComboBox = new JComboBox<String>();

        for (String label : labels) {
            myComboBox.addItem(label);
        }

        myComboBox.setMaximumSize(smallBoxDimension);
        return myComboBox;
    }

    JPanel newCheckBoxList(String[] labels) {
        JPanel categoryList = new JPanel();
        categoryList.setLayout(new BoxLayout(categoryList, BoxLayout.Y_AXIS));
        for (String label : labels) {
            categoryList.add(new JCheckBox(label));
        }
        return categoryList;
    }

    JPanel newScrollCheckBoxList(String text, JPanel categoryList) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(text);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JScrollPane scrollableCheckBoxes = new JScrollPane(categoryList);
        scrollableCheckBoxes.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(title);
        panel.add(scrollableCheckBoxes);

        return panel;
    }
}

public class HW3 {
    public static void main(String[] args) {
        new Yelp_GUI();
    }
}
