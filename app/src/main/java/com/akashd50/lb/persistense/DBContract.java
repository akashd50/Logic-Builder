package com.akashd50.lb.persistense;

import android.provider.BaseColumns;

public class DBContract {
    private DBContract(){}
    public static class DBEntry implements BaseColumns{
        public static final String TABLE_NAME = "logictables";
        public static final String BOARD_TABLE_NAME = "logicboards";
        public static final String APP_VARS_TABLE = "appvars";

        public static final String BOARD_NAME = "boardName";
        public static final String DIMENSIONS_X = "dx";
        public static final String DIMENSIONS_Y = "dy";


        public static final String C_BOARD_ID = "boardID";
        public static final String C_INDEX_X = "indexX";
        public static final String C_INDEX_Y = "indexY";
        public static final String C_TYPE = "type";
        public static final String C_STYLE = "style";

        public static final String APP_VARS_BOARD_ID = "boardsID";
    }

}
