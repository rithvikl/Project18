package tcss450.uw.edu.project18;

import android.app.DatePickerDialog;
import java.text.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import tcss450.uw.edu.project18.event.Event;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditEventFragment.OnEditEventInteractionListener} interface
 * to handle interaction events.
 */
public class EditEventFragment extends Fragment
    implements DatePickerDialog.OnDateSetListener {

    private OnEditEventInteractionListener mListener;
    private EditText mEventItemTitleEditText;
    //private EditText mEventItemDateEditText;
    private TextView mEventEditDate;
    private EditText mEventItemCommentEditText;
    private String mEventItemPhotoId;
    private Event mEventItem;

    public EditEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_view_event, container, false);
        mEventItemTitleEditText = (EditText) view.findViewById(R.id.event_item_title_edit);
        //TODO change this to use the DatePickingFragment, use a button to open
        //mEventItemDateEditText = (EditText) view.findViewById(R.id.event_item_date_edit);
        mEventEditDate = (TextView) view.findViewById(R.id.event_edit_date_display);
        mEventItemCommentEditText = (EditText) view.findViewById(R.id.event_item_comment_edit);
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
            updateView((Event) args.getSerializable(ViewEventFragment.EVENT_ITEM_SELECTED));
        }
    }

    public void updateView(Event event) {
        if (event != null) {
            mEventItem = event;
            mEventItemTitleEditText.setText(event.getTitle());
            //mEventItemDateEditText.setText(event.getDate());
            try {
                mEventEditDate.setText(Driver.parseDateForDisplay(
                        event.getDate()));
            } catch (ParseException e) {
                Log.i("EditEvent:start", "Could not retrieve date.");
            }
            mEventItemCommentEditText.setText(event.getComment());
            // TODO: Get photo and attach to ImageView
            mEventItemPhotoId = event.getId();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //TODO use this to set the date of the fragment; format YYYYMMDD; use the driver
        try {
            mEventItem.setDate(Driver.parseDateForDB(year,monthOfYear+1,dayOfMonth));
            mEventEditDate.setText(Driver.parseDateForDisplay(
                    mEventItem.getDate()));
        } catch (ParseException e) {
            Log.i("EditEvent:set", "Could not set date.");
        }
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
        void onEditEventInteraction(String url);
        void callback(boolean result, String message);
    }
}
