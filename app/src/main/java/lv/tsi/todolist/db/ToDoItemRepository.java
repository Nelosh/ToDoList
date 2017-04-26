package lv.tsi.todolist.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lv.tsi.todolist.ToDoItem;

import static lv.tsi.todolist.db.SQLBuilder.*;
import static lv.tsi.todolist.db.ToDoDBHelper.ToDoEntry.*;

public class ToDoItemRepository {
    private ToDoDBHelper dbHelper;

    Map<Cursor, SQLiteDatabase> unclosed = new HashMap<>();

    public ToDoItemRepository(Context context) {
        dbHelper = new ToDoDBHelper(context);
    }

    public List<ToDoItem> getAll() {
        Cursor cursor = getCursorToAll();

        List<ToDoItem> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            ToDoItem toDoItem = toDoListFromCursor(cursor);
            result.add(toDoItem);
        }

        cursor.close();
        return result;
    }

    public ToDoItem getById(Long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLBuilder sqlBuilder = new SQLBuilder();
        String sql = sqlBuilder.SELECT(_ID, TITLE_COLUMN, DETAILS_COLUMN, DATE_COLUMN, CHECKED_COLUMN)
                               .FROM(TABLE_NAME)
                               .WHERE(expr(_ID,"=",id.toString()))
                               .toString();

        ToDoItem item = null;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            item = toDoListFromCursor(cursor);
        }
        cursor.close();
        db.close();
        return item;

    }

    public void insert(ToDoItem item) {
        execSql(new SQLBuilder().INSERT_INTO(TABLE_NAME,
                                             TITLE_COLUMN,
                                             DETAILS_COLUMN,
                                             DATE_COLUMN,
                                             CHECKED_COLUMN)
                                .VALUES(item.getTitle(),
                                        item.getDetails(),
                                        item.getFormattedDate(),
                                        item.isChecked() ? "1" : "0")
                                .toString());

    }

    public void update(ToDoItem newValues) {
        execSql(new SQLBuilder().UPDATE(TABLE_NAME)
                                .SET(expr(TITLE_COLUMN, "=", newValues.getTitle()),
                                     expr(DETAILS_COLUMN, "=", newValues.getDetails()),
                                     expr(DATE_COLUMN, "=", newValues.getFormattedDate()),
                                     expr(CHECKED_COLUMN, "=", newValues.isChecked() ? "1" : "0")
                                )
                                .WHERE(new SQLExpression(_ID, "=", newValues.getId().toString()))
                                .toString());
    }

    public void deleteById(Long id) {
        execSql(new SQLBuilder().DELETE()
                                .FROM(TABLE_NAME)
                                .WHERE(new SQLExpression(_ID, "=", id.toString()))
                                .toString());


    }

    public Cursor getCursorToAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLBuilder sqlBuilder = new SQLBuilder();
        String sql = sqlBuilder.SELECT(_ID, TITLE_COLUMN, DETAILS_COLUMN, DATE_COLUMN, CHECKED_COLUMN)
                               .FROM(TABLE_NAME)
                               .ORDER_BY(TITLE_COLUMN)
                               .toString();

        Cursor cursor = db.rawQuery(sql, null);
        unclosed.put(cursor, db);
        return cursor;
    }

    private void execSql(String SQL) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.enableWriteAheadLogging();
        db.execSQL(SQL);
        db.close();
    }

    private SQLExpression expr(String column, String op, String value) {
        return new SQLExpression(column, op, value);
    }

    public ToDoItem toDoListFromCursor(Cursor cursor) {
        return new ToDoItem(
                cursor.getLong(cursor.getColumnIndex(_ID)),
                cursor.getString(cursor.getColumnIndex(TITLE_COLUMN)),
                cursor.getString(cursor.getColumnIndex(DETAILS_COLUMN)),
                cursor.getString(cursor.getColumnIndex(DATE_COLUMN)),
                cursor.getInt(cursor.getColumnIndex(CHECKED_COLUMN)) != 0
        );
    }

    public void close(Cursor cursor) {
        cursor.close();
        if (unclosed.containsKey(cursor)) {
            unclosed.get(cursor).close();
        }
        unclosed.remove(cursor);
    }


}
