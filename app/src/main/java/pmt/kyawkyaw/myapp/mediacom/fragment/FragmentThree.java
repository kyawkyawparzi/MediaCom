package pmt.kyawkyaw.myapp.mediacom.fragment;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import pmt.kyawkyaw.myapp.mediacom.R;
import pmt.kyawkyaw.myapp.mediacom.model.User;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentThree extends Fragment {

    ImageView profile_image;
    TextView username;

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;


    private static final int IMAGE_REQUEST=1;
    StorageReference storageReference;
    private Uri imageUri;
    private StorageTask uploadTask;


    public FragmentThree() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_three, container, false);
        profile_image=view.findViewById(R.id.profile_image_up);
        username=view.findViewById(R.id.username);


        storageReference= FirebaseStorage.getInstance().getReference("uploads");


        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

//                Log.i("URL",user.getPicture());

                if(user.getPicture().equals("default")){
                    Log.i("URL","equal default");
                    profile_image.setImageResource(R.drawable.person_blue);
                }else {
                    Log.i("URL","not equal default");
                    Glide.with(FragmentThree.this.getContext()).load(user.getPicture()).into(profile_image);
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
        return view;
    }

    private void openImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }


    private void uploadImage(){
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if(imageUri!=null){

//            Log.i("aung", "one");
            final StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask=fileReference.putFile(imageUri);
            Log.i("aung", "two");
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then( Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){

                        throw task.getException();

                    }
                    Log.i("aung", "three");
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
                        Toast.makeText(getContext(),"Successful",Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(),"No image selected",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            Log.i("REQUEST CODE", requestCode+"");
            Log.i("RELULT CODE", resultCode+"");
            imageUri=data.getData();

            if(uploadTask!=null&&uploadTask.isInProgress()){
//                Log.i("aung", "REACHED EEE");
                Toast.makeText(getContext(),"Upload is in progress",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(),"Upload Start",Toast.LENGTH_LONG).show();
//                Log.i("aung", "ReachUpload()call");
                uploadImage();
            }

        }
    }
    private String getFileExtension(Uri uri){
//        Toast.makeText(getContext(),"Get file extension",Toast.LENGTH_SHORT).show();
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}
