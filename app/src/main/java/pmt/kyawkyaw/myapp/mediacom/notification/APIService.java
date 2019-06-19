package pmt.kyawkyaw.myapp.mediacom.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAH_HaGIQ:APA91bFZZK6PfPI9ZITd_X9inlxNZMk_m-BYdLyBLX3_2ddH2MLOz56SEoVWMY__VHDDtTYtCY08Pps7027GdirIIf640ewQRcWnWpGrvhKxUqj-5Fgy1bGL9kTBqkdJTnHCOEEabCPv"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
