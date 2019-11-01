package com.example.flappypenguin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper
{
    private static final String databaseName = "FlappyPenguin";
    public static final String tableName = "Scores";
    public static final String[] columnNames = {"ID", "QuestionText", "AnswerOneText", "AnswerTwoText", "AnswerThreeText", "CorrectAnswerNumber"};
    public static final String[] columnTypes = {"INTEGER", "VARCHAR(50)", "VARCHAR(50)", "VARCHAR(50)", "VARCHAR(50)", "INTEGER"};

    public MySQLiteHelper(Context context)
    {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        String columns = "";

        for (int count = 0; count < columnNames.length-1; count++)
            columns += columnNames[count] + " " + columnTypes[count] + ", ";
        columns += columnNames[columnNames.length-1] + " " + columnTypes[columnNames.length-1]; // no comma after last column

        database.execSQL("CREATE TABLE " + tableName + " (" + columns + ")");
    }

    public void deleteDatabase(Context context)
    {
        context.deleteDatabase(databaseName);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
