package android.bignerdranch.mycheckins.database;

import android.bignerdranch.mycheckins.Checkin;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.media.Image;

import java.util.Date;
import java.util.UUID;

public class CheckinCursorWrapper extends CursorWrapper {
    public CheckinCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Checkin getCheckin() {
        String uuidString = getString(getColumnIndex(CheckinDbSchema.CheckinTable.Cols.UUID));
        String title = getString(getColumnIndex(CheckinDbSchema.CheckinTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CheckinDbSchema.CheckinTable.Cols.DATE));
        String place = getString(getColumnIndex(CheckinDbSchema.CheckinTable.Cols.PLACE));
        String details = getString(getColumnIndex(CheckinDbSchema.CheckinTable.Cols.DETAILS));
        double lat = getDouble(getColumnIndex(CheckinDbSchema.CheckinTable.Cols.LATITUDE));
        double lon = getDouble(getColumnIndex(CheckinDbSchema.CheckinTable.Cols.LONGITUDE));


        Checkin checkin = new Checkin(UUID.fromString(uuidString));
        checkin.setTitle(title);
        checkin.setDate(new Date(date));
        checkin.setPlace(place);
        checkin.setDetails(details);
        checkin.setLat(lat);
        checkin.setLon(lon);

        return checkin;
    }
}
