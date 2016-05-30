package tcss450.uw.edu.project18;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.robotium.solo.Solo;

/**
 * Created by Melinda Robertson on 5/29/2016.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo solo;

    public LoginActivityTest(Class<LoginActivity> activityClass) {
        super(activityClass);
    }
    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testLoad() {
        assertTrue("Login page opened.", solo.searchText("Sign in"));
    }

    public void testRegister() {
        solo.clickOnButton(R.id.email_register_button);
        assertTrue("Register page opened.", solo.searchText("Enter Email"));
        solo.clickOnButton(R.id.date_button);
        assertTrue("Opened Date Picker", solo.searchText("Enter date..."));
        solo.clickOnButton("Cancel");
        assertTrue("Returned to login.", solo.searchText("Enter Email"));
    }

    public void testLogin() {
        EditText email = (EditText) solo.getView(R.id.email);
        solo.clearEditText(email);
        solo.enterText(email, "test@test.com");
        EditText pwd = (EditText) solo.getView(R.id.password);
        solo.clearEditText(pwd);
        solo.enterText(pwd, "Testing1");
        solo.clickOnButton(R.id.email_sign_in_button);
        assertTrue("Logged in", solo.searchText("Gather"));

        solo.clickOnView(getActivity().findViewById(R.id.menu_logout));
    }


    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
