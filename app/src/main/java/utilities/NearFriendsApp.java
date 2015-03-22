package utilities;

import android.app.Application;

import com.hacer_app.near_friends.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;

import java.util.List;

import handlers.HandlerUser;
import models.User;

@EApplication
public class NearFriendsApp extends Application {

    @Bean protected System mSystem;
    @Bean protected HandlerUser mHandlerUser;

    @Override public void onCreate() {
        super.onCreate();
        Parse.initialize(this, getString(R.string.applicationId), getString(R.string.clientKey));
        ParseObject.registerSubclass(User.class);

        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ParseQuery<User> users = ParseQuery.getQuery(User.class);
                users.whereEqualTo("phone", mSystem.getPhoneNumber());
                users.findInBackground(new FindCallback<User>() {
                    public void done(List<User> users, ParseException exception) {
                        if (users.isEmpty()) createUser();
                        else mHandlerUser.setHim(users.get(0));
                    }
                });
            }
        });
    }

    private void createUser() {
        final User user = new User();
        user.setPhone(mSystem.getPhoneNumber());
        user.setName(mSystem.getPhoneUsername());
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mHandlerUser.setHim(user);
            }
        });
    }

}
