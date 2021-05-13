package com.jobesk.nourv.Utils;

import com.jobesk.nourv.Model.BluetoothDevice;

import java.util.ArrayList;

public class Constants {
    //FOR NOTIFICATIONS
    public static final String TAG = "Constants";
    public static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    public static final String serverKey = "key=" + "AAAA16zdSPs:APA91bFoWZ3QlTie2554YGhJDwEADdJ0XWyPqbzkEtJPsypOrPx1_72pyyUI3X5-NbNcg4up9dvv273894IMdtw0yCxOy1FBIeIJ32Sp6bfFJLbkfXAZsSc6PcCaqhCsY8krO2pNiX3w";
    public static final String contentType = "application/json";

    public static  String NOTIFICATION_TITLE="";
    public static  String NOTIFICATION_MESSAGE="";
    public static  String USER_ID="";
    public static  String TOPIC="";
    public static ArrayList<BluetoothDevice> MY_DEVICES = new ArrayList<>();
}
