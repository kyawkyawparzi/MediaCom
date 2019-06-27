package pmt.kyawkyaw.myapp.mediacom.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.internal.Objects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import pmt.kyawkyaw.myapp.mediacom.R;
import pmt.kyawkyaw.myapp.mediacom.model.Chat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.viewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    FirebaseUser firebaseUser;
    Intent intent;

    private Context context;
    List<Chat>get_get_chat_data;
    public String image;

    private Context myContext;

    private int visible_gone;
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
        Chat chat_data=get_get_chat_data.get(position);

        if(chat_data.getMessage().equals("")){
            Glide.with(context).load(chat_data.getImage()).into(viewHolder.send_image);
            viewHolder.send_message.setText(chat_data.getMessage());
            viewHolder.send_message.setVisibility(View.GONE);
        }
        if(chat_data.getImage().equals("")){
            viewHolder.send_message.setText(chat_data.getMessage());
            viewHolder.send_image.setVisibility(View.GONE);
        }

        Glide.with(context).load(chat_data.getImage()).into(viewHolder.send_image);
        viewHolder.send_message.setText(chat_data.getMessage());

        viewHolder.get_times.setText(chat_data.getSendtime());
        if(!image.equals("default")){
            Glide.with(context).load(image).into(viewHolder.profile_image);

        }else {
            viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }

//        viewHolder.left.setText(chat_data.getSendtime());
//        viewHolder.right.setText(chat_data.getSendtime());
        //seen unseen
        if(position==get_get_chat_data.size()-1){
            if (chat_data.isIsseen()){
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

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        public TextView send_message;
        public ImageView profile_image;
        private TextView txt_seen,get_times;
        public ImageView send_image;

        public viewHolder(View itemView){
            super(itemView);
            get_times=itemView.findViewById(R.id.send_times);
            send_image=itemView.findViewById(R.id.send_image);

            send_message=itemView.findViewById(R.id.send_message);
            profile_image=itemView.findViewById(R.id.profile_image);
            txt_seen=itemView.findViewById(R.id.txt_seen);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            visible_gone=get_times.getVisibility()==View.GONE?1:0;
            //Toast.makeText(context,visible_gone+"",Toast.LENGTH_LONG).show();
            if(visible_gone==1){
                //Toast.makeText(context,"Reach if",Toast.LENGTH_LONG).show();
                get_times.setVisibility(View.VISIBLE);
            }else {
                //Toast.makeText(context,"Reach else", Toast.LENGTH_LONG).show();
                get_times.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            final Chat data=get_get_chat_data.get(getAdapterPosition());

            AlertDialog alert = new AlertDialog.Builder(myContext).create();
            alert.setTitle("Delete entry");
            alert.setMessage("Are you sure to delete : "+get_get_chat_data.get(getAdapterPosition()).getMessage());
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chats").child(get_get_chat_data.get(getAdapterPosition()).getChatid());
                    reference.removeValue();
                    Toast.makeText(context,get_get_chat_data.get(getAdapterPosition()).getMessage()+" is deleted",Toast.LENGTH_LONG).show();
                }
            });
            alert.setButton(AlertDialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // DO SOMETHING HERE

                }
            });
            alert.show();
            return true;
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
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        myContext=recyclerView.getContext();
    }
}