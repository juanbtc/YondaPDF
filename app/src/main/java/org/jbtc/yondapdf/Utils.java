package org.jbtc.yondapdf;

public class Utils {

    public static final int NOTIFICATION_ID_FOREGROUND_SERVICE = 1068021;
    public static final String dbName = "bookslightnovel";

    public static final String ACTION_START = "tts.start";
    public static final String ACTION_PREV = "tts.prev";
    public static final String ACTION_PLAY = "tts.play";
    public static final String ACTION_PAUSE = "tts.pause";
    public static final String ACTION_STOP = "tts.stop";
    public static final String ACTION_NEX = "tts.next";
    public static final String ACTION_CLOSE = "tts.close";
    //public static final String ACTION_SEND_DATA = "tts.send.data";

    public static final byte STATE_NOT_INIT = 0;
    public static final byte STATE_PLAYING = 10;
    public static final byte STATE_PAUSED = 20;
    public static final byte STATE_STOPED = 30;
    public static final byte STATE_CLOSED = 40;
}
