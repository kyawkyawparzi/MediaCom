package pmt.kyawkyaw.myapp.mediacom;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

//  String package_name="pmt.kyawkyaw.myapp.easy2conn";
    Button btn_login,btn_signin;
    EditText login_email,login_passwrod;

    //firebase
    FirebaseAuth auth;

    private PackageManager pm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btn_login=findViewById(R.id.btn_login);
        btn_signin=findViewById(R.id.btn_signup);
        login_email=findViewById(R.id.login_email);
        login_passwrod=findViewById(R.id.login_password);

        btn_login.setOnClickListener(this);
        btn_signin.setOnClickListener(this);

        //firebase
        auth= FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_signup:
                startActivity(new Intent(getApplicationContext(),SignUp.class));
                break;
        }
//                pm=this.getPackageManager();
//                Intent gotoMediaCom = pm.getLaunchIntentForPackage(package_name);
//                if (gotoMediaCom!=null){
//                    this.startActivity(gotoMediaCom);
    }

    private void login() {
        String get_login_email = login_email.getText().toString();
        String get_login_password = login_passwrod.getText().toString();
        if (TextUtils.isEmpty(get_login_email) || TextUtils.isEmpty(get_login_password)) {
            Toast.makeText(getApplicationContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
        } else {
            auth.signInWithEmailAndPassword(get_login_email, get_login_password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(), MainView.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Your password or email was something wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}