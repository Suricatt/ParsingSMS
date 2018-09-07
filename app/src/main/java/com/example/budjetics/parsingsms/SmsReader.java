package com.example.budjetics.parsingsms;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;

public final class SmsReader{

    private Uri inboxURI = Uri.parse("content://sms/inbox");
    private String[] reqCols = new String[]{"_id", "address", "body"};
    private final ContentResolver cr;

    public SmsReader(ContentResolver cr) {
        this.cr = cr;
    }

    public ArrayList<String> getSms(String id, String adress, Date date){
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);
        return null;
    }


}
