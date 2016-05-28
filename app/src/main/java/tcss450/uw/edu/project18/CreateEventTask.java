package tcss450.uw.edu.project18;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import tcss450.uw.edu.project18.event.Event;

/**
 * Created by rithvikl on 5/27/16.
 */
public class CreateEventTask extends AsyncTask<Void, Integer, String> {

    long totalSize = 0;

    EditEventFragment.OnEditEventInteractionListener mListener;

    ProgressBar mProgressBar;

    String mPhotoPath;

    Event mCreatedEvent;

    SharedPreferences mShared;

    public CreateEventTask(EditEventFragment.OnEditEventInteractionListener listener,
                           ProgressBar progressBar, String photoPath, Event createdEvent, SharedPreferences shared) {
        super();
        this.mListener = listener;
        this.mProgressBar = progressBar;
        this.mPhotoPath = photoPath;
        this.mCreatedEvent = createdEvent;
        this.mShared = shared;
    }

    @Override
    protected void onPreExecute() {
        // setting progress bar to zero
        mProgressBar.setProgress(0);
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        // Making progress bar visible
        mProgressBar.setVisibility(View.VISIBLE);

        // updating progress bar value
        mProgressBar.setProgress(progress[0]);

        Log.i("Create:progress", "still going..." + progress[0]);

    }

    @Override
    @SuppressWarnings("deprecation")
    protected String doInBackground(Void... params) {
        String responseString = null;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(FileUploadConfig.FILE_UPLOAD_URL);

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            Log.i("CREATE:do", "File Path: " + mPhotoPath);
            File sourceFile = new File(mPhotoPath);

            // Adding file data to http body
            entity.addPart("file", new FileBody(sourceFile));

            String email = mShared.getString("email", null);

            // Extra parameters if you want to pass to server
            entity.addPart("email", new StringBody(email));
            entity.addPart("title", new StringBody(mCreatedEvent.getTitle()));
            entity.addPart("date", new StringBody(mCreatedEvent.getDate()));
            entity.addPart("comment", new StringBody(mCreatedEvent.getComment()));
            entity.addPart("tags", new StringBody(mCreatedEvent.getTags()));

            totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            Log.i("CREATE:do", "httppost: " + entity.toString());

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: " + statusCode;
            }

        } catch (ClientProtocolException e) {
            responseString = e.toString();
        } catch (IOException e) {
            responseString = e.toString();
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("CREATE:post", "Response from server: " + result);
        super.onPostExecute(result);

        try {
            JSONObject jo = new JSONObject(result);
            String status = (String) jo.get("result");
            if (status.equals("success"))
                mListener.createEventCallback(true, "Successfully created event.", mCreatedEvent);
            else mListener.createEventCallback(false, "There was an error creating your event.", null);
        } catch (JSONException e) {
            Log.e("CreateEventTask:post", "Could not parse JSON: " + e.getMessage());
            mListener.createEventCallback(false, "There was a format error in your data.", null);
        }
    }
}
