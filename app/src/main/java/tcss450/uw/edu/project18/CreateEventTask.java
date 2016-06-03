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

/**
 * Creates a new event by sending infromation about the event to a database.
 *
 * @version 20160601
 * @author Rithvik Lagisetti
 */
public class CreateEventTask extends AsyncTask<String, Void, String> {

    /**
     * The listener is waiting for this task to complete.
     */
    EditEventFragment.OnEditEventInteractionListener mListener;

    /**
     * Constructor.
     * @param listener is the listener for task completion.
     */
    public CreateEventTask(EditEventFragment.OnEditEventInteractionListener listener) {
        super();
        this.mListener = listener;
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
                String s;
                while ((s = br.readLine()) != null) {
                    response += s;
                }
                if(Driver.DEBUG)
                    Log.i("CreatEventTask:do", response);
            } catch (MalformedURLException e) {
                Log.d("CreateEventTask:do", "MalformedURL; cannot update event data.");
            } catch (IOException e) {
                Log.d("CreateEventTask:do", "IOException; could not open URL. " + e.getMessage());
            } finally {
                if (con != null)
                    con.disconnect();
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("CREATE:post", "Response from server: " + result);
        super.onPostExecute(result);

        try {
            JSONObject jo = new JSONObject(result);
            String status = (String) jo.get("event");
            Log.i("CREATE:post", "Status:" + status);
            if (status.equals("success")) {
                mListener.createEventCallback(true, "Successfully created event.");
            } else {
                mListener.createEventCallback(false, "There was an error creating your event.");
            }
        } catch (JSONException e) {
            Log.e("CreateEventTask:post", "Could not parse JSON: " + e.getMessage());
            mListener.createEventCallback(false, "There was an error in your data.");
        }
    }
}
