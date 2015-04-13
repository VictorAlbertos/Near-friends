package adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hacer_app.near_friends.R;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;

import java.util.List;

import models.Contact;
import utilities.NearFriendsApp;

@EBean
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    @App NearFriendsApp app;
    private List<Contact> contacts;

    public ContactsAdapter init(List<Contact> aContacts) {
        contacts = aContacts;
        return this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        ViewHolder viewHolder = new ViewHolder(root);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.tv_initial_name.setVisibility(View.VISIBLE);
        holder.tv_initial_name.setText(contact.getFirstLetterUserName());
        holder.tv_name.setText(contact.getUserName());
        holder.tv_distance.setText("");
        holder.tv_last_update.setText("");


        String urlAvatar = contact.getURLAvatar();
        Picasso.with(app).load(urlAvatar).fit().centerCrop().placeholder(R.drawable.white_placeholder).into(holder.iv_avatar);


        if (!contact.isRegistered()) return;
        holder.tv_distance.setText(contact.getDistanceFormatted());
        holder.tv_initial_name.setVisibility(View.INVISIBLE);
        holder.tv_last_update.setText(contact.getLastUpdateFormatted());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_initial_name, tv_name, tv_distance, tv_last_update;
        public ViewHolder(View view) {
            super(view);
            iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
            tv_initial_name = (TextView) view.findViewById(R.id.tv_initial_name);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_distance = (TextView) view.findViewById(R.id.tv_distance);
            tv_last_update = (TextView) view.findViewById(R.id.tv_last_update);
        }
    }
}
