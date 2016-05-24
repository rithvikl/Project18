package tcss450.uw.edu.project18;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rithvikl on 5/23/16.
 */
public class GetPhotoUrlTask extends AsyncTask<String, Void, String> {

    private String mImageHolderFragment;

    WeakReference<Activity> mWeakActivity;

    public GetPhotoUrlTask(Activity activity) {
        mWeakActivity = new WeakReference<Activity>(activity);
    }

    @Override
    protected String doInBackground(String... urls) {
        String response = "";
        HttpURLConnection urlConnection = null;
        try {
//                Log.i("DEBUG", urls[0]);
//                Log.i("DEBUG", urls[1]);
            mImageHolderFragment = urls[1];
            URL urlObject = new URL(urls[0]);
            urlConnection = (HttpURLConnection) urlObject.openConnection();

            InputStream content = urlConnection.getInputStream();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }

        } catch (Exception e) {
            response = "Unable to get photo URL, Reason: " + e.getMessage();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return response;
    }


    /**
     * It checks to see if there was a problem with the URL(Network) which is when an
     * exception is caught. It tries to call the parse Method and checks to see if it was successful.
     * If not, it displays the exception.
     *
     * @param result
     */
    @Override
    protected void onPostExecute(String result) {
        // Something wrong with the network or the URL.
        try {
            JSONObject jsonObject = new JSONObject(result);
            String status = (String) jsonObject.get("result");
            if (status.equals("success")) {
                String photo_url = "http://" + jsonObject.get("url");
                Log.i("DEBUG", photo_url);
//                Log.i("DEBUG", result);
                DownloadPhotoTask downloadPhotoTask = new DownloadPhotoTask(mWeakActivity.get());
                downloadPhotoTask.execute(new String[]{photo_url, "view"});
            } else {
//                Toast.makeText(getApplicationContext(), "Failed to add: " + jsonObject.get("error"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
//            Toast.makeText(getApplicationContext(), "Something wrong with the data" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
