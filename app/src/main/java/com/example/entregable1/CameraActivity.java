package com.example.entregable1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.entregable1.entity.User;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;

public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 0x512;
    private static final int TAKE_PHOTO_CODE = 0x514;
    private Button takePictureButton;
    private ImageView takePictureImage;
    private FirebaseFirestore mDatabase;
    private FirebaseUser firebaseUser;
    private String image;
    private static final String TAG = "Acme-Explorer";
    ActivityResultLauncher<Intent> filtroLauncher;
    private FirestoreService firestoreService;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_storage_example);

        mDatabase = FirebaseFirestore.getInstance();
        takePictureImage = findViewById(R.id.take_picture_image);
        takePictureButton = findViewById(R.id.take_picture_button);
        takePictureButton.setOnClickListener(v -> takePicture());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestoreService = FirestoreService.getServiceInstance();
        firestoreService.getUser(firebaseUser.getUid(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null && documentSnapshot.exists()){
                    user = documentSnapshot.toObject(User.class);
                    loadProfileImage(user);
                } else if (error != null) {
                    Log.e("FirestoreError", "Error al obtener usuario", error);
                }
            }
        });

    }

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Snackbar.make(takePictureButton, R.string.take_picture_camera_rationale, BaseTransientBottomBar.LENGTH_LONG)
                        .setAction(R.string.take_picture_camera_rationale_ok, click -> {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            }
        } else {
            // Permiso de cámara concedido, iniciar la cámara directamente
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Verificar que la app de la cámara exista para manejar este intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, TAKE_PHOTO_CODE);
        } else {
            Toast.makeText(this, "No hay aplicación de cámara disponible", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK && data != null) {
            // Obtenemos la imagen como Bitmap desde los extras del intent
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                if (imageBitmap != null) {
                    // Mostrar la imagen en el ImageView
                    takePictureImage.setImageBitmap(imageBitmap);

                    // Convertir imagen a Base64
                    String imageData = bitmapToBase64(imageBitmap);

                    // Guardar en Firestore
                    saveImageToFirestore(imageData);
                    finish();
                } else {
                    Toast.makeText(this, "Error al obtener la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Convertir Bitmap a String Base64
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Guardar la imagen en Firestore
    private void saveImageToFirestore(String imageData) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            mDatabase.collection("users").document(firebaseUser.getUid())
                    .update("profileImage", imageData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Imagen guardada en Firestore");
                        Toast.makeText(CameraActivity.this,
                                "Imagen guardada correctamente", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error al guardar la imagen", e);
                        Toast.makeText(CameraActivity.this,
                                "Error al guardar imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.w(TAG, "Usuario no autenticado, no se puede guardar la imagen en Firestore");
            Toast.makeText(this, "Necesitas iniciar sesión para guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(this, R.string.camera_not_granted, Toast.LENGTH_SHORT).show();
            }
        }
    }

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

    private void loadProfileImage(User user) {
        String profileImageBase64 = user.getprofileImage();

        if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
            // Convertir de Base64 a Bitmap
            Bitmap profileBitmap = base64ToBitmap(profileImageBase64);

            if (profileBitmap != null) {
                // Establecer el bitmap en el ImageView
                takePictureImage.setImageBitmap(profileBitmap);
            } else {
                // Establecer una imagen por defecto si hay error
                takePictureImage.setImageResource(R.drawable.perfil);
            }
        } else {
            // No hay imagen de perfil, mostrar imagen por defecto
            takePictureImage.setImageResource(R.drawable.perfil);
        }
    }
}