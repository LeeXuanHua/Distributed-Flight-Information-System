package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

// Class to simulate communication omission failure for both replies and requests
@Slf4j
public class Simulate {
    public static int FAILURE_RATE;
    public static boolean isFailure(ReqOrReplyEnum reqOrReplyEnum) {
        // 1. Read in our config file and get the failure % chance we defined there
        try {
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

        } catch (FileNotFoundException e) {
            log.error("FileNotFoundError: " + e.getMessage());
        }

        // 4. Choose a random number
        Random rand = new Random();
        int randInt = rand.nextInt(100)+1;

        // 5. Determine result: success or (simulated) failure
        if (randInt <= FAILURE_RATE) {
            if (reqOrReplyEnum == ReqOrReplyEnum.REPLY) {
                log.warn("Loss of reply message occurred! :(");
            } else {
                log.warn("Loss of request message occurred! :(");
            }
            return true;
        } else {
            return false;
        }
    }
}
