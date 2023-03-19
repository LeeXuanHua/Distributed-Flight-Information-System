package com.example.demo.utils;

import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.Optional;


public class InputValidator {
    /*
     * Check if string is a valid integer within two bounds, inclusive.
     */
    public static boolean isInteger(String s, Optional<Integer> min, Optional<Integer> max) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        if (!(min.isPresent() && max.isPresent())) {
            return true;
        }
        return (min.get() <= Integer.parseInt(s)) && (Integer.parseInt(s) <= max.get());
    }

    public static boolean isValidIp(String s) {
        InetAddressValidator validator = InetAddressValidator.getInstance();
        return validator.isValid(s);
    }
}
