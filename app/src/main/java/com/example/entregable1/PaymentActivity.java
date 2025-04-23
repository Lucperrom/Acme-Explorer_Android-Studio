package com.example.entregable1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.entregable1.paypalrest.CreatePaymentRequest;
import com.example.entregable1.paypalrest.PayPalApiClient;
import com.example.entregable1.paypalrest.PaymentResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {
    private PayPalApiClient paypalClient;
    private String paymentIdParaEjecutar;

    private double precioTrip = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        paypalClient = PayPalApiClient.getInstance(true); // Asegúrate de usar false en producción
        SharedPreferences sharedPreferences = getSharedPreferences(TripDetailActivity.PREF_NAME, MODE_PRIVATE);
        String json = sharedPreferences.getString(TripDetailActivity.PRECIO_KEY, null);
        //String precioString = getIntent().getStringExtra("PRECIO_TRIP");
        //Log.i("PaymentActivity", "Precio del viaje recibido: " + precioString);
        if(getIntent().getData() != null){
            Log.i("PaymentActivity", getIntent().getData().toString());
            onNewIntent(getIntent());
            return;
        }
        //precioTrip = Double.parseDouble(precioString);
        if (json != null) {
            Gson gson = new Gson();
            Type tipoPrecio = new TypeToken<Double>() {}.getType();
            precioTrip = gson.fromJson(json, tipoPrecio);

        } else {
            precioTrip = 0.0;
        }
        Log.i("PaymentActivity", "Precio del viaje convertido a double: " + precioTrip);
        // Iniciar el proceso de pago al crear la actividad
        iniciarPagoPaypal();
    }

    private void iniciarPagoPaypal() {
        paypalClient.getAccessToken(new PayPalApiClient.AuthCallback() {
            @Override
            public void onSuccess(String accessToken) {
                crearSolicitudDePago();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(PaymentActivity.this, "Error al obtener el token de PayPal: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }, this);
    }

    private void crearSolicitudDePago() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setIntent("sale");

        CreatePaymentRequest.Payer payer = new CreatePaymentRequest.Payer();
        payer.setPayment_method("paypal");
        request.setPayer(payer);


        CreatePaymentRequest.Transaction transaction = new CreatePaymentRequest.Transaction();
        CreatePaymentRequest.Amount amount = new CreatePaymentRequest.Amount();
        amount.setCurrency("EUR");
        amount.setTotal(String.format(Locale.US, "%.2f", this.precioTrip));
        transaction.setAmount(amount);
        transaction.setDescription("Pago del viaje");

        List<CreatePaymentRequest.Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        request.setTransactions(transactions);

        CreatePaymentRequest.RedirectUrls redirectUrls = new CreatePaymentRequest.RedirectUrls();
        redirectUrls.setReturn_url("com.example.entregable1://paymentsuccess");
        redirectUrls.setCancel_url("com.example.entregable1://paymentcancel");
        request.setRedirect_urls(redirectUrls);

        paypalClient.createPayment(request, new PayPalApiClient.PaymentCallback() {
            @Override
            public void onSuccess(PaymentResponse paymentResponse) {
                String approvalUrl = null;
                for (PaymentResponse.Link link : paymentResponse.getLinks()) {
                    if ("approval_url".equals(link.getRel())) {
                        approvalUrl = link.getHref();
                        break;
                    }
                }

                if (approvalUrl != null) {
                    paymentIdParaEjecutar = paymentResponse.getId(); // Guardar el ID del pago
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(approvalUrl));
                    startActivity(intent);
                    finish();



                } else {
                    Toast.makeText(PaymentActivity.this, "Error: No se encontró la URL de aprobación", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(PaymentActivity.this, "Error al crear el pago: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri data = intent.getData();
        if (data != null) {
            String host = data.getHost();

            if ("paymentsuccess".equals(host)) {
                String paymentId = data.getQueryParameter("paymentId");
                String payerId = data.getQueryParameter("PayerID");

                if (paymentId != null && payerId != null) {
                    ejecutarPago(paymentId, payerId);
                } else {
                    Toast.makeText(this, "Error: Parámetros de pago incompletos", Toast.LENGTH_LONG).show();
                }
            } else if ("paymentcancel".equals(host)) {
                Toast.makeText(this, "Pago cancelado por el usuario", Toast.LENGTH_LONG).show();
                // Aquí podrías volver a la pantalla de detalles del viaje o mostrar un mensaje
            }
        }
    }

    private void ejecutarPago(String paymentId, String payerId) {
        paypalClient.executePayment(paymentId, payerId, new PayPalApiClient.PaymentCallback() {
            @Override
            public void onSuccess(PaymentResponse paymentResponse) {
                runOnUiThread(() -> {
                    if ("approved".equals(paymentResponse.getState())) {
                        Toast.makeText(PaymentActivity.this, "¡Pago completado con éxito!", Toast.LENGTH_LONG).show();
                        // Aquí puedes realizar acciones como actualizar la UI, la base de datos, etc.
                        // Quizás finalizar esta actividad y volver a la de detalles del viaje con un indicador de éxito.
                        Intent intent = new Intent(PaymentActivity.this, TripDetailActivity.class);
                        intent.putExtra("PAGADO", "pagado");
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(PaymentActivity.this, "El estado del pago no es 'approved': " + paymentResponse.getState(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(PaymentActivity.this, "Error al ejecutar el pago: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        }, this);
    }
}