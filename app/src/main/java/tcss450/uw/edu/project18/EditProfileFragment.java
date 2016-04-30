package tcss450.uw.edu.project18;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Calendar;
import java.util.IllegalFormatException;


/**
 * Allows the user to create or edit their profile.
 * The fields are the username (email), day, month and year of their birthday,
 * a password and a confirmation of the password.
 * @author Melinda Robertson
 * @version 20160429
 */
public class EditProfileFragment extends Fragment {

    /**
     * Not sure what I'm using this for.
     */
    public static final String PROFILE_ITEM = "profile";
    //TODO create this php file
    /**
     * The URLs for adding or editing the user
     * profile. I might consolidate this.
     */
    public static final String PROFILE_ADD_URL =
            "http://cssgate.insttech.washington.edu/~memre/addprofile.php?";
    public static final String PROFILE_EDIT_URL =
            "http://cssgate.insttech.washington.edu/~memre/editprofile.php?";
    /**
     * Text boxes that hold the information from the user.
     */
    private TextView profileEmail;
    private TextView profileBDay;
    private TextView profileBMonth;
    private TextView profileBYear;
    private TextView profilePass1;
    private TextView profilePass2;

    private EditProfileListener mListener;

    /**
     * Stores the user's information for sending to
     * the database.
     */
    private String[] profileQuery;
    /**
     * Whether or not the user is logged in.
     * This helps determine if the user is creating
     * or editing a profile.
     */
    private boolean loggedin;

    /**
     * The constructor.
     */
    public EditProfileFragment() {
        // Required empty public constructor
        profileQuery = new String[3];
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditProfileListener) {
            mListener = (EditProfileListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement EditProfileListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        profileEmail = (TextView) view.findViewById(R.id.profile_email);
        profileBDay = (TextView) view.findViewById(R.id.profile_day);
        profileBMonth = (TextView) view.findViewById(R.id.profile_month);
        profileBYear = (TextView) view.findViewById(R.id.profile_year);
        profilePass1 = (TextView) view.findViewById(R.id.profile_password);
        profilePass2 = (TextView) view.findViewById(R.id.profile_confirm);
        Button btn = (Button) view.findViewById(R.id.profile_submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checks if input is valid and builds query
                String query = buildURL(v);
                if (query == null) return;
                //insert into database

            }
        });
        return view;
    }

    /**
     * Updates the text boxes with the user's information.
     */
    public void updateProfile() {
        SharedPreferences sp = getActivity().getSharedPreferences(
                getString(R.string.LOGIN_PREFS),
                Context.MODE_PRIVATE);
        loggedin = sp.getBoolean(getString(R.string.LOGGEDIN), false);
        if (loggedin) {
            profileEmail.setText(sp.getString(getString(R.string.USERNAME),""));
            try {
                String[] date = Driver.parseDate(sp.getString(getString(R.string.BDATE),""));
                profileBDay.setText(date[0]);
                profileBMonth.setText(date[1]);
                profileBYear.setText(date[2]);
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Unable to get profile information.",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    /**
     * Validates the information in the text boxes.
     * @return true if the information can be used,
     *          false otherwise.
     */
    private boolean validate() {
        String email = profileEmail.getText().toString();
        String day = profileBDay.getText().toString();
        String month = profileBMonth.getText().toString();
        String year = profileBYear.getText().toString();
        String pass1 = profilePass1.getText().toString();
        String pass2 = profilePass2.getText().toString();

        if (!Driver.isValidEmail(email)){
            Toast.makeText(getActivity(), "Invalid email.", Toast.LENGTH_LONG).show();
            return false;
        }
        String result = Driver.isValidPassword(LoginActivity.PROFILE_NEW, pass1, pass2);
        if (!result.contains("success")) {
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            return false;
        }
        result = Driver.isValidDate(day, month, year);
        if (result.contains("error")){
            Toast.makeText(getActivity(), "Invalid date.", Toast.LENGTH_LONG).show();
            return false;
        }
        profileQuery[0] = email;
        profileQuery[1] = pass1;
        profileQuery[2] = result;
        return true;
    }

    /**
     * Validates and creates the URL to query the database.
     * @param view
     * @return the URL for editing or adding a profile.
     */
    public String buildURL(View view) {
        StringBuilder sb = new StringBuilder();
        try {
            if (loggedin) sb.append(PROFILE_EDIT_URL);
            else sb.append(PROFILE_ADD_URL);
            if (!validate()) {
                throw new IllegalArgumentException();
            }
            sb.append("username=");
            sb.append(URLEncoder.encode(profileQuery[0], "UTF-8"));
            sb.append("&birthday=");
            sb.append(URLEncoder.encode(profileQuery[1], "UTF-8"));
            sb.append("&password=");
            sb.append(URLEncoder.encode(profileQuery[2], "UTF-8"));
            //TODO get the id for flickr
        } catch (Exception e) {
            Toast.makeText(view.getContext(), "Something bad happened.",
                    Toast.LENGTH_LONG).show();
        }
        return sb.toString();
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            updateProfile();
        } else
            loggedin = false;
    }

    public interface EditProfileListener {
        void editProfile(String url);
    }
}
