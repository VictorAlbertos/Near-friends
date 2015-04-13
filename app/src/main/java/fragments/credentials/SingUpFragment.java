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

@EFragment(R.layout.sing_up_fragment)
public class SingUpFragment extends CredentialsFragment {
    @ViewById protected MaterialEditText ed_email, ed_user_name, ed_phone, ed_password;
    @ViewById protected CircularProgressButton bt_sing_up;

    @Click protected void bt_sing_up() {
        boolean fieldsFilled = fieldsFilled(Arrays.asList(ed_email, ed_user_name, ed_phone, ed_password));
        if(!fieldsFilled) return;
        if(validEmail(ed_email)) doSingUp();
    }

    @StringRes(R.string.sing_up_success) protected String sing_up_success;
    private void doSingUp() {
        bt_sing_up.setProgress(50);
        bt_sing_up.setIndeterminateProgressMode(true);

        user.singUp(ed_email.getText().toString(), ed_user_name.getText().toString(), ed_phone.getText().toString(),
            ed_user_name.getText().toString(), new UserService.Callback() {
                @Override
                public void onResponse(String messageFailure) {
                    bt_sing_up.setProgress(0);
                    bt_sing_up.setIndeterminateProgressMode(false);

                    if (messageFailure.isEmpty()) {
                        goToMainActivity();
                        system.showToast(sing_up_success);
                    } else system.showToast(messageFailure);
                }
        });
    }
}
