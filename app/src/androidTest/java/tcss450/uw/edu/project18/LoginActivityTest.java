package tcss450.uw.edu.project18;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.robotium.solo.Solo;

/**
 * Tests the LoginActivity.
 *
 * @author Melinda Robertson
 * @version 20160601
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    /**
     * Pseudo user.
     */
    private Solo solo;

    /**
     * Creates a new testing class.
     */
    public LoginActivityTest() {
        super(LoginActivity.class);
    }
    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Tests that the login page loads when the app opens.
     */
    public void testLoad() {
        assertTrue("Login page opened.", solo.searchText("Sign in"));
    }

    /**
     * Tests the register fragment.
     */
    public void testRegister() {
        solo.clickOnButton("Register");
        assertTrue("Register page opened.", solo.searchText("Must be longer"));
        solo.clickOnButton("Choose your birthday");
        assertTrue("Opened Date Picker", solo.searchText("Enter date..."));
        solo.clickOnButton("Cancel");
        assertTrue("Returned to register.", solo.searchText("Must be longer"));
        solo.clickOnButton("Cancel");
        assertTrue("Returned to login.", solo.searchText("Sign in"));
    }

    /**
     * Tests logging into a dummy account.
     * @throws Exception if solo is interrupted.
     */
    public void testZLogin() throws Exception {
        EditText email = (EditText) solo.getView(R.id.email);
        solo.clearEditText(email);
        solo.enterText(email, "test@test.com");
        EditText pwd = (EditText) solo.getView(R.id.password);
        solo.clearEditText(pwd);
        solo.enterText(pwd, "Testing1");
        solo.clickOnButton("Sign in");
        assertTrue("Logged in", solo.searchText("Gather"));
    }


    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
