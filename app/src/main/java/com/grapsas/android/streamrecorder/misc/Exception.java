package com.grapsas.android.streamrecorder.misc;


public class Exception extends java.lang.Exception {

    private int code;


    public Exception() {
        super();
    }

    public Exception( String detailMessage ) {
        super( detailMessage );
    }

    public Exception( String detailMessage, int code ) {
        super( detailMessage );
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
