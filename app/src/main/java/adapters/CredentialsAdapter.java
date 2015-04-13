package adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;

import fragments.credentials.SingInFragment_;
import fragments.credentials.SingUpFragment_;
import utilities.NearFriendsApp;

@EBean
public class CredentialsAdapter extends FragmentStatePagerAdapter {
    @App NearFriendsApp app;

    public CredentialsAdapter(Context context) {
        super(((FragmentActivity)context).getSupportFragmentManager());
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) return new SingInFragment_();
        else return new SingUpFragment_();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
