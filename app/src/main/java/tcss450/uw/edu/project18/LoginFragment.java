package tcss450.uw.edu.project18;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        mEmailView = (AutoCompleteTextView) this.getActivity().findViewById(R.id.email);
        if(Driver.DEBUG) Log.i("LoginFragment", "EmailView:" + mEmailView.toString());
        return v;
    }

    public AutoCompleteTextView getmEmailView() {
        return mEmailView;
    }

    public EditText getmPasswordView() {
        return mPasswordView;
    }

    public View getmProgressView() {
        return mProgressView;
    }

    public View getmLoginFormView() {
        return mLoginFormView;
    }

}
