package services;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.location.LocationRequest;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscription;
import rx.functions.Action1;

@EBean
public class GPSService {
    @Bean UserService user;
    private Subscription subscription;
    private final static long elapseUpdate = 10000;

    public void subscribe(final Context context) {
        if((subscription != null && !subscription.isUnsubscribed()) || !isEnabled(context)) return;

        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1)
                .setInterval(elapseUpdate);

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
        subscription = locationProvider.getUpdatedLocation(request).subscribe(new Action1<Location>() {
            @Override
            public void call(Location location) {
                user.updateLocation(location);
                subscription.unsubscribe();
                subscribe(context);
            }
        });
    }

    public void unsubscribe() {
        if(subscription != null) subscription.unsubscribe();
    }

    public void requestLocation(Context context, final GPSCallback callback) {
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
        locationProvider.getLastKnownLocation().subscribe(new Action1<Location>() {
            @Override
            public void call(Location location) {
                callback.onLastLocationKnown(location);
            }
        });
    }

    public boolean isEnabled(Context context) {
        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);;
        return mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public interface GPSCallback {
        void onLastLocationKnown(Location location);
    }
}
