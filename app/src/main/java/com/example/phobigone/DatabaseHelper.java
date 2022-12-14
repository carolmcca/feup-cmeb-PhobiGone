package com.example.phobigone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final Map<String, List<String>> TABLES = new HashMap<String, List<String>>() {{
        put("Setting", Arrays.asList("exp_train_time", "notifications", "sound"));
        put("Train", Arrays.asList("id", "date", "train_time", "streak"));
        put("Test", Arrays.asList("id", "date", "level", "perc_img"));
        put("Badge", Arrays.asList("id", "name", "description", "earned", "icon"));
    }};
    private static final String DATABASE_NAME = "phobiGone.db";
    private static final int DATABASE_VERSION = 1;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd");


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create all database tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Setting( " +
                "id INTEGER PRIMARY KEY, " +
                "device_id TEXT," +
                "device_name TEXT, " +
                "exp_train_time INTEGER, " +
                "notifications BOOLEAN, " +
                "sound BOOLEAN);");
        db.execSQL("INSERT INTO Setting(id, exp_train_time, notifications, sound) VALUES(1, 15, 1, 1);");
        db.execSQL("CREATE TABLE IF NOT EXISTS Train( " +
                "id INTEGER PRIMARY KEY, " +
                "date TEXT, " +
                "train_time REAL, " +
                "streak INTEGER);");
        db.execSQL("CREATE TABLE IF NOT EXISTS Test( " +
                "id INTEGER PRIMARY KEY, " +
                "date TEXT, " +
                "level INTEGER, " +
                "perc_img REAL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS Badge( " +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "description TEXT, " +
                "icon TEXT, " +
                "earned BOOLEAN);");
    }

    //Drop all tables on upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        for (String table : TABLES.keySet()){
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
    }

    //Add new train entry to database
    public void addTrain(float train_time) {
        //Get current date
        LocalDate localDate = LocalDate.now();
        String date = dtf.format(localDate);
        //Get database
        SQLiteDatabase db = this.getWritableDatabase();
        //Calculate current streak
        Integer streak = calcStreak(date);
        //Insert the new entry onto the database
        String query = "INSERT INTO Train(date, train_time, streak) VALUES( '" + date + "', " + train_time + ", " + streak + ")";
        db.execSQL(query);
    }

    //Add new test entry to database
    public void addTest(Integer level, float perc_img) {
        //Get current date
        LocalDate localDate = LocalDate.now();
        String date = dtf.format(localDate);
        //Get database
        SQLiteDatabase db = this.getWritableDatabase();
        //Insert the new entry onto the database
        String query = "INSERT INTO Test(date, level, perc_img) VALUES( '" + date + "', " + level + ", " + perc_img + ")";
        db.execSQL(query);
    }

    //Set the badge with id id to earned on the database
    public void earnBadge(Integer id) {
        //Get database
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> badge = readRowFromTable("SELECT * FROM Badge WHERE id = " + id + ";");
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", badge.get(0));
        contentValues.put("name", badge.get(1));
        contentValues.put("description", badge.get(2));
        contentValues.put("icon", badge.get(3));
        contentValues.put("earned", 1);
        //Change the state of the badge to conquered on database
        db.replace("Badge", null, contentValues);
    }

    //Replace the settings currently on the database by the ones passed as arguments
    public void saveSettings(String device_id, String device_name, boolean notifications, boolean sound, Integer time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("id", 1);
        content.put("device_name", device_name);
        content.put("device_id", device_id);
        content.put("notifications", notifications);
        content.put("sound", sound);
        content.put("exp_train_time", time);
        db.replace("Setting", null, content);
    }

    //Calculate the streak on day date
    private Integer calcStreak(String date) {
        //Get day from date
        Integer day = Integer.valueOf(date.substring(8));
        //Get the streak of the last entry of train on the table and the corresponding date
        List<String> data = readRowFromTable("SELECT date, streak FROM Train ORDER BY date DESC;");
        Integer streak = 1;
        if (data.size()!=0) {
            Integer dbDay = Integer.valueOf(data.get(0).substring(8));
            //If the last training session was today, the streak is the same
            if (dbDay == day) {
                streak = Integer.valueOf(data.get(1));
            }
            //If today is the first day of the month
            else if (day == 1) {
                //Calculate last month
                Integer lastMonth = (Integer.valueOf(date.substring(5, 7)) - 2 + 12) % 12 + 1;
                //Get current year
                Integer year = Integer.valueOf(date.substring(0, 4));
                //Get month and year of the last train entry
                Integer dbMonth = Integer.valueOf(data.get(0).substring(5, 7));
                Integer dbYear = Integer.valueOf(data.get(0).substring(0, 4));
                //Get number of days on the last month
                YearMonth yearMonth = YearMonth.of(year, lastMonth);
                int daysInLastMonth = yearMonth.lengthOfMonth();

                //If the day on the database is the day before current day
                if (dbDay.equals(daysInLastMonth) && dbMonth.equals(lastMonth) && ((lastMonth.equals(12) && dbYear.equals(year-1))||(!lastMonth.equals(12) && year.equals(dbYear))))
                    streak = Integer.valueOf(data.get(1)) + 1;
            }
            //If the day on the database is the day before current day
            else if (dbDay == day - 1) {
                streak = Integer.valueOf(data.get(1)) + 1;
            }
        }
        return streak;
    }

    public List<String> readColumnFromTable(String query){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> data = new ArrayList<>();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                data.add(cursor.getString(0));
            }
        }

        return data;
    }

    //Read one row from the table defined on the query
    public List<String> readRowFromTable(String query){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> data = new ArrayList<>();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            for(int i=0;i< cursor.getColumnCount();i++)
                data.add(cursor.getString(i));
        }

        return data;
    }

    //Read multiple rows from the table defined on the query
    public List<List<String>> readTableFromDatabase(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<List<String>> data = new ArrayList<>();
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        if (cursor.getCount() != 0) {
            while(cursor.moveToNext()) {
                List<String> row = new ArrayList<>();
                for (int i = 0; i < cursor.getColumnCount(); i++)
                    row.add(cursor.getString(i));
                data.add(row);
            }
        }

        return data;
    }

    //Get bluetooth address from the Settings table
    public String getAddress() {
        return readRowFromTable("SELECT device_id FROM Setting;").get(0);
    }

    //Get all earned badges
    public ArrayList<Badge> getEarnedBadges() {
        List<List<String>> badgesArray = readTableFromDatabase("SELECT id, name, description, icon FROM Badge WHERE earned = 1;");
        ArrayList<Badge> badges = new ArrayList();
        for (List<String> badgeArray:badgesArray) {
            badges.add(new Badge(Integer.valueOf(badgeArray.get(0)), badgeArray.get(1), badgeArray.get(2), badgeArray.get(3)));
        }
        return badges;
    }

    //Get all settings from the Settings table
    public HashMap<String, String> getSettings() {
        List<String> settingsList = readRowFromTable("SELECT device_name, notifications, sound, exp_train_time FROM Setting;");
        HashMap<String, String> settings = new HashMap<String, String>(){{put("device_name", settingsList.get(0)); put("notifications", settingsList.get(1)); put("sound", settingsList.get(2)); put("exp_train_time", settingsList.get(3));}};
        return settings;
    }
}
