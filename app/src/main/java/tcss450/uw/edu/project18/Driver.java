package tcss450.uw.edu.project18;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Mindy on 4/20/2016.
 */
public class Driver {

    AppCompatActivity current;
    Profile user;

    public Driver (AppCompatActivity current) {
        //TODO connect to the network
        this.current = current;
    }

    public void setCurrent(AppCompatActivity current) {
        this.current = current;
    }

    public void handleFragmentSwitch(int content, Fragment next) {
        FragmentManager sfm = current.getSupportFragmentManager();
        sfm.beginTransaction().replace(content, next).commit();
    }

    public void handleActivitySwitch(int content, Class next) {
        Intent intent = new Intent(current, next);
    }

    public void openDatePicker(View view) {
        //TODO this is a fragment(?) that should be layed over
        //the current activity
    }

    public void onUpdateUser(View view) {
        //TODO what happens when the user registers or updates
        //profile information
    }

}
