package tcss450.uw.edu.project18;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

import tcss450.uw.edu.project18.event.Event;

/**
 * Created by Mindy on 5/30/2016.
 */
public class EventTest {

    Event event;

    public EventTest() {

    }

    @Before
    public void setUp() {
        event = new Event("1", "My Title", "My comment.", "00000000", "tag1,tag2", "1234.jpg");
    }

    @Test
    public void testConstructor() {
        Event e = new Event("1", "My Title", "My comment.", "00000000", "tag1,tag2", "1234.jpg");
        assertNotNull(e);
    }

    @Test
    public void testGetId() {
        assertEquals("Id equals", "1", event.getId());
    }

    @Test
    public void testGetTitle() {
        assertEquals("title equals", "My Title", event.getTitle());
    }
    @Test
    public void testGetComment() {
        assertEquals("comment equals", "My comment.", event.getComment());
    }
    @Test
    public void testGetDate() {
        assertEquals("date equals", "00000000", event.getDate());
    }
    @Test
    public void testGetTags() {
        assertEquals("tag equals", "tag1,tag2", event.getTags());
        assertEquals("tag array equals", "tag1", event.getTagsAsArray()[0]);
    }
    @Test
    public void testGetPhoto() {
        assertEquals("photo equals", "1234.jpg", event.getPhotoFileName());
    }

    @Test
    public void testSetId() {
        event.setId("2");
        assertEquals("id set equals", "2", event.getId());
        event.setId("1");
        assertEquals("id set equals", "1", event.getId());
    }
    @Test
    public void testSetTitle() {
        event.setTitle("2");
        assertEquals("title set equals", "2", event.getTitle());
        event.setTitle("My Title");
        assertEquals("title set equals", "My Title", event.getTitle());
    }
    @Test
    public void testSetComment() {
        event.setComment("2");
        assertEquals("comment set equals", "2", event.getComment());
        event.setComment("My comment.");
        assertEquals("comment set equals", "My comment.", event.getComment());
    }
    @Test
    public void testSetDate() {
        event.setDate("2");
        assertEquals("date set equals", "2", event.getDate());
        event.setDate("00000000");
        assertEquals("date set equals", "00000000", event.getDate());
    }
    @Test
    public void testSetTags() {
        event.setTags("2");
        assertEquals("tag set equals", "2", event.getTags());
        event.setTags("tag1,tag2");
        assertEquals("tag set equals", "tag1,tag2", event.getTags());
    }
    @Test
    public void testSetPhoto() {
        event.setPhotoFileName("2");
        assertEquals("photo set equals", "2", event.getPhotoFileName());
        event.setPhotoFileName("1234.jpg");
        assertEquals("photo set equals", "1234.jpg", event.getPhotoFileName());
    }

    @Test
    public void testContainsTag() {
        assertTrue("tag contains", event.containsTag("tag1"));
    }

    @Test
    public void testParseJSON() throws Exception {
        String json = "{\"result\":\"success\",\"data\":[{\"id\":\"1\"," +
                "\"title\":\"My Title\"," +
                "\"comment\":\"My comment.\"," +
                "\"date\":\"00000000\"," +
                "\"tags\":\"tag1,tag2\"," +
                "\"photoFileName\":\"1234.jpg\"},"
                + "{\"id\":\"2\","
                + "\"title\":\"My Awesome Title\","
                + "\"comment\":\"This is a comment that I hope will not be too long.\","
                + "\"date\":\"20160405\","
                + "\"tags\":\"tag2,tag3\","
                + "\"photoFileName\":\"1235.jpg\"}]}";
        String json2 = "{\"result\": \"success\"}";
        ArrayList<Event> array = new ArrayList<Event>();
        Event.parseEventJSON(json,array);
        assertEquals("parse json", event.getComment(),
                array.get(0).getComment());
    }
}
