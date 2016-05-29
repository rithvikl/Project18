package tcss450.uw.edu.project18;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Calendar;

import tcss450.uw.edu.project18.event.Event;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditEventFragment.OnEditEventInteractionListener} interface
 * to handle interaction events.
 */
public class EditEventFragment extends Fragment
    implements DatePickerDialog.OnDateSetListener, Serializable {

    /**
     * The shared preferences file used for storing user info
     */
    private SharedPreferences mShared;

    private String mUser;

    public static final String EDIT_EVENT_URL =
            "http://cssgate.insttech.washington.edu/~_450atm18/editevent.php?";

    public static final String CREATE_EVENT_URL =
            "http://cssgate.insttech.washington.edu/~_450atm18/upload.php?";

    public static final String GET_PHOTO_URL =
            "http://cssgate.insttech.washington.edu/~_450atm18/loadpicture.php?";

    public static final String PHOTO_FILE_PATH = "photo_file_path";

    public static final float ROTATE_90 = 90;

    private OnEditEventInteractionListener mEditListener;

    private EditText mEventItemTitleEditText;
    private TextView mEventEditDate;
    private EditText mEventItemCommentEditText;
    private EditText mEventTagsEditText;
    private ImageView mEventImageView;
    public ProgressBar mProgressBar;

    private String mEventItemId;
    private String mEventItemPhotoFilePath;
    private Event mEventItem;
    private String mEventItemNewDate;
//    private Bitmap mBitmap;

    public EditEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEditEventInteractionListener) {
            mEditListener = (OnEditEventInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEditEventInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the user's username from shared preferences
        mShared = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        mUser = mShared.getString(getString(R.string.USER), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edit_event, container, false);
        mEventItemTitleEditText = (EditText) view.findViewById(R.id.event_item_title_edit);
        mEventEditDate = (TextView) view.findViewById(R.id.event_edit_date_display);
        mEventItemCommentEditText = (EditText) view.findViewById(R.id.event_item_comment_edit);
        mEventTagsEditText = (EditText) view.findViewById(R.id.event_edit_tags);
        mEventImageView = (ImageView) view.findViewById(R.id.event_item_photo_edit);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        final SharedPreferences shared = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS),
                Context.MODE_PRIVATE);
        final EditEventFragment that = this;
        Button datebtn = (Button) view.findViewById(R.id.event_edit_date_button);
        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable(DatePickingFragment.LISTEN, that);
                try {
                    int[] vals = Driver.getValueOfDate(shared.getString(
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
                    Log.i("EditEvent:date", "Created Bundle, attempting to create fragment.");
                }
                DatePickingFragment fragment = new DatePickingFragment();
                fragment.setArguments(b);
                if (Driver.DEBUG) Log.i("EditProfile:date", "Created fragment, showing...");
                fragment.show(getActivity().getSupportFragmentManager(), "launch");
            }
        });
        Button cancelbtn = (Button) view.findViewById(R.id.edit_event_cancel_button);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        Button submitbtn = (Button) view.findViewById(R.id.event_edit_submit_button);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    mEventItem.setTitle(mEventItemTitleEditText.getText().toString());
                    mEventItem.setDate(mEventItemNewDate);
                    mEventItem.setComment(mEventItemCommentEditText.getText().toString());
                    mEventItem.setTags(mEventTagsEditText.getText().toString());
                    if (mEventItemId != "-1") {
                        mEditListener.onEditEventInteraction(buildEditURL(), mEventItem);
                    } else {
                        mEditListener.onCreateEventInteraction(mProgressBar, mEventItem, buildCreateURL());
                    }
                } else {
                    Toast.makeText(v.getContext(),
                            "No network connection available. Please connect to a network to edit or create events.",
                            Toast.LENGTH_LONG) .show();
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            //can be used for creating an event as well
            mEventItemPhotoFilePath = args.getString(PHOTO_FILE_PATH);
            mEventItem = (Event) args.getSerializable(ViewEventFragment.EVENT_ITEM_SELECTED);
            updateView(mEventItem);
        }
    }

    /**
     * Update the fragment with an Event's information.
     * @param event is the Event to update with.
     */
    public void updateView(Event event) {
        if (event != null) {
            mEventItemTitleEditText.setText(event.getTitle());
            mEventItemNewDate = event.getDate();
            try {
                mEventEditDate.setText(Driver.parseDateForDisplay(
                        event.getDate()));
            } catch (ParseException e) {
                Log.i("EditEvent:start", "Could not retrieve date.");
            }
            mEventItemCommentEditText.setText(event.getComment());
            mEventTagsEditText.setText(event.getTags());

            mEventItemId = event.getId();
            if (mEventItemId != "-1") {
                // Download the photo from the web service
//                String get_photo_url = Uri.parse(GET_PHOTO_URL)
//                        .buildUpon()
//                        .appendQueryParameter("email", mUser)
//                        .appendQueryParameter("id", mEventItemId)
//                        .build()
//                        .toString();
//                GetPhotoUrlTask task = new GetPhotoUrlTask(getActivity());
//                task.execute(new String[]{get_photo_url, "edit"});
                DownloadImageTask dit = new DownloadImageTask(mEventImageView, mEventItem.getPhotoFileName(), getContext());
                dit.execute();
            } else {
                // Event is being created, get photo from device
                File imgFile = new  File(mEventItemPhotoFilePath);
                if(imgFile.exists()){

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    try {
                        ExifInterface exif = new ExifInterface(imgFile.getAbsolutePath());
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                        if (orientation == 6) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(ROTATE_90);
                            Bitmap rotatedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
                            mEventImageView.setImageBitmap(rotatedBitmap);
                        } else {
                            mEventImageView.setImageBitmap(myBitmap);
                        }
                    } catch (IOException e) {
                        Log.e("CREATE", "Unable to find file: " + e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        try {
            mEventItemNewDate = Driver.parseDateForDB(year,monthOfYear+1,dayOfMonth);
            mEventEditDate.setText(Driver.parseDateForDisplay(mEventItemNewDate));
        } catch (ParseException e) {
            Log.i("EditEvent:set", "Could not set date.");
        }
    }

    /**
     * Create the URL for updating an Event.
     * @return a String URL.
     */
    public String buildEditURL() {
        StringBuilder sb = new StringBuilder();
        try {
            SharedPreferences sp = getActivity().getSharedPreferences(getString(
                    R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            sb.append(EDIT_EVENT_URL);
            sb.append("email=");
            sb.append(URLEncoder.encode(sp.getString(getString(R.string.USER),null), "UTF-8"));
            sb.append("&id=");
            sb.append(URLEncoder.encode(mEventItem.getId(), "UTF-8"));
            sb.append("&title=");
            sb.append(URLEncoder.encode(mEventItemTitleEditText.getText().toString(), "UTF-8"));
            sb.append("&date=");
            sb.append(URLEncoder.encode(mEventItemNewDate, "UTF-8"));
            sb.append("&comment=");
            sb.append(URLEncoder.encode(mEventItemCommentEditText.getText().toString(), "UTF-8"));
            sb.append("&tags=");
            sb.append(URLEncoder.encode(mEventTagsEditText.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            if (Driver.DEBUG) Toast.makeText(getActivity(), "Illegal something.",
                    Toast.LENGTH_LONG).show();
        }
        Log.i("EDIT", "Edit URL: " + sb.toString());
        return sb.toString();
    }

    /**
     * Create the URL for creating an Event.
     * @return a String URL.
     */
    public String buildCreateURL() {
        StringBuilder sb = new StringBuilder();
        try {
            SharedPreferences sp = getActivity().getSharedPreferences(getString(
                    R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            sb.append(CREATE_EVENT_URL);
            sb.append("&email=");
            sb.append(URLEncoder.encode(sp.getString(getString(R.string.USER),null), "UTF-8"));
            sb.append("&title=");
            sb.append(URLEncoder.encode(mEventItemTitleEditText.getText().toString(), "UTF-8"));
            sb.append("&date=");
            sb.append(URLEncoder.encode(mEventItemNewDate, "UTF-8"));
            sb.append("&comment=");
            sb.append(URLEncoder.encode(mEventItemCommentEditText.getText().toString(), "UTF-8"));
            sb.append("&tags=");
            sb.append(URLEncoder.encode(mEventTagsEditText.getText().toString(), "UTF-8"));
            sb.append("&photoFileName=");
            sb.append(URLEncoder.encode(mEventItem.getPhotoFileName(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            if (Driver.DEBUG) Toast.makeText(getActivity(), "Illegal something.",
                    Toast.LENGTH_LONG).show();
        }
        Log.i("CREATE", "Create URL: " + sb.toString());
        return sb.toString();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnEditEventInteractionListener {
        void onEditEventInteraction(String url, Event editedEvent);
        void editEventCallback(boolean result, String message, Event editedEvent);
        void onCreateEventInteraction(ProgressBar progressBar, Event createdEvent, String url);
        void createEventCallback(boolean result, String message);
        void uploadImageCallback(boolean result, String message);
    }
}
