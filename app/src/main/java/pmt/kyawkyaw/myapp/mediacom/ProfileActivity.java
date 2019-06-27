package pmt.kyawkyaw.myapp.mediacom;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;


import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import pmt.kyawkyaw.myapp.mediacom.model.User;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView profile_image,back_arrow;
    TextView username;
    private static final int IMAGE_REQUEST=999;

    RelativeLayout logout_layout;

    //online offline value event listener;
    ValueEventListener listener;

    //firebase
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    private Uri imageUri;
    private StorageTask uploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_image=findViewById(R.id.profile_image_profile);
        username=findViewById(R.id.username);
        logout_layout=findViewById(R.id.logout_layout);
        back_arrow=findViewById(R.id.back_arrow);
        storageReference= FirebaseStorage.getInstance().getReference("uploads");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        //Toast.makeText(getApplicationContext(),firebaseUser.getUid()+"",Toast.LENGTH_LONG).show();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                //Log.i("URL",user.getPicture());
                if(user.getPicture().equals("default")){
                    //Log.i("URL","equal default");
                    profile_image.setImageResource(R.drawable.person_blue);
                }else {
                    //Log.i("URL","not equal default");
                    Glide.with(ProfileActivity.this.getApplicationContext()).load(user.getPicture()).into(profile_image);
                }
            }
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        logout_layout.setOnClickListener(this);
        profile_image.setOnClickListener(this);
        back_arrow.setOnClickListener(this);
    }
//    private void openImage() {
//        Intent intent=new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent,IMAGE_REQUEST);
//    }
//    private void uploadImage() {
//        final ProgressDialog progressDialog=new ProgressDialog(getApplicationContext());
//        progressDialog.setMessage("Uploading");
//        progressDialog.show();
//        if(imageUri!=null){
//            //Log.i("aung", "one");
//            final StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
//            uploadTask=fileReference.putFile(imageUri);
//            //Log.i("aung", "two");
//            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then( Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if(!task.isSuccessful()){
//                        throw task.getException();
//                    }
//                    //Log.i("aung", "three");
//                    return fileReference.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>(){
//                @Override
//                public void onComplete(@NonNull Task task) {
//                    if(task.isSuccessful()){
//                        Uri downloadUri= (Uri) task.getResult();
//                        String myUri=downloadUri.toString();
//                        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//                        HashMap<String,Object> map=new HashMap<>();
//                        map.put("picture",myUri);
//                        databaseReference.updateChildren(map);
//                        progressDialog.dismiss();
//                        //  Log.i("url", myUri);
//                        Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_SHORT).show();
//                    }else {
//                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getApplicationContext(),"onFailureState"+e.getMessage(),Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                }
//            });
//        }else {
//            Toast.makeText(getApplicationContext(),"No image selected",Toast.LENGTH_SHORT).show();
//        }
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.i("URL", "onactivityresult()");
//        if(requestCode==IMAGE_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
//            Log.i("REQUEST CODE", requestCode+"");
//            Log.i("RELULT CODE", resultCode+"");
//            imageUri=data.getData();
//
//            if(uploadTask!=null&&uploadTask.isInProgress()){
//                //Log.i("aung", "REACHED EEE");
//                Toast.makeText(getApplicationContext(),"Upload is in progress",Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Log.i("aung", "ReachUploadImage()call");
//                uploadImage();
//            }
//        }
//    }
//    private String getFileExtension(Uri uri){
//        //Toast.makeText(getApplicationContext(),"Get file extension",Toast.LENGTH_SHORT).show();
//        ContentResolver contentResolver=getApplicationContext().getContentResolver();
//        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profile_image_profile:

                break;
            case R.id.logout_layout:
                status("offline");
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                break;
            case R.id.back_arrow:
                startActivity(new Intent(getApplicationContext(),MainView.class));
                finish();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainView.class));
        finish();
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
}