package activities.main;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hacer_app.near_friends.R;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.annotations.res.StringRes;

import java.util.List;

import activities.BaseActionBarActivity;
import adapters.ContactsAdapter;
import fragments.customs.MyLocationBlurryMapFragment;
import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import models.Contact;
import services.Contacts;
import services.UserService;
import utilities.HelperSystem;
import utilities.MyHeaderStikkyAnimator;
import utilities.RecyclerItemClickListener;


@EActivity(R.layout.contacts_activity)
public class ContactsActivity extends BaseActionBarActivity {
    @ViewById protected View v_target_blur, rl_sticky_header;
    @ViewById protected RecyclerView rv_contacts;
    @ViewById protected ProgressWheel pw;
    @FragmentById protected MyLocationBlurryMapFragment map_fragment;
    @Bean protected ContactsAdapter adapter;
    @Bean protected Contacts contactsService;
    @Bean protected MyHeaderStikkyAnimator stikkyAnimator;
    @Bean protected HelperSystem system;
    private List<Contact> contacts;

    @AfterViews protected void init() {
        if (!gps.isEnabled(this)) return;

        map_fragment.setTargetToBlur(v_target_blur);

        setUpRecyclerView();
        setStickyHeader();
    }

    private void setUpRecyclerView() {
        rv_contacts.setLayoutManager(new LinearLayoutManager(this));
        contactsService.getAsyncAll(false, new Contacts.ContactsCallback() {
            @Override
            public void onContactsRetrieved(List<Contact> aContacts) {
                pw.setVisibility(View.INVISIBLE);
                contacts = aContacts;
                rv_contacts.setAdapter(adapter.init(contacts));
            }
        });
        rv_contacts.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Contact contact = contacts.get(position);
                        if(!contact.isRegistered()) askToSendWhatsApp(contact.getPhone());
                        else {
                            contactsService.setCurrentSelectedContact(contact);
                            ContactsMapActivity_.intent(ContactsActivity.this).start();
                        }
                    }
                })
        );
    }

    @StringRes(R.string.app_name) protected String app_name;
    @StringRes(R.string.ask_to_send_whatsapp) protected String ask_to_send_whatsapp;
    @StringRes(R.string.agree) protected String agree;
    @StringRes(R.string.cancel) protected String cancel;
    private void askToSendWhatsApp(final String phoneNumber) {
        new MaterialDialog.Builder(this)
                .title(app_name)
                .content(ask_to_send_whatsapp)
                .positiveText(agree)
                .negativeText(cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        system.sendWhatsAppToSpecificContact(ContactsActivity.this, phoneNumber);
                    }
                })
                .show();
    }

    @DimensionPixelOffsetRes(R.dimen._60dp)int heightStickyHeader;
    private void setStickyHeader() {
        StikkyHeaderBuilder.stickTo(rv_contacts)
                .setHeader(rl_sticky_header)
                .minHeightHeader(heightStickyHeader)
                .animator(stikkyAnimator)
                .build();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshRecyclerView();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void refreshRecyclerView() {
        pw.setVisibility(View.VISIBLE);
        rv_contacts.setVisibility(View.INVISIBLE);

        contactsService.getAsyncAll(false, new Contacts.ContactsCallback() {
            @Override
            public void onContactsRetrieved(List<Contact> aContacts) {
                contacts = aContacts;
                adapter.init(contacts);
                adapter.notifyDataSetChanged();

                pw.setVisibility(View.INVISIBLE);
                rv_contacts.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

        Drawable refresh = new IconDrawable(this, Iconify.IconValue.fa_adjust.fa_refresh)
                .colorRes(R.color.accent)
                .actionBarSize();

        menu.findItem(R.id.refresh).setIcon(refresh);

        return super.onCreateOptionsMenu(menu);
    }

    @Click protected void rl_avatar() {
        ProfileActivity_.intent(this).start();
    }

    @Click protected void v_map() {
        contactsService.setCurrentSelectedContact(null);
        ContactsMapActivity_.intent(this).start();
    }

    @ViewById protected ImageView iv_avatar;
    @ViewById protected View tv_icon_avatar;
    @Bean protected UserService user;
    @Override protected void onResume() {
        super.onResume();

        String urlAvatar = user.getUrlAvatar();
        if(!urlAvatar.isEmpty()) {
            Picasso.with(this).load(urlAvatar).placeholder(R.drawable.white_placeholder)
                    .fit().centerCrop().into(iv_avatar);
            tv_icon_avatar.setVisibility(View.INVISIBLE);
        } else tv_icon_avatar.setVisibility(View.VISIBLE);
    }
}
