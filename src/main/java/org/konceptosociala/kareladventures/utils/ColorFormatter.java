package org.konceptosociala.kareladventures.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ColorFormatter extends Formatter {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    @Override
    public String format(LogRecord record) {
        String color;
        switch (record.getLevel().getName()) {
            case "SEVERE":
                color = RED;
                break;
            case "WARNING":
                color = YELLOW;
                break;
            case "INFO":
                color = GREEN;
                break;
            case "CONFIG":
                color = BLUE;
                break;
            case "FINE":
                color = CYAN;
                break;
            case "FINER":
                color = PURPLE;
                break;
            case "FINEST":
                color = WHITE;
                break;
            default:
                color = WHITE;
                break;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s[%s] %s%s%n", color, record.getLevel().getName(), record.getMessage(), RESET));

        // Include stack trace if there is an exception
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            builder.append(sw.toString());
        }

        return builder.toString();
    }
}
