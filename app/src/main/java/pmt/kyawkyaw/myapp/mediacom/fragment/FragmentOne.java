package pmt.kyawkyaw.myapp.mediacom.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import pmt.kyawkyaw.myapp.mediacom.R;
import pmt.kyawkyaw.myapp.mediacom.adapter.UserDataAdapt;
import pmt.kyawkyaw.myapp.mediacom.model.Chatlist;
import pmt.kyawkyaw.myapp.mediacom.model.User;
import pmt.kyawkyaw.myapp.mediacom.notification.Token;
public class FragmentOne extends Fragment {

    private RecyclerView recyclerView;
    private UserDataAdapt userdataadapter;
    private List<User> getUser;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private List<Chatlist> userList;

    public FragmentOne() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_one, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getUser=new ArrayList<>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        userList=new ArrayList<>();

        databaseReference= FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chatlist chatlist=snapshot.getValue(Chatlist.class);
                    userList.add(chatlist);
                }
                chatList();
            }
            private void chatList() {
                getUser=new ArrayList<>();
                databaseReference=FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        getUser.clear();
                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            User user=snapshot.getValue(User.class);
                            for(Chatlist chatlist:userList){
                                if(user.getId().equals(chatlist.getId())){
                                    getUser.add(user);
                                }
                            }
                        }
                        userdataadapter=new UserDataAdapt(getContext(),getUser,true);
                        recyclerView.setAdapter(userdataadapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //token
//        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }
    //token
//    private void updateToken(String token){
//        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Tokens");
//        Token token1=new Token(token);
//        databaseReference.child(firebaseUser.getUid()).setValue(token1);
//    }
}