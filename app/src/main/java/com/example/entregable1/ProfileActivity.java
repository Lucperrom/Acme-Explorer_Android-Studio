package com.example.entregable1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Base64;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.entregable1.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {

    private TextView name,email,phone;
    private Button btnChangePhoto,btnSaveProfile;
    private ImageView imageProfile;
    private static FirebaseFirestore mDatabase;
    private static FirebaseUser firebaseUser;
    private static FirestoreService firestoreService;
    private static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name = findViewById(R.id.editName);
        email = findViewById(R.id.editEmail);
        phone = findViewById(R.id.editPhone);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        imageProfile = findViewById(R.id.imageProfile);
        mDatabase = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        EventListener<DocumentSnapshot> snapshotListener;
        firestoreService = FirestoreService.getServiceInstance();
        firestoreService.getUser(firebaseUser.getUid(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null && documentSnapshot.exists()){
                    user = documentSnapshot.toObject(User.class);
                    Log.i("Acme-Explorer", "Firestore lectura individual" + user.toString());
                    name.setText(user.getName() != null ? user.getName() : "");
                    phone.setText(user.getPhone() != null ? user.getPhone() : "");
                    loadProfileImage(user);
                } else if (error != null) {
                    Log.e("FirestoreError", "Error al obtener usuario", error);
                }
            }
        });
        email.setText(firebaseUser.getEmail());

        btnChangePhoto.setOnClickListener(view -> {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        });
        btnSaveProfile.setOnClickListener(view -> {
            mDatabase.collection("users").document(firebaseUser.getUid()).update("name", name.getText().toString());
            mDatabase.collection("users").document(firebaseUser.getUid()).update("phone", phone.getText().toString());
            Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
    // Método para convertir Base64 a Bitmap
    private Bitmap base64ToBitmap(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }

        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            Log.e("ImageConverter", "Error al convertir Base64 a Bitmap", e);
            return null;
        }
    }

    // Método para cargar la imagen en el ImageView
    private void loadProfileImage(User user) {
        String profileImageBase64 = user.getprofileImage();

        if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
            // Convertir de Base64 a Bitmap
            Bitmap profileBitmap = base64ToBitmap(profileImageBase64);

            if (profileBitmap != null) {
                // Establecer el bitmap en el ImageView
                imageProfile.setImageBitmap(profileBitmap);
            } else {
                // Establecer una imagen por defecto si hay error
                imageProfile.setImageResource(R.drawable.perfil);
            }
        } else {
            // No hay imagen de perfil, mostrar imagen por defecto
            imageProfile.setImageResource(R.drawable.perfil);
        }
    }
}