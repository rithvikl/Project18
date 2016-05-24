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
 * This will send data to the database to edit a user's profile.
 * This could be adding a profile or editing it. The URL is built
 * in the calling class.
 * Created by Mindy on 4/30/2016.
 * @author Melinda Robertson
 * @version 20160430
 */
public class EditProfileTask extends AsyncTask<String, Void, String> {

    /**
     * The calling class with be a listener. This is a chain
     * where the listener is getting responses from the
     * edit profile fragment then sending that to this task
     * along with itself so that it can get a editProfileCallback
     * when the task finishes.
     */
    private EditProfileFragment.EditProfileListener epl;

    /**
     *
     * @param epl
     */
    public EditProfileTask(EditProfileFragment.EditProfileListener epl) {
        super();
        this.epl = epl;
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
                    Log.i("EditProfileTask", response);
            } catch (MalformedURLException e) {
                Log.d("EditProfile:do", "MalformedURL; cannot update user data.");
            } catch (IOException e) {
                Log.d("EditProfile:do", "IOException; could not open URL.");
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
                epl.editProfileCallback(true, "Successfully edited user profile.");
            else epl.editProfileCallback(false, "There was an error editing your profile.");
        } catch (JSONException e) {
            Log.d("EditProfile:post", "Could not parse JSON response.");
            epl.editProfileCallback(false, "There was a format error in your data.");
        }
    }
}
