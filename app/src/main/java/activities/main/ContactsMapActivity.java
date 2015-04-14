package activities.main;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hacer_app.near_friends.R;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;

import java.util.ArrayList;
import java.util.List;

import activities.BaseActionBarActivity;
import models.Contact;
import services.Contacts;
import services.GPSService;
import utilities.PicassoMarker;

@EActivity(R.layout.contacts_map_fragment)
public class ContactsMapActivity extends BaseActionBarActivity implements OnMapReadyCallback {
    @FragmentById protected MapFragment map_fragment;
    @ViewById protected ProgressWheel progress_wheel;
    @Bean protected Contacts contactsService;
    private Contact currentSelectedContact;
    private GoogleMap googleMap;

    @AfterViews protected void init() {
        if (!gps.isEnabled(this)) return;

        currentSelectedContact = contactsService.getCurrentSelectedContact();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        map_fragment.getMapAsync(this);
    }

    @Override public void onMapReady(GoogleMap aGoogleMap) {
        googleMap = aGoogleMap;

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        gps.requestLocation(this, new GPSService.GPSCallback() {
            @Override
            public void onLastLocationKnown(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (currentSelectedContact != null) latLng = getMidPointBetweenUserLoggedAndCurrentUser(location);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });

        populateMarkers();
        rePopulateMarkers();
    }

    private LatLng getMidPointBetweenUserLoggedAndCurrentUser(Location location) {
        double lat1 = location.getLatitude();
        double long1 = location.getLongitude();

        double lat2 = currentSelectedContact.getLocation().getLatitude();
        double long2 = currentSelectedContact.getLocation().getLongitude();

        return new LatLng((lat1+lat2)/2, (long1+long2)/2);
    }

    private List<PicassoMarker> targets = new ArrayList<>();
    private void populateMarkers() {
        progress_wheel.setVisibility(View.VISIBLE);

        contactsService.getAsyncRegistered(new Contacts.ContactsCallback() {
            @Override
            public void onContactsRetrieved(List<Contact> contacts) {
                targets.clear();
                if (currentSelectedContact != null) {
                    currentSelectedContact = contactsService.updateCurrentSelectedContact();
                    addCustomMarker(currentSelectedContact);
                    progress_wheel.setVisibility(View.INVISIBLE);
                    return;
                }
                for (Contact contact : contacts) {
                    addCustomMarker(contact);
                }
                progress_wheel.setVisibility(View.INVISIBLE);
            }
        });
    }

    @DimensionPixelOffsetRes(R.dimen._50dp) protected int sizeMarker;
    private void addCustomMarker(Contact contact) {
        if (contact == null) return;

        double latitude = contact.getLocation().getLatitude();
        double longitude = contact.getLocation().getLongitude();
        String title = contact.getUserName();
        String distanceFormatted = contact.getDistanceFormatted();

        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude))
                .title(title).snippet(distanceFormatted);

        Marker marker = googleMap.addMarker(markerOptions);
        View root = View.inflate(this, R.layout.item_contact_map, null);
        ((TextView)root.findViewById(R.id.tv_initial_name)).setText(contact.getFirstLetterUserName());
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(root)));

        PicassoMarker target = new PicassoMarker(this, marker);
        targets.add(target);
        Picasso.with(this).load(contact.getURLAvatar()).resize(sizeMarker, sizeMarker)
                .centerCrop().into(target);
    }

    public Bitmap createDrawableFromView(View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    @UiThread(delay = 15000) protected void rePopulateMarkers() {
        googleMap.clear();
        populateMarkers();
        rePopulateMarkers();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
