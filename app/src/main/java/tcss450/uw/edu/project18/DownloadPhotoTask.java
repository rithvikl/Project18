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
 */
public class DownloadPhotoTask extends AsyncTask<String, Void, Bitmap>{

    private ImageView mImageView;

    private ProgressDialog mProgressDialog;

    public DownloadPhotoTask(ImageView imageView, ProgressDialog progressDialog) {
        mImageView = imageView;
        mProgressDialog = progressDialog;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        HttpURLConnection urlConnection = null;
        Bitmap bitmap = null;
        try {
//            mImageHolderFragment = urls[1];

            Log.i("DOWNLOADPHOTO", "DownloadingPhoto " + urls[0]);
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
//        try {
//            if (mImageHolderFragment.equals("view")) {
//                imageView = (ImageView) activity.findViewById(R.id.event_item_photo);
//            } else {
//                imageView = (ImageView) activity.findViewById(R.id.event_item_photo_edit);
//            }

//            try {
//                ExifInterface exif = new ExifInterface();
//                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

//                if (orientation == 6) {
//                    Matrix matrix = new Matrix();
//                    matrix.postRotate(EditEventFragment.ROTATE_90);
//                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    mImageView.setImageBitmap(bitmap);
                    mProgressDialog.dismiss();
//                } else {
//                    mEventImageView.setImageBitmap(myBitmap);
//                }
//            } catch (IOException e) {
//                Log.e("CREATE", "Unable to find file: " + e.getMessage());
//            }
//            Log.i("DOWNLOADPHOTO:post", mImageHolderFragment);
//        } catch (Exception e) {
//            Log.e("DOWNLOADPHOTO:post", "Failed to Download: " + e);

//            Toast.makeText(mWeakActivity.get(), "Unable to download photo. Reason: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
    }
}
