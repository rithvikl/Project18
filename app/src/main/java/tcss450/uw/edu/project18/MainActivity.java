package tcss450.uw.edu.project18;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tcss450.uw.edu.project18.event.Event;


/**
 * The main activity that has a drawer navigation pane for settings and
 * searching for events. This activity also creates the fragments for the
 * list of events.
 * @author Melinda Robertson, Rithvik Lagisetti
 * @version 20160430
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        EventListFragment.OnListFragmentInteractionListener,
        EditEventFragment.OnEditEventInteractionListener,
        EditProfileFragment.EditProfileListener,
        ViewEventFragment.OnDeleteEventInteractionListener,
        ConfirmDialogFragment.onConfirmInteraction,
        Serializable {

    /**
     * Number for requesting a picture taken.
     */
    private static final int REQUEST_TAKE_PHOTO = 100;

    /**
     * File path for a picture.
     */
    String mPhotoFilePath;

    /**
     * File name of a picture.
     */
    String mPhotoFileName;

    /**
     * Holds information about the current user's session.
     */
    private SharedPreferences mShared;

    /**
     * Instance of viewEventFragment
     */
    private ViewEventFragment mViewEventFragment;

    /**
     * Instance of createEventFragment
     */
    private EditEventFragment mEditEventFragment;

    /**
     * Shows a list of events. This is the fragment the main activity starts with.
     */
    private EventListFragment mEventListFragment;

    /**
     * A newly created event.
     */
    private Event mCreatedEvent;

    /**
     * An event to delete.
     */
    private Event mDeletedEvent;

    /**
     * The url for deleting an event.
     */
    private String mDeleteUrl;

    /**
     * The search bar to filter the list.
     */
    private SearchView mSearchView;

    /**
     * Search menu button that opens the search view.
     */
    public MenuItem mSearchMenu;

    /**
     * Shows the progress of tasks.
     */
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mShared = getSharedPreferences(getString(R.string.LOGIN_PREFS),
                Context.MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Driver.DEBUG)
            Log.i("Main:create", "Toolbar" + toolbar.getMenu().size());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView user = (TextView) navigationView.getHeaderView(0)
                .findViewById(R.id.nav_header_username);
        String name = mShared.getString(getString(R.string.USER), "Welcome!");
        user.setText("Hello " + name);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Please wait...");

        // Navigate to event list fragment
        if (savedInstanceState == null || getSupportFragmentManager().findFragmentById(R.id.list) == null) {
            Log.i("MAIN_ACTIVITY", "On Create");
            mEventListFragment = new EventListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_container, mEventListFragment, "EVENT_LIST_FRAG").commit();
            getFragmentManager().executePendingTransactions();
        }
    }

    /**
     * Navigate back to previous screen
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu the menu of the activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        mSearchMenu = menu.findItem(R.id.action_search);
//            item.setVisible(true);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenu);
        mSearchView.setOnQueryTextListener(mEventListFragment.mQueryTextListener);

        // Get the search close button image view
        ImageView closeButton = (ImageView) mSearchView.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d("FILTER", "Search close button clicked");

                //Clear query
                mSearchView.setQuery("", false);
                //Collapse the action view
                mSearchView.onActionViewCollapsed();
                //Collapse the search widget
                mSearchMenu.collapseActionView();

                mEventListFragment.onQueryTextSubmit("");
            }
        });

        return true;
    }

    /**
     * Reacts to click of camera and logout buttons
     * in action bar
     * @param item the button that was clicked
     * @return true if item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // the camera should add an event
        if (id == R.id.menu_camera) {
            this.takePicture();
            return true;
        } else if (id == R.id.menu_logout) {
            DialogFragment fragment = new ConfirmDialogFragment();
            Bundle args = new Bundle();
            args.putString(ConfirmDialogFragment.CONFIRM_MESSAGE, "Logout?");
            args.putSerializable(ConfirmDialogFragment.CONFIRM_LISTEN, this);
            fragment.setArguments(args);
            fragment.show(getFragmentManager(), "onOptionsItemSelected");
            return true;
        } else if (id == R.id.action_search) {
            Log.i("FILTER", "Search selected");
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks to see if the user has confirmed logging out then executes
     * logout if true.
     * @param confirm is whether or not to logout the user.
     */
    public void logout(boolean confirm) {
        if (confirm) {
            mShared.edit().clear();
            mShared.edit().putBoolean(getString(R.string.LOGGEDIN), false).commit();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onConfirm(boolean confirm) {
        if (confirm) logout(true);
    }

    /**
     * Determines what the navigation drawer items do.
     * @param item the navigation item selected.
     * @return true no matter what.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            takePicture();
        }  else if (id == R.id.nav_manage) {
            //open profile editor
            EditProfileFragment epf = new EditProfileFragment();
            Bundle args = new Bundle(); //these are useless
            epf.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, epf)
                    .addToBackStack(null)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Checking device has camera hardware or not
     * @return true if the device has a camera, false otherwise.
     */
    private boolean supportsCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Start the camera to take a picture.
     */
    public void takePicture() {
        if (supportsCamera()) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = saveImageFile();
                    Log.i("PHOTOFILE", mPhotoFilePath);

                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e("PHOTOFILE", "FAILED TO MAKE PHOTO PATH");
                }

                if (photoFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Sorry! your device does not support camera", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handle the photo taken with the camera.
     * @param requestCode is the code for requesting a photograph to be taken.
     * @param resultCode is the result of the request.
     * @param data is the returned intent.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Calendar curDate = Calendar.getInstance();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
            String formattedDate = dateFormatter.format(curDate.getTime());

            // Create new event with current date
            Event createdEvent = new Event("-1", "", "", formattedDate, "", mPhotoFileName);
            Log.i("CREATE", "Created Event: " + createdEvent.toString());

            mEditEventFragment = new EditEventFragment();
            Bundle args = new Bundle();
            args.putString(EditEventFragment.PHOTO_FILE_PATH, mPhotoFilePath);
            args.putSerializable(ViewEventFragment.EVENT_ITEM_SELECTED, createdEvent);
            mEditEventFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, mEditEventFragment).addToBackStack(null).commit();
        } else {
            Log.e("CREATE", "failed to deliver image");
            Log.i("CREATE", "Result Code: " + resultCode);
            Log.i("CREATE", "Request Code: " + requestCode);
        }
    }

    /**
     * Saves an image taken with the device's camera to a public directory.
     * @return the File that the image was saved to.
     * @throws IOException if the file could not be created.
     */
    private File saveImageFile() throws IOException {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GatherFileUpload");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("CREATE", "Oops! Failed create GatherFileUpload directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        File image = new File(mediaStorageDir.getPath() + File.separator + mPhotoFileName);

        // Save a file: path for use with ACTION_VIEW intents
        mPhotoFileName = "IMG_" + timeStamp + ".jpg";
        mPhotoFilePath = image.getAbsolutePath();
        return image;

    }

    /**
     * This will open a new fragment that displays the event details.
     * @param item is the Event that was selected.
     */
    @Override
    public void onListFragmentInteraction(Event item) {

        closeSearchMenu();
        Bundle args;
        if (mViewEventFragment == null) {
            mViewEventFragment = new ViewEventFragment();
            args = new Bundle();
            args.putSerializable(ViewEventFragment.EVENT_ITEM_SELECTED, item);
            mViewEventFragment.setArguments(args);
        } else {
            args = mViewEventFragment.getArguments();
            args.clear();
            args.putSerializable(ViewEventFragment.EVENT_ITEM_SELECTED, item);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mViewEventFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Safely closes the search menu.
     */
    public void closeSearchMenu() {
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchMenu);
        //Clear query
        searchView.setQuery("", false);
        //Collapse the action view
        searchView.onActionViewCollapsed();
        //Collapse the search widget
        mSearchMenu.collapseActionView();
    }

    @Override
    public void onCreateEventInteraction(Event createdEvent, String createURL) {
        Log.i("CREATE", "Create task started");
        mCreatedEvent = createdEvent;

        mProgressDialog.setTitle("Creating Event");
        mProgressDialog.show();
        CreateEventTask cet = new CreateEventTask(this);
        cet.execute(createURL);
    }

    @Override
    public void createEventCallback(boolean result, String message) {
        if (result) {
            UploadPhotoTask uit = new UploadPhotoTask(this);
            uit.execute(new String[]{mPhotoFilePath, mCreatedEvent.getPhotoFileName()});
        } else {
            mProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Failed to create event: " + message,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void uploadImageCallback(boolean status, String response) {

        // Insert event in Sqlite
        EventDB eventDB = mEventListFragment.getEventDB();
        eventDB.insertEvent(mCreatedEvent);

        mProgressDialog.dismiss();

        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onEditEventInteraction(String url, Event editedEvent) {
        mProgressDialog.setTitle("Updating Event");
        mProgressDialog.show();
        EditEventTask eet = new EditEventTask(this, editedEvent);
        eet.execute(url);
    }

    @Override
    public void editEventCallback(boolean result, String message, Event editedEvent) {
        mProgressDialog.dismiss();
        if (result) {
            // Update view fragment with new data
            mViewEventFragment.updateView(editedEvent);
            // Edit event in Sqlite
            EventDB eventDB = mEventListFragment.getEventDB();
            eventDB.editEvent(editedEvent);
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            Toast.makeText(getApplicationContext(), message,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void editProfile(String url) {
        EditProfileTask ept = new EditProfileTask(this);
        ept.execute(url);
    }

    @Override
    public void editProfileCallback(boolean success, String message) {
        if (Driver.DEBUG)
            Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_LONG).show();
        if (success)
            getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onDeleteEventInteraction(String url, Event event) {
        this.mDeletedEvent = event;
        this.mDeleteUrl = url;
        DeleteEventTask deleteEventTask = new DeleteEventTask(this, mDeletedEvent);
        deleteEventTask.execute(mDeleteUrl);
    }

    @Override
    public void deleteEventCallback(boolean result, String message, Event event) {
        if (result) {
            // Delete event from Sqlite
            EventDB eventDB = mEventListFragment.getEventDB();
            eventDB.deleteEvent(event);
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

}