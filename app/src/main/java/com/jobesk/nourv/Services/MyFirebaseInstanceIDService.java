/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jobesk.nourv.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


@SuppressWarnings("deprecation")
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    SharedPreferences sharedpreferences;
   // SessionManager sessionManager;
    public static final String PREF_NAME = "UserPref" ;
    private static final String TAG = "MyFirebaseIIDService";
    @Override
    public void onTokenRefresh()
    {
        sharedpreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        //FirebaseMessaging.getInstance().subscribeToTopic("/topics/chat");

//        editor.putString( params.put("deviceID",android_id));
//        editor.commit();
        sendRegistrationToServer(refreshedToken);
        SharedPreferences userDetails = MyFirebaseInstanceIDService.this.getSharedPreferences("UserPref", MODE_PRIVATE);
       //Log.e(TAG, "my token: " + userDetails.getString(sessionManager.DEVICE_TOKEN,""));
        Log.d("ssssssssssss", String.valueOf(userDetails));


    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.


    }
}
