package pmt.kyawkyaw.myapp.mediacom;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pmt.kyawkyaw.myapp.mediacom.adapter.AppUtils;
import pmt.kyawkyaw.myapp.mediacom.adapter.MessageAdapter;
import pmt.kyawkyaw.myapp.mediacom.model.Chat;
import pmt.kyawkyaw.myapp.mediacom.model.User;
import pmt.kyawkyaw.myapp.mediacom.notification.APIService;
import pmt.kyawkyaw.myapp.mediacom.notification.Client;
import pmt.kyawkyaw.myapp.mediacom.notification.Data;
import pmt.kyawkyaw.myapp.mediacom.notification.MyResponse;
import pmt.kyawkyaw.myapp.mediacom.notification.Sender;
import pmt.kyawkyaw.myapp.mediacom.notification.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingActivity extends Activity implements View.OnClickListener {

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    CircleImageView profile_image;
    TextView username;
    RecyclerView recycler_messaging;
    EditText message_sent;
    ImageButton btn_sent;
    CircleImageView back_arrow;
    Intent intent;
    String user_id;

    //chat
    List<Chat> get_chat_data;
    MessageAdapter messageAdapter;

    //seen and unseen
    ValueEventListener listener;


    //for notification
//    APIService apiService;
//    boolean notify=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        message_sent=findViewById(R.id.message_sent);
        btn_sent=findViewById(R.id.btn_sent);
        back_arrow=findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(this);

        //for notification
//        apiService= Client.getClient("http://fcm.googleapis.com/").create(APIService.class);

        recycler_messaging=findViewById(R.id.recycler_messaging);
        recycler_messaging.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recycler_messaging.setLayoutManager(linearLayoutManager);

        //will be load username and profile image when you clicked user
        intent=getIntent();
        user_id=intent.getStringExtra("id");
        loadUsernameProfileImage();
//        Calendar calendar=Calendar.getInstance();
//        final String current_time= DateFormat.getDateInstance().format(calendar.getTime());
//        final String current_time= AppUtils.getFormattedDateString(AppUtils.getCurrentDateTime());
        btn_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //for notification
//                notify=true;


                final String current_time= AppUtils.getFormattedDateString(AppUtils.getCurrentDateTime());
                String get_message=message_sent.getText().toString();
                if(!get_message.equals("")){
                    sendMessage(firebaseUser.getUid(),user_id,get_message,current_time);
                }else {
                    Toast.makeText(getApplicationContext(),"Plese enter a text!",Toast.LENGTH_LONG).show();
                }
                message_sent.setText("");
            }
        });
    }
    private void sendMessage(String sender, final String receiver, final String message,String time) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

        HashMap<String ,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        hashMap.put("sendtime",time);

        reference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatreference=FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(user_id);
        chatreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatreference.child("id").setValue(user_id);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //do for notification
//        final String msg=message;
//        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User get_users=dataSnapshot.getValue(User.class);
//                if(notify){
//                    sendNotification(receiver,get_users.getUsername(),msg);
//                }
//                notify=false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }
    //for notification
//    private void sendNotification(String receiver, final String username, final String msg) {
//        final DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
//        Query query=tokens.orderByKey().equalTo(receiver);
//
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    Token token=snapshot.getValue(Token.class);
//                    Log.i("msg", token+"");
//                    Data data=new Data(firebaseUser.getUid(),R.drawable.person_blue,username+": "+msg,"New Message",user_id);
//
//                    Sender sender=new Sender(data,token.getToken());
//                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
//                        @Override
//                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//                            if(response.body().success!=1){
//                                Toast.makeText(MessagingActivity.this,"Failed",Toast.LENGTH_LONG).show();
//                            }
//                        }
//                        @Override
//                        public void onFailure(Call<MyResponse> call, Throwable t) {
//
//                        }
//                    });
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }
    private void loadUsernameProfileImage() {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(user_id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getPicture().equals("default")){
                    profile_image.setImageResource(R.drawable.person_blue);
                }else {
                    Glide.with(getApplicationContext()).load(user.getPicture()).into(profile_image);
                }
                readMessages(firebaseUser.getUid(),user_id,user.getPicture());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        seenMessage(user_id);
    }
    private void readMessages(final String myid, final String user_id, final String imgaes){

        get_chat_data=new ArrayList<>();
        databaseReference=FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                get_chat_data.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat_data=snapshot.getValue(Chat.class);
                    if(chat_data.getReceiver().equals(myid)&&chat_data.getSender().equals(user_id)||chat_data.getReceiver().equals(user_id)&&chat_data.getSender().equals(myid)){
                        get_chat_data.add(chat_data);
                    }
                }
                messageAdapter=new MessageAdapter(getApplicationContext(),get_chat_data,imgaes);
                recycler_messaging.setAdapter(messageAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //seen and unseen
    private void seenMessage(final String user_id){
        databaseReference=FirebaseDatabase.getInstance().getReference("Chats");
        listener=databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat get_chat=snapshot.getValue(Chat.class);
                    if(get_chat.getReceiver().equals(firebaseUser.getUid())&&get_chat.getSender().equals(user_id)){
                        HashMap<String,Object> hashMap=new HashMap();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //for online and offline
    private void status(String status){
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        databaseReference.updateChildren(hashMap);
    }
    protected void onResume() {
        super.onResume();
        status("online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        databaseReference.removeEventListener(listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_arrow:
                this.finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}