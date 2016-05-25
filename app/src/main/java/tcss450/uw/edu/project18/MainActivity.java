package tcss450.uw.edu.project18;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        EditProfileFragment.EditProfileListener{

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int REQUEST_TAKE_PHOTO = 1;

    String mPhotoPath;
    
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

    //hiding the toolbar and fab
    //https://mzgreen.github.io/2015/06/23/How-to-hideshow-Toolbar-when-list-is-scrolling%28part3%29/

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

        // Navigate to event list fragment
        if (savedInstanceState == null || getSupportFragmentManager().findFragmentById(R.id.list) == null) {
            Log.i("MAIN_ACTIVITY", "On Create");
            EventListFragment eventListFragment = new EventListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_container, eventListFragment).commit();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        // the camera should add an event
        if (id == R.id.menu_camera) {
            this.takePicture();
            return true;
        } else if (id == R.id.menu_logout) {
            mShared.edit().putBoolean(getString(R.string.LOGGEDIN), false).commit();
            mShared.edit().clear();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * do things in the buttons here
     * @param item the navigation item selected
     * @return true
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
        } else if (id == R.id.nav_share) {
            //share via social media
        } else if (id == R.id.nav_send) {
            //send via email
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Start the camera to take a picture
     */
    public void takePicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = saveImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Handle the photo taken with the camera
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
//                mEditEventFragment = new EditEventFragment();
//                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, mEditEventFragment).addToBackStack(null).commit();
            }
        }
    }

    private File saveImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    /**
     * This will open a new fragment that displays the event details
     * @param item
     */
    @Override
    public void onListFragmentInteraction(Event item) {
        mViewEventFragment = new ViewEventFragment();
        Bundle args = new Bundle();
        args.putSerializable(ViewEventFragment.EVENT_ITEM_SELECTED, item);
        mViewEventFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, mViewEventFragment)
                .addToBackStack(null)
                .commit();
    }

    public void editEvent(View view) {
        if (mViewEventFragment != null)
            mViewEventFragment.editEvent(view);
    }

//    public void editEvent(View view) {
//        Log.i("DEBUG", "edit - Main");
//        mViewEventFragment.editEvent(view);
//    }

    @Override
    public void onEditEventInteraction(String url) {
        EditEventTask eet = new EditEventTask(this);
        eet.execute(url);
    }

    public void editEventCallback(boolean result, String message) {
        if (Driver.DEBUG)
            Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_LONG).show();
        if (result) {
            getSupportFragmentManager().popBackStackImmediate();
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
}