package pmt.kyawkyaw.myapp.mediacom.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.util.List;

import pmt.kyawkyaw.myapp.mediacom.R;
import pmt.kyawkyaw.myapp.mediacom.model.Chat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.viewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    FirebaseUser firebaseUser;

    private Context context;
    List<Chat>get_get_chat_data;
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
        Chat chat_data=get_get_chat_data.get(position);
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

        public viewHolder(View itemView){
            super(itemView);
            get_times=itemView.findViewById(R.id.send_times);

            send_message=itemView.findViewById(R.id.send_message);
            profile_image=itemView.findViewById(R.id.profile_image);
            txt_seen=itemView.findViewById(R.id.txt_seen);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(context,get_get_chat_data.get(getAdapterPosition()).getMessage()+"",Toast.LENGTH_LONG).show();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MyAlertDialogTheme));
            alertDialog.setTitle("Your title");
            alertDialog.setMessage("your message ");
            alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // DO SOMETHING HERE

                }
            });

            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(context,get_get_chat_data.get(getAdapterPosition()).getMessage()+"",Toast.LENGTH_LONG).show();
            AlertDialog alert = new AlertDialog.Builder(context).create();

            alert.setTitle("Delete entry");
            alert.setMessage("Are you sure you want to delete?");
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

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
}