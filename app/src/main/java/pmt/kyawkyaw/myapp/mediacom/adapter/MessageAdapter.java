package pmt.kyawkyaw.myapp.mediacom.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import pmt.kyawkyaw.myapp.mediacom.R;
import pmt.kyawkyaw.myapp.mediacom.model.Chat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.viewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    FirebaseUser firebaseUser;


    private Context context;
    List<Chat> get_get_chat_data;
    public String image;

    public MessageAdapter(Context context, List<Chat> get_get_chat_data,String image) {
        this.context = context;
        this.get_get_chat_data = get_get_chat_data;
        this.image=image;
    }

    @NonNull
    @Override
    public MessageAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==MSG_TYPE_RIGHT){
            View v= LayoutInflater.from(context).inflate(R.layout.chat_right,viewGroup,false);
            return new MessageAdapter.viewHolder(v);
        }else {
            View v= LayoutInflater.from(context).inflate(R.layout.chat_left,viewGroup,false);
            return new MessageAdapter.viewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.viewHolder viewHolder, int position) {
        Chat chat=get_get_chat_data.get(position);
        viewHolder.send_message.setText(chat.getMessage());
        if(!image.equals("default")){
            Glide.with(context).load(image).into(viewHolder.profile_image);

        }else {
            viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }


        //seen unseen
        if(position==get_get_chat_data.size()-1){
            if (chat.isIsseen()){
                viewHolder.txt_seen.setVisibility(View.VISIBLE);
                viewHolder.txt_seen.setText("Seen");
            }else {
                viewHolder.txt_seen.setVisibility(View.VISIBLE);
                viewHolder.txt_seen.setText("Delivered");
            }
        }else {
            //else task is to check the last message
            viewHolder.txt_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return get_get_chat_data.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        public TextView send_message;
        public ImageView profile_image;
        public TextView txt_seen;

        public viewHolder(View itemView){
            super(itemView);
            send_message=itemView.findViewById(R.id.send_message);
            profile_image=itemView.findViewById(R.id.profile_image);
            txt_seen=itemView.findViewById(R.id.txt_seen);
        }
    }
    //to decide the place of sender and receiver position(left or right)
    @Override
    public int getItemViewType(int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(get_get_chat_data.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}