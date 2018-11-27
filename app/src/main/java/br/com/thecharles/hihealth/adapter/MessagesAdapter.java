package br.com.thecharles.hihealth.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.helper.UserFirebase;
import br.com.thecharles.hihealth.model.Message;
import br.com.thecharles.hihealth.model.User;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    private List<Message> messages;
    private Context context;

    private static final int TYPR_SENDER = 0;
    private static final int TYPR_RECEIVER = 1;

    public MessagesAdapter(List<Message> list , Context c) {
        this.context = c;
        this.messages = list;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = null;
        if (viewType == TYPR_SENDER) {

            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_message_sender, parent,false);
        } else if (viewType == TYPR_RECEIVER) {
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_message_receiver, parent,false);
        }

        return new MessagesViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
        Message message = messages.get(position);

        String msg = message.getMessage();
        String file = message.getFile();

        if (file != null) {
//            Uri url = Uri.parse(file);

            //Esconder o texto
            holder.message.setVisibility(View.GONE);
        } else {
            holder.message.setText(msg);
            //Esconder a imagem
            holder.image.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messages.get(position);
        String idUser = UserFirebase.getUId();

        if (idUser.equals(message.getIdSender())) {
            return TYPR_SENDER;
        }

        return TYPR_RECEIVER;
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        ImageView image;

        public MessagesViewHolder(View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.textMessageText);
            image = itemView.findViewById(R.id.imageMessageFile);
        }
    }
}
