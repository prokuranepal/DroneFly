package com.example.swainstha.dronefly;

import android.provider.BaseColumns;

/**
 * Created by swainstha on 6/2/18.
 */

public final class StatusSave {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private StatusSave() {}

    /* Inner class that defines the table contents */
    public static class StatusEntry implements BaseColumns {
        public static final String TABLE_NAME = "status";
        public static final String COLUMN_NAME_1 = "lat";
        public static final String COLUMN_NAME_2 = "lng";
        public static final String COLUMN_NAME_3 = "alt";
        public static final String COLUMN_NAME_4 = "arm";
        public static final String COLUMN_NAME_5 = "status";
        public static final String COLUMN_NAME_6 = "head";

    }


}
