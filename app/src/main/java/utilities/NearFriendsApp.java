package utilities;

import android.app.Application;

import com.hacer_app.near_friends.R;
import com.parse.Parse;

import org.androidannotations.annotations.EApplication;

import activities.BaseActionBarActivity;

@EApplication
public class NearFriendsApp extends Application {
    @Override public void onCreate() {
        super.onCreate();
        Parse.initialize(this, getString(R.string.applicationId), getString(R.string.clientKey));
    }

    private BaseActionBarActivity currentBaseActionBarActivity;

    public BaseActionBarActivity getCurrentActivity(){return currentBaseActionBarActivity;}

    public void setCurrentActivity(BaseActionBarActivity currentBaseActionBarActivity){
        this.currentBaseActionBarActivity = currentBaseActionBarActivity;
    }

}
