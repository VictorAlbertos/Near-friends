package activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import services.GPSService;
import utilities.NearFriendsApp;

@EActivity
public class BaseActionBarActivity extends ActionBarActivity {
    @App protected NearFriendsApp app;
    @Bean public GPSService gps;

    @Override public void onCreate(Bundle savedInstanceState) {
        gps.subscribe(this);
        super.onCreate(savedInstanceState);

        if (!gps.isEnabled(this) && !(this instanceof ActivateGPSMessageActivity)) {
            ActivateGPSMessageActivity_.intent(this)
                    .flags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .start();
            finish();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        app.setCurrentActivity(this);
    }

    @Override protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override protected void onDestroy() {
        clearReferences();
        super.onDestroy();
        gps.unsubscribe();
    }

    private void clearReferences(){
        Activity currActivity = app.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this)) app.setCurrentActivity(null);
    }
}
