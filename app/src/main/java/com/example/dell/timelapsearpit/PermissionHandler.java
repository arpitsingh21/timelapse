package com.example.dell.timelapsearpit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dell on 29-07-2017.
 */

public class PermissionHandler {
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123;
    private Activity activity;
    private boolean hasRights = false;
    List<String> allPermissions;

    public PermissionHandler(Activity activity) {
        this.activity = activity;
    }

    public boolean requestPermission(String[] permissionsNeeded) {
        allPermissions = new ArrayList<>(Arrays.asList(permissionsNeeded));

        final List<String> permissionsMissing = new ArrayList<>();
        for( String permission : allPermissions ) {
            if (!addPermission(permissionsMissing, permission)) {
               // Log.i(LibraryInfo.TAG,"permissions missing:" + permission);
            }
        }
        if (permissionsMissing.size() > 0) {
            ActivityCompat.requestPermissions(activity,permissionsMissing.toArray(new String[permissionsMissing.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        } else {
            hasRights = true;
        }
        return true;
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissionsRequested, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                // Check for Rationale Option
                hasRights = true;
                boolean shouldShowRequestPermissionRationale = false;
                StringBuilder message = new StringBuilder("You need to grant access to ");
                for (int i = 0; i < permissionsRequested.length; i++) {
                   // Log.i(LibraryInfo.TAG,"i:" + i + ":" + permissionsRequested[i] + ":" + grantResults[i] );
                    if( grantResults[i] != PackageManager.PERMISSION_GRANTED ) {
                        if( ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequested[i]) ) {
                            shouldShowRequestPermissionRationale = true;
                            if( !hasRights ) {
                                message.append(", ");
                            }
                            message.append(permissionsRequested[i]);
                        }
                        hasRights = false;
                    }
                }
                if( shouldShowRequestPermissionRationale ) {
                    new AlertDialog.Builder(activity)
                            .setMessage(message)
                            .create()
                            .show();
                }
            }
            break;
        }
    }

    public boolean hasRights() {
        return hasRights;
    }
}