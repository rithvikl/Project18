package tcss450.uw.edu.project18;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.project18.event.Event;

/**
 * Created by rithvikl on 5/26/16.
 */
public class EventDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Event.db";
    private static final String EVENT_TABLE = "Event";

    private EventDBHelper mEventDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public EventDB(Context context) {
        mEventDBHelper = new EventDBHelper(context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mEventDBHelper.getWritableDatabase();
    }

    /**
     * Inserts the course into the local sqlite table. Returns true if successful, false otherwise.
     * @param id
     * @param title
     * @param comment
     * @param date
     * @param tags
     * @return true or false
     */
    public boolean insertEvent(String id, String title, String comment, String date, String tags) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("title", title);
        contentValues.put("comment", comment);
        contentValues.put("date", date);
        contentValues.put("tags", tags);

        long rowId = mSQLiteDatabase.insert("Event", null, contentValues);
        return rowId != -1;
    }


    /**
     * Returns the list of courses from the local Course table.
     * @return list
     */
    public List<Event> getEvents() {

        String[] columns = {
                "id", "title", "comment", "date", "tags"
        };

        Cursor c = mSQLiteDatabase.query(
                EVENT_TABLE,                        // The table to query
                columns,                             // The columns to return
                null,                                // The columns for the WHERE clause
                null,                                // The values for the WHERE clause
                null,                                // don't group the rows
                null,                                // don't filter by row groups
                null                                 // The sort order
        );

        c.moveToFirst();
        List<Event> list = new ArrayList<Event>();
        for (int i=0; i<c.getCount(); i++) {
            String id = c.getString(0);
            String title = c.getString(1);
            String comment = c.getString(2);
            String date = c.getString(3);
            String tags = c.getString(4);
            Event event = new Event(id, title, comment, date, tags);
            list.add(event);
            c.moveToNext();
        }

        return list;
    }

    /**
     * Delete all the data from the COURSE_TABLE
     */
    public void deleteEvents() {
        mSQLiteDatabase.delete(EVENT_TABLE, null, null);
    }

    public void deleteEvent(Event event) {
        String whereClause = "id= '" + event.getId() + "'";
        mSQLiteDatabase.delete(EVENT_TABLE, whereClause, null);
    }


    public void closeDB() {
        mSQLiteDatabase.close();
    }


    class EventDBHelper extends SQLiteOpenHelper {

        private static final String CREATE_EVENT_SQL = "CREATE TABLE IF NOT EXISTS Event "
                + "(id TEXT PRIMARY KEY, title TEXT, comment TEXT, date TEXT, tags TEXT)";

        private static final String DROP_EVENT_SQL = "DROP TABLE IF EXISTS Event";

        public EventDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_EVENT_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_EVENT_SQL);
            onCreate(sqLiteDatabase);
        }
    }

}
