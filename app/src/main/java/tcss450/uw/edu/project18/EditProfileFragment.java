package tcss450.uw.edu.project18;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.DatePicker;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.ParseException;
import java.util.Calendar;
import java.util.IllegalFormatException;

import tcss450.uw.edu.project18.event.Event;


/**
 * Allows the user to create or edit their profile.
 * The fields are the username (email), day, month and year of their birthday,
 * a password and a confirmation of the password.
 * @author Melinda Robertson
 * @version 20160429
 */
public class EditProfileFragment extends Fragment
    implements java.io.Serializable, android.app.DatePickerDialog.OnDateSetListener {

    /**
     * Not sure what this is using this for.
     */
    public static final String PROFILE_ITEM = "profile";
    //TODO create these php files
    /**
     * The URLs for adding or editing the user
     * profile. I might consolidate this.
     */
    public static final String PROFILE_ADD_URL =
            "http://cssgate.insttech.washington.edu/~_450atm18/addprofile.php?";
    public static final String PROFILE_EDIT_URL =
            "http://cssgate.insttech.washington.edu/~_450atm18/editprofile.php?";
    /**
     * Text boxes that hold the information from the user.
     */
    private EditText profileEmail;
    private TextView profileDate;
    private TextView profilePass1;
    private TextView profilePass2;

    /**
     * The listener object for the submit button.
     */
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

    private SharedPreferences mShared;

    /**
     * The constructor.
     */
    public EditProfileFragment() {
        //holds the user's information
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

        mShared = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS),
                Context.MODE_PRIVATE);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        profileEmail = (EditText) view.findViewById(R.id.profile_email);
        profileDate = (TextView) view.findViewById(R.id.profile_date);
        Button date = (Button) view.findViewById(R.id.date_button);
        final EditProfileFragment that = this;
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable(DatePickingFragment.LISTEN, that);
                try {
                    int[] vals = Driver.getValueOfDate(mShared.getString(
                            getString(R.string.BDAY), "00000000"));
                    b.putInt(DatePickingFragment.YEAR, vals[0]);
                    b.putInt(DatePickingFragment.MONTH, vals[1]);
                    b.putInt(DatePickingFragment.YEAR, vals[2]);
                } catch (ParseException e) {
                    Calendar c = Calendar.getInstance();
                    Log.i("EditProfile:date", "Incorrect format for date. Using default.");
                    b.putInt(DatePickingFragment.YEAR, c.get(Calendar.YEAR));
                    b.putInt(DatePickingFragment.MONTH, c.get(Calendar.MONTH));
                    b.putInt(DatePickingFragment.DAY, c.get(Calendar.DAY_OF_MONTH));
                }
                if (Driver.DEBUG) {
                    Log.i("EditProfile:date", "Created Bundle, attempting to create fragment.");
                }
                DatePickingFragment fragment = new DatePickingFragment();
                fragment.setArguments(b);
                if (Driver.DEBUG) Log.i("EditProfile:date", "Created fragment, showing...");
                fragment.show(getActivity().getSupportFragmentManager(), "launch");
            }
        });
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
        Button btncancel = (Button) view.findViewById(R.id.profile_cancel);
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.editProfileCallback(true,"Action Canceled");
            }
        });
        return view;
    }

    /**
     * Updates the text boxes with the user's information.
     */
    public void updateProfile() {
        loggedin = mShared.getBoolean(getString(R.string.LOGGEDIN), false);
        if (loggedin) {
            profileEmail.setText(mShared.getString(getString(R.string.USER),""));
            profileEmail.setEnabled(false);
            try {
                profileDate.setText(Driver.parseDateForDisplay(mShared.getString(
                        getString(R.string.BDAY), "00000000")));
            } catch (ParseException e) {
                Calendar c = Calendar.getInstance();
                Log.i("EditProfile:date", "Incorrect format for date. Using default.");
                try {
                    String date = Driver.parseDateForDisplay(c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                    profileDate.setText(date);
                } catch (ParseException e2) {
                    Toast.makeText(getActivity(), "Unable to get profile information.",
                        Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Validates the information in the text boxes.
     * @view is for sending toasts. Still trying to get this to work.
     * @return true if the information can be used,
     *          false otherwise.
     */
    private boolean validate(View v) {
        String email = profileEmail.getText().toString();
        String date = profileDate.getText().toString();
        String pass1 = profilePass1.getText().toString();
        String pass2 = profilePass2.getText().toString();
        if (!Driver.isValidEmail(email)){
            Toast.makeText(getActivity(), "Invalid email.", Toast.LENGTH_LONG).show();
            profileEmail.requestFocus();
            return false;
        }
        if (date.isEmpty() || profileQuery[1] == null) {
            Toast.makeText(getActivity(), "Enter or confirm the date.", Toast.LENGTH_LONG).show();
            return false;
        }
        String result = Driver.isValidPassword(LoginActivity.PROFILE_NEW, pass1, pass2);
        if (!result.contains("success")) {
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            profilePass1.requestFocus();
            return false;
        }
        if (loggedin) {
            //TODO check if the password equals old...naw too hard...
        }
        if (Driver.DEBUG) {
            String print = "{email:" + email + ", "
                    + ", pass1:" + pass1 + ", pass2:" + pass2;
            Log.i("EditProfile:valid4", print);
        }
        profileQuery[0] = email;
        //profileQuery[1] = result;
        profileQuery[2] = pass1;
        if(Driver.DEBUG)
            Log.i("EditProfile:text1", "{0:" + profileQuery[0] +
                    ", 1:" + profileQuery[1] +
                    ", 2:" + profileQuery[2]);
        return true;
    }

    /**
     * Validates and creates the URL to query the database.
     * @param view for making toasts.
     * @return the URL for editing or adding a profile.
     */
    public String buildURL(View view) {
        StringBuilder sb = new StringBuilder();
        try {
            if (loggedin) {
                sb.append(PROFILE_EDIT_URL);
                sb.append("uid=");
                sb.append(URLEncoder.encode(mShared.getString(
                        getString(R.string.UID), "-1"),"UTF-8"));
                sb.append("&");
            }
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        try {
            profileQuery[1] = Driver.parseDateForDB(year, monthOfYear + 1, dayOfMonth);
            profileDate.setText(Driver.parseDateForDisplay(profileQuery[1]));
        } catch (ParseException e) {
            Log.i("EditProfile:dateset", "Unusable date.");
            profileQuery[1] = "00000000";
        }
    }

    /**
     * The listener is for sending data to the database
     * to be inserted or updated depending on whether the
     * user is logged in.
     */
    public interface EditProfileListener {
        /**
         * Triggers the listener to start the edit profile
         * task that talks to the database.
         * @param url is the url to use to update or insert a record.
         */
        void editProfile(String url);

        /**
         * This lets the calling listener know when the update
         * task is done.
         * @param success indicates if the operation was successful.
         * @param message the message to display to the user.
         */
        void editProfileCallback(boolean success, String message);
    }
}
