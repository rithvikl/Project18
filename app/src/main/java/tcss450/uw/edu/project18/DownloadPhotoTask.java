package tcss450.uw.edu.project18;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rithvikl on 5/23/16.
 *
 * Downloads a photograph for an event from Cloudinary.
 */
public class DownloadPhotoTask extends AsyncTask<String, Void, Bitmap>{

    /**
     * The image view in which to put the photograph.
     */
    private ImageView mImageView;

    /**
     * A progress dialog to indicate the progress of the task.
     */
    private ProgressDialog mProgressDialog;

    /**
     * Constructor.
     * @param imageView is the image view for the photograph.
     * @param progressDialog is a dialog to indicate task progress.
     */
    public DownloadPhotoTask(ImageView imageView, ProgressDialog progressDialog) {
        mImageView = imageView;
        mProgressDialog = progressDialog;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        HttpURLConnection urlConnection = null;
        Bitmap bitmap = null;
        try {

            Log.i("DOWNLOADPHOTO", "DownloadingPhoto " + urls[0]);
            URL urlObject = new URL(urls[0]);
            urlConnection = (HttpURLConnection) urlObject.openConnection();

            InputStream is = new BufferedInputStream(urlObject.openStream());
            bitmap = BitmapFactory.decodeStream(is);

        } catch (Exception e) {
            if (Driver.DEBUG) Log.i("DownloadPhoto", "Unable to download the image, Reason: "
                    + e.getMessage());
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
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
            mProgressDialog.dismiss();
        } else {
            mProgressDialog.dismiss();
        }
    }
}
