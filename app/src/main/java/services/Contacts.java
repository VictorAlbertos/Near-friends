package services;

import android.database.Cursor;
import android.location.Location;
import android.provider.ContactsContract;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Contact;
import utilities.NearFriendsApp;

@EBean(scope = EBean.Scope.Singleton)
public class Contacts {
    @App protected NearFriendsApp app;
    @Bean protected GPSService gps;

    private List<Contact> contacts;
    @Background public void getAsyncAll(boolean updated, final ContactsCallback contactsCallback) {
        if(contacts != null && !updated) {
            returnContactsOnUIThread(contactsCallback);
            return;
        }

        contacts = new ArrayList<>();

        Cursor phones = app.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(new Contact(name, phoneNumber));
        }
        phones.close();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("phoneNumber", getAllPhoneNumbersContacts());
        try {
            List<ParseUser> users = query.find();

            for (ParseUser user : users) {
                String phoneNumber = user.getString("phoneNumber");
                for (Contact contact : contacts) {
                    if (contact.getPhone().equals(phoneNumber))
                        contact.setParseUser(user);
                }
            }
            setDistanceFromUserForEveryContact(contactsCallback);

        } catch (ParseException e) {
            e.printStackTrace();
            contactsCallback.onContactsRetrieved(null);
        }
    }

    private void setDistanceFromUserForEveryContact(final ContactsCallback contactsCallback) {
        gps.requestLocation(app, new GPSService.GPSCallback() {
            @Override
            public void onLastLocationKnown(Location location) {
                for (Contact contact : contacts) {
                    if (contact.getLocation() == null) continue;
                    float distanceInMeters = location.distanceTo(contact.getLocation());
                    contact.setDistanceInMetersFromCurrentUser(distanceInMeters);
                }
                Collections.sort(contacts, new Contact());
                returnContactsOnUIThread(contactsCallback);
            }
        });
    }

    @Background public void getAsyncRegistered(final ContactsCallback contactsCallback) {
        getAsyncAll(true, new ContactsCallback() {
            @Override
            public void onContactsRetrieved(List<Contact> contacts) {
                List<Contact> registered = new ArrayList<Contact>();
                for (Contact contact : contacts) {
                    if (contact.isRegistered()) registered.add(contact);
                }
                contactsCallback.onContactsRetrieved(registered);
            }
        });
    }

    @UiThread protected void returnContactsOnUIThread(ContactsCallback contactsCallback) {
        contactsCallback.onContactsRetrieved(contacts);
    }

    private List<String> getAllPhoneNumbersContacts() {
        List<String> phones = new ArrayList(contacts.size());

        for (Contact contact : contacts) {
            phones.add(contact.getPhone());
        }

        return phones;
    }

    private Contact currentSelectedContact;
    public Contact getCurrentSelectedContact() {return currentSelectedContact;}
    public void setCurrentSelectedContact(Contact currentSelectedContact) {
        this.currentSelectedContact = currentSelectedContact;
    }

    public Contact updateCurrentSelectedContact() {
        if(currentSelectedContact == null) return null;

        for (Contact contact : contacts) {
            if (contact.getPhone().equals(currentSelectedContact.getPhone())) {
                currentSelectedContact = contact;
            }
        }

        return currentSelectedContact;
    }

    public interface ContactsCallback {
        void onContactsRetrieved(List<Contact> contacts);
    }
}
