package com.example.entregable1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.entregable1.paypalrest.CreatePaymentRequest;
import com.example.entregable1.paypalrest.PayPalApiClient;
import com.example.entregable1.paypalrest.PaymentResponse;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private PayPalApiClient paypalClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Inicializar el cliente (true para sandbox, false para producción)
        paypalClient = PayPalApiClient.getInstance(true);

        // Obtener token primero
        paypalClient.getAccessToken(new PayPalApiClient.AuthCallback() {
            @Override
            public void onSuccess(String accessToken) {
                // Ahora podemos crear un pago
                createPayment();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(PaymentActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createPayment() {
        // Crear el objeto de solicitud
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setIntent("sale");

        CreatePaymentRequest.Payer payer = new CreatePaymentRequest.Payer();
        payer.setPayment_method("paypal");
        request.setPayer(payer);

        CreatePaymentRequest.Transaction transaction = new CreatePaymentRequest.Transaction();
        CreatePaymentRequest.Amount amount = new CreatePaymentRequest.Amount();
        amount.setCurrency("USD");
        amount.setTotal("10.00");
        transaction.setAmount(amount);
        transaction.setDescription("Compra en mi aplicación");

        List<CreatePaymentRequest.Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        request.setTransactions(transactions);

        CreatePaymentRequest.RedirectUrls redirectUrls = new CreatePaymentRequest.RedirectUrls();
        redirectUrls.setReturn_url("com.tuapp://paymentsuccess");
        redirectUrls.setCancel_url("com.tuapp://paymentcancel");
        request.setRedirect_urls(redirectUrls);

        // Llamar a la API
        paypalClient.createPayment(request, new PayPalApiClient.PaymentCallback() {
            @Override
            public void onSuccess(PaymentResponse paymentResponse) {
                // Buscar la URL de aprobación
                String approvalUrl = null;
                for (PaymentResponse.Link link : paymentResponse.getLinks()) {
                    if (link.getRel().equals("approval_url")) {
                        approvalUrl = link.getHref();
                        break;
                    }
                }

                if (approvalUrl != null) {
                    // Abrir el navegador para que el usuario apruebe el pago
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(approvalUrl));
                    startActivity(intent);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(PaymentActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}