package lv.tsi.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ToDoDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ToDoList.db";

    private static final String TODO_CREATE_SQL = "CREATE TABLE " + ToDoEntry.TABLE_NAME + " (" +
                                                  ToDoEntry._ID + " INTEGER PRIMARY KEY," +
                                                  ToDoEntry.TITLE_COLUMN + " TEXT," +
                                                  ToDoEntry.DETAILS_COLUMN + " TEXT," +
                                                  ToDoEntry.DATE_COLUMN + " TEXT," +
                                                  ToDoEntry.CHECKED_COLUMN + " INTEGER)";

    private static final String TODO_DROP_SQL = "DROP TABLE_NAME IF EXISTS " + ToDoEntry.TABLE_NAME;

    public ToDoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TODO_CREATE_SQL);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TODO_DROP_SQL);
        onCreate(db);
    }

    public static class ToDoEntry implements BaseColumns {
        public static final String TABLE_NAME = "TODO_ITEM";
        public static final String TITLE_COLUMN = "TITLE";
        public static final String DETAILS_COLUMN = "DETAILS";
        public static final String DATE_COLUMN = "DATE";
        public static final String CHECKED_COLUMN = "CHECKED";
    }

}
