package models;

import android.location.Location;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class Contact implements Comparator<Contact> {
    private String userName;
    private String phone;
    private ParseUser parseUser;
    private float distanceInMetersFromCurrentUser = Float.MAX_VALUE;

    public Contact(){}

    public Contact(String userName, String phone) {
        this.userName = userName;
        this.phone = phone;
    }

    public boolean isRegistered() {
        return parseUser != null;
    }

    public ParseUser getParseUser() {
        return parseUser;
    }

    public void setParseUser(ParseUser parseUser) {
        this.parseUser = parseUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getPhone() {
        if (phone == null) return "";
        return preventPotentialMess(phone);
    }

    private String preventPotentialMess(String phoneNumber) {
        return phoneNumber.replace("+34", "").replaceAll("\\s+","");
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstLetterUserName() {
        return String.valueOf(userName.charAt(0)).toUpperCase();
    }

    public float getDistance() {
        return distanceInMetersFromCurrentUser;
    }

    public String getDistanceFormatted() {
        String distanceInKm = String.format("%.2f", distanceInMetersFromCurrentUser / 1000);
        return distanceInKm + " kms.";
    }

    public String getURLAvatar() {
        if (parseUser == null) return "http://postimg.org/image/p2zx0vpyl";
        ParseFile parseFile = parseUser.getParseFile("avatar");
        if(parseFile != null) return parseFile.getUrl();
        else return "http://postimg.org/image/p2zx0vpyl";
    }

    public void setDistanceInMetersFromCurrentUser(float distanceInMetersFromCurrentUser) {
        this.distanceInMetersFromCurrentUser = distanceInMetersFromCurrentUser;
    }

    public Location getLocation() {
        if(parseUser == null) return null;

        ParseGeoPoint geoPoint = parseUser.getParseGeoPoint("lastKnownPosition");
        if (geoPoint == null) return  null;

        Location location = new Location("");
        location.setLatitude(geoPoint.getLatitude());
        location.setLongitude(geoPoint.getLongitude());

        return location;
    }

    public String getLastUpdateFormatted() {
        if (parseUser == null) return "";

        PrettyTime prettyTime = new PrettyTime();
        prettyTime.setLocale(new Locale("en"));
        Date lastUpdate = parseUser.getDate("lastDateUpdate");
        return prettyTime.format(lastUpdate);
    }

    @Override public int compare(Contact c1, Contact c2) {
        return c1.getDistance() < c2.getDistance() ? -1
                : c1.getDistance() > c2.getDistance() ? 1
                : 0;
    }
}
