package tcss450.uw.edu.project18;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
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
    //TODO create these php files
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
                if (query == null || query.isEmpty()) {
                    Toast.makeText(v.getContext(), "There was an error editing your profile.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(Driver.DEBUG) Log.i("EditProfile:submit", query);
                //insert into database
                mListener.editProfile(query);
            }
        });
        if (Driver.DEBUG) {
            profileEmail.setText("memre911@gmail.com");
            profileBDay.setText("17");
            profileBMonth.setText("6");
            profileBYear.setText("1987");
            profilePass1.setText("Qaz123");
            profilePass2.setText("Qaz123");
        }
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
            profileEmail.setText(sp.getString(getString(R.string.USER),""));
            try {
                String[] date = Driver.parseDate(sp.getString(getString(R.string.BDAY),""));
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
    private boolean validate(View v) {
        String email = profileEmail.getText().toString();
        String day = profileBDay.getText().toString();
        String month = profileBMonth.getText().toString();
        String year = profileBYear.getText().toString();
        String pass1 = profilePass1.getText().toString();
        String pass2 = profilePass2.getText().toString();
        if (!Driver.isValidEmail(email)){
            Toast.makeText(v.getContext(), "Invalid email.", Toast.LENGTH_LONG).show();
            profileEmail.requestFocus();
            return false;
        }
        String result = Driver.isValidPassword(LoginActivity.PROFILE_NEW, pass1, pass2);
        if (!result.contains("success")) {
            Toast.makeText(v.getContext(), result, Toast.LENGTH_LONG).show();
            profilePass1.requestFocus();
            return false;
        }
        try {
            result = Driver.isValidDate(day, month, year);
        } catch (IllegalArgumentException e){
            if(Driver.DEBUG) Log.i("EPF:date", e.getMessage());
            Toast.makeText(v.getContext(), "Invalid date.", Toast.LENGTH_LONG).show();
            profileBDay.requestFocus();
            return false;
        }
        if (Driver.DEBUG) {
            String print = "{email:" + email + ", day:" + day
                    + ", month:" + month + ", year:" + year
                    + ", pass1:" + pass1 + ", pass2:" + pass2;
            Log.i("EditProfile:valid4", print);
        }
        profileQuery[0] = email;
        profileQuery[1] = result;
        profileQuery[2] = pass1;
        if(Driver.DEBUG)
            Log.i("EditProfile:text1", "{0:" + profileQuery[0] +
                    ", 1:" + profileQuery[1] +
                    ", 2:" + profileQuery[2]);
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
            boolean arg = validate(view);
            if(Driver.DEBUG)
                Log.i("EditProfile:text2", "{0:" + profileQuery[0] +
                        ", 1:" + profileQuery[1] +
                        ", 2:" + profileQuery[2]);
            if (!arg) {
                throw new IllegalArgumentException();
            }

            sb.append(getString(R.string.USER));
            sb.append("=");
            sb.append(URLEncoder.encode(profileQuery[0], "UTF-8"));
            sb.append("&");
            sb.append(getString(R.string.BDAY));
            sb.append("=");
            sb.append(URLEncoder.encode(profileQuery[1], "UTF-8"));
            sb.append("&");
            sb.append(getString(R.string.PWD));
            sb.append("=");
            sb.append(URLEncoder.encode(profileQuery[2], "UTF-8"));
            sb.append("&");
            sb.append(getString(R.string.GID));
            sb.append("=");
            //TODO get the id for flickr
            sb.append(URLEncoder.encode("0000", "UTF-8"));
            if(Driver.DEBUG) Log.i("EditProfile:build", sb.toString());
        } catch (IllegalArgumentException e) {
            //Toast.makeText(view.getContext(), "Arguments: We were unable to update your profile.",
            //        Toast.LENGTH_LONG).show();
            return null;
        } catch (UnsupportedEncodingException e3) {
            //Toast.makeText(view.getContext(), "Encoding: We were unable to update your profile.",
            //        Toast.LENGTH_LONG).show();
            return null;
        } catch(Exception e2) {
            //if(Driver.DEBUG) Toast.makeText(view.getContext(), e2.getLocalizedMessage(),
            //        Toast.LENGTH_SHORT).show();
            return null;
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

    /**
     * The listener is for sending data to the database
     * to be inserted or updated depending on whether the
     * user is logged in.
     */
    public interface EditProfileListener {
        void editProfile(String url);
        void callback(boolean success, String message);
    }
}
