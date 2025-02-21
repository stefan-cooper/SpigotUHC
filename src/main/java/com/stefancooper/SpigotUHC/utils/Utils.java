package com.stefancooper.SpigotUHC.utils;

public class Utils {

    // Some things are managed in Minecraft ticks. Use this to convert from seconds to ticks
    public static long secondsToTicks (int seconds) {
        return (long) seconds * 20;
    }
}
