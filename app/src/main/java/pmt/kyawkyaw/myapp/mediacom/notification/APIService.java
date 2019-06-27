package pmt.kyawkyaw.myapp.mediacom.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAH_HaGIQ:APA91bEogg74Vh14-MAF9Q5k3ZDuDD3gM6BdVZRCnUGzIZVKS6RlZ3Jyi6FNgYZh1P7hcnR-k4r_TuqSb_niML-S5xOVaUlS50FWOiPcd5WOXLTzVWSdb3CZ8svqvAsGmNA5EwLfSGsx"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
