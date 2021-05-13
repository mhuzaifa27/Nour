package com.jobesk.nourv.FirebaseServices;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jobesk.nourv.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phamngocthanh on 7/24/17.
 */

public class UserService implements ChildEventListener, ValueEventListener {
    private DatabaseReference mLanguagePreference;
    public boolean isLoaded = false;




    public UserService(){
            mLanguagePreference = FirebaseDatabase.getInstance().getReference("users");
            mLanguagePreference.keepSynced(true);
            mLanguagePreference.addChildEventListener(this);
            mLanguagePreference.addValueEventListener(this);
    }

    public void configureDatabase(){

    }

    private Query mQuery;
    private ChangeEventListener mListener;
    private List<DataSnapshot> mSnapshots = new ArrayList<>();

    public UserService(Query ref) {
        mQuery = ref;
        mQuery.addChildEventListener(this);
        mQuery.addValueEventListener(this);
    }

    public void updateQuery(Query ref) {
        if (mQuery != null) {
            cleanup();
        }
        mQuery = ref;
        mQuery.addChildEventListener(this);
        mQuery.addValueEventListener(this);
    }

    public void cleanup() {
        mQuery.removeEventListener((ValueEventListener) this);
        mQuery.removeEventListener((ChildEventListener) this);
    }

    public int getCount() {
        return mSnapshots.size();
    }

    public DataSnapshot getItem(int index) {
        return mSnapshots.get(index);
    }

    public int getIndexOfItem(DataSnapshot snapshot) {
        return mSnapshots.indexOf(snapshot);
    }

    private int getIndexForKey(String key) {
        int index = 0;
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        throw new IllegalArgumentException("Key not found");
    }

    public boolean existedIndexForKey(String key) {
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public DataSnapshot snapshotForKey(String key) {
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return snapshot;
            }
        }
        return null;
    }

    public User getUserById(String key){
        if(mSnapshots != null && mSnapshots.size() == 0){
            return null;
        }
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return snapshot.getValue(User.class);
            }
        }
        return null;
    }

    public User getUserBySenderId(String key){
        if(mSnapshots != null && mSnapshots.size() == 0){
            return null;
        }
        for (DataSnapshot snapshot : mSnapshots) {
            try {
                User customer = snapshot.getValue(User.class);
                if (customer != null && customer.userId.equalsIgnoreCase(key)) {
                    return snapshot.getValue(User.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public User getUserByEmail(String mEmail){
        if (mEmail == null || mEmail.length() == 0)
            return null;
        if(mSnapshots == null || mSnapshots.size() == 0)
            return null;

        for (DataSnapshot snapshot : mSnapshots) {
            try {
                User driver = snapshot.getValue(User.class);
                if (driver != null && driver.email != null && driver.email.equalsIgnoreCase(mEmail)) {
                    return snapshot.getValue(User.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public User getUserByPhoneNUmber(String number){
        if (number == null || number.length() == 0)
            return null;
        if(mSnapshots == null || mSnapshots.size() == 0)
            return null;

        for (DataSnapshot snapshot : mSnapshots) {
            try {
                User customer = snapshot.getValue(User.class);
                if (customer != null && customer.phoneNumber != null && customer.phoneNumber.equalsIgnoreCase(number)) {
                    return snapshot.getValue(User.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
        if (existedIndexForKey(snapshot.getKey())) {
            return;
        }
        int index = 0;
        if (previousChildKey != null) {
            index = getIndexForKey(previousChildKey) + 1;
        }
        mSnapshots.add(index, snapshot);
        notifyChangedListeners(ChangeEventListener.EventType.ADDED, index);
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.set(index, snapshot);
        isLoaded = true;
        notifyChangedListeners(ChangeEventListener.EventType.CHANGED, index);
    }

    @Override
    public void onChildRemoved(DataSnapshot snapshot) {
        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(index);
        notifyChangedListeners(ChangeEventListener.EventType.REMOVED, index);
    }

    @Override
    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
        int oldIndex = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(oldIndex);
        int newIndex = previousChildKey == null ? 0 : (getIndexForKey(previousChildKey) + 1);
        mSnapshots.add(newIndex, snapshot);
        notifyChangedListeners(ChangeEventListener.EventType.MOVED, newIndex, oldIndex);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        mListener.onDataChanged();
    }

    @Override
    public void onCancelled(DatabaseError error) {
        notifyCancelledListeners(error);
    }

    public void setOnChangedListener(ChangeEventListener listener) {
        mListener = listener;
    }

    protected void notifyChangedListeners(ChangeEventListener.EventType type, int index) {
        notifyChangedListeners(type, index, -1);
    }

    protected void notifyChangedListeners(ChangeEventListener.EventType type, int index, int oldIndex) {
        if (mListener != null) {
            mListener.onChildChanged(type, index, oldIndex);
        }
    }

    protected void notifyCancelledListeners(DatabaseError error) {
        if (mListener != null) {
            mListener.onCancelled(error);
        }
    }

//    public User getUserById(String userId){
//        for (int i = 0; i < getCount(); i++) {
//            if(getItem(i).getKey().equalsIgnoreCase(userId)){
//                return getItem(i).getValue(User.class);
//            }
//        }
//        return null;
//    }

    public void registerUser(String email, String userName, String userId, String phoneNumber, String country,String address, String image, DatabaseReference.CompletionListener completionListener) {
        User driver =new User();
        driver.setEmail(email);
        driver.setUserId(userId);
        driver.setPhoneNumber(phoneNumber);
        driver.setUserName(userName);
        driver.setCountry(country);
        driver.setAddress(address);
        driver.setImage(image);

        Map<String, Object> postValues = driver.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(userId, postValues);
        mLanguagePreference.updateChildren(childUpdates, completionListener);
    }

    //    public void updateUser(String userName, String userId, String avatar, DatabaseReference.CompletionListener completionListener){
//        User user =new User();
//        user.setUserName(userName);
//        user.setUserId(userId);
//        Map<String, Object> postValues = user.toMap();
//        Map<String, Object> childUpdates = new HashMap<>();
//
//        // Trong truong hop add thi trong postValue != null
//        // Trong truong hop delete thi set postValue = null
//        childUpdates.put(userId, postValues);
//
//        mLanguagePreference.updateChildren(childUpdates, completionListener);
//    }
//    public void updateUserLocation(String userId, Double latitude, Double longitude, DatabaseReference.CompletionListener completionListener) {
//        User user =new User();
//        user.setLatitude(latitude);
//        user.setLongitude(longitude);
//        user.setUpdatedAt(System.currentTimeMillis()/1000);
//        Map<String, Object> postValues = user.toMap();
//        Map<String, Object> childUpdates = new HashMap<>();
//
//        // Trong truong hop add thi trong postValue != null
//        // Trong truong hop delete thi set postValue = null
//        //childUpdates.put(userId, postValues);
//        mLanguagePreference.child(userId).child("longitude").setValue(longitude);
//        mLanguagePreference.child(userId).child("latitude").setValue(latitude);
//        //mLanguagePreference.updateChildren(childUpdates, completionListener);
//    }

    /*public void updateUserProfile(String id,String name, String number, String email, String img, DatabaseReference.CompletionListener completionListener) {
        Driver user = new Driver();
        user.setImage(img);
        user.setUserName(name);
        user.setEmail(email);
        user.setPhoneNumber(number);

        Map<String, Object> postValues = user.toMap();

//        mLanguagePreference.updateChildren(postValues, completionListener);

        Map<String, Object> childUpdates = new HashMap<>();

        Log.e("id",SessionManager.getKeyFirebaseId());
        childUpdates.put(id,postValues);
//        childUpdates.put(AppState.currentBpackUser.getUserId(), postValues);
        mLanguagePreference.updateChildren(childUpdates, completionListener);
    }
*/
    /*
    public void updateRating(float rating, int ratingCount, String userId, DatabaseReference.CompletionListener completionListener){
        User user =new User();
        user.setRating(rating);
        user.setRatingCount(ratingCount);

        Map<String, Object> postValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        mLanguagePreference.child(userId).child("rating").setValue(rating);
        mLanguagePreference.child(userId).child("ratingCount").setValue(ratingCount);
    }*/
  /* public void updateUserLocation(String userId,Double latitude,Double longitude, DatabaseReference.CompletionListener completionListener){
       Customer customer =new Customer();
       customer.setLatitude(latitude);
       customer.setLongitude(longitude);

       mLanguagePreference.child(userId).child("longitude").setValue(longitude);
       mLanguagePreference.child(userId).child("latitude").setValue(latitude);
   }*/

}
