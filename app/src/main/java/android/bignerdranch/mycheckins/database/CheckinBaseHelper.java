package android.bignerdranch.mycheckins.database;

import android.bignerdranch.mycheckins.database.CheckinDbSchema.CheckinTable;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CheckinBaseHelper extends SQLiteOpenHelper {
    private static int VERSION = 1;
    private static final String DATABASE_NAME = "checkinBase.db";

    public CheckinBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CheckinTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CheckinTable.Cols.UUID + ", " +
                CheckinTable.Cols.TITLE + ", " +
                CheckinTable.Cols.DATE + ", " +
                CheckinTable.Cols.PLACE + ", " +
                CheckinTable.Cols.DETAILS + ", " +
                //CheckinTable.Cols.LOCATION + ", " +
                CheckinTable.Cols.IMAGE + ")"
        ); //IDK if this is correct
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
