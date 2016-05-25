package tcss450.uw.edu.project18;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;

import tcss450.uw.edu.project18.event.Event;


/**
 * A simple {@link DialogFragment} subclass.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ShareDialogFragment extends DialogFragment {

    //TODO there is a way to have icons but that may just be too much
    public static final String[] SHARE_LIST = {"Email", "Text"};

    Event mEvent;
    Image mImage;

    public ShareDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            mEvent = (Event) args.getSerializable(
                    ViewEventFragment.EVENT_ITEM_SELECTED);
            mImage = (Image) args.getSerializable(
                    ViewEventFragment.EVENT_IMAGE_SELECTED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        onCreateDialog().show();
        return inflater.inflate(R.layout.fragment_share_dialog, container, false);
    }

    public Dialog onCreateDialog() {
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
            if (file == null) throw new Exception();
            if (file.getFreeSpace() < (long)(file.getTotalSpace()*.9)) {
                return;
            }
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_STREAM, file);
            email.setType("image/jpeg");
            getActivity().startActivity(Intent.createChooser(email,"Use..."));
        } catch (Exception e) {
            Log.i("Share:email","There was an error sending the email.");
        }
    }

    public boolean isExternWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) return true;
        return false;
    }

    public File saveToTemp() throws IOException{
        File file = new File(getActivity()
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp");
        if (!file.createNewFile()) {
            Log.i("Share:file", "File not created.");
        }

        return file;
    }
}
