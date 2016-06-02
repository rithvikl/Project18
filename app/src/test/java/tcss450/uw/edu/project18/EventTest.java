package tcss450.uw.edu.project18;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import tcss450.uw.edu.project18.event.Event;

/**
 * Created by Mindy on 5/30/2016.
 *
 * Tests that the event class is working properly.
 */
public class EventTest {

    /**
     * A sample event.
     */
    Event event;

    /**
     * Required constructor.
     */
    public EventTest() {

    }

    @Before
    public void setUp() {
        event = new Event("1", "My Title", "My comment.", "00000000", "tag1,tag2", "1234.jpg");
    }

    /**
     * Tests the constructor.
     */
    @Test
    public void testConstructor() {
        Event e = new Event("1", "My Title", "My comment.", "00000000", "tag1,tag2", "1234.jpg");
        assertNotNull(e);
    }

    /**
     * Tests getting the id.
     */
    @Test
    public void testGetId() {
        assertEquals("Id equals", "1", event.getId());
    }

    /**
     * Tests getting the title.
     */
    @Test
    public void testGetTitle() {
        assertEquals("title equals", "My Title", event.getTitle());
    }

    /**
     * Tests getting the comment.
     */
    @Test
    public void testGetComment() {
        assertEquals("comment equals", "My comment.", event.getComment());
    }

    /**
     * Tests getting the date.
     */
    @Test
    public void testGetDate() {
        assertEquals("date equals", "00000000", event.getDate());
    }

    /**
     * Tests getting the tags.
     */
    @Test
    public void testGetTags() {
        assertEquals("tag equals", "tag1,tag2", event.getTags());
        assertEquals("tag array equals", "tag1", event.getTagsAsArray()[0]);
    }

    /**
     * Tests getting the photo file name.
     */
    @Test
    public void testGetPhoto() {
        assertEquals("photo equals", "1234.jpg", event.getPhotoFileName());
    }

    /**
     * Tests setting the id.
     */
    @Test
    public void testSetId() {
        event.setId("2");
        assertEquals("id set equals", "2", event.getId());
        event.setId("1");
        assertEquals("id set equals", "1", event.getId());
    }

    /**
     * Tests setting the title.
     */
    @Test
    public void testSetTitle() {
        event.setTitle("2");
        assertEquals("title set equals", "2", event.getTitle());
        event.setTitle("My Title");
        assertEquals("title set equals", "My Title", event.getTitle());
    }

    /**
     * Tests setting the comment.
     */
    @Test
    public void testSetComment() {
        event.setComment("2");
        assertEquals("comment set equals", "2", event.getComment());
        event.setComment("My comment.");
        assertEquals("comment set equals", "My comment.", event.getComment());
    }

    /**
     * Tests setting the date.
     */
    @Test
    public void testSetDate() {
        event.setDate("2");
        assertEquals("date set equals", "2", event.getDate());
        event.setDate("00000000");
        assertEquals("date set equals", "00000000", event.getDate());
    }

    /**
     * Tests setting the tags.
     */
    @Test
    public void testSetTags() {
        event.setTags("2");
        assertEquals("tag set equals", "2", event.getTags());
        event.setTags("tag1,tag2");
        assertEquals("tag set equals", "tag1,tag2", event.getTags());
    }

    /**
     * Tests setting the photo file name.
     */
    @Test
    public void testSetPhoto() {
        event.setPhotoFileName("2");
        assertEquals("photo set equals", "2", event.getPhotoFileName());
        event.setPhotoFileName("1234.jpg");
        assertEquals("photo set equals", "1234.jpg", event.getPhotoFileName());
    }

    /**
     * Tests whether the containsTag method is working.
     */
    @Test
    public void testContainsTag() {
        assertTrue("tag contains", event.containsTag("tag1"));
    }
}
