package com.tardin.appioca.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.tardin.appioca.R;

import java.util.HashMap;
import java.util.Map;

public class Util {
    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connection = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connection.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void errorOptions(Context context, String answer) {
        StringBuilder errorString = new StringBuilder();
        HashMap<String, String> errors = new HashMap<String, String>() {{
//            put("interrupted connection", context.getResources().getString(R.string.error_auth_firebase_connection));
//            put("least 6 characters", context.getResources().getString(R.string.error_auth_password_6_char));
//            put("password is invalid", context.getResources().getString(R.string.error_auth_password_invalid));
//            put("address is badly", context.getResources().getString(R.string.error_auth_email_invalid));
//            put("There is no user", context.getResources().getString(R.string.error_auth_email_not_registered));
//            put("address is already", context.getResources().getString(R.string.error_auth_email_already_exists));
        }};
        for (Map.Entry<String, String> entry : errors.entrySet()) {
            String key = entry.getKey();
            String text = entry.getValue();
            if (answer.contains(key)) errorString.append(text).append(" ");
        }
        if (errorString.toString().length() > 0)
            Toast.makeText(context, errorString.toString(), Toast.LENGTH_LONG).show();
        else Toast.makeText(context, answer, Toast.LENGTH_LONG).show();
    }
}
