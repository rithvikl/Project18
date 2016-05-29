package tcss450.uw.edu.project18;

import android.os.AsyncTask;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rithvikl on 5/28/16.
 */
public class UploadPhotoTask extends AsyncTask<String, Void, String> {

    private Cloudinary mCloudinary;

    EditEventFragment.OnEditEventInteractionListener mListener;

    public UploadPhotoTask(EditEventFragment.OnEditEventInteractionListener listener) {
        Map config = new HashMap();
        config.put("cloud_name", "gathercloud");
        config.put("api_key", "341693643768687");
        config.put("api_secret", "CKl9isOYrdDwAM1r9_QVJFjtPZU");
        mCloudinary = new Cloudinary(config);

        mListener = listener;
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
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("UPLOAD:post", "Response from server: " + result);
        mListener.uploadImageCallback(true, "Successfully created event.");
    }
}
