package fragments.credentials;

import com.dd.CircularProgressButton;
import com.hacer_app.near_friends.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.Arrays;

import services.UserService;

@EFragment(R.layout.sing_in_fragment)
public class SingInFragment extends CredentialsFragment {
    @ViewById protected MaterialEditText ed_user_name, ed_password;
    @ViewById protected CircularProgressButton bt_sing_in;

    @Click protected void bt_sing_in() {
        boolean fieldsFilled = fieldsFilled(Arrays.asList(ed_user_name, ed_password));
        if(fieldsFilled) doSingIn();
    }

    @StringRes(R.string.sing_in_success) protected String sing_in_success;
    private void doSingIn() {
        bt_sing_in.setProgress(50);
        bt_sing_in.setIndeterminateProgressMode(true);

        user.singIn(ed_user_name.getText().toString(), ed_password.getText().toString(), new UserService.Callback() {
            @Override
            public void onResponse(String messageFailure) {
                bt_sing_in.setProgress(0);
                bt_sing_in.setIndeterminateProgressMode(false);
                if (messageFailure.isEmpty()) {
                    goToMainActivity();
                    system.showToast(sing_in_success);
                } else system.showToast(messageFailure);
            }
        });
    }
}
