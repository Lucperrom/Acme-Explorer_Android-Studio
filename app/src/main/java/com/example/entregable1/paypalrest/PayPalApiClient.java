package com.example.entregable1.paypalrest;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;

import com.example.entregable1.R;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PayPalApiClient {
    private static final String TAG = "PayPalApiClient";
    //public static final String CLIENT_ID = R.values.login_mail_error_1;
    //private static final String SECRET = "EIbRhct0yH9PZCplcPprmt11i_56KPSUkBnSD2uk9RUXUgAl_FXYMymM4rscBLY1maMYzrJQ5EpKRRhd";

    private static PayPalApiClient instance;
    private final PayPalApiService apiService;
    private String accessToken;
    private final boolean sandbox;

    private PayPalApiClient(boolean sandbox) {
        this.sandbox = sandbox;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(sandbox ? "https://api.sandbox.paypal.com/" : "https://api.paypal.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(PayPalApiService.class);
    }

    public static synchronized PayPalApiClient getInstance(boolean sandbox) {
        if (instance == null) {
            instance = new PayPalApiClient(sandbox);
        }
        return instance;
    }

    public void getAccessToken(final AuthCallback callback, Activity activity) {
        // Codificar credenciales en formato Base64
        String credentials = activity.getString(R.string.client_id) + ":" + activity.getString(R.string.secret) ;
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        // Solicitar token de acceso
        Call<PayPalApiService.AccessTokenResponse> call = apiService.getToken(auth, "client_credentials");
        call.enqueue(new Callback<PayPalApiService.AccessTokenResponse>() {
            @Override
            public void onResponse(Call<PayPalApiService.AccessTokenResponse> call, Response<PayPalApiService.AccessTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    accessToken = response.body().getAccess_token();
                    callback.onSuccess(accessToken);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                        Log.e(TAG, "Error al obtener token: " + errorBody);
                        callback.onError("Error de autenticación: " + response.code() + " - " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Error al leer respuesta de error", e);
                        callback.onError("Error de autenticación: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PayPalApiService.AccessTokenResponse> call, Throwable t) {
                Log.e(TAG, "Fallo en la solicitud de token", t);
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    public void createPayment(CreatePaymentRequest request, final PaymentCallback callback, Activity activity) {
        if (accessToken == null) {
            getAccessToken(new AuthCallback() {
                @Override
                public void onSuccess(String token) {
                    // Una vez obtenido el token, crear el pago
                    executeCreatePayment(token, request, callback);
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError("Error al obtener token: " + errorMessage);
                }
            }, activity);
        } else {
            // Si ya tenemos un token, crear el pago directamente
            executeCreatePayment(accessToken, request, callback);
        }
    }

    private void executeCreatePayment(String token, CreatePaymentRequest request, final PaymentCallback callback) {
        String auth = "Bearer " + token;
        Call<PaymentResponse> call = apiService.createPayment(auth, request);

        call.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                        Log.e(TAG, "Error al crear pago: " + errorBody);
                        callback.onError("Error al crear pago: " + response.code() + " - " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Error al leer respuesta de error", e);
                        callback.onError("Error al crear pago: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Log.e(TAG, "Fallo en la solicitud de creación de pago", t);
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    public void executePayment(String paymentId, String payerId, final PaymentCallback callback, Activity activity) {
        if (accessToken == null) {
            getAccessToken(new AuthCallback() {
                @Override
                public void onSuccess(String token) {
                    // Una vez obtenido el token, ejecutar el pago
                    executePaymentWithToken(token, paymentId, payerId, callback);
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError("Error al obtener token: " + errorMessage);
                }
            }, activity);
        } else {
            // Si ya tenemos un token, ejecutar el pago directamente
            executePaymentWithToken(accessToken, paymentId, payerId, callback);
        }
    }

    private void executePaymentWithToken(String token, String paymentId, String payerId, final PaymentCallback callback) {
        String auth = "Bearer " + token;
        PayPalApiService.PayerIdRequest payerIdRequest = new PayPalApiService.PayerIdRequest(payerId);

        Call<PaymentResponse> call = apiService.executePayment(auth, paymentId, payerIdRequest);

        call.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                        Log.e(TAG, "Error al ejecutar pago: " + errorBody);
                        callback.onError("Error al ejecutar pago: " + response.code() + " - " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Error al leer respuesta de error", e);
                        callback.onError("Error al ejecutar pago: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Log.e(TAG, "Fallo en la solicitud de ejecución de pago", t);
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    // Interfaces para manejar callbacks
    public interface AuthCallback {
        void onSuccess(String accessToken);
        void onError(String errorMessage);
    }

    public interface PaymentCallback {
        void onSuccess(PaymentResponse paymentResponse);
        void onError(String errorMessage);
    }
}