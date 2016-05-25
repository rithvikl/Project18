package tcss450.uw.edu.project18.event;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * A class representing one event
 * @author Rithvik Lagisetti
 */
public class Event implements Serializable {

    /**
     * The path to the image file for the event
     */
    private String mId;

    /**
     * The title of the event
     */
    private String mTitle;

    /**
     * The comment on the event
     */
    private String mComment;

    /**
     * The date the event was created
     */
    private String mDate;

    /**
     * The tags associated with the event
     */
        private String mTags;

    /**
     * The name of the id property of each event
     */
    public static final String ID = "id";

    /**
     * The name of the title property of each event
     */
    public static final String TITLE = "title";

    /**
     * The name of the coment property of each event
     */
    public static final String COMMENT = "comment";

    /**
     * The name of the date property of each event
     */
    public static final String DATE = "date";

    /**
     * The name of the tag property of each event
     */
        public static final String TAGS = "tags";

    /**
     * Contructor for an event
     * @param id the id number of the image
     * @param title the title of the event
     * @param comment the comment on the event
     * @param date the date the event was created
     */
    public Event (String id, String title, String comment, String date, String tags) {
        this.mId = id;
        this.mTitle = title;
        this.mComment = comment;
        this.mDate = date;
        this.mTags = tags;
    }

    /**
     * Getter for the image id
     * @return the id number of the image
     */
    public String getId () {
        return this.mId;
    }

    /**
     * Getter for the event title
     * @return the title of the event
     */
    public String getTitle () {
        return this.mTitle;
    }

    /**
     * Getter for the event comment
     * @return the comment on the event
     */
    public String getComment() {
        return mComment;
    }

    /**
     * Getter for the event date
     * @return the date the event was created
     */
    public String getDate () {
        return this.mDate;
    }

    /**
     * Getter for the tags associated with an event
     * @return the tags of the event
     */
    public String getTags () {
        return this.mTags;
    }

    public String[] getTagsAsArray() {
        return mTags.split(",");
    }

    public void setId (String id) {
        this.mId = id;
    }

    public void setTitle (String title) {
        this.mTitle = title;
    }

    /**
     * Setter
     * @param comment
     */
    public void setComment(String comment) {
        this.mComment = comment;
    }

    /**
     * Setter for the event creation date
     * @param date the date the event was created
     */
    public void setDate (String date) {
        this.mDate = date;
    }

    /**
     * Setter for the event tags
     * @param tags the tags associated with the event
     */
    public void setTags (String tags) {
        this.mTags = tags;
    }

    public void addTag(String tag) {
        this.mTags += "," + tag.trim();
    }

    public boolean containsTag(String tag) {
        return this.mTags.contains(tag);
    }

    public void removeTag(String tag) {
        mTags.replace(tag + ",","");
    }


    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns event list if successful.
     * @param eventJSON the stringified event JSON data
     * @return failure reason or null if successful.
     */
    public static String parseEventJSON(String eventJSON, List<Event> eventList) {
        String reason = null;
        if (eventJSON != null) {
            try {
                Log.i("EVENTJSON AFTER", eventJSON);
                JSONObject jsonBody = new JSONObject(eventJSON);
                JSONArray arr = jsonBody.getJSONArray("data");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Event event = new Event(
                            obj.getString(Event.ID),
                            obj.getString(Event.TITLE),
                            obj.getString(Event.COMMENT),
                            obj.getString(Event.DATE),
                            obj.getString(Event.TAGS)
                    );
                    eventList.add(event);
                }
            } catch (JSONException e) {
                reason =  "You do not have any events to show! Click the camera above to add a new event.";
                Log.e("JSON_fail", reason);
            }

        }
        return reason;
    }

    @Override
    public String toString() {
        return "{id: " + mId + ", title: " + mTitle + ", comment: " + mComment + ", date: " + mDate + ", tags: " + mTags + "}";
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
