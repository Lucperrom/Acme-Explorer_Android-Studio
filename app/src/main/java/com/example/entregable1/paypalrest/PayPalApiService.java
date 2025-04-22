package com.example.entregable1.paypalrest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PayPalApiService {
    @FormUrlEncoded
    @POST("v1/oauth2/token")
    Call<AuthResponse> getAccessToken(
            @Header("Authorization") String authHeader,
            @Field("grant_type") String grantType
    );

    @POST("v1/payments/payment")
    Call<PaymentResponse> createPayment(
            @Header("Authorization") String authHeader,
            @Body CreatePaymentRequest paymentRequest
    );

/*    @POST("v1/payments/payment/{paymentId}/execute")
    Call<PaymentResponse> executePayment(
            @Header("Authorization") String authHeader,
            @Path("paymentId") String paymentId,
            @Body ExecutePaymentRequest executeRequest
    );
    *(/
 */
}
