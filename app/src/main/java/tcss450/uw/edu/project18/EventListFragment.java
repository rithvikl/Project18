package tcss450.uw.edu.project18;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.project18.event.Event;

/**
 * A fragment representing a list of events.
 * @author Rithvik Lagisetti
 * @version 20160504
 */
public class EventListFragment extends Fragment implements SearchView.OnQueryTextListener {

    /**
     * String for the column-count property
     */
    private static final String ARG_COLUMN_COUNT = "column-count";

    /**
     * The url of the web service to get a list of events
     */
    private static final String GET_EVENTS_URL = "http://cssgate.insttech.washington.edu/~_450atm18/download.php?";

    /**
     * The number of columns of the list
     */
    private int mColumnCount = 1;

    /**
     * The listener for the fragment
     */
    private OnListFragmentInteractionListener mListener;

    public SearchView.OnQueryTextListener mQueryTextListener;

    public EventDB mEventDB;

    public MyEventRecyclerViewAdapter mAdapter;

    /**
     * The Recycler to bind the list of events
     */
    private RecyclerView mRecyclerView;

    /**
     * The list of the user's events
     */
    private List<Event> mEventList;

    private List<Event> mFullEventList;

    /**
     * The shared preferences file used for storing user info
     */
    private SharedPreferences mShared;

    /**
     * The username of the currently logged in user
     */
    private String mUser;

    private ProgressDialog mProgressDialog;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventListFragment() {
    }

    /**
     * Creates an instance of the fragment
     * @param columnCount the number of columns of the list
     * @return the list fragment
     */
    @SuppressWarnings("unused")
    public static EventListFragment newInstance(int columnCount) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Sets some member fields when the fragment is created
     * @param savedInstanceState the saved instance of the app
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        // Get the user's username from shared preferences
        mShared = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        mUser = mShared.getString(getString(R.string.USER), "");
    }

    /**
     * Creates the list of events when the fragment is created
     * @param inflater inflates the fragment
     * @param container the container to hold it
     * @param savedInstanceState the saved instance of the app
     * @return the created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Loading Events");
        mProgressDialog.setMessage("Please wait...");

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Get the list of events if network connection exists
        if (networkInfo != null && networkInfo.isConnected()) {

            // Build the query url
            // Execute async task with url
            mProgressDialog.show();
            DownloadEventsTask task = new DownloadEventsTask();
            String method_url = buildUrlString();
            task.execute(new String[]{method_url});
        } else {

            // User can't do anything without network connection
            Toast.makeText(view.getContext(),
                    "No network connection available. Please connect to a network to view pictures and edit or create events.",
                    Toast.LENGTH_LONG) .show();

            if (mEventDB == null) {
                mEventDB = new EventDB(getActivity());
            }
            if (mEventList == null) {
                mEventList = mEventDB.getEvents();
            }
            mAdapter = new MyEventRecyclerViewAdapter(mEventList, mListener);
            mRecyclerView.setAdapter(mAdapter);
        }

        return view;
    }

    /**
     * Build the url string for getting the list of events
     * @return the url string
     */
    public String buildUrlString () {
        String url = Uri.parse(GET_EVENTS_URL)
                .buildUpon()
                .appendQueryParameter("email", mUser)
                .appendQueryParameter("tag", "ALL")
                .build()
                .toString();
        return url;
    }


    /**
     * Initialize the listener when attached
     * @param context context of the app
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
            mQueryTextListener = this;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    /**
     * destroy the listener when detached
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mQueryTextListener = null;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        Log.i("FILTER", "Query: " + query);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i("FILTER", "Query: " + query);
        if (query != "") {
            Log.i("FILTER", "Pre filter full list:" + mEventList.toString());

            List<Event> tempList = new ArrayList<Event>();
            tempList.addAll(mFullEventList);
            final List<Event> filteredEventList = filter(tempList, query);
            mAdapter.animateTo(filteredEventList);
            mRecyclerView.scrollToPosition(0);

            Log.i("FILTER", "Post filter full list:" + mEventList.toString());
            Log.i("FILTER", "Post filter filtered list:" + filteredEventList);

        } else {
            Log.i("FILTER", "Empty query list:" + mFullEventList.toString());
            mAdapter.animateTo(mFullEventList);
            mRecyclerView.scrollToPosition(0);
        }

        // Check if no view has focus:
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        return true;
    }


    private List<Event> filter(List<Event> events, String query) {
        query = query.toLowerCase();

        final List<Event> filteredEventList = new ArrayList<>();
        for (Event event : events) {
            final String text = event.getTags().toLowerCase();
            if (text.contains(query)) {
                filteredEventList.add(event);
            }
        }
        return filteredEventList;
    }

    /**
     * This interface implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Event item);
    }

    public EventDB getEventDB() {
        return mEventDB;
    }

    /**
     * Async task to get the user's list of events
     */
    private class DownloadEventsTask extends AsyncTask<String, Void, String> {

        /**
         * The background call
         * @param urls list of urls to call
         * @return the JSON response string
         */
        @Override protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();
                    InputStream content = urlConnection.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s;
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                } catch (Exception e) {
                    response = "Unable to download the list of events, Reason: " + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * Method to create the list from the JSON response
         * @param result the result
         */
        @Override protected void onPostExecute(String result) {
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                return;
            }

            mEventList = new ArrayList<Event>();
            Log.i("RESPONSE", mEventList.toString());
            result = Event.parseEventJSON(result, mEventList);
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                return;
            }
            if (!mEventList.isEmpty()) {
                mFullEventList = new ArrayList<Event>();
                mFullEventList.addAll(mEventList);

                if (mEventDB == null) {
                    mEventDB = new EventDB(getActivity());
                }

                // Delete old data so that you can refresh the local database with the network data.
                mEventDB.deleteEvents();

                // Also, add to the local database
                for (int i=0; i < mEventList.size(); i++) {
                    Event event = mEventList.get(i);
                    mEventDB.insertEvent(event);
                }
                mAdapter = new MyEventRecyclerViewAdapter(mEventList, mListener);
                mRecyclerView.setAdapter(mAdapter);
            }
            mProgressDialog.dismiss();
        }
    }
}
