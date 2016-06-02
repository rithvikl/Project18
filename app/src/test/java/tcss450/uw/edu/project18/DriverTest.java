package tcss450.uw.edu.project18;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Melinda Robertson on 5/8/2016.
 *
 * Tests if the Driver class works.
 *
 * @author Melinda Robertson
 * @version 20160601
 */
public class DriverTest{

    /**
     * Required constructor.
     */
    public DriverTest() {

    }

    /**
     * Tests if validation for email is working.
     */
    @Test
    public void testIsValidEmail() {
        assertTrue(Driver.isValidEmail("test@test.com"));
        assertFalse(Driver.isValidEmail("memre.com"));
        assertFalse(Driver.isValidEmail(""));
    }

    /**
     * Tests if the validation for dates is working.
     */
    @Test
    public void testIsValidDate() {
        assertTrue(Driver.isValidDate("20160518"));
        assertTrue(Driver.isValidDate("20161218"));
        assertTrue(Driver.isValidDate("20160118"));
        assertFalse(Driver.isValidDate("18450312"));
        assertFalse(Driver.isValidDate("20169912"));
        assertFalse(Driver.isValidDate("19850334"));
        assertFalse(Driver.isValidDate("00000000"));
    }

    /**
     * Tests if the validation for passwords is working.
     */
    @Test
    public void testIsValidPassword() {
        assertEquals(Driver.isValidPassword(
                LoginActivity.PROFILE_NEW,"Testing1", "Testing1"), "success");
        assertEquals(Driver.isValidPassword(
                LoginActivity.PROFILE_NEW,"Testing1", "Test"),
                Driver.PWD_ERROR_MATCH);
        assertEquals(Driver.isValidPassword(
                LoginActivity.PROFILE_NEW,"Testng1", "Testng1"),
                Driver.PWD_ERROR_8CHAR);
        assertEquals(Driver.isValidPassword(
                LoginActivity.PROFILE_NEW,"testing1", "testing1"),
                Driver.PWD_ERROR_UPPER);
        assertEquals(Driver.isValidPassword(
                LoginActivity.PROFILE_NEW,"TESTING1", "TESTING1"),
                Driver.PWD_ERROR_LOWER);
        assertEquals(Driver.isValidPassword(
                LoginActivity.PROFILE_NEW,"Testings", "Testings"),
                Driver.PWD_ERROR_NUM);
    }

    /**
     * Tests if dates can be parsed properly for database storage.
     */
    @Test
    public void testParseDateForDB() {
        assertEquals(Driver.parseDateForDB(1950, 3, 15), "19500315");
    }

    /**
     * Tests if dates can be parsed properly for display.
     * @throws Exception if the date cannot be parsed.
     */
    @Test
    public void testParseDateForDisplay() throws Exception {
        assertEquals(Driver.parseDateForDisplay(1950,3,15),
                "March 15, 1950");
        assertEquals(Driver.parseDateForDisplay("19500315"),
                "March 15, 1950");
    }

    /**
     * Tests if the integer values in a date (YYYYMMDD) can be extracted.
     * @throws Exception if the date could not be parsed.
     */
    @Test
    public void testGetValueOfDate() throws Exception {
        assertArrayEquals(Driver.getValueOfDate("19500315"),
                new int[]{1950,3,15});
    }
}
