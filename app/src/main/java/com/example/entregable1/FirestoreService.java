package com.example.entregable1;

import android.util.Log;

import com.example.entregable1.entity.Trip;
import com.example.entregable1.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class FirestoreService {
    private static String userId;
    private static FirebaseFirestore mDatabase;
    private static FirestoreService service;

    public static FirestoreService getServiceInstance(){
        if(service == null ||mDatabase == null) {
            mDatabase = FirebaseFirestore.getInstance();
            service = new FirestoreService();
        }
        if(userId == null || userId.isEmpty()) {
            userId = FirebaseAuth.getInstance().getCurrentUser() != null? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        }
        return service;
    }

    public void saveTrip(Trip trip, OnCompleteListener<DocumentReference> listener){
        mDatabase.collection("users").document(userId).collection("trips").add(trip).addOnCompleteListener(listener);
    }

    public void getTrips(OnCompleteListener<QuerySnapshot> querySnapshotListener) {
        mDatabase.collection("users").document(userId).collection("trips").get().addOnCompleteListener(querySnapshotListener);
    }

    public void getTrip(String id, EventListener<DocumentSnapshot> snapshotListener){
        mDatabase.collection("users").document(userId).collection("trips").document(id).addSnapshotListener(snapshotListener);
    }

    public void getUser(String id, EventListener<DocumentSnapshot> snapshotListener){
        mDatabase.collection("users").document(id).addSnapshotListener(snapshotListener);
    }
    public void saveUser(User user) {
        if (user.getUid() == null) return;
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid()) // Nunca uses .add()
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Usuario guardado"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error guardando usuario", e));
    }


    public ListenerRegistration getTrips(EventListener<QuerySnapshot> querySnapshotOnCompleteListener){
        return mDatabase.collection("users").document(userId).collection("trips").addSnapshotListener(querySnapshotOnCompleteListener);
    }

}
