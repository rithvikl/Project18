package tcss450.uw.edu.project18.event;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 */
public class Event implements Serializable {

    private String mFileName;
    private String mUser;
    private String mTitle;
    private String mTags;
    private java.sql.Date mDate;

    public static final String FILENAME = "filename";
    public static final String USER = "user";
    public static final String TITLE = "title";
    public static final String TAGS = "tags";
    public static final String DATE = "date";

    public Event (String filename, String user, String title, String tags, java.sql.Date date) {
        this.mFileName = filename;
        this.mUser = user;
        this.mTitle = title;
        this.mTags = tags;
        this.mDate = date;
    }

    public String getFileName () {
        return this.mFileName;
    }

    public String getTitle () {
        return this.mTitle;
    }

    public String getUser () {
        return this.mUser;
    }

    public String getTags () {
        return this.mTags;
    }

    public java.sql.Date getDate () {
        return this.mDate;
    }


    public void setFilename (String filename) {
        this.mFileName = filename;
    }

    public void setTitle (String title) {
        this.mTitle = title;
    }

    public void setUser (String user) {
        this.mUser = user;
    }

    public void setTags (String tags) {
        this.mTags = tags;
    }

    public void setDate (java.sql.Date date) {
        this.mDate = date;
    }


    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns event list if success.
     * @param eventJSON
     * @return reason or null if successful.
     */
    public static String parseCourseJSON(String eventJSON, List<Event> eventList) {
        String reason = null;
        if (eventJSON != null) {
            try {
                JSONObject jsonBody = new JSONObject(eventJSON);
                JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
                JSONArray arr = photosJsonObject.getJSONArray("photo");

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Event event = new Event(
                            obj.getString(Event.FILENAME),
                            obj.getString(Event.USER),
                            obj.getString(Event.TITLE),
                            obj.getString(Event.TAGS),
                            (java.sql.Date) obj.get(Event.DATE)
                    );
                    eventList.add(event);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
                Log.e("JSON_fail", reason);
            }

        }
        return reason;
    }

//
//    /**
//     * An array of sample (event) items.
//     */
//    public static final List<EventItem> ITEMS = new ArrayList<EventItem>();
//
//    /**
//     * A map of sample (event) items, by ID.
//     */
//    public static final Map<String, EventItem> ITEM_MAP = new HashMap<String, EventItem>();
//
//    private static final int COUNT = 25;
//
//    static {
//        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createEventItem(i));
//        }
//    }
//
//    private static void addItem(EventItem item) {
//        ITEMS.add(item);
//        ITEM_MAP.put(item.id, item);
//    }
//
//    private static EventItem createEventItem(int position) {
//        return new EventItem(String.valueOf(position), "Item " + position, makeDetails(position));
//    }
//
//    private static String makeDetails(int position) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("Details about Item: ").append(position);
//        for (int i = 0; i < position; i++) {
//            builder.append("\nMore details information here.");
//        }
//        return builder.toString();
//    }
//
//    /**
//     * A event item representing a piece of content.
//     */
//    public static class EventItem {
//        public final String id;
//        public final String content;
//        public final String details;
//
//        public EventItem(String id, String content, String details) {
//            this.id = id;
//            this.content = content;
//            this.details = details;
//        }
//
//        @Override
//        public String toString() {
//            return content;
//        }
//    }
}
