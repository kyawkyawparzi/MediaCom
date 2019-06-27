package pmt.kyawkyaw.myapp.mediacom;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pmt.kyawkyaw.myapp.mediacom.adapter.MessageAdapter;
import pmt.kyawkyaw.myapp.mediacom.interfaceforpicturesent.callmethod_pic_request_code;
import pmt.kyawkyaw.myapp.mediacom.model.Chat;
import pmt.kyawkyaw.myapp.mediacom.model.User;
import pmt.kyawkyaw.myapp.mediacom.notification.APIService;
import pmt.kyawkyaw.myapp.mediacom.notification.Client;
import pmt.kyawkyaw.myapp.mediacom.notification.Data;
import pmt.kyawkyaw.myapp.mediacom.notification.Response;
import pmt.kyawkyaw.myapp.mediacom.notification.Sender;
import pmt.kyawkyaw.myapp.mediacom.notification.Token;
import pmt.kyawkyaw.myapp.mediacom.timestamp.AppUtils;
import retrofit2.Call;
import retrofit2.Callback;

public class MessagingActivity extends Activity implements View.OnClickListener , callmethod_pic_request_code {

    //for notification
    String mUID;
    APIService apiService;
    boolean notify=false;
    
    
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;

    //image send view
    ImageView show_image;

    //for image send
    private static final int IMAGE_REQUEST=1;
    StorageReference storageReference;
    private Uri myImages;
    private StorageTask uploadPhoto;

    CircleImageView profile_image;
    TextView username;
    RecyclerView recycler_messaging;
    EditText message_sent;
    ImageButton btn_sent;
    CircleImageView back_arrow;
    Intent intent;
    String user_id;
    ImageButton choose_image;

    //chat
    List<Chat> get_chat_data;
    MessageAdapter messageAdapter;
    String imageNull="";
    String messageNull="";

    //seen and unseen
    ValueEventListener listener;

    TextView onoff;


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
        onoff=findViewById(R.id.on_off);
        choose_image=findViewById(R.id.choose_image);
        show_image=findViewById(R.id.show_image);

        //for image horizontal view in recycler
//        recyclerView=findViewById(R.id.recycler_pic_view);
//        mylayout=findViewById(R.id.layout_cancel_ok);
//        myok=findViewById(R.id.positive_icon);
//        mycancel=findViewById(R.id.negative_icon);

//        myok.setOnClickListener(this);
//        mycancel.setOnClickListener(this);

        //load picture on recycler view
        recycler_messaging=findViewById(R.id.recycler_messaging);
        recycler_messaging.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recycler_messaging.setLayoutManager(linearLayoutManager);
        choose_image.setOnClickListener(this);

        //for imageSend location
        storageReference= FirebaseStorage.getInstance().getReference("sendImageFile");


        //will be load username and profile image when you clicked user
        intent=getIntent();
        user_id=intent.getStringExtra("id");
        loadUsernameProfileImage();
        
        //for notification
        apiService= Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        checkUserStatus();
        updateToken(FirebaseInstanceId.getInstance().getToken());

        btn_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for notification
                notify=true;
                final String current_time= AppUtils.getFormattedDateString(AppUtils.getCurrentDateTime());
                String get_message=message_sent.getText().toString();
                if(!get_message.equals("")){
                    sendMessage(firebaseUser.getUid(),user_id,get_message,current_time,imageNull);
                }else {
                    show_image.setVisibility(View.GONE);
                    sendImage(firebaseUser.getUid(),user_id,current_time,messageNull);
                }
                message_sent.setText("");
            }
        });
    }
    private void sendMessage(String sender, final String receiver,String message,String time,String nullImage) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chats");

        String chat_id=reference.push().getKey();

        HashMap<String ,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("image",nullImage);
        hashMap.put("isseen",false);
        hashMap.put("sendtime",time);
        hashMap.put("chatid",chat_id);

        reference.child(chat_id).setValue(hashMap);
        
        //for notification start
        final DatabaseReference myMsgReference=FirebaseDatabase.getInstance().getReference("Users").child(sender);
        myMsgReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(notify){
                    sendNotification(receiver,user.getUsername(),message);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //for notification end

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
    }

    private void sendNotification(String receiver, String username, String message) {
        DatabaseReference allTokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=allTokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Token token=snapshot.getValue(Token.class);
                    Data data=new Data(firebaseUser.getUid(),username+":"+message,"New Message",receiver,R.drawable.m_one);

                    Sender sender=new Sender(data,token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(getApplicationContext(),response.message(),Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUsernameProfileImage() {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(user_id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getStatus().equals("offline")){
                    //Toast.makeText(getApplicationContext(),user.getUsername()+" is in offline",Toast.LENGTH_LONG).show();
                }else {
                    onoff.setText("Online");
                }
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
        //for notification
        checkUserStatus();
        super.onResume();
        //for online offline
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
            case R.id.choose_image:

                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhotoIntent,IMAGE_REQUEST);

//                  showImage();
//                final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_pic_view);
//                recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
//                recyclerView.setHasFixedSize(true);
//                List<String> mFiles = FileUtil.findMediaFiles(getApplicationContext());
//                //Toast.makeText(getApplicationContext(),mFiles.toString()+"",Toast.LENGTH_LONG).show();
//                recyclerView.setAdapter(new GalleryAdapter(getApplicationContext(), mFiles));
//                recyclerView.setVisibility(View.VISIBLE);
//                mylayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void showImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri){
        //Toast.makeText(getContext(),"Get file extension",Toast.LENGTH_SHORT).show();
        ContentResolver contentResolver=getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void sendImage(final String sender, final String receiver, final String time, final String nullMessage){
//        final ProgressDialog progressDialog=new ProgressDialog(getApplicationContext());
//        progressDialog.setMessage("Uploading");
//        progressDialog.show();

        if(myImages!=null){

            final StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(myImages));
            uploadPhoto=fileReference.putFile(myImages);
            uploadPhoto.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then( Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){

                        throw task.getException();

                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>(){
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Uri downloadUri= (Uri) task.getResult();
                        String myUri=downloadUri.toString();

                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chats");

                        String chat_id=reference.push().getKey();

                        HashMap<String ,Object> hashMap=new HashMap<>();
                        hashMap.put("sender",sender);
                        hashMap.put("receiver",receiver);
                        hashMap.put("message",nullMessage);
                        hashMap.put("image",myUri);
                        hashMap.put("isseen",false);
                        hashMap.put("sendtime",time);
                        hashMap.put("chatid",chat_id);

                        reference.child(chat_id).setValue(hashMap);
//                        HashMap<String,Object> map=new HashMap<>();
//                        map.put("picture",myUri);
//                        databaseReference.updateChildren(map);
//                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_SHORT).show();

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

                    }else {
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"No image selected",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            Log.i("REQUEST CODE", requestCode+"");
            Log.i("RELULT CODE", resultCode+"");
            myImages=data.getData();

            if(uploadPhoto!=null&&uploadPhoto.isInProgress()){
//                Log.i("aung", "REACHED EEE");
                Toast.makeText(getApplicationContext(),"Upload is in progress",Toast.LENGTH_SHORT).show();
            }
            else {

                show_image.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(myImages).into(show_image);
//                Toast.makeText(getApplicationContext(),"Upload Start",Toast.LENGTH_LONG).show();
//                Log.i("aung", "ReachUpload()call");
//                uploadImage();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    
    
    //for notification
    public void updateToken(String token){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken=new Token(token);
        reference.child(mUID).setValue(mToken);
    }
    private void checkUserStatus(){
        FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
        if(fuser!=null){
            mUID=fuser.getUid();
            SharedPreferences sp=getSharedPreferences("noti_auth_user",MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("Current_USERID",mUID);
            editor.apply();
        }else {
            Toast.makeText(getApplicationContext(),"No user",Toast.LENGTH_SHORT).show();
        }
    }
}