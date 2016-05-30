package tcss450.uw.edu.project18;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.cloudinary.Cloudinary;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rithvikl on 5/29/16.
 */
public class DownloadPhotoUrlTask extends AsyncTask<Void, Void, String>{

    private Cloudinary mCloudinary;

    private ImageView mImageView;

    private String mPhotoFileName;

    private ProgressDialog mProgressDialog;

    public DownloadPhotoUrlTask(ImageView imageView, String photoFileName, Context context) {
        Map config = new HashMap();
        config.put("cloud_name", "gathercloud");
        config.put("api_key", "341693643768687");
        config.put("api_secret", "CKl9isOYrdDwAM1r9_QVJFjtPZU");
        mCloudinary = new Cloudinary(config);
        mImageView = imageView;
        mPhotoFileName = photoFileName;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("Loading Event");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {

        String imageUrl = mCloudinary.url().generate(mPhotoFileName + ".jpg");
        StringBuilder imageUrlRotate = new StringBuilder(imageUrl);
        imageUrlRotate.insert(51, "a_auto_left,r_40/");

        return imageUrlRotate.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("DOWNLOAD", "Result: " + result);

        DownloadPhotoTask dpt = new DownloadPhotoTask(mImageView, mProgressDialog);
        dpt.execute(result);
    }
}
