package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

@Slf4j
public class Simulate {
    public static int FAILURE_RATE;
    public static boolean isFailure(String reqOrReply) throws FileNotFoundException, URISyntaxException {
        // 1. Read in our config file
        Scanner in = new Scanner(new FileReader(Paths.get("").toAbsolutePath() + "/src/main/java/com/example/demo/utils/" + "Failure.txt"));
        StringBuilder sb = new StringBuilder();
        while(in.hasNext()) {
            sb.append(in.next());
        }
        in.close();
        String outString = sb.toString();

        // 2. If the value set in Failure.txt is a valid integer, use it. else, use default (0)
        if (InputValidator.isInteger(outString, Optional.of(0), Optional.of(100))) {
            FAILURE_RATE = Integer.parseInt(outString);
            log.trace("Using failure rate of {}%", FAILURE_RATE);
        } else {
            FAILURE_RATE = 0;
            log.trace("Invalid failure rate configured in Failure.txt, setting default failure rate of 0% i.e. will never fail");
        }

        // 3. Prevent memory leak
        in.close();

        Random rand = new Random();
        int randInt = rand.nextInt(100)+1;

        if (randInt <= FAILURE_RATE) {
            log.warn("Loss of {} message occurred! :(", reqOrReply.toLowerCase());
            return true;
        } else {
            return false;
        }
    }
}
