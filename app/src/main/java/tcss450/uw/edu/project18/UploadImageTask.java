package tcss450.uw.edu.project18;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import tcss450.uw.edu.project18.event.Event;

/**
 * Created by rithvikl on 5/28/16.
 */
public class UploadImageTask extends AsyncTask<String, Integer, String> {

    private Cloudinary mCloudinary;

    private Event mCreatedEvent;

    private String mPhotoFilePath;

    private ProgressBar mProgressBar;

    EditEventFragment.OnEditEventInteractionListener mListener;

    public UploadImageTask(String photoFilePath, ProgressBar progressBar, EditEventFragment.OnEditEventInteractionListener listener) {
        Map config = new HashMap();
        config.put("cloud_name", "gathercloud");
        config.put("api_key", "341693643768687");
        config.put("api_secret", "CKl9isOYrdDwAM1r9_QVJFjtPZU");
        mCloudinary = new Cloudinary(config);

        mPhotoFilePath = photoFilePath;
        mProgressBar = progressBar;
        mListener = listener;
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

        Log.i("UPLOAD:progress", "still going..." + progress[0]);

    }

    @Override
    protected String doInBackground(String... params) {

        try {
            String filePath = params[0];
            File initialFile = new File(filePath);
            InputStream photoStream = new FileInputStream(initialFile);

            String fileName = params[1];
            mCloudinary.uploader().upload(photoStream, ObjectUtils.asMap("public_id", fileName));
            Log.i("UPLOAD:", "Image uploading");
        } catch (IOException e) {
            Log.e("UPLOAD:error:", e.getMessage());
//            mListener.uploadImageCallback(false, "Failed to upload image.", mCreatedEvent);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("UPLOAD:post", "Response from server: " + result);
        mListener.uploadImageCallback(true, "Successfully created event.");
//        super.onPostExecute(result);

//        try {
//            JSONObject jo = new JSONObject(result);
//            String status = (String) jo.get("result");
//            if (status.equals("success"))
//                mListener.createEventCallback(true, "Successfully created event.", mCreatedEvent);
//            else mListener.createEventCallback(false, "There was an error creating your event.", null);
//        } catch (JSONException e) {
//            Log.e("CreateEventTask:post", "Could not parse JSON: " + e.getMessage());
//            mListener.createEventCallback(false, "There was a format error in your data.", null);
//        }
    }
}
