package tcss450.uw.edu.project18;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.project18.event.Event;
//import tcss450.uw.edu.project18.event.Event.EventItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class EventListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String FLICKR_URL = "https://api.flickr.com/services/rest/";
    private static final String API_KEY = "219eeae9d3fec8448f88e5cf788a390a";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
//    private CourseDB mCourseDB;
    private List<Event> mEventList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventListFragment() {
    }

    @SuppressWarnings("unused")
    public static EventListFragment newInstance(int columnCount) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

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
//            mRecyclerView.setAdapter(new MyEventRecyclerViewAdapter(Event.ITEMS, mListener));
        }

        // If we can't use menu buttons, will use floating button
//        FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
//        floatingActionButton.show();

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Get the list of events if network connection exists
        if (networkInfo != null && networkInfo.isConnected()) {

            // Get the user's gallery ID from shared preferences
            // Build the Flickr query url
            // Execute async task with url
            DownloadEventsTask task = new DownloadEventsTask();
            String method_url = buildUrlString();
            task.execute(new String[]{method_url});
        } else {

            // User can't do anything without network connection
            Toast.makeText(view.getContext(),
                    "No network connection available. Please connect to a network to see your events.",
                    Toast.LENGTH_SHORT) .show();
        }

        return view;
    }

    public String buildUrlString () {
        String url = Uri.parse(FLICKR_URL)
                .buildUpon()
                .appendQueryParameter("method", "flickr.photos.getRecent")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build()
                .toString();
        return url;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Event item);
    }

    // Get the user's galleryID and use that to get their photos
    private class DownloadEventsTask extends AsyncTask<String, Void, String> {

        @Override protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();
                    InputStream content = urlConnection.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
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

        @Override protected void onPostExecute(String result) {
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                return;
            }

            mEventList = new ArrayList<Event>();
            result = Event.parseCourseJSON(result, mEventList);
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                return;
            }
            if (!mEventList.isEmpty()) {
                mRecyclerView.setAdapter(new MyEventRecyclerViewAdapter(mEventList, mListener));

//                if (mCourseDB == null) {
//                    mCourseDB = new CourseDB(getActivity());
//                }
//
//                // Delete old data so that you can refresh the local
//                // database with the network data.
//                mCourseDB.deleteCourses();
//
//                // Also, add to the local database
//                for (int i=0; i < mCourseList.size(); i++) {
//                    Course course = mCourseList.get(i);
//                    mCourseDB.insertCourse(course.getCourseId(),
//                            course.getShortDescription(),
//                            course.getLongDescription(),
//                            course.getPrereqs());
//                }
            }
        }
    }
}
