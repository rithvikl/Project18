package tcss450.uw.edu.project18;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import tcss450.uw.edu.project18.event.Event;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewEventFragment.OnViewEventInteractionListener} interface
 * to handle interaction events.
 */
public class ViewEventFragment extends Fragment {

    /**
     * The shared preferences file used for storing user info
     */
    private SharedPreferences mShared;

    private String mUser;

    public static final String EVENT_ITEM_SELECTED = "EventItemSelected";
    public static final String EVENT_IMAGE_SELECTED = "EventImageSelected";

    public static final String GET_PHOTO_URL =
            "http://cssgate.insttech.washington.edu/~_450atm18/loadpicture.php?";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_view_event, container, false);
        mEventItemTitleTextView = (TextView) view.findViewById(R.id.event_item_title);
        mEventItemDateTextView = (TextView) view.findViewById(R.id.event_item_date);
        mEventItemCommentTextView = (TextView) view.findViewById(R.id.event_item_comment);
        mEventItemPhotoView = (ImageView) view.findViewById(R.id.event_item_photo);
        Button editbtn = (Button) view.findViewById(R.id.event_item_button);
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEvent(v);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Share an event", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //TODO create an dialog that lets them choose between email or messaging
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
            updateView((Event) args.getSerializable(EVENT_ITEM_SELECTED));
        }
    }

    public void updateView(Event event) {
        if (event != null) {
            mEventItem = event;
            mEventItemTitleTextView.setText(event.getTitle());
            mEventItemDateTextView.setText(event.getDate());
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
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_container, editEventFragment).addToBackStack(null).commit();
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
        void onViewEventInteraction(Uri uri);
    }
}
