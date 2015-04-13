package fragments.customs;


import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.location.Location;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import activities.BaseActionBarActivity;
import services.GPSService;
import utilities.BlurEffect;

@EFragment
public class MyLocationBlurryMapFragment extends MapFragment implements OnMapReadyCallback  {
    @Bean protected BlurEffect blurEffect;
    private GPSService gpsService;
    private View v_target_blur;

    @AfterViews protected void init() {
        gpsService = ((BaseActionBarActivity)getActivity()).gps;
        getView().setAlpha(0);
        getMapAsync(this);
    }

    public void setTargetToBlur(View target) {
        v_target_blur = target;
        v_target_blur.setAlpha(0);
    }

    @Override public void onMapReady(final GoogleMap googleMap) {
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        gpsService.requestLocation(getActivity(), new GPSService.GPSCallback() {
            @Override
            public void onLastLocationKnown(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        });

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition arg0) {
                blurMap(googleMap);
            }
        });
    }

    @UiThread(delay = 2000) protected void blurMap(final GoogleMap googleMap) {
        googleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                if(v_target_blur == null) return;
                blurEffect.applyOverlap(bitmap, v_target_blur);
                ObjectAnimator.ofFloat(v_target_blur, View.ALPHA, 0, 1).setDuration(1000).start();
            }
        });
    }
}
