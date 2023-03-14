package com.example.demo.utils;

import java.util.Optional;

public class InputValidator {
    public static boolean isInteger(String s, Optional<Integer> min, Optional<Integer> max) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        if (!(min.isPresent() && max.isPresent())) {
            return true;
        }
        if ((min.get() <= Integer.parseInt(s)) && (Integer.parseInt(s) <= max.get())) {
            return true;
        }
        return false;
    }
}
