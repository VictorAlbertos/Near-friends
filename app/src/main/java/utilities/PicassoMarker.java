package utilities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.hacer_app.near_friends.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import activities.main.ContactsMapActivity;

public class PicassoMarker implements Target {
    private Marker marker;
    private ContactsMapActivity activity;

    public PicassoMarker(ContactsMapActivity activity, Marker marker) {
        this.marker = marker;
        this.activity = activity;
    }

    @Override
    public int hashCode() {
        return marker.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PicassoMarker) {
            Marker marker = ((PicassoMarker) o).marker;
            return this.marker.equals(marker);
        } else {
            return false;
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        final View root = View.inflate(activity, R.layout.item_contact_map, null);
        final ImageView iv_avatar = (ImageView)root.findViewById(R.id.iv_avatar);
        iv_avatar.setImageBitmap(bitmap);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(activity.createDrawableFromView(root)));
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {}

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {}
}
