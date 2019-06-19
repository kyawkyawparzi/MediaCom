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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import pmt.kyawkyaw.myapp.mediacom.model.User;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    private static final int IMAGE_REQUEST=999;

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
        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);

        storageReference= FirebaseStorage.getInstance().getReference("uploads");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                Log.i("URL",user.getPicture());
                if(user.getPicture().equals("default")){
                    Log.i("URL","equal default");
                    profile_image.setImageResource(R.drawable.person_blue);
                }else {
                    Log.i("URL","not equal default");
                    Glide.with(ProfileActivity.this.getApplicationContext()).load(user.getPicture()).into(profile_image);
                }
            }
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openImage();
            }
        });
    }
    private void openImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private void uploadImage() {
        final ProgressDialog progressDialog=new ProgressDialog(getApplicationContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if(imageUri!=null){

            //Log.i("aung", "one");
            final StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask=fileReference.putFile(imageUri);
            //Log.i("aung", "two");
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then( Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){

                        throw task.getException();

                    }
                    //Log.i("aung", "three");
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>(){
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Uri downloadUri= (Uri) task.getResult();
                        String myUri=downloadUri.toString();
                        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("picture",myUri);
                        databaseReference.updateChildren(map);
                        progressDialog.dismiss();
                        //  Log.i("url", myUri);
                        Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"onFailureState"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"No image selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("URL", "onactivityresult()");
        if(requestCode==IMAGE_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            Log.i("REQUEST CODE", requestCode+"");
            Log.i("RELULT CODE", resultCode+"");
            imageUri=data.getData();

            if(uploadTask!=null&&uploadTask.isInProgress()){
                //Log.i("aung", "REACHED EEE");
                Toast.makeText(getApplicationContext(),"Upload is in progress",Toast.LENGTH_SHORT).show();
            }
            else {
                Log.i("aung", "ReachUploadImage()call");
                uploadImage();
            }
        }
    }

    private String getFileExtension(Uri uri){
        //Toast.makeText(getApplicationContext(),"Get file extension",Toast.LENGTH_SHORT).show();
        ContentResolver contentResolver=getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
}