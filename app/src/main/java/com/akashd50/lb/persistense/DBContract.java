package com.akashd50.lb.persistense;

import android.provider.BaseColumns;

public class DBContract {
    private DBContract(){}
    public static class DBEntry implements BaseColumns{
        public static final String TABLE_NAME = "logictables";
        public static final String COLUMN_NAME_TITLE = "c1";
        public static final String COLUMN_NAME_SUBTITLE = "sc1";

        public static final String INDEX_X = "indexX";
        public static final String INDEX_Y = "indexY";
        public static final String TYPE = "type";
        public static final String STYLE = "style";
    }

}
