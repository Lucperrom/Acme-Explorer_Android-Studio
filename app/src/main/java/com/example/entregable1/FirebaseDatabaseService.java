package com.example.entregable1;

import android.util.Log;

import com.example.entregable1.entity.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseService {
    private static String userId;
    private static FirebaseDatabaseService service;
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabaseService getServiceInstance() {
        if (service == null || mDatabase == null) {
            service = new FirebaseDatabaseService();
            mDatabase = FirebaseDatabase.getInstance();
            Log.d("FirebaseDebug", "FirebaseDatabase instance created: " + mDatabase);
            mDatabase.setPersistenceEnabled(true);
        }
        if(userId == null || userId.isEmpty()) {
            userId = FirebaseAuth.getInstance().getCurrentUser() != null? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        }
        return service;
    }
    public DatabaseReference getTrip(String tripId) {
       return mDatabase.getReference("/trips/" + tripId).getRef();
    }

    public DatabaseReference getTrip() {
        return mDatabase.getReference("trips/").getRef();
    }

    public void saveTrip(Trip trip, DatabaseReference.CompletionListener completionListener){
        mDatabase.getReference("/trips/").push().setValue(trip, completionListener);

    }
}
