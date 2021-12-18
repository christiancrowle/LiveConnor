package com.picsofbread.liveconnor;

public class LogLine {
    public int count;
    public String message;

    public LogLine(int count, String message) {
        set(count, message);
    }

    public void set(int count, String message) {
        this.count = count;
        this.message = message;
    }
}
