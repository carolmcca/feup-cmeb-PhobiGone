.headers ON
.mode column

DROP TABLE IF EXISTS Badge;
DROP TABLE IF EXISTS Train;
DROP TABLE IF EXISTS Test;
DROP TABLE IF EXISTS Image;
DROP TABLE IF EXISTS Setting;

CREATE TABLE IF NOT EXISTS Image(
    id INTEGER PRIMARY KEY,
    file_name TEXT,
    level INTEGER
);

CREATE TABLE IF NOT EXISTS Setting(
    id INTEGER PRIMARY KEY,
    device_name TEXT,
    device_id TEXT,
    exp_train_time INTEGER,
    notifications BOOLEAN,
    sound BOOLEAN
);

CREATE TABLE IF NOT EXISTS Train(
    id INTEGER PRIMARY KEY,
    date TEXT,
    train_time INTEGER,
    streak INTEGER
);

CREATE TABLE IF NOT EXISTS Test(
    id INTEGER PRIMARY KEY,
    date TEXT,
    level INTEGER,
    perc_img REAL
);

CREATE TABLE IF NOT EXISTS Badge(
    id INTEGER PRIMARY KEY,
    name TEXT,
    description TEXT,
    icon TEXT,
    earned BOOLEAN
);


INSERT INTO Setting(id, exp_train_time, notifications, sound) VALUES(1, 15, true, true);

INSERT INTO Train(date, train_time, streak) VALUES ('2022/10/25',60,1);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/10/27',30,1);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/10/27',40,1);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/10/28',50,2);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/10/29',50,3);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/10/30',40,4);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/10/31',50,5);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/10/31',60,5);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/03',20,1);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/07',10,1);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/08',20,2);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/09',30,3);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/10',30,4);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/11',40,5);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/11',30,5);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/12',50,6);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/13',50,7);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/14',60,8);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/15',50,9);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/16',60,10);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/21',30,1);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/21',30,1);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/22',40,2);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/24',30,1);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/28',50,1);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/29',50,2);
INSERT INTO Train(date, train_time, streak) VALUES ('2022/11/30',50,3);

INSERT INTO Test VALUES (1,'2022/10/25',1,16);
INSERT INTO Test VALUES (2,'2022/10/26',1,20);
INSERT INTO Test VALUES (3,'2022/10/28',1,25);
INSERT INTO Test VALUES (4,'2022/11/01',2,33);
INSERT INTO Test VALUES (5,'2022/11/03',1,25);
INSERT INTO Test VALUES (6,'2022/11/03',2,29);
INSERT INTO Test VALUES (7,'2022/11/05',2,33);
INSERT INTO Test VALUES (8,'2022/11/07',2,45);
INSERT INTO Test VALUES (9,'2022/11/07',2,41);
INSERT INTO Test VALUES (10,'2022/11/10',2,37);
INSERT INTO Test VALUES (11,'2022/11/13',3,79);
INSERT INTO Test VALUES (12,'2022/11/16',3,91);
INSERT INTO Test VALUES (13,'2022/11/17',3,83);
INSERT INTO Test VALUES (14,'2022/11/19',1,20);
INSERT INTO Test VALUES (15,'2022/11/19',2,37);
INSERT INTO Test VALUES (16,'2022/11/21',3,58);
INSERT INTO Test VALUES (17,'2022/11/24',3,91);
INSERT INTO Test VALUES (18,'2022/11/26',3,87);
INSERT INTO Test VALUES (19,'2022/11/29',3,79);
INSERT INTO Test VALUES (20,'2022/11/30',3,95);

INSERT INTO Badge(id, name, description, icon, earned) VALUES(1, "Begginer", "You've done 5 training sessions", "badge_1", true);
INSERT INTO Badge(id, name, description, icon, earned) VALUES(2, "Intermediate", "You've done 10 training sessions", "badge_2", true);
INSERT INTO Badge(id, name, description, icon, earned) VALUES(3, "Advanced", "You've done 20 training sessions", "badge_3", false);
INSERT INTO Badge(id, name, description, icon, earned) VALUES(4, "Baby streaker", "You've trained 2 days in a row", "badge_4", true);
INSERT INTO Badge(id, name, description, icon, earned) VALUES(5, "Toddler streaker", "You've trained 4 days in a row", "badge_5", true);
INSERT INTO Badge(id, name, description, icon, earned) VALUES(6, "Teen streaker", "You've trained 8 days in a row", "badge_6", true);
INSERT INTO Badge(id, name, description, icon, earned) VALUES(7, "Adult streaker", "You've trained 15 days in a row", "badge_7", false);
INSERT INTO Badge(id, name, description, icon, earned) VALUES(8, "Senior streaker", "You've trained 30 days in a row", "badge_8", false);
INSERT INTO Badge(id, name, description, icon, earned) VALUES(9, "Warrior", "You've trained 60 days in a row", "badge_9", false);
