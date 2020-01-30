package com.guvno.robotic;

import android.content.Context;

import java.util.Date;

public class CallReceiver extends PhonecallReceiver {

    public static FuncInterface callback;
    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start)
    {
        if (callback != null) {
            callback.notify("onIncomingCallAnswered: " + number);
        }
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end)
    {
        //
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start)
    {
        //
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end)
    {
        //
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start)
    {
        //
    }

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        if (callback != null) {
            callback.notify("onIncomingCallReceived: " + number);
        }
    }

    public interface FuncInterface
    {
        // A non-abstract (or default) function
        void notify(String number);
    }
}
