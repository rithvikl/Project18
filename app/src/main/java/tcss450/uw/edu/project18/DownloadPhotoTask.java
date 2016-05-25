package tcss450.uw.edu.project18;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rithvikl on 5/23/16.
 */
public class DownloadPhotoTask extends AsyncTask<String, Void, Bitmap>{

    private String mImageHolderFragment;

    WeakReference<Activity> mWeakActivity;

    public DownloadPhotoTask(Activity activity) {
        mWeakActivity = new WeakReference<Activity>(activity);
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        HttpURLConnection urlConnection = null;
        Bitmap bitmap = null;
        try {
            mImageHolderFragment = urls[1];

            Log.i("DEBUG", "DownloadingPhoto " + urls[0]);
            URL urlObject = new URL(urls[0]);
            urlConnection = (HttpURLConnection) urlObject.openConnection();

            InputStream is = new BufferedInputStream(urlObject.openStream());
            bitmap = BitmapFactory.decodeStream(is);

        } catch (Exception e) {
            String response = "Unable to download the image, Reason: "
                    + e.getMessage();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return bitmap;
    }


    /**
     * It checks to see if there was a problem with the URL(Network) which is when an
     * exception is caught. It tries to call the parse Method and checks to see if it was successful.
     * If not, it displays the exception.
     *
     */
    @Override
    protected void onPostExecute(Bitmap bitmap) {

        // Something wrong with the network or the URL.
        try {
            Activity activity = mWeakActivity.get();
            if (activity != null) {
                ImageView imageView = null;
                if (mImageHolderFragment == "view") {
                    imageView = (ImageView) activity.findViewById(R.id.event_item_photo);
                } else {
                    imageView = (ImageView) activity.findViewById(R.id.event_item_photo_edit);
                }
                imageView.setImageBitmap(bitmap);
                Log.i("DEBUG", mImageHolderFragment);
            }
        } catch (Exception e) {
            Log.e("DEBUG", e.getMessage());
            Toast.makeText(mWeakActivity.get(), "Unable to download photo. Reason: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
