--ENTITIES--

CREATE TABLE Yelp_user (
    yelping_since DATE NOT NULL,
    name VARCHAR2(40) NOT NULL,
    id CHAR(22) NOT NULL,
    review_count INTEGER NOT NULL,
    friend_count INTEGER NOT NULL,
    average_star NUMBER NOT NULL,
    num_votes INTEGER NOT NULL,
    CONSTRAINT pk_user_id PRIMARY KEY(id)
);


CREATE TABLE Business (
    id CHAR(22) NOT NULL,
    full_street VARCHAR2(120) NOT NULL,
    is_open CHAR NOT NULL,
    city VARCHAR2(30) NOT NULL,
    state VARCHAR2(3) NOT NULL,
    latitude NUMBER,
    longitude NUMBER,
    name VARCHAR2(100) NOT NULL,
    CONSTRAINT pk_business_id PRIMARY KEY(id),
    CONSTRAINT is_valid_boolean CHECK (is_open IN ('T', 'F'))
);

CREATE TABLE Review (
    id CHAR(22) NOT NULL,
    u_id CHAR(22) NOT NULL,
    publish_date DATE NOT NULL,
    star INTEGER NOT NULL,
    b_id CHAR(22) NOT NULL,
    content LONG NOT NULL,
    CONSTRAINT pk_review_id PRIMARY KEY(id),
    CONSTRAINT fk_review_uid FOREIGN KEY(u_id) REFERENCES Yelp_user ON DELETE CASCADE,
    CONSTRAINT fk_review_bid FOREIGN KEY(b_id) REFERENCES Business ON DELETE CASCADE,
    CONSTRAINT is_valid_star CHECK (star > 0 AND star < 6)
);

CREATE TABLE CheckinInfo (
    b_id CHAR(22) NOT NULL,
    hour INTEGER NOT NULL,
    day INTEGER NOT NULL,
    CONSTRAINT fk_checkininfo_bid FOREIGN KEY(b_id) REFERENCES Business ON DELETE CASCADE,
    CONSTRAINT is_valid_hour CHECK (hour >= 0 AND hour < 24),
    CONSTRAINT is_valid_day CHECK (day >= 0 AND day < 7)
);

--RELATIONSHIP--

CREATE TABLE Friend (
    u_id1 CHAR(22) NOT NULL,
    u_id2 CHAR(22) NOT NULL,
    CONSTRAINT pk_friend PRIMARY KEY(u_id1, u_id2),
    CONSTRAINT fk_friend_uid1 FOREIGN KEY(u_id1) REFERENCES Yelp_user ON DELETE CASCADE,
    CONSTRAINT fk_friend_uid2 FOREIGN KEY(u_id2) REFERENCES Yelp_user ON DELETE CASCADE
);

CREATE TABLE Neighborhood (
    b_id CHAR(22) NOT NULL,
    hood VARCHAR2(30) NOT NULL,
    constraint fk_neighborhood FOREIGN KEY(b_id) REFERENCES Business ON DELETE CASCADE
);

CREATE TABLE VoteOn (
    r_id CHAR(22) NOT NULL,
    vote_type VARCHAR2(10) NOT NULL,
    constraint fk_voteon FOREIGN KEY(r_id) REFERENCES Review ON DELETE CASCADE
);

CREATE TABLE OperationTime (
    b_id CHAR(22) NOT NULL,
    day INTEGER NOT NULL,
    close_hour DATE NOT NULL,
    open_hour DATE NOT NULL,
    constraint pk_operationtime PRIMARY KEY(b_id, day),
    constraint fk_operationtime FOREIGN KEY(b_id) REFERENCES Business ON DELETE CASCADE,
    CONSTRAINT is_valid_operationDay CHECK (day >= 0 AND day < 7)
);

CREATE TABLE CategoryHasCategory (
    super VARCHAR2(100) NOT NULL,
    sub VARCHAR2(100) NOT NULL,
    constraint pk_chc PRIMARY KEY(super, sub)
);

CREATE TABLE BusinessInCategory (
    b_id CHAR(22) NOT NULL,
    c_name VARCHAR2(100) NOT NULL,
    constraint pk_bic PRIMARY KEY(b_id, c_name),
    constraint fk_bic FOREIGN KEY(b_id) REFERENCES Business ON DELETE CASCADE
);

CREATE TABLE Attribute (
    b_id CHAR(22) NOT NULL,
    key VARCHAR2(50) NOT NULL,
    constraint pk_attribute PRIMARY KEY(b_id, key),
    constraint fk_attribute FOREIGN KEY(b_id) REFERENCES Business ON DELETE CASCADE
);

CREATE TABLE MainCategory (
    name VARCHAR2(100) NOT NULL,
    PRIMARY KEY(name)
);


INSERT INTO MainCategory VALUES('Active Life');
INSERT INTO MainCategory VALUES('Arts &' ||' Entertainment');
INSERT INTO MainCategory VALUES('Automotive');
INSERT INTO MainCategory VALUES('Car Rental');
INSERT INTO MainCategory VALUES('Cafes');
INSERT INTO MainCategory VALUES('Beauty &' ||' Spas');
INSERT INTO MainCategory VALUES('Convenience Stores');
INSERT INTO MainCategory VALUES('Dentists');
INSERT INTO MainCategory VALUES('Doctors');
INSERT INTO MainCategory VALUES('Drugstores');
INSERT INTO MainCategory VALUES('Department Stores');
INSERT INTO MainCategory VALUES('Education');
INSERT INTO MainCategory VALUES('Event Planning &' ||' Services');
INSERT INTO MainCategory VALUES('Flowers &' ||' Gifts');
INSERT INTO MainCategory VALUES('Food');
INSERT INTO MainCategory VALUES('Health &' ||' Medical');
INSERT INTO MainCategory VALUES('Home Services');
INSERT INTO MainCategory VALUES('Home &' ||' Garden');
INSERT INTO MainCategory VALUES('Hospitals');
INSERT INTO MainCategory VALUES('Hotels &' ||' Travel');
INSERT INTO MainCategory VALUES('Hardware Stores');
INSERT INTO MainCategory VALUES('Grocery');
INSERT INTO MainCategory VALUES('Medical Centers');
INSERT INTO MainCategory VALUES('Nurseries &' ||' Gardening');
INSERT INTO MainCategory VALUES('Nightlife');
INSERT INTO MainCategory VALUES('Restaurants');
INSERT INTO MainCategory VALUES('Shopping');
INSERT INTO MainCategory VALUES('Transportation');

ALTER TABLE Business DISABLE CONSTRAINT is_valid_boolean;
ALTER TABLE Review DISABLE CONSTRAINT fk_review_uid;
ALTER TABLE Review DISABLE CONSTRAINT fk_review_bid;
ALTER TABLE CheckinInfo DISABLE CONSTRAINT fk_checkininfo_bid;
ALTER TABLE Friend DISABLE CONSTRAINT pk_friend;
ALTER TABLE Friend DISABLE CONSTRAINT fk_friend_uid1;
ALTER TABLE Friend DISABLE CONSTRAINT fk_friend_uid2;
ALTER TABLE Neighborhood DISABLE CONSTRAINT fk_neighborhood;
ALTER TABLE VoteOn DISABLE CONSTRAINT fk_voteon;
ALTER TABLE OperationTime DISABLE CONSTRAINT pk_operationtime;
ALTER TABLE OperationTime DISABLE CONSTRAINT fk_operationtime;
ALTER TABLE OperationTime DISABLE CONSTRAINT is_valid_operationDay;
ALTER TABLE CategoryHasCategory DISABLE CONSTRAINT pk_chc;
ALTER TABLE BusinessInCategory DISABLE CONSTRAINT pk_bic;
ALTER TABLE BusinessInCategory DISABLE CONSTRAINT fk_bic;
ALTER TABLE Attribute DISABLE CONSTRAINT pk_attribute;
ALTER TABLE Attribute DISABLE CONSTRAINT fk_attribute;
ALTER TABLE Review DISABLE CONSTRAINT pk_review_id;
ALTER TABLE Business DISABLE CONSTRAINT pk_business_id;
ALTER TABLE Yelp_user DISABLE CONSTRAINT pk_user_id;

