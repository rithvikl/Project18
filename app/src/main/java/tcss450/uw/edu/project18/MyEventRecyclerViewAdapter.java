package tcss450.uw.edu.project18;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tcss450.uw.edu.project18.EventListFragment.OnListFragmentInteractionListener;
import tcss450.uw.edu.project18.event.Event;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Event} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyEventRecyclerViewAdapter extends RecyclerView.Adapter<MyEventRecyclerViewAdapter.ViewHolder> {

    /**
     * The list of events to be displayed
     */
    private final List<Event> mValues;

    /**
     * The listener for the event list fragment
     */
    private final OnListFragmentInteractionListener mListener;

    /**
     * Constructor to instanciate the recylcer and initialize the list and listener
     * @param items the list of user's events
     * @param listener the fragment interaction listener
     */
    public MyEventRecyclerViewAdapter(List<Event> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    /**
     * Creates a holder for each event itme
     * @param parent the view to fill with event
     * @param viewType the type of the view
     * @return the newly created viewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Bind the event data to the viewholder
     * @param holder the viewholder to bind event data to
     * @param position the positions of event in the list
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).getTitle());
        holder.mCommentView.setText(mValues.get(position).getComment());
        holder.mDateView.setText(mValues.get(position).getDate());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    /**
     * The number of events in the list
     * @return the number of events
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Viewholder class to hold each event
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * The view to hold an event
         */
        public final View mView;

        /**
         * The TextView to hold the event title
         */
        public final TextView mTitleView;

        /**
         * The TextView to hold the event comment
         */
        public final TextView mCommentView;

        /**
         * The TextView to hold the event date
         */
        public final TextView mDateView;

        /**
         * The event itself
         */
        public Event mItem;

        /**
         * Constructor for the viewholder
         * @param view the view containing the elements
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.event_title);
            mCommentView = (TextView) view.findViewById(R.id.event_comment);
            mDateView = (TextView) view.findViewById(R.id.event_date);
        }

        /**
         * String representation of the viewholder and it's event
         * @return the title of the event
         */
        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
