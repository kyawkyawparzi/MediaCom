package pmt.kyawkyaw.myapp.mediacom;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import pmt.kyawkyaw.myapp.mediacom.adapter.BackpressCtrl;
import pmt.kyawkyaw.myapp.mediacom.fragment.FragmentOne;
import pmt.kyawkyaw.myapp.mediacom.fragment.FragmentThree;
import pmt.kyawkyaw.myapp.mediacom.fragment.FragmentTwo;
import pmt.kyawkyaw.myapp.mediacom.model.User;

public class MainView extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    AppBarLayout appBarLayout;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private TextView mTextMessage;

    //fragments
     FragmentOne fone;
     FragmentTwo ftwo;
     FragmentThree fthree;

     //firebase
    FirebaseUser fuser;
    DatabaseReference reference;
    CircleImageView profile_image;
    BottomNavigationView my_view;

    private int get_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setStatusBarColor(getColor(R.color.colorWhiteDark));

        appBarLayout=findViewById(R.id.appbar);
        bottomNavigationView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        fone=new FragmentOne();
        ftwo=new FragmentTwo();
        fthree=new FragmentThree();
        navController= Navigation.findNavController(this,R.id.fragment_hosts);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().findItem(R.id.navigation_chat).setChecked(true);

        //load user profile
        loadProfile();
        profile_image=findViewById(R.id.profile_image);

        my_view=findViewById(R.id.nav_view);

        //scroll listener
        recyclerView=findViewById(R.id.recycler_view);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                finish();
            }
        });

    }

    private void loadProfile() {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User my_data=dataSnapshot.getValue(User.class);
                if(my_data.getPicture().equals("default")){
                    profile_image.setImageResource(R.drawable.person_blue);
                }else {
                    Glide.with(getApplicationContext()).load(my_data.getPicture()).into(profile_image);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        get_id = menuItem.getItemId();
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem mItem = bottomNavigationView.getMenu().getItem(i);
            boolean isChecked = mItem.getItemId() == menuItem.getItemId();
            menuItem.setChecked(isChecked);
        }
        switch (menuItem.getItemId()) {
                case R.id.navigation_chat:
                    navController.navigate(R.id.fragmentOne);
                    BackpressCtrl.ctrl="nav_chat";
                    break;
                case R.id.navigation_people:
                    navController.navigate(R.id.fragmentTwo);
                    BackpressCtrl.ctrl="nav_people";
                    //Toast.makeText(getApplicationContext(),"Fragment Two",Toast.LENGTH_LONG).show();
                    break;
                case R.id.navigation_public:
                    //Toast.makeText(getApplicationContext(),"Fragment Two",Toast.LENGTH_LONG).show();
                    navController.navigate(R.id.fragmentThree);
                    BackpressCtrl.ctrl="nav_public";
                    break;
            }
        return false;
    }
    //for online and offline
    private void status(String status){
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    //for online and offline
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
    @Override
    public void onBackPressed() {
        //.onBackPressed();
       if(!BackpressCtrl.ctrl.equals("nav_chat")) {
            navController.navigate(R.id.fragmentOne);
            BackpressCtrl.ctrl="nav_chat";
            bottomNavigationView.getMenu().findItem(R.id.navigation_chat).setChecked(true);
       }
       else {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
       }
    }
}