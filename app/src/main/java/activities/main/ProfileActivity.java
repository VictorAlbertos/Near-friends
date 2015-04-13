package activities.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hacer_app.near_friends.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.io.File;

import activities.BaseActionBarActivity;
import activities.credentials.CredentialsActionBarActivity_;
import services.UserService;
import utilities.BlurEffect;
import utilities.image_picker.OnPictureTaken;
import utilities.image_picker.PickerImage;

@EActivity(R.layout.profile_activity)
public class ProfileActivity extends BaseActionBarActivity implements OnPictureTaken {
    private PickerImage pickerImage;
    @ViewById protected ImageView iv_avatar;
    @Bean protected UserService user;

    @AfterViews protected void init() {
        populateFields();
        pickerImage = new PickerImage(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @ViewById protected TextView tv_user_name, tv_email, tv_phone;
    @StringRes(R.string.user_name) protected String user_name;
    @StringRes(R.string.email) protected String email;
    @StringRes(R.string.phone) protected String phone;
    private void populateFields() {
        String urlAvatar = user.getUrlAvatar();
        if(!urlAvatar.isEmpty()) loadImage(urlAvatar);

        tv_user_name.setText(user_name + ": " + user.getName());
        tv_email.setText(email + ": " + user.getEmail());
        tv_phone.setText(phone + ": " + user.getPhoneNumber());

    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Click protected void iv_avatar() {
        askToTakeImage();
    }

    @StringRes(R.string.app_name) protected String app_name;
    @StringRes(R.string.choose_image) protected String choose_image;
    @StringRes(R.string.gallery) protected String gallery;
    @StringRes(R.string.camera) protected String camera;
    private void askToTakeImage() {
        new MaterialDialog.Builder(this)
                .title(app_name)
                .content(choose_image)
                .positiveText(gallery)
                .negativeText(camera)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        pickerImage.sendGalleryIntent(ProfileActivity.this);
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        pickerImage.sendCameraIntent(ProfileActivity.this);
                        dialog.dismiss();                      }
                })
                .show();
    }

    @Bean protected BlurEffect blurEffect;
    @ViewById protected ImageView iv_landscape_avatar, iv_blurry_avatar;
    @Override public void pictureTaken(Bitmap bitmap, File file) {
        user.saveImageInBackground(file);
        loadImage(file);
    }

    private void loadImage(Object object) {
        File file = object instanceof File ? (File) object : null;
        String url = object instanceof String ? (String) object : null;

        Callback callback = new Callback() {
            @Override public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable)iv_landscape_avatar.getDrawable()).getBitmap();
                iv_landscape_avatar.setImageBitmap(bitmap);
                blurEffect.applyOverlap(iv_landscape_avatar, iv_blurry_avatar);
            }
            @Override public void onError() {}
        };

        if (file != null) {
            Picasso.with(this).load(file).placeholder(R.drawable.white_placeholder)
                    .fit().centerCrop().into(iv_avatar);
            Picasso.with(app).load(file).fit().centerCrop().into(iv_landscape_avatar, callback);
        } else if (url != null) {
            Picasso.with(this).load(url).placeholder(R.drawable.white_placeholder)
                    .fit().centerCrop().into(iv_avatar);
            Picasso.with(app).load(url).fit().centerCrop().into(iv_landscape_avatar, callback);
        }
    }

    @Click protected void bt_logout() {
        user.logOut();
        CredentialsActionBarActivity_.intent(this)
                .flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).start();
    }
}
