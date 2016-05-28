package tcss450.uw.edu.project18;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.audiofx.EnvironmentalReverb;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;

import tcss450.uw.edu.project18.event.Event;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewEventFragment.OnViewEventInteractionListener} interface
 * to handle interaction events.
 */
public class ViewEventFragment extends Fragment
    implements Serializable,
    ConfirmDialogFragment.onConfirmInteraction{

    /**
     * The shared preferences file used for storing user info
     */
    private SharedPreferences mShared;

    private String mUser;

    public static final String EVENT_ITEM_SELECTED = "EventItemSelected";

    public static final String GET_PHOTO_URL =
            "http://cssgate.insttech.washington.edu/~_450atm18/loadpicture.php?";

    public static final String DELETE_EVENT_URL =
            "http://cssgate.insttech.washington.edu/~_450atm18/deleteevent.php?";

    private OnViewEventInteractionListener mListener;
    private TextView mEventItemTitleTextView;
    private TextView mEventItemDateTextView;
    private TextView mEventItemCommentTextView;
    private ImageView mEventItemPhotoView;
    private String mEventItemPhotoId;
    private Event mEventItem;


    public ViewEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the user's username from shared preferences
        mShared = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        mUser = mShared.getString(getString(R.string.USER), "");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnViewEventInteractionListener) {
            mListener = (OnViewEventInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnViewEventInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_view_event, container, false);
        mEventItemTitleTextView = (TextView) view.findViewById(R.id.event_item_title);
        mEventItemDateTextView = (TextView) view.findViewById(R.id.event_item_date);
        mEventItemCommentTextView = (TextView) view.findViewById(R.id.event_item_comment);
        mEventItemPhotoView = (ImageView) view.findViewById(R.id.event_item_photo);
        final ViewEventFragment that = this;
        Button editbtn = (Button) view.findViewById(R.id.event_item_button);
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEvent(v);
            }
        });
        Button deleteBtn = (Button) view.findViewById(R.id.event_item_delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open confirm first
                DialogFragment fragment = new ConfirmDialogFragment();
                Bundle args = new Bundle();
                args.putString(ConfirmDialogFragment.CONFIRM_MESSAGE, "Delete?");
                args.putSerializable(ConfirmDialogFragment.CONFIRM_LISTEN, that);
                fragment.setArguments(args);
                fragment.show(getActivity().getFragmentManager(), "onOptionsItemSelected");
            }
        });
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Share an event", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //TODO create an dialog that lets them choose between email or messaging?
                Bundle args = new Bundle();
                args.putSerializable(ShareDialogFragment.SHARE_VIEW_FRAGMENT, that);
                ShareDialogFragment share = new ShareDialogFragment();
                share.setArguments(args);
                share.show(getActivity().getFragmentManager(), "onCreateView");*/
                sendEmail();
            }
        });
        return view;
    }

    @Override
    public void onConfirm(boolean confirm) {
        if(confirm) mListener.onViewEventInteraction(buildDeleteURL(), mEventItem);
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
            updateView((Event) args.getSerializable(EVENT_ITEM_SELECTED));
        }
    }

    public void updateView(Event event) {
        if (event != null) {
            mEventItem = event;
            mEventItemTitleTextView.setText(event.getTitle());

            try {
                mEventItemDateTextView.setText(Driver.parseDateForDisplay(
                        event.getDate()));
            } catch (ParseException e) {
                Log.i("ViewEvent:start", "Could not retrieve date.");
            }

            mEventItemCommentTextView.setText(event.getComment());
            mEventItemPhotoId = event.getId();
            String get_photo_url = Uri.parse(GET_PHOTO_URL)
                    .buildUpon()
                    .appendQueryParameter("email", mUser)
                    .appendQueryParameter("id", mEventItemPhotoId)
                    .build()
                    .toString();
            GetPhotoUrlTask task = new GetPhotoUrlTask(getActivity());
            task.execute(new String[]{get_photo_url, "view"});
        }
    }

    public String buildDeleteURL() {
        StringBuilder sb = new StringBuilder();
        try {
            SharedPreferences sp = getActivity().getSharedPreferences(getString(
                    R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            sb.append(DELETE_EVENT_URL);
            sb.append("email=");
            sb.append(URLEncoder.encode(sp.getString(getString(R.string.USER),null), "UTF-8"));
            sb.append("&id=");
            sb.append(URLEncoder.encode(mEventItem.getId(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            if (Driver.DEBUG) Toast.makeText(getActivity(), "Illegal something.",
                    Toast.LENGTH_LONG).show();
        }
        Log.i("DELETE", "DELETE URL: " + sb.toString());
        return sb.toString();
    }

    /**
     * Opens a fragment to edit an Event.
     * @param view is the button that triggered calling this function,
     *             but it doesn't matter.
     */
    public void editEvent(View view) {
            EditEventFragment editEventFragment = new EditEventFragment();
            Bundle args = new Bundle();
            args.putSerializable(ViewEventFragment.EVENT_ITEM_SELECTED, mEventItem);
            editEventFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, editEventFragment).addToBackStack(null).commit();
    }

    public Bitmap getImage() {
        if(Driver.DEBUG) {
            Log.d("view:image", "" + mEventItemPhotoView);
            Log.d("view:image", "" + mEventItemPhotoView.getDrawable());
        }
        return ((BitmapDrawable)mEventItemPhotoView.getDrawable()).getBitmap();
    }

    public Event getEvent() {
        return mEventItem;
    }
    public void sendEmail() {
        try {
            File file = saveToTemp();
            if (file == null) throw new Exception("File is null.");
            /*if (file.getFreeSpace() < (long)(file.getTotalSpace()*.9)) {
                throw new Exception("Not enough room.");
            }*/
            Intent email = new Intent(Intent.ACTION_SEND);
            //Uri uri = FileProvider.getUriForFile(getActivity(),getString(R.string.FILE_AUTH),file);
            Uri uri = Uri.fromFile(file);
            email.putExtra(Intent.EXTRA_STREAM, uri);
            email.setType("image/jpeg");
            getActivity().startActivity(Intent.createChooser(email,"Use..."));
        } catch (Exception e) {
            Log.i("Share:email",e.getMessage());
        }
    }

    public boolean isExternWritable() {
        String state = Environment.getExternalStorageState();
        if (Driver.DEBUG) Log.d("view:writable", "State=" + state);
        if (Environment.MEDIA_MOUNTED.equals(state)) return true;
        return false;
    }

    public File saveToTemp() throws IOException {
        if(!isExternWritable()) throw new IOException("Cannot write to external storage.");
        File file = new File(Environment.getExternalStorageDirectory(), "tmp.jpg");
        //if (Driver.DEBUG) Log.d("view:save", file.getAbsolutePath());
        if (!file.createNewFile()) {
            Log.i("Share:file", "File not created.");
        } else Log.i("Share:file", "File created.");
        //if (Driver.DEBUG) Log.d("view:save", file.getAbsolutePath());
        Bitmap mImage = getImage();
        //save image to file
        if (mImage != null) {
            OutputStream fout = new FileOutputStream(file);
            mImage.compress(Bitmap.CompressFormat.JPEG, 85, fout);
            fout.flush();
            fout.close();
        }
        //if (Driver.DEBUG) Log.d("view:save", file.getAbsolutePath());
        return file;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnViewEventInteractionListener {
        void onViewEventInteraction(String url, Event event);
        void deleteEventCallback(boolean result, String message, Event event);
    }
}
