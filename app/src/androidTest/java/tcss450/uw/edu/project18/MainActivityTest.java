package tcss450.uw.edu.project18;

import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * Created by Mindy on 5/30/2016.
 *
 * Test class for the main activity:
 *  - open menu
 *  - open edit profile
 *  - view event
 *  - open edit event
 *  - open date picker from edit event
 *  - open share intent
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testOpenSideBar() {
        solo.setNavigationDrawer(Solo.OPENED);
        assertTrue("sidebar opened", solo.searchText("Welcome!"));
        solo.setNavigationDrawer(Solo.CLOSED);
    }

    public void testEditProfile() {
        solo.setNavigationDrawer(Solo.OPENED);
        solo.clickOnButton("Edit Profile");
        assertTrue("found edit profile", solo.searchText("Must be longer"));
        solo.goBack();
        assertTrue("returned to event list", solo.searchText("Gather"));
    }

    public void testViewEvent() {
        solo.clickInRecyclerView(0);
        assertTrue("opened view event", solo.searchText(""));
    }

    public void testEditEvent() {
        solo.clickInRecyclerView(0);
        solo.clickOnButton("Edit");
        assertTrue("editing event", solo.searchText(""));
        solo.clickOnButton("Edit Date");
        assertTrue("edit date", solo.searchText("Enter date..."));

    }
}
