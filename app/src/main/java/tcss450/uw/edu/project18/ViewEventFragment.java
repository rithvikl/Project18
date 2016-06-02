package tcss450.uw.edu.project18;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;

import tcss450.uw.edu.project18.event.Event;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDeleteEventInteractionListener} interface
 * to handle interaction events.
 *
 * Let's the user view an event's details.
 *
 * @author Rithvik Lagissetti
 * @version 20160530
 */
public class ViewEventFragment extends Fragment
    implements Serializable, ConfirmDialogFragment.onConfirmInteraction {

    /**
     * Key for sending the selected event through a Bundle.
     */
    public static final String EVENT_ITEM_SELECTED = "EventItemSelected";
    /**
     * URL for deleting events.
     */
    public static final String DELETE_EVENT_URL =
            "http://cssgate.insttech.washington.edu/~_450atm18/deleteevent.php?";

    /**
     * Listens for the delete button to be pressed.
     */
    private OnDeleteEventInteractionListener mListener;
    /**
     * UI objects
     */
    private TextView mEventItemTitleTextView;
    private TextView mEventItemDateTextView;
    private TextView mEventItemCommentTextView;
    private ImageView mEventItemPhotoView;

    /**
     * Event being displayed.
     */
    private Event mEventItem;


    public ViewEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDeleteEventInteractionListener) {
            mListener = (OnDeleteEventInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDeleteEventInteractionListener");
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
        //confirms deletion before deleting event
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragment = new ConfirmDialogFragment();
                Bundle args = new Bundle();
                args.putString(ConfirmDialogFragment.CONFIRM_MESSAGE, "Delete?");
                args.putSerializable(ConfirmDialogFragment.CONFIRM_LISTEN, that);
                fragment.setArguments(args);
                fragment.show(getActivity().getFragmentManager(), "deleteButton");
            }
        });
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // During startup, check if there are arguments passed to the fragment.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateView((Event) args.getSerializable(EVENT_ITEM_SELECTED));
        }
    }

    /**
     * Updates the UI views with information from an Event.
     * @param event holds the information to update.
     */
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

            DownloadPhotoUrlTask dit = new DownloadPhotoUrlTask(mEventItemPhotoView, mEventItem.getPhotoFileName(), getContext());
            dit.execute();
        }
    }

    /**
     * Creates a URL for deleting an event.
     * @return the URL string for deleting an event.
     */
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

    /**
     * Creates and returns a bitmap from what is set in the ImageView.
     * @return a bitmap from the photo view.
     */
    public Bitmap getImage() {
        if(Driver.DEBUG) {
            Log.d("view:image", "" + mEventItemPhotoView);
            Log.d("view:image", "" + mEventItemPhotoView.getDrawable());
        }
        return ((BitmapDrawable)mEventItemPhotoView.getDrawable()).getBitmap();
    }

    /**
     * Get an event.
     * @return the event that is being displayed.
     */
    public Event getEvent() {
        return mEventItem;
    }

    /**
     * Saves the photograph to a temporary file and starts an intent
     * to send it via another app.
     */
    public void sendEmail() {
        try {
            File file = saveToTemp();
            if (file == null) throw new Exception("File is null.");
            Intent email = new Intent(Intent.ACTION_SEND);
            Uri uri = Uri.fromFile(file);
            email.putExtra(Intent.EXTRA_STREAM, uri);
            email.setType("image/jpeg");
            getActivity().startActivity(Intent.createChooser(email,"Use..."));
        } catch (Exception e) {
            Log.i("Share:email",e.getMessage());
        }
    }

    /**
     * Determines if the external storage is writable.
     * @return true if a file can be saved to external storage, false otherwise.
     */
    public boolean isExternWritable() {
        String state = Environment.getExternalStorageState();
        if (Driver.DEBUG) Log.d("view:writable", "State=" + state);
        if (Environment.MEDIA_MOUNTED.equals(state)) return true;
        return false;
    }

    /**
     * Saves an image to a temporary file.
     * @return the file that was saved to.
     * @throws IOException if the file cannot be saved.
     */
    public File saveToTemp() throws IOException {
        if(!isExternWritable()) throw new IOException("Cannot write to external storage.");
        File file = new File(Environment.getExternalStorageDirectory(), "tmp.jpg");
        if (Driver.DEBUG) Log.d("view:save", file.getAbsolutePath());
        if (!file.createNewFile()) {
            Log.i("Share:file", "File not created.");
        } else Log.i("Share:file", "File created.");
        Bitmap mImage = getImage();
        //save image to file
        if (mImage != null) {
            OutputStream fout = new FileOutputStream(file);
            mImage.compress(Bitmap.CompressFormat.JPEG, 85, fout);
            fout.flush();
            fout.close();
        }
        return file;
    }

    @Override
    public void onConfirm(boolean confirm) {
        mListener.onDeleteEventInteraction(buildDeleteURL(), mEventItem);
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
    public interface OnDeleteEventInteractionListener {
        /**
         * Called when the user requests an event to be deleted.
         * @param url is the delete url.
         * @param event is the Event to delete.
         */
        void onDeleteEventInteraction(String url, Event event);

        /**
         * Called when the deletion task is complete.
         * @param result is whether or not the task was successful.
         * @param message is an error message if applicable.
         * @param event is the event that was deleted.
         */
        void deleteEventCallback(boolean result, String message, Event event);
    }
}
