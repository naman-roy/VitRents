package com.company.vitrents;

import android.content.Context;
import android.widget.Toast;

public class MyUtils {

    public  static  final String USER_TYPE_GOOGLE="Google";

    public  static  final String USER_TYPE_PHONE="Phone";

    public  static  final String USER_TYPE_EMAIL="Email";

    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public  static long timestamp(){
        return System.currentTimeMillis();
    }

}
