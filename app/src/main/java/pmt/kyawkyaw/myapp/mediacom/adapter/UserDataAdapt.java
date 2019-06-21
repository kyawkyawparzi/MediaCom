package pmt.kyawkyaw.myapp.mediacom.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pmt.kyawkyaw.myapp.mediacom.MessagingActivity;
import pmt.kyawkyaw.myapp.mediacom.R;
import pmt.kyawkyaw.myapp.mediacom.model.Chat;
import pmt.kyawkyaw.myapp.mediacom.model.User;

public class UserDataAdapt extends RecyclerView.Adapter<UserDataAdapt.viewHolder> {
    private Context context;
    List<User> get_data_adapt;
    boolean ischat;
    String thelastmessage;
    String sender,receiver;

    public UserDataAdapt(Context context, List<User> get_data_adapt,boolean ischat) {
        this.context = context;
        this.get_data_adapt = get_data_adapt;
        this.ischat=ischat;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(context).inflate(R.layout.user_item,viewGroup,false);
        return new UserDataAdapt.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, int position) {
        //Log.i("load", "Set Data On View");
        final User user=get_data_adapt.get(position);
        viewHolder.username.setText(user.getUsername());
        if(user.getPicture().equals("default")){
            viewHolder.profile_image.setImageResource(R.drawable.person_blue);
        }else {
            Glide.with(UserDataAdapt.this.context).load(user.getPicture()).into(viewHolder.profile_image);
        }

        //last message
        lastMessage(user.getId(),viewHolder.default_message);

        //online and offline
        if(ischat){
            if(user.getStatus().equals("online")){
                viewHolder.on.setVisibility(View.VISIBLE);
                viewHolder.off.setVisibility(View.GONE);
            }else {
                viewHolder.on.setVisibility(View.GONE);
                viewHolder.off.setVisibility(View.VISIBLE);
            }
        }else {
            viewHolder.on.setVisibility(View.GONE);
            viewHolder.off.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //last message
                viewHolder.default_message.setVisibility(View.GONE);

                Intent i=new Intent(context, MessagingActivity.class);
                i.putExtra("id",user.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return get_data_adapt.size();
    }

    //create view holder to handle view
    public class viewHolder extends RecyclerView.ViewHolder{

        private CircleImageView on,off;
        public TextView username;
        public ImageView profile_image;
        private TextView default_message;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            profile_image=itemView.findViewById(R.id.profile_image);

            //online offline
            on=itemView.findViewById(R.id.img_on);
            off=itemView.findViewById(R.id.img_off);

            //last message
            default_message=itemView.findViewById(R.id.default_message);
        }
    }
    //last message
    private void lastMessage(final String userid, final TextView lastmsg){
        thelastmessage="default";
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.isIsseen()==false&&(chat.getReceiver().equals(firebaseUser.getUid())&&chat.getSender().equals(userid)||
                            chat.getReceiver().equals(userid)&&chat.getSender().equals(firebaseUser.getUid()))){
                        thelastmessage=chat.getMessage();
                        sender=chat.getSender();
                        receiver=chat.getReceiver();
                    }else {
                        thelastmessage="seen";
                    }
                }
                switch (thelastmessage){
                    case "seen":
                        lastmsg.setText("");
                        break;
                    default:
//                        if(sender==firebaseUser.getUid()){
//                            lastmsg.setText("You Send : "+thelastmessage);
//                            break;
//                        }
//                        if (!(receiver==firebaseUser.getUid())){
//                            lastmsg.setText("New Msg : "+thelastmessage);
//                        }
                        lastmsg.setText(thelastmessage);
                }
                thelastmessage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
