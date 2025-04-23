package com.example.entregable1;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.entregable1.entity.Enlace;
import com.example.entregable1.entity.Trip;
import com.example.entregable1.entity.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AuthActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0x152;
    private FirebaseAuth mAuth;
    private Button signinButtonGoogle, siginButtonMail, loginButtonSingUp;
    private TextInputLayout loginEmailParent, loginPassParent;
    private AutoCompleteTextView loginEmail, loginPass;
    private ValueEventListener valueEventListener;
    private FirebaseDatabaseService firebaseDatabaseService;
    EnlaceAdapter enlaceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mAuth = FirebaseAuth.getInstance();
        signinButtonGoogle = (Button) findViewById(R.id.login_button_google);
        siginButtonMail = (Button) findViewById(R.id.login_button_mail);
        loginButtonSingUp = (Button) findViewById(R.id.login_button_register);
        loginEmail = (AutoCompleteTextView) findViewById(R.id.login_email_et);
        loginPass = (AutoCompleteTextView) findViewById(R.id.login_pass_et);
        loginEmailParent = (TextInputLayout) findViewById(R.id.login_email);
        loginPassParent = (TextInputLayout) findViewById(R.id.login_pass);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_client_id))
                .requestEmail()
                .build();

        signinButtonGoogle.setOnClickListener(l -> attemptLoginGoogle(googleSignInOptions));
        siginButtonMail.setOnClickListener(l -> attemptLoginEmail());
        loginButtonSingUp.setOnClickListener(l -> redirectSignUpActivity());



    }

    private void redirectSignUpActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra(SignupActivity.EMAIL_PARAM, loginEmail.getText().toString());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = result.getResult(ApiException.class);
                assert account != null;
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                if(mAuth == null) mAuth = FirebaseAuth.getInstance();
                if(mAuth != null){
                    mAuth.signInWithCredential(credential).addOnCompleteListener(this,task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            checkUserDatabaseLogin(user);
                        } else {
                            showErrorDialogMail();
                        }
                    });
                }else{
                    showGooglePlayServicesError();
                }

            }catch (ApiException exception){
                showErrorDialogMail();
            }
        }
    }

    private void attemptLoginGoogle(GoogleSignInOptions googleSignInOptions) {
        GoogleSignInClient googleSignIn = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signInIntent = googleSignIn.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void attemptLoginEmail() {
            loginEmailParent.setError(null);
            loginPassParent.setError(null);

            if(loginEmail.getText().toString().isEmpty()){
                loginEmailParent.setErrorEnabled(true);
                loginEmailParent.setError(getString(R.string.login_mail_error_1));
            } else if(loginPass.getText().toString().isEmpty()){
                loginPassParent.setErrorEnabled(true);
                loginPassParent.setError(getString(R.string.login_mail_error_2));
            } else {
                signInEmail();
            }
        }

    private void signInEmail() {
        if (mAuth == null){
            mAuth= FirebaseAuth.getInstance();
        }
        if (mAuth != null){
            mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPass.getText().toString()).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful() || task.getResult().getUser() == null){
                    showErrorDialogMail();
                }else if (!task.getResult().getUser().isEmailVerified()){
                    showErrorEmailVerified(task.getResult().getUser());
                }else{
                    FirebaseUser user = task.getResult().getUser();
                    checkUserDatabaseLogin(user);
                }
            });
        }else{
            showGooglePlayServicesError();
        }
    }

    private void checkUserDatabaseLogin(FirebaseUser user) {
        //Dummy
        //TODO: complete
        FirestoreService firestoreService = FirestoreService.getServiceInstance();
        firestoreService.getUser(user.getUid(), (documentSnapshot, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Error checking user", error);
                        return;
                    }

                    if (documentSnapshot == null || !documentSnapshot.exists()) {
                        // El usuario no existe en Firestore, crearlo
                        User newUser = new User();
                        newUser.setUid(user.getUid());

                        firestoreService.saveUser(newUser);
                    }
                });

        Toast.makeText(this, String.format(getString(R.string.login_completed), user.getEmail()), Toast.LENGTH_SHORT).show();
        enlaceAdapter = new EnlaceAdapter(Enlace.generaEnlaces(), this);
        enlaceAdapter.notifyDataSetChanged();
        startActivity(new Intent(this, DisponibleSeleccionado.class));
        finish();

        firebaseDatabaseService = FirebaseDatabaseService.getServiceInstance();
        //startActivity(new Intent(this, FirebaseStorageExample.class));

    }

   /* @Override
    protected void onPause() {
        super.onPause();
        if(firebaseDatabaseService != null && valueEventListener != null)
            firebaseDatabaseService.getTrip().removeEventListener(valueEventListener);
    }
    */

    private void showErrorEmailVerified(FirebaseUser user) {
        hideLoginButton(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.login_verified_mail_error)
                .setPositiveButton(R.string.login_verified_mail_error_ok, ((dialog, which) -> {
                    user.sendEmailVerification().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            Snackbar.make(loginEmail, getString(R.string.login_verified_mail_error_sent), Snackbar.LENGTH_SHORT).show();
                        }else{
                            Snackbar.make(loginEmail, getString(R.string.login_verified_mail_error_no_sent), Snackbar.LENGTH_SHORT).show();
                        }

                    });
                })).setNegativeButton(R.string.login_verified_mail_error_cancel, (dialog,which) -> {
                }).show();
    }

    private void hideLoginButton(boolean hide) {
        TransitionSet transitionSet = new TransitionSet();
        Transition layoutFade = new AutoTransition();
        layoutFade.setDuration(1000);
        transitionSet.addTransition(layoutFade);

        if(hide){
            TransitionManager.beginDelayedTransition(findViewById(R.id.login_main_layout), transitionSet);
            signinButtonGoogle.setVisibility(View.GONE);
            siginButtonMail.setVisibility(View.GONE);
            loginButtonSingUp.setVisibility(View.GONE);
            loginEmailParent.setEnabled(false);
            loginPassParent.setEnabled(false);
        }else{
            TransitionManager.beginDelayedTransition(findViewById(R.id.login_main_layout), transitionSet);
            signinButtonGoogle.setVisibility(View.VISIBLE);
            siginButtonMail.setVisibility(View.VISIBLE);
            loginButtonSingUp.setVisibility(View.VISIBLE);
            loginEmailParent.setEnabled(true);
            loginPassParent.setEnabled(true);
        }
    }

    private void showErrorDialogMail() {
        hideLoginButton(false);
        Snackbar.make(siginButtonMail, getString(R.string.login_mail_access_error), Snackbar.LENGTH_SHORT).show();
    }

    private void showGooglePlayServicesError() {
        hideLoginButton(false);
        Snackbar.make(loginButtonSingUp, getString(R.string.login_google_play_services_error), Snackbar.LENGTH_LONG).setAction(R.string.login_download_gps, v -> {
            try{
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.gps_download_url))));
            }catch(Exception exception){
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.market_download_url))));
            }
        }).show();
    }
}

