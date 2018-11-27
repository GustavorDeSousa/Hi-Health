package br.com.thecharles.hihealth.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.model.User;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder>{

    private List<User> contacts;
    private Context context;
    private Switch switchAlert;

    public ContactsAdapter(List<User> listContacts, Context c, Switch alert) {
        this.contacts = listContacts;
        this.context = c;
        this.switchAlert = alert;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_contacts, parent, false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        User user = contacts.get(position);

        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        holder.alert.setChecked(user.isAlert());

        if (user.getPhoto() != null) {

        } else {
            holder.photo.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        }

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;
        TextView name ,email;
        Switch alert;

        public MyViewHolder(View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.ivContacts);
            name = itemView.findViewById(R.id.tvName);
            email = itemView.findViewById(R.id.tvEmail);
            alert = itemView.findViewById(R.id.sAlert);
        }
    }
}
