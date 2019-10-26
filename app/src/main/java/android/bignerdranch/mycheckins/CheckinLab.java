package android.bignerdranch.mycheckins;

import android.bignerdranch.mycheckins.database.CheckinBaseHelper;
import android.bignerdranch.mycheckins.database.CheckinCursorWrapper;
import android.bignerdranch.mycheckins.database.CheckinDbSchema;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CheckinLab {
    private static CheckinLab sCheckinLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CheckinLab get(Context context) {
        if (sCheckinLab == null) {
            sCheckinLab = new CheckinLab(context);
        }
        return sCheckinLab;
    }

    private CheckinLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CheckinBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addCheckin(Checkin c) {
        ContentValues values = getContentValues(c);

        mDatabase.insert(CheckinDbSchema.CheckinTable.NAME, null, values);
    }

    public List<Checkin> getCheckins() {
        List<Checkin> checkins = new ArrayList<>();

        CheckinCursorWrapper cursor = queryCheckins(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                checkins.add(cursor.getCheckin());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return checkins;
    }

    public Checkin getCheckin(UUID id) {
        CheckinCursorWrapper cursor = queryCheckins(
                CheckinDbSchema.CheckinTable.Cols.UUID + " = ?",
                new String[] {
                        id.toString()
                }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCheckin();
        } finally {
            cursor.close();
        }
    }

    public File getImageFile(Checkin checkin) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, checkin.getImageFilename());
    }

    public void updateCheckin(Checkin checkin) {
        String uuidString = checkin.getId().toString();
        ContentValues values = getContentValues(checkin);

        mDatabase.update(CheckinDbSchema.CheckinTable.NAME, values,
                CheckinDbSchema.CheckinTable.Cols.UUID + " = ?",
                new String[] {
                        uuidString
                });
    }

    private CheckinCursorWrapper queryCheckins(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
            CheckinDbSchema.CheckinTable.NAME,
            null,
            whereClause,
            whereArgs,
            null,
            null,
            null
        );

        return new CheckinCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Checkin checkin) {
        ContentValues values = new ContentValues();
        values.put(CheckinDbSchema.CheckinTable.Cols.UUID, checkin.getId().toString());
        values.put(CheckinDbSchema.CheckinTable.Cols.TITLE, checkin.getTitle());
        values.put(CheckinDbSchema.CheckinTable.Cols.DATE, checkin.getDate().getTime());
        values.put(CheckinDbSchema.CheckinTable.Cols.PLACE, checkin.getPlace());
        values.put(CheckinDbSchema.CheckinTable.Cols.DETAILS, checkin.getDetails());
        values.put(CheckinDbSchema.CheckinTable.Cols.IMAGE, checkin.getImageFilename());
        values.put(CheckinDbSchema.CheckinTable.Cols.LATITUDE, checkin.getLat());
        values.put(CheckinDbSchema.CheckinTable.Cols.LONGITUDE, checkin.getLon());

        return values;
    }
}
