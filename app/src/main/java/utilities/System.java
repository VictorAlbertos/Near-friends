package utilities;

import android.accounts.Account;
import android.accounts.AccountManager;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@EBean(scope = EBean.Scope.Singleton)
public class System {

    @App protected NearFriendsApp mNearFriendsApp;

    public String getPhoneNumber() {
        AccountManager manager = AccountManager.get(mNearFriendsApp);
        Account[] accounts = manager.getAccounts();

        ArrayList<String> googleAccounts = new ArrayList<String>();
        for (Account account : accounts) {
            String type = account.type;
            if(type.equals("com.whatsapp")){
                return account.name;
            }
        }

        return "";
    }

    public String getPhoneUsername() {
        AccountManager manager = AccountManager.get(mNearFriendsApp);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<>();

        for (Account account : accounts) {
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1) return parts[0];
        }
        return null;
    }
}
