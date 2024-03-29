package pmt.kyawkyaw.myapp.mediacom;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    Button register,cancel;
    EditText your_name,your_email,your_password;

    //firebase
    FirebaseAuth auth;
    DatabaseReference reference;
    CircleImageView back_arrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        register=findViewById(R.id.register);
        your_name=findViewById(R.id.name);
        your_email=findViewById(R.id.email);
        your_password=findViewById(R.id.password);
        cancel=findViewById(R.id.cancel);
        back_arrow=findViewById(R.id.back_arrow);

        this.getWindow().setStatusBarColor(getColor(R.color.colorWhiteDark));

        back_arrow.setOnClickListener(this);
        cancel.setOnClickListener(this);
        register.setOnClickListener(this);

        //firebase
        auth=FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                String get_name=your_name.getText().toString();
                String get_email=your_email.getText().toString();
                String get_password=your_password.getText().toString();
                if(TextUtils.isEmpty(get_name)||TextUtils.isEmpty(get_email)||TextUtils.isEmpty(get_password)){
                    Toast.makeText(getApplicationContext(),"All fields are required!",Toast.LENGTH_SHORT).show();
                }else if (get_password.length()<6){
                    Toast.makeText(getApplicationContext(),"Password must have at least six!",Toast.LENGTH_SHORT).show();
                }else {
                    register(get_name,get_email,get_password);
                }
                break;
            case R.id.cancel:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                this.finish();
                break;
            case R.id.back_arrow:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                this.finish();
                break;
        }
    }
    private void register(final String get_name, String get_email, String get_password) {
        auth.createUserWithEmailAndPassword(get_email,get_password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser fuser=auth.getCurrentUser();
                        assert fuser!=null;
                        String userID=fuser.getUid();
                        reference= FirebaseDatabase.getInstance().getReference("Users").child(userID);
                        HashMap<String,String> hashMap=new HashMap<>();

                        hashMap.put("id",userID);
                        hashMap.put("username",get_name);
                        hashMap.put("picture","default");
                        hashMap.put("status","offline");
                        hashMap.put("search",get_name.toLowerCase());

                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    startActivity(new Intent(getApplicationContext(),MainView.class));
                                }
                            }
                        });
                    }else {
                        Toast.makeText(getApplicationContext(),"This email was verified!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}