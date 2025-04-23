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
    Call<AccessTokenResponse> getToken(
            @Header("Authorization") String authorization,
            @Field("grant_type") String grantType
    );

    @POST("v1/payments/payment")
    Call<PaymentResponse> createPayment(
            @Header("Authorization") String authorization,
            @Body CreatePaymentRequest request
    );

    @POST("v1/payments/payment/{paymentId}/execute")
    Call<PaymentResponse> executePayment(
            @Header("Authorization") String authorization,
            @Path("paymentId") String paymentId,
            @Body PayerIdRequest payerId
    );

    class AccessTokenResponse {
        private String access_token;
        private String token_type;
        private String app_id;
        private int expires_in;

        public String getAccess_token() {
            return access_token;
        }
    }

    class PayerIdRequest {
        private String payer_id;

        public PayerIdRequest(String payer_id) {
            this.payer_id = payer_id;
        }
    }
}
