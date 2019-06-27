package pmt.kyawkyaw.myapp.mediacom.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pmt.kyawkyaw.myapp.mediacom.R;
import pmt.kyawkyaw.myapp.mediacom.adapter.UserDataAdapt;
import pmt.kyawkyaw.myapp.mediacom.model.User;


public class FragmentTwo extends Fragment {

    RecyclerView recycler_user;
    private List<User> data_adapt;
    private UserDataAdapt userDataAdapt;

    //search user
    //one=>if you search you need to create "search" child on database, coz this child will store letter will be lowercase
    //if you do "one" you need to change the value of send user's name to "lowercase" see on "onTextChanger()"
    //(in "readUserDataOnRecyclerUser()")you need to do if statement to search the user,if the text null will show all users,if you search your search data will show
    EditText search_user;
    public FragmentTwo() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_two, container, false);
        recycler_user=view.findViewById(R.id.recycler_view);
        recycler_user.setHasFixedSize(true);
        recycler_user.setLayoutManager(new LinearLayoutManager(getContext()));
        data_adapt=new ArrayList<>();
        readUserDataOnRecyclerUser();

        //search user
        search_user=view.findViewById(R.id.search);
        search_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void searchUsers(String search_text) {
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(search_text).endAt(search_text+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data_adapt.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User get_user=snapshot.getValue(User.class);
                    if(!get_user.getId().equals(firebaseUser.getUid())){
                        data_adapt.add(get_user);
                    }
                }
                userDataAdapt=new UserDataAdapt(getContext(),data_adapt,false);
                recycler_user.setAdapter(userDataAdapt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUserDataOnRecyclerUser() {
        //Log.i("load", "load read user");
        final FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dreference= FirebaseDatabase.getInstance().getReference("Users");
        dreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //you need to do if statement to search the user
                // don't be find user if null
                if(search_user.getText().toString().equals("")){

                    data_adapt.clear();
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        User fire_user_data=snapshot.getValue(User.class);

                        assert fire_user_data!=null;
                        assert fuser!=null;
                        //Log.i("load", "Load Data");
                        if(!fire_user_data.getId().equals(fuser.getUid())){
                            data_adapt.add(fire_user_data);
                        }
                    }
                    userDataAdapt=new UserDataAdapt(getContext(),data_adapt,false);
                    recycler_user.setAdapter(userDataAdapt);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
