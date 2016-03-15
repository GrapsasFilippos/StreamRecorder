package com.grapsas.android.streamrecorder.exception;


public class IOException extends com.grapsas.android.streamrecorder.exception.Exception {

    public IOException() {
        super();
    }

    public IOException( String detailMessage, int code ) {
        super( detailMessage, code );
    }

    public IOException( String detailMessage, int code, Throwable cause ) {
        super( detailMessage, code, cause );
    }

}
