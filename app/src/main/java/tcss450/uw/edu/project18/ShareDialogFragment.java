package tcss450.uw.edu.project18;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import tcss450.uw.edu.project18.event.Event;


/**
 * A simple {@link DialogFragment} subclass.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ShareDialogFragment extends DialogFragment {

    //TODO there is a way to have icons but that may just be too much
    public static final String[] SHARE_LIST = {"Email", "Text"};
    public static final String SHARE_VIEW_FRAGMENT = "shareViewFragment";

    public static final boolean LIST = false;

    ViewEventFragment fragment;
    Event mEvent;
    Bitmap mImage;

    public ShareDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            fragment = (ViewEventFragment) args.getSerializable(SHARE_VIEW_FRAGMENT);
            if (fragment != null) {
                mImage = fragment.getImage();
                mEvent = fragment.getEvent();
            }
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (LIST) return createList();
        else {
            sendEmail();
            return new AlertDialog.Builder(getActivity()).create();
        }
    }

    public Dialog createList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (mEvent != null) {
            builder.setTitle("Share...");
            builder.setItems(SHARE_LIST, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO need to open the service
                    switch (which) {
                        case 0:

                            break;
                        case 1:
                            break;
                        default:
                    }
                }
            });
        } else {
            builder.setMessage("Sorry, something went wrong.");
        }
        return builder.create();
    }

    public void sendEmail() {
        try {
            File file = saveToTemp();
            if (file == null) throw new Exception("File is null.");
            /*if (file.getFreeSpace() < (long)(file.getTotalSpace()*.9)) {
                throw new Exception("Not enough room.");
            }*/
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_STREAM, file);
            email.setType("image/jpeg");
            getActivity().startActivity(Intent.createChooser(email,"Use..."));
        } catch (Exception e) {
            Log.i("Share:email",e.getMessage());
        }
    }

    public boolean isExternWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) return true;
        return false;
    }

    public File saveToTemp() throws IOException{
        if(!isExternWritable()) throw new IOException();
        File file = new File(getActivity()
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp");
        if (!file.createNewFile()) {
            Log.i("Share:file", "File not created.");
        }
        //save image to file
        if (mImage != null) {
            OutputStream fout = new FileOutputStream(file);
            mImage.compress(Bitmap.CompressFormat.JPEG, 85, fout);
            fout.flush();
            fout.close();
        }
        return file;
    }
}
