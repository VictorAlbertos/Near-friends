package services;

import android.location.Location;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

@EBean
public class UserService {

    public void singIn(String userName, String password, final Callback callback) {
        ParseUser.logInInBackground(userName, password, new LogInCallback() {
            public void done(ParseUser user, ParseException exception) {
                String failureMessage = exception != null ? exception.getMessage() : "";
                callback.onResponse(failureMessage);
            }
        });
    }

    public void singUp(String email, String userName, String phoneNumber, String password, final Callback callback) {
        ParseUser user = new ParseUser();
        user.setEmail(email);
        user.setUsername(userName);
        user.put("phoneNumber", preventPotentialMess(phoneNumber));
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException exception) {
                String failureMessage = exception != null ? exception.getMessage() : "";
                callback.onResponse(failureMessage);
            }
        });
    }

    private String preventPotentialMess(String phoneNumber) {
        return phoneNumber.replace("+34", "").replaceAll("\\s+","");
    }

    public boolean isSingIn() {
        return ParseUser.getCurrentUser() != null;
    }

    public void logOut() {
        ParseUser.logOut();
    }

    public void updateLocation(Location location) {
        ParseUser user = ParseUser.getCurrentUser();
        if(user == null) return;
        user.put("lastDateUpdate", new Date());
        user.put("lastKnownPosition", new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
        user.saveInBackground();
    }

    public String getUrlAvatar() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseFile parseFile = user.getParseFile("avatar");
        if (parseFile != null) return parseFile.getUrl();
        else return "";
    }

    public String getName() {
        ParseUser user = ParseUser.getCurrentUser();
        return user.getUsername();
    }

    public String getEmail() {
        ParseUser user = ParseUser.getCurrentUser();
        return user.getEmail();
    }

    public String getPhoneNumber() {
        ParseUser user = ParseUser.getCurrentUser();
        return user.getString("phoneNumber");
    }

    @Background public void saveImageInBackground(File file) {
        try {
            InputStream input = FileUtils.openInputStream(file);
            byte[] bytes = IOUtils.toByteArray(input);
            ParseUser user = ParseUser.getCurrentUser();
            ParseFile parseFile = new ParseFile(file.getName(), bytes);
            parseFile.save();
            user.put("avatar", parseFile);
            user.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Callback {
        void onResponse(String failureMessage);
    }
}
