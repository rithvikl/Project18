package tcss450.uw.edu.project18;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;

/**
 * Created by rithvikl on 5/28/16.
 */
public class ConfirmDeleteDialogFragment extends DialogFragment {

    public ConfirmDeleteDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity main = (MainActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setMessage("Are you sure you want to delete this event?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //go back and logout
                main.deleteConfirmed(true);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                main.deleteConfirmed(false);
            }
        });
        return builder.create();
    }
}
