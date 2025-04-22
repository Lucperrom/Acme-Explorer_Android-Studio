package com.example.entregable1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    public static final String EMAIL_PARAM ="email_parameter";
    private EditText login_email_et, login_pass_et, login_pass_confirmation_et;

    private TextInputLayout login_email, login_pass, login_pass_confirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        String emailParam = getIntent().getStringExtra(EMAIL_PARAM);

        login_email_et = findViewById(R.id.login_email_et);
        login_pass_et = findViewById(R.id.login_pass_et);
        login_pass_confirmation_et = findViewById(R.id.login_pass_confirmation_et);
        login_email = (TextInputLayout) findViewById(R.id.login_email);
        login_pass = (TextInputLayout) findViewById(R.id.login_pass);
        login_pass_confirmation = (TextInputLayout) findViewById(R.id.login_pass_confirmation);

        login_email_et.setText(emailParam);
        findViewById(R.id.signup_button).setOnClickListener(l -> {
            if (login_email_et.getText().toString().isEmpty()) {
                login_email.setErrorEnabled(true);
                login_email.setError(getString(R.string.signup_error_user));
            } else if (login_pass_et.getText().toString().isEmpty()) {
                login_pass.setErrorEnabled(true);
                login_pass.setError(getString(R.string.signup_error_pass));
            } else if (login_pass_confirmation_et.getText().toString().isEmpty()) {
                login_pass_confirmation.setErrorEnabled(true);
                login_pass_confirmation.setError(getString(R.string.signup_error_pass));
            } else if (!login_pass_confirmation_et.getText().toString().equals(login_pass_et.getText().toString())) {
                login_pass.setErrorEnabled(true);
                login_pass.setError(getString(R.string.signup_error_pass_not_match));
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(login_email_et.getText().toString(), login_pass_et.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, getString(R.string.signup_created), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, DisponibleSeleccionado.class));
                    } else {
                        Toast.makeText(this, getString(R.string.signup_created_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}