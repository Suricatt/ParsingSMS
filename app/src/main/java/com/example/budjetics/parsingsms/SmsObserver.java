package com.example.budjetics.parsingsms;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.net.Uri;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class SmsObserver extends ContentObserver {

    private Context context;
    private static int initialPos;
    private static final String TAG = "SMSContentObserver";
    private static final Uri uriSMS = Uri.parse("content://sms/inbox");

    public SmsObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange){
        super.onChange(selfChange);
        queryLastSentSMS();
    }

    public int getLastMsgId() {

        Cursor cur = context.getContentResolver().query(uriSMS, null, null, null, null);
        cur.moveToFirst();
        int lastMsgId = cur.getInt(cur.getColumnIndex("_id"));
        Log.i(TAG, "Last sent message id: " + String.valueOf(lastMsgId));
        return lastMsgId;
    }

    protected void queryLastSentSMS() {

        Observable.create(s -> {

            Cursor cur =
                    context.getContentResolver().query(uriSMS, null, null, null, null);
            if (cur.moveToNext()) {

                try {
                    if (initialPos != getLastMsgId()) {
                        // Here you get the last sms. Do what you want.
                        String receiver = cur.getString(cur.getColumnIndex("address"));
                        s.onNext(receiver);
                        // Then, set initialPos to the current position.
                        initialPos = getLastMsgId();
                    }
                } catch (Exception e) {
                    // Treat exception here
                }
            }
            cur.close();
            s.onComplete();
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(address -> Log.i("Test", String.valueOf(address)));

    }

}
