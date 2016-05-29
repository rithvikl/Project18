package tcss450.uw.edu.project18;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

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
 * Created by rithvikl on 5/27/16.
 */
public class CreateEventTask extends AsyncTask<String, Void, String> {

    long totalSize = 0;

    EditEventFragment.OnEditEventInteractionListener mListener;

    ProgressBar mProgressBar;

    String mPhotoPath;

    Event mCreatedEvent;

    SharedPreferences mShared;

    public CreateEventTask(EditEventFragment.OnEditEventInteractionListener listener) {
        super();
        this.mListener = listener;
//        this.mProgressBar = progressBar;
//        this.mPhotoPath = photoPath;
//        this.mCreatedEvent = createdEvent;
//        this.mShared = shared;
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

//    @Override
//    protected void onPreExecute() {
//        // setting progress bar to zero
//        mProgressBar.setProgress(0);
//        super.onPreExecute();
//    }

//    @Override
//    protected void onProgressUpdate(Integer... progress) {
//        // Making progress bar visible
//        mProgressBar.setVisibility(View.VISIBLE);
//
//        // updating progress bar value
//        mProgressBar.setProgress(progress[0]);
//
//        Log.i("Create:progress", "still going..." + progress[0]);
//
//    }

//    @Override
//    @SuppressWarnings("deprecation")
//    protected String doInBackground(Void... params) {
//        String responseString = null;
//
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpPost httppost = new HttpPost(FileUploadConfig.FILE_UPLOAD_URL);
//
//        try {
//            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
//                    new AndroidMultiPartEntity.ProgressListener() {
//
//                        @Override
//                        public void transferred(long num) {
//                            publishProgress((int) ((num / (float) totalSize) * 100));
//                        }
//                    });
//
//            Log.i("CREATE:do", "File Path: " + mPhotoPath);
//            File sourceFile = new File(mPhotoPath);
//
//            // Adding file data to http body
//            entity.addPart("file", new FileBody(sourceFile));
//
//            String email = mShared.getString("email", null);
//
//            // Extra parameters if you want to pass to server
//            entity.addPart("email", new StringBody(email));
//            entity.addPart("title", new StringBody(mCreatedEvent.getTitle()));
//            entity.addPart("date", new StringBody(mCreatedEvent.getDate()));
//            entity.addPart("comment", new StringBody(mCreatedEvent.getComment()));
//            entity.addPart("tags", new StringBody(mCreatedEvent.getTags()));
//
//            totalSize = entity.getContentLength();
//            httppost.setEntity(entity);
//
//            Log.i("CREATE:do", "httppost: " + entity.toString());
//
//            // Making server call
//            HttpResponse response = httpclient.execute(httppost);
//            HttpEntity r_entity = response.getEntity();
//
//            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode == 200) {
//                // Server response
//                responseString = EntityUtils.toString(r_entity);
//            } else {
//                responseString = "Error occurred! Http Status Code: " + statusCode;
//            }
//
//        } catch (ClientProtocolException e) {
//            responseString = e.toString();
//        } catch (IOException e) {
//            responseString = e.toString();
//        }
//
//        return responseString;
//    }
}
