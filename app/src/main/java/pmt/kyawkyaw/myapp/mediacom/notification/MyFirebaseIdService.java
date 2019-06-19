package pmt.kyawkyaw.myapp.mediacom.notification;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        String tokenRefresh=FirebaseInstanceId.getInstance().getToken();
        if(firebaseUser!=null){
            updateToken(tokenRefresh);
        }
    }

    private void updateToken(String tokenRefresh) {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token=new Token(tokenRefresh);
        databaseReference.child(firebaseUser.getUid()).setValue(token);
    }
}
