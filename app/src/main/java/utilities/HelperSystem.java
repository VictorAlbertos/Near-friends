package utilities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.github.johnpersano.supertoasts.SuperToast;
import com.hacer_app.near_friends.R;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;

import java.util.regex.Pattern;

@EBean
public class HelperSystem {
    @App NearFriendsApp app;
    private static final Pattern rfc2822 = Pattern.compile(
            "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
    );
    public boolean isValidEmail(String email) {
        return rfc2822.matcher(email).matches();
    }

    public void showToast(String message) {
        int textColor = app.getResources().getColor(R.color.white);
        SuperToast superToast = new SuperToast(app);
        superToast.setBackground(SuperToast.Background.BLUE);
        superToast.setTextColor(textColor);
        superToast.setText(message);
        superToast.setDuration(SuperToast.Duration.MEDIUM);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.show();
    }

    public void sendWhatsAppToSpecificContact(Activity activity, String phoneNumber) {
        Uri mUri = Uri.parse("smsto:" + phoneNumber);
        Intent mIntent = new Intent(Intent.ACTION_SENDTO, mUri);
        mIntent.setPackage("com.whatsapp");
        mIntent.putExtra("sms_body", "The text goes here");
        mIntent.putExtra("chat", true);
        activity.startActivity(mIntent);
    }
}
