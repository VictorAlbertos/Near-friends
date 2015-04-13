package activities;

import android.content.Intent;

import com.hacer_app.near_friends.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import activities.credentials.CredentialsActionBarActivity_;

@EActivity(R.layout.activate_gps_message_activity)
public class ActivateGPSMessageActivity extends BaseActionBarActivity {

    @Override protected void onResume() {
        super.onResume();
        if(gps.isEnabled(this))
            CredentialsActionBarActivity_.intent(this).start();
    }

    @Click protected void bt_go_to_GPS() {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }
}
