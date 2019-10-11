package android.bignerdranch.mycheckins.database;

public class CheckinDbSchema {
    public static final class CheckinTable {
        public static final String NAME = "checkins";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String PLACE = "place";
            public static final String DETAILS = "details";
            //public static final String LOCATION = "location";
            public static final String IMAGE = "image";
        }
    }
}
