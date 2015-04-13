package activities.credentials;

import android.os.Bundle;
import android.view.View;

import com.hacer_app.near_friends.R;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

import activities.BaseActionBarActivity;
import activities.main.ContactsActivity_;
import adapters.CredentialsAdapter;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import fragments.customs.MyLocationBlurryMapFragment;
import services.UserService;
import utilities.BlurEffect;


@EActivity()
public class CredentialsActionBarActivity extends BaseActionBarActivity {
    @Bean protected CredentialsAdapter credentialsAdapter;
    @Bean protected UserService user;
    @Bean protected BlurEffect blurEffect;
    @ViewById protected VerticalViewPager vertical_vp;
    @ViewById protected View v_target_blur;
    @FragmentById protected MyLocationBlurryMapFragment map_fragment;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!gps.isEnabled(this)) return;

        if (user.isSingIn()) {
            ContactsActivity_.intent(this).start();
            return;
        }

        setContentView(R.layout.activity_credentials);
        vertical_vp.setAdapter(credentialsAdapter);
        map_fragment.setTargetToBlur(v_target_blur);
    }

}
