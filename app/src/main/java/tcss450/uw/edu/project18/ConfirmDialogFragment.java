package tcss450.uw.edu.project18;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Melinda Robertson on 5/26/2016.
 */
public class ConfirmDialogFragment extends DialogFragment {

    public final static String CONFIRM_MESSAGE = "confirm_message";
    public final static String CONFIRM_LISTEN = "confirm_listen";

    private String message;
    private onConfirmInteraction listen;

    public ConfirmDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle args = getArguments();
        if(args != null) {
            message = args.getString(CONFIRM_MESSAGE);
            listen = (onConfirmInteraction) args.getSerializable(CONFIRM_LISTEN);
        } else
            message = "Confirm action?";
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //go back and logout
                listen.onConfirm(true);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listen.onConfirm(false);
            }
        });
        return builder.create();
    }

    interface onConfirmInteraction {
        public void onConfirm(boolean confirm);
    }
}
