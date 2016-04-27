package tcss450.uw.edu.project18.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class EventContent {

    /**
     * An array of sample (event) items.
     */
    public static final List<EventItem> ITEMS = new ArrayList<EventItem>();

    /**
     * A map of sample (event) items, by ID.
     */
    public static final Map<String, EventItem> ITEM_MAP = new HashMap<String, EventItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createEventItem(i));
        }
    }

    private static void addItem(EventItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static EventItem createEventItem(int position) {
        return new EventItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A event item representing a piece of content.
     */
    public static class EventItem {
        public final String id;
        public final String content;
        public final String details;

        public EventItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
