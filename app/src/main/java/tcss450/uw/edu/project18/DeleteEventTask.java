package tcss450.uw.edu.project18;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import tcss450.uw.edu.project18.event.Event;

/**
 * Created by rithvikl on 5/26/16.
 *
 * Deletes an event by sending the id to the database.
 */
public class DeleteEventTask extends AsyncTask<String, Void, String> {

    /**
     * The listener waiting for this task to complete.
     */
    public ViewEventFragment.OnDeleteEventInteractionListener mListener;

    /**
     * The event to delete.
     */
    public Event mEvent;

    /**
     * Constructor.
     * @param listener is the listener waiting for the task to complete.
     * @param event is the event to delete.
     */
    public DeleteEventTask (ViewEventFragment.OnDeleteEventInteractionListener listener, Event event) {
        super();
        mListener = listener;
        mEvent = event;
    }

    @Override
    protected String doInBackground(String... urls) {
        String response = "";
        HttpURLConnection con = null;
        for (String url: urls) {
            try {
                URL urlobj = new URL(url);
                con = (HttpURLConnection) urlobj.openConnection();
                InputStream content = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = br.readLine()) != null) {
                    response += s;
                }
            } catch (MalformedURLException e) {
                Log.d("DeleteEventTask:do", "MalformedURL; cannot delete.");
            } catch (IOException e) {
                Log.d("DeleteEventTask:do", "IOException; could not open URL. " + e.getMessage());
            } finally {
                if (con != null)
                    con.disconnect();
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jo = new JSONObject(result);
            String status = (String) jo.get("result");
            if (status.equals("success")) {
                mListener.deleteEventCallback(true, "Successfully deleted event", mEvent);
            } else {
                Log.d("DeleteEventTask:fail", result);
                mListener.deleteEventCallback(false, "Unable to delete your event.", null);
            }
        } catch (JSONException e) {
            Log.d("DeleteEventTask:post", "Could not parse JSON response. " + e.getMessage());
            mListener.deleteEventCallback(false, "There was a format error in your data.", null);
        }
    }
}
