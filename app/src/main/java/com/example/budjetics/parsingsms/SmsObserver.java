package com.example.budjetics.parsingsms;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.net.Uri;
import android.util.Log;

import java.sql.Timestamp;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SmsObserver extends ContentObserver {

    private String startDate;
    private ContentResolver content;
    private static int initialPos;
    private static final String TAG = "SMSContentObserver";
    private static final Uri uriSMS = Uri.parse("content://sms/inbox");
    private final Observer<String[]> observer;

    /**
     * @param handler
     * @param content
     * @param startDate
     * @param observer
     */
    public SmsObserver(Handler handler, ContentResolver content, Date startDate, Observer<String[]> observer) {
        super(handler);
        this.content = content;
        this.observer = observer;
        this.startDate = String.valueOf(startDate.getTime());
        content.registerContentObserver(uriSMS, true, this);
    }

    @Override
    public void onChange(boolean selfChange){
        super.onChange(selfChange);
        getSMSFromDate(startDate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer);
    }

    public int getLastMsgId() {

        Cursor cur = content.query(uriSMS, null, null, null, null);
        cur.moveToFirst();
        int lastMsgId = cur.getInt(cur.getColumnIndex("_id"));
        Log.i(TAG, "Last sent message id: " + String.valueOf(lastMsgId));
        return lastMsgId;
    }

    protected Observable<String[]> getSMSFromDate(String date) {

        return Observable.create(s -> {
            Cursor cur =
                    content.query(uriSMS, new String[]{"_ID", "date", "body", "address"}, "date > ?", new String[]{date}, null);
            while (cur.moveToNext()) {

                try {
                    String body = cur.getString(cur.getColumnIndex("body"));
                    String address = cur.getString(cur.getColumnIndex("address"));
                    s.onNext(new String[]{body, address});
                    startDate = cur.getString(cur.getColumnIndex("date"));
                } catch (Exception e) {
                    // Treat exception here
                }
            }
            cur.close();
            s.onComplete();
        });

    }

}
