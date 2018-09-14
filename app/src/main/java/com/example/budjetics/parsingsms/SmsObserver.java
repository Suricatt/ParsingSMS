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
import io.reactivex.subjects.BehaviorSubject;

public class SmsObserver extends ContentObserver {

    private String startDate;
    private ContentResolver content;
    private static int initialPos;
    private static final String TAG = "SMSContentObserver";
    private static final Uri uriSMS = Uri.parse("content://sms/inbox");
    private final BehaviorSubject<String[]> source;

    /**
     * @param handler
     * @param content
     * @param startDate
     */
    public SmsObserver(Handler handler, ContentResolver content, Date startDate) {
        super(handler);
        this.content = content;
        this.startDate = String.valueOf(startDate.getTime());
        content.registerContentObserver(uriSMS, true, this);
        source = BehaviorSubject.create();
    }

    public BehaviorSubject<String[]> getSource(){
        return source;
    }

    @Override
    public void onChange(boolean selfChange){
        super.onChange(selfChange);
        getSMSFromDate(startDate);
    }

    public int getLastMsgId() {

        Cursor cur = content.query(uriSMS, null, null, null, null);
        cur.moveToFirst();
        int lastMsgId = cur.getInt(cur.getColumnIndex("_id"));
        Log.i(TAG, "Last sent message id: " + String.valueOf(lastMsgId));
        return lastMsgId;
    }

    private void getSMSFromDate(String date) {

        Cursor cur =
                content.query(uriSMS, new String[]{"_ID", "date", "body", "address"}, "date > ?", new String[]{date}, null);
        while (cur.moveToNext()) {

            try {
                String body = cur.getString(cur.getColumnIndex("body"));
                String address = cur.getString(cur.getColumnIndex("address"));
                source.onNext(new String[]{body, address});
                startDate = cur.getString(cur.getColumnIndex("date"));
            } catch (Exception e) {
                // Treat exception here
            }
        }
        cur.close();

    }

}
