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
 * Created by Melinda Robertson on 5/19/2016.
 */
public class EditEventTask extends AsyncTask<String, Void, String> {

    EditEventFragment.OnEditEventInteractionListener mListener;

    public EditEventTask (EditEventFragment.OnEditEventInteractionListener listener) {
        super();
        this.mListener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";
        HttpURLConnection con = null;
        for (String url: params) {
            try {
                URL urlobj = new URL(url);
                con = (HttpURLConnection) urlobj.openConnection();
                InputStream content = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = br.readLine()) != null) {
                    response += s;
                }
                if(Driver.DEBUG)
                    Log.i("EditEventTask:do", response);
            } catch (MalformedURLException e) {
                Log.d("EditEventTask:do", "MalformedURL; cannot update event data.");
            } catch (IOException e) {
                Log.d("EditEventTask:do", "IOException; could not open URL.");
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
            if (status.equals("success"))
                mListener.editEventCallback(true, "Successfully edited event.");
            else mListener.editEventCallback(false, "There was an error editing your event.");
        } catch (JSONException e) {
            Log.d("EditEventTask:post", "Could not parse JSON response.");
            mListener.editEventCallback(false, "There was a format error in your data.");
        }
    }
}
