package com.jaipurice.app.utils;

import java.util.ArrayList;

/**
 * Created by SoNu on 8/11/2017.
 */

public interface PermissionResultCallback
{
    void PermissionGranted(int request_code);
    void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions);
    void PermissionDenied(int request_code);
    void NeverAskAgain(int request_code);
}