package pmt.kyawkyaw.myapp.mediacom.notification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String tokenRefersh= FirebaseInstanceId.getInstance().getToken();
        if(firebaseUser!=null){
            updateToken(tokenRefersh);
        }
    }

    private void updateToken(String tokenRefersh) {
        FirebaseUser firebaseUser1=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token=new Token(tokenRefersh);
        databaseReference.child(firebaseUser1.getUid()).setValue(token);
    }
}
