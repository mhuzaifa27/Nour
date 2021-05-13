package com.jobesk.nourv.FirebaseServices;

import android.content.Intent;
import android.text.format.DateFormat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jobesk.nourv.Model.BluetoothDevice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by phamngocthanh on 7/24/17.
 */

public class BluetoothDeviceService implements ChildEventListener, ValueEventListener {
    private DatabaseReference mLanguagePreference;
    public boolean isLoaded = false;




    public BluetoothDeviceService(){
        mLanguagePreference = FirebaseDatabase.getInstance().getReference("Devices");
        mLanguagePreference.keepSynced(true);
        mLanguagePreference.addChildEventListener(this);
        mLanguagePreference.addValueEventListener(this);
    }

    public void configureDatabase(){

    }

    private Query mQuery;
    private ChangeEventListener mListener;
    private List<DataSnapshot> mSnapshots = new ArrayList<>();

    public BluetoothDeviceService(Query ref) {
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

    public BluetoothDevice getUserById(String key){
        if(mSnapshots != null && mSnapshots.size() == 0){
            return null;
        }
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return snapshot.getValue(BluetoothDevice.class);
            }
        }
        return null;
    }

    public BluetoothDevice getUserBySenderId(String key){
        if(mSnapshots != null && mSnapshots.size() == 0){
            return null;
        }
        for (DataSnapshot snapshot : mSnapshots) {
            try {
                BluetoothDevice BluetoothDevice = snapshot.getValue(BluetoothDevice.class);
                if (BluetoothDevice != null && BluetoothDevice.deviceAddress.equalsIgnoreCase(key)) {
                    return snapshot.getValue(BluetoothDevice.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public BluetoothDevice getUserByEmail(String mEmail){
        if (mEmail == null || mEmail.length() == 0)
            return null;
        if(mSnapshots == null || mSnapshots.size() == 0)
            return null;

        for (DataSnapshot snapshot : mSnapshots) {
            try {
                BluetoothDevice BluetoothDevice = snapshot.getValue(BluetoothDevice.class);
                if (BluetoothDevice != null && BluetoothDevice.deviceAddress != null && BluetoothDevice.deviceAddress.equalsIgnoreCase(mEmail)) {
                    return snapshot.getValue(BluetoothDevice.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*public BluetoothDevice getUserByPhoneNUmber(String number){
        if (number == null || number.length() == 0)
            return null;
        if(mSnapshots == null || mSnapshots.size() == 0)
            return null;

        for (DataSnapshot snapshot : mSnapshots) {
            try {
                BluetoothDevice BluetoothDevice = snapshot.getValue(BluetoothDevice.class);
                if (BluetoothDevice != null && BluetoothDevice.phoneNumber != null && BluetoothDevice.phoneNumber.equalsIgnoreCase(number)) {
                    return snapshot.getValue(BluetoothDevice.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }*/

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

    public void registerDevice(String userId,String deviceAddress, String deviceTitle, String type, String timePaired,String comment, String lastUpdated,String latitude,String longitude,DatabaseReference.CompletionListener completionListener){
        BluetoothDevice bluetoothDevice =new BluetoothDevice();

        bluetoothDevice.setDeviceAddress(deviceAddress);
        bluetoothDevice.setDeviceTitle(deviceTitle);
        bluetoothDevice.setTimePaired(timePaired);
        bluetoothDevice.setComment(comment);
        bluetoothDevice.setLastUpdated(lastUpdated);
        bluetoothDevice.setUserId(userId);
        bluetoothDevice.setType(type);
        bluetoothDevice.setBattery("100");
        bluetoothDevice.setRssi("0");
        bluetoothDevice.setLatitude(latitude);
        bluetoothDevice.setLongitude(longitude);

        String id = mLanguagePreference.push().getKey();
        bluetoothDevice.setId(id);

        Map<String, Object> postValues = bluetoothDevice.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(id, postValues);
        mLanguagePreference.updateChildren(childUpdates, completionListener);
    }

    public void updateBattery(String deviceId, String battery, DatabaseReference.CompletionListener completionListener){
        BluetoothDevice bluetoothDevice =new BluetoothDevice();
        bluetoothDevice.setBattery(battery);
        Map<String, Object> postValues = bluetoothDevice.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        mLanguagePreference.child(deviceId).child("battery").setValue(battery);
    }
   public void updateUserLocation(String deviceId, String latitude, String longitude, DatabaseReference.CompletionListener completionListener){
       BluetoothDevice bluetoothDevice =new BluetoothDevice();
       bluetoothDevice.setLatitude(latitude);
       bluetoothDevice.setLongitude(longitude);
       bluetoothDevice.setLastUpdated(getDateTime(System.currentTimeMillis()/1000));
        mLanguagePreference.child(deviceId).child("longitude").setValue(longitude);
        mLanguagePreference.child(deviceId).child("latitude").setValue(latitude);
       mLanguagePreference.child(deviceId).child("lastUpdated").setValue(getDateTime(System.currentTimeMillis()/1000));
        //mLanguagePreference.updateChildren(childUpdates, completionListener);
    }
    public void updateRssi(String deviceId,String rssi, DatabaseReference.CompletionListener completionListener){
        BluetoothDevice bluetoothDevice =new BluetoothDevice();
        bluetoothDevice.setRssi(rssi);
        bluetoothDevice.setLastUpdated(getDateTime(System.currentTimeMillis()/1000));
        mLanguagePreference.child(deviceId).child("rssi").setValue(rssi);
        mLanguagePreference.child(deviceId).child("lastUpdated").setValue(getDateTime(System.currentTimeMillis()/1000));
        //mLanguagePreference.updateChildren(childUpdates, completionListener);
    }
    public void deleteDevice(String deviceId){
        mLanguagePreference.child(deviceId).setValue(null);
        //mLanguagePreference.updateChildren(childUpdates, completionListener);
    }
    private String getDateTime(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000L);
        String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        return date;
    }
    public void modifyDevice(String deviceId,String deviceTitle, String type,String comment){
        mLanguagePreference.child(deviceId).child("comment").setValue(comment);
        mLanguagePreference.child(deviceId).child("type").setValue(type);
        mLanguagePreference.child(deviceId).child("deviceTitle").setValue(deviceTitle);
    }
    /* public void updateUserProfile(String name, String description, String age, String number, String image, DatabaseReference.CompletionListener completionListener){
        User user =new User();
        user.setName(name);
        user.setDescription(description);
        user.setUserAge(age);
        user.setPhoneNumber(number);
        user.setAvatar(image);
        user.setEmail(AppState.currentBpackUser.getEmail());
        user.setUserId(AppState.currentBpackUser.getUserId());
        user.setGender(AppState.currentBpackUser.getGender());
        user.setUserType(AppState.currentBpackUser.getUserType());
        user.setLatitude(AppState.currentBpackUser.getLatitude());
        user.setLongitude(AppState.currentBpackUser.getLongitude());
        user.setCreatedAt(System.currentTimeMillis()/1000);
        user.setUpdatedAt(System.currentTimeMillis()/1000);

        Map<String, Object> postValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(AppState.currentBpackUser.getUserId(), postValues);
        mLanguagePreference.updateChildren(childUpdates, completionListener);
    }
    public void updateRating(float rating, int ratingCount, String userId, DatabaseReference.CompletionListener completionListener){
        User user =new User();
        user.setRating(rating);
        user.setRatingCount(ratingCount);

        Map<String, Object> postValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        mLanguagePreference.child(userId).child("rating").setValue(rating);
        mLanguagePreference.child(userId).child("ratingCount").setValue(ratingCount);
    }*/
   /*public void updateUserLocation(String userId,String latitude,String longitude, DatabaseReference.CompletionListener completionListener){
       BluetoothDevice BluetoothDevice =new BluetoothDevice();
       BluetoothDevice.setLatitude(latitude);
       BluetoothDevice.setLongitude(longitude);

       mLanguagePreference.child(userId).child("longitude").setValue(longitude);
       mLanguagePreference.child(userId).child("latitude").setValue(latitude);
   }*/

}

