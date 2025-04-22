package com.example.entregable1.paypalrest;

import android.util.Base64;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PayPalApiClient {
    private static final String BASE_URL_SANDBOX = "https://api.sandbox.paypal.com/";
    private static final String BASE_URL_PRODUCTION = "https://api.paypal.com/";

    private static final String CLIENT_ID = "tu_client_id_de_paypal";
    private static final String SECRET = "tu_secret_de_paypal";

    private static PayPalApiClient instance;
    private PayPalApiService apiService;
    private String accessToken;

    private PayPalApiClient(boolean isSandbox) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        String baseUrl = isSandbox ? BASE_URL_SANDBOX : BASE_URL_PRODUCTION;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(PayPalApiService.class);
    }

    public static synchronized PayPalApiClient getInstance(boolean isSandbox) {
        if (instance == null) {
            instance = new PayPalApiClient(isSandbox);
        }
        return instance;
    }

    // Obtener token de acceso
    public void getAccessToken(final AuthCallback callback) {
        String credentials = CLIENT_ID + ":" + SECRET;
        String base64Auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        apiService.getAccessToken(base64Auth, "client_credentials").enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    accessToken = response.body().getAccess_token();
                    callback.onSuccess(accessToken);
                } else {
                    callback.onError("Error de autenticación: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    // Crear un pago
    public void createPayment(CreatePaymentRequest request, final PaymentCallback callback) {
        if (accessToken == null) {
            callback.onError("Token de acceso no disponible");
            return;
        }

        String authHeader = "Bearer " + accessToken;
        apiService.createPayment(authHeader, request).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error al crear pago: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    // Interfaces para callbacks
    public interface AuthCallback {
        void onSuccess(String accessToken);
        void onError(String errorMessage);
    }

    public interface PaymentCallback {
        void onSuccess(PaymentResponse paymentResponse);
        void onError(String errorMessage);
    }
}
