package pmt.kyawkyaw.myapp.mediacom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.victor.loading.newton.NewtonCradleLoading;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

//  String package_name="pmt.kyawkyaw.myapp.easy2conn";
    Button btn_login;
    TextView txt_create;
    EditText login_email,login_passwrod;
    CheckBox remember;

    //firebase
    FirebaseAuth auth;

    //Sharepreference
    SharedPreferences sharedPreferences;

    //progress
    private NewtonCradleLoading newtonCradleLoading;
    private Button button;

    //layout
    LinearLayout my_layout,my_layout_welcome_next,progress_show_hide_this;
    TextView login_account;

    //backpress ctrl
    String ctrl_value;

    private PackageManager pm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(Color.WHITE);

        btn_login=findViewById(R.id.btn_login);
        txt_create=findViewById(R.id.create_one);
        login_email=findViewById(R.id.login_email);
        login_passwrod=findViewById(R.id.login_password);
        remember=findViewById(R.id.remember_me);
        newtonCradleLoading=findViewById(R.id.newton_cradle_loading);
        my_layout=findViewById(R.id.hide_layout);
        login_account=findViewById(R.id.login_account);
        my_layout_welcome_next=findViewById(R.id.my_layout_welcome_next);
        progress_show_hide_this=findViewById(R.id.layout_when_progress_click_disable);

        login_account.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        txt_create.setOnClickListener(this);
        remember.setOnClickListener(this);

        //firebase
        auth= FirebaseAuth.getInstance();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                ctrl_value="btnLogin";
                my_layout.setVisibility(View.GONE);
                my_layout_welcome_next.setVisibility(View.VISIBLE);
                progress_show_hide_this.setVisibility(View.GONE);
                newtonCradleLoading.setVisibility(View.VISIBLE);
                newtonCradleLoading.start();
                login();
                break;
            case R.id.create_one:
                startActivity(new Intent(getApplicationContext(),SignUp.class));
                this.finish();
                break;
            case R.id.login_account:
                ctrl_value="loginAccount";
                my_layout_welcome_next.setVisibility(View.GONE);
                my_layout.setVisibility(View.VISIBLE);
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
            newtonCradleLoading.stop();
            my_layout.setVisibility(View.VISIBLE);
            progress_show_hide_this.setVisibility(View.VISIBLE);
            my_layout_welcome_next.setVisibility(View.GONE);
            newtonCradleLoading.setVisibility(View.GONE);
        } else {
            auth.signInWithEmailAndPassword(get_login_email, get_login_password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if(remember.isChecked()){
                                    String get_email=login_email.getText().toString();
                                    String get_password=login_passwrod.getText().toString();

                                    sharedPreferences=getSharedPreferences("mydatampt",MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString("email",get_email);
                                    editor.putString("password",get_password);
                                    editor.commit();
                                    //Toast.makeText(getApplicationContext(),"Share reach",Toast.LENGTH_LONG).show();
                                }
                                startActivity(new Intent(getApplicationContext(), MainView.class));
                                newtonCradleLoading.stop();
                                finish();
                            } else {
                                newtonCradleLoading.stop();
                                my_layout.setVisibility(View.VISIBLE);
                                newtonCradleLoading.setVisibility(View.GONE);
                                progress_show_hide_this.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "Your password or email was something wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        login_passwrod.setText("");
        my_layout.setVisibility(View.GONE);
        my_layout.setVisibility(View.GONE);
        progress_show_hide_this.setVisibility(View.VISIBLE);
        my_layout_welcome_next.setVisibility(View.VISIBLE);
        newtonCradleLoading.setVisibility(View.GONE);
        sharedPreferences=getSharedPreferences("mydatampt",MODE_PRIVATE);
        String name1=sharedPreferences.getString("email","");
        login_email.setText(name1);
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if(ctrl_value.equals("loginAccount")){
            my_layout.setVisibility(View.GONE);
            my_layout_welcome_next.setVisibility(View.VISIBLE);
            ctrl_value="";
        }
//        else if(ctrl_value.equals("btnLogin")){
//            my_layout.setVisibility(View.GONE);
//            my_layout_welcome_next.setVisibility(View.VISIBLE);
//            progress_show_hide_this.setVisibility(View.VISIBLE);
//            ctrl_value="";
//        }
        else {
            finish();
        }
    }
}