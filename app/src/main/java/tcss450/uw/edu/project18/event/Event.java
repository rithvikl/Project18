package tcss450.uw.edu.project18.event;

import android.location.Location;
import android.util.Log;

import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.tags.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Event implements Serializable {

    private String mPhotoId;
    private String mTitle;
//    private String mDescription;
//    private String[] mTags;
//    private Date mDate;

    public static final String PHOTO = "id";
    public static final String TITLE = "title";
//    public static final String DESCRIPTION = "photo.description";
//    public static final String TAGS = "photo.tags";
//    public static final String DATE = "date";
//    public static final String LOCATION = "location";

    public Event (String photoId, String title) {
        this.mPhotoId = photoId;
        this.mTitle = title;
//        this.mDescription = description;
//        this.mTags = tags;
//        this.mDate = date;
    }

    public String getPhotoId () {
        return this.mPhotoId;
    }

    public String getTitle () {
        return this.mTitle;
    }

//    public String getDescription () {
//        return this.mDescription;
//    }

//    public String[] getTags () {
//        return this.mTags;
//    }
//
//    public Date getDate () {
//        return this.mDate;
//    }

    public void setPhotoId (String photoId) {
        this.mPhotoId = photoId;
    }

    public void setTitle (String title) {
        this.mTitle = title;
    }

//    public void setDescription (String description) {
//        this.mDescription = description;
//    }

//    public void setTags (String[] tags) {
//        this.mTags = tags;
//    }
//
//    public void setDate (Date date) {
//        this.mDate = date;
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
                JSONObject jsonBody = new JSONObject(eventJSON);
                JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
//                JSONArray arr = new JSONArray(eventJSON);
                JSONArray arr = photosJsonObject.getJSONArray("photo");

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Event event = new Event(
                            obj.getString(Event.PHOTO),
                            obj.getString(Event.TITLE)
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
