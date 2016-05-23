package tcss450.uw.edu.project18;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import tcss450.uw.edu.project18.event.Event;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewEventFragment.OnViewEventInteractionListener} interface
 * to handle interaction events.
 */
public class ViewEventFragment extends Fragment {

    public static final String EVENT_ITEM_SELECTED = "EventItemSelected";

    private OnViewEventInteractionListener mListener;
    private TextView mEventItemTitleTextView;
    private TextView mEventItemDateTextView;
    private TextView mEventItemCommentTextView;
    private String mEventItemPhotoId;
    private Event mEventItem;


    public ViewEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_view_event, container, false);
        mEventItemTitleTextView = (TextView) view.findViewById(R.id.event_item_title);
        mEventItemDateTextView = (TextView) view.findViewById(R.id.event_item_date);
        mEventItemCommentTextView = (TextView) view.findViewById(R.id.event_item_comment);
        Button editbtn = (Button) view.findViewById(R.id.event_item_button);
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEvent(v);
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
            // TODO: Get photo and attach to ImageView
            mEventItemPhotoId = event.getId();
        }
    }

    /**
     * Opens a fragment to edit an Event.
     * @param view is the button that triggered calling this function.
     */
    public void editEvent(View view) {
        if (view.getId() == R.id.event_item_button) {
            EditEventFragment editEventFragment = new EditEventFragment();
            Bundle args = new Bundle();
            args.putSerializable(ViewEventFragment.EVENT_ITEM_SELECTED, mEventItem);
            editEventFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_container, editEventFragment).addToBackStack(null).commit();
        }
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
