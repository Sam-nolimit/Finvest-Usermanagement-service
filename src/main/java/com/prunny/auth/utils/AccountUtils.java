package com.prunny.auth.utils;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Random;

public class AccountUtils {


    public static boolean validatePassword(String password, String cpassword) {
        return password.equals(cpassword);
    }


    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";




}
