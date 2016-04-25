package tcss450.uw.edu.project18;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    public static final String PROFILE_ITEM = "profile";
    private TextView profileEmail;

    public EditProfileFragment() {
        // Required empty public constructor
    }
    public void updateProfile(Profile profile) {
        if (profile != null) {
            profileEmail.setText(profile.getUsername());
        }
    }

    private boolean validate() {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        profileEmail = (TextView) view.findViewById(R.id.profile_email);

        Button btn = (Button) view.findViewById(R.id.profile_submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            updateProfile((Profile) args.getSerializable(PROFILE_ITEM));
        }
    }

}
