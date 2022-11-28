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
        put("Image", Arrays.asList("id", "file_name", "level"));
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all database tables
        db.execSQL("CREATE TABLE IF NOT EXISTS Image( " +
                "id INTEGER PRIMARY KEY, " +
                "file_name TEXT, " +
                "level INTEGER);");
        db.execSQL("CREATE TABLE IF NOT EXISTS Setting( " +
                "exp_train_time INTEGER PRIMARY KEY, " +
                "notifications BOOLEAN, " +
                "sound BOOLEAN);");
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



    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        for (String table : TABLES.keySet()){
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
    }

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


    //TODO: Just to populate database, remove before submission
    public void addTrain(String date, float train_time) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer streak = calcStreak(date);
        String query = "INSERT INTO Train(date, train_time, streak) VALUES( '" + date + "', " + train_time + ", " + streak + ")";
        db.execSQL(query);
    }

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

    public void earnBadge(Integer id) {
        //Get database
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> badge = readRowFromTable("SELECT * FROM Badge WHERE id = " + id + ";");
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", badge.get(0));
        contentValues.put("name", badge.get(1));
        contentValues.put("description", badge.get(2));
        contentValues.put("earned", 1);
        //Change the state of the badge to conquered on database
        db.replace("Badge", null, contentValues);
    }

    private Integer calcStreak(String date) {
        Integer day = Integer.valueOf(date.substring(8));
        List<String> data = readRowFromTable("SELECT date, streak FROM Train ORDER BY date DESC;");
        Integer streak = 1;
        if (data.size()!=0) {
            Integer dbDay = Integer.valueOf(data.get(0).substring(8));
            if (dbDay == day) {
                streak = Integer.valueOf(data.get(1));
            }
            else if (day == 1) {
                Integer lastMonth = (Integer.valueOf(date.substring(5, 7)) - 2 + 12) % 12 + 1;
                Integer year = Integer.valueOf(date.substring(0, 4));
                Integer dbMonth = Integer.valueOf(data.get(0).substring(5, 7));
                Integer dbYear = Integer.valueOf(data.get(0).substring(0, 4));
                YearMonth yearMonth = YearMonth.of(year, lastMonth);
                int daysInLastMonth = yearMonth.lengthOfMonth();

                if (dbDay.equals(daysInLastMonth) && dbMonth.equals(lastMonth) && ((lastMonth.equals(12) && dbYear.equals(year-1))||(!lastMonth.equals(12) && year.equals(dbYear))))
                    streak = Integer.valueOf(data.get(1)) + 1;
            }
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


    public ArrayList<Badge> getEarnedBadges() {
        List<List<String>> badgesArray = readTableFromDatabase("SELECT name, description, icon FROM Badge WHERE earned = 1;");
        ArrayList<Badge> badges = new ArrayList();
        for (List<String> badgeArray:badgesArray) {
            badges.add(new Badge(badgeArray.get(0), badgeArray.get(1), badgeArray.get(2)));
        }
        return badges;
    }
}
