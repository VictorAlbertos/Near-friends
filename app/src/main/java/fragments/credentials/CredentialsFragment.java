package fragments.credentials;

import android.support.v4.app.Fragment;

import com.hacer_app.near_friends.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;

import java.util.List;

import activities.main.ContactsActivity_;
import services.UserService;
import utilities.HelperSystem;

@EFragment
public class CredentialsFragment extends Fragment {
    @Bean protected UserService user;

    @Bean HelperSystem system;
    @StringRes(R.string.email_field_validation) protected  String email_field_validation;
    protected boolean validEmail(MaterialEditText materialEditText) {
        if(system.isValidEmail(materialEditText.getText().toString())) return true;
        else {
            materialEditText.setError(email_field_validation);
            return false;
        }
    }

    protected boolean fieldsFilled(List<MaterialEditText> materialEditTexts) {
        for (MaterialEditText materialEditText : materialEditTexts) {
            if (!isFieldFilled(materialEditText)) return false;
        }
        return true;
    }

    @StringRes(R.string.mandatory_field) protected  String mandatory_field;
    protected boolean isFieldFilled(MaterialEditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError(mandatory_field);
            return false;
        } else return true;
    }

    @UiThread(delay = 1000)
    protected void goToMainActivity() {
        ContactsActivity_.intent(this).start();
    }
}
