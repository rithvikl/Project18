package tcss450.uw.edu.project18;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * Created by Melinda Robertson on 5/30/2016.
 *
 * Test class for the main activity.
 *
 *  @author Melinda Robertson
 *  @version 20160106
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    /**
     * Pseudo user.
     */
    private Solo solo;

    /**
     * Required constructor.
     */
    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Tests if the navigation drawer opens.
     */
    public void testAOpenSideBar() {
        //solo.setNavigationDrawer(Solo.OPENED);
        solo.clickOnImageButton(0);
        assertTrue("sidebar opened", solo.searchText("Hello"));
        //solo.setNavigationDrawer(Solo.CLOSED);
//        solo.swipe(new PointF(300,300), new PointF(300,300),
//                new PointF(0,300), new PointF(0,300));
        solo.drag(300,0,300,300,10);
    }

    /**
     * Tests if the view event fragment opens.
     */
    public void testCViewEvent() {
        solo.clickInRecyclerView(0);
        assertTrue("opened view event", solo.searchText(""));
    }

    /**
     * Tests if the edit event fragment opens.
     */
    public void testDEditEvent() {
        solo.clickInRecyclerView(0);
        solo.clickOnButton("Edit");
        assertTrue("editing event", solo.searchText(""));
        solo.clickOnButton("Edit Date");
        assertTrue("edit date", solo.searchText("Enter date..."));
        solo.clickOnButton("Cancel"); //exit date picker
        solo.clickOnButton("Cancel"); //cancel edit
        solo.goBack(); //return from view event
    }
}
