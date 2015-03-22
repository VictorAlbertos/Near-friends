package models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;


@ParseClassName("User")
public class User extends ParseObject {

    private String phone, email, name;
    private int minMetersToSendNotifications;
    private ParseGeoPoint lastKnownPosition;
    private ParseInstallation parseInstallation;
    private ParseFile avatar;

    public User() {}

    public ParseGeoPoint getLastKnownPosition() {
        return getParseGeoPoint("astKnownPosition");
    }

    public void setLastKnownPosition(ParseGeoPoint lastKnownPosition) {
        super.put("lastKnownPosition", lastKnownPosition);
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        super.put("name", name);    }

    public ParseFile getAvatar() {
        return getParseFile("avatar");
    }

    public void setAvatar(ParseFile avatar) {
        put("avatar", avatar);
    }

    public int getMinMetersToSendNotifications() {
        return getNumber("minMetersToSendNotifications").intValue();
    }

    public void setMinMetersToSendNotifications(int minMetersToSendNotifications) {
        put("minMetersToSendNotifications", minMetersToSendNotifications);
    }

    public String getPhone() {
        return getString("phone");
    }

    public void setPhone(String phone) {
        put("phone", phone);
    }

    public String getEmail() {
        return getString("email");
    }

    public void setEmail(String email) {
        put("phone", email);
    }

    public ParseInstallation getParseInstallation() {
        return getParseInstallation();
    }

    public void setParseInstallation(ParseInstallation parseInstallation) {
        put("installation", parseInstallation);
    }
}
