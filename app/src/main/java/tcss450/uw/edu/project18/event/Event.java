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

    private String mFile;
    private String mTitle;
    private String mComment;
    private String mDate;
    //    private String mTags;

    public static final String FILE = "file";
    public static final String TITLE = "title";
    public static final String COMMENT = "comment";
    public static final String DATE = "date";
    //    public static final String TAGS = "tags";

    public Event (String filename, String title, String comment, String date) {
        this.mFile = filename;
        this.mTitle = title;
        this.mComment = comment;
        this.mDate = date;
        //        this.mTags = tags;
    }

    public String getFile () {
        return this.mFile;
    }

    public String getTitle () {
        return this.mTitle;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public String getDate () {
        return this.mDate;
    }

//    public String getTags () {
//        return this.mTags;
//    }

    public void setFile (String filename) {
        this.mFile = filename;
    }

    public void setTitle (String title) {
        this.mTitle = title;
    }

    public void setDate (String date) {
        this.mDate = date;
    }

//    public void setTags (String tags) {
//        this.mTags = tags;
//    }


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
                Log.i("Response", eventJSON);
                JSONObject jsonBody = new JSONObject(eventJSON);
//                JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
                JSONArray arr = jsonBody.getJSONArray("data");

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Event event = new Event(
                            obj.getString(Event.FILE),
                            obj.getString(Event.TITLE),
                            obj.getString(Event.COMMENT),
                            obj.getString(Event.DATE)
//                            obj.getString(Event.TAGS)
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
