package com.example.budjetics.parsingsms;

import android.Manifest;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            SmsObserver observer = new SmsObserver(new Handler(), this);
            observer.queryLastSentSMS();
            Uri inboxURI = Uri.parse("content://sms/inbox");

            // TODO rewrite required permission and test for API less then API 23
            checkPermission();

            getContentResolver().registerContentObserver(inboxURI, true, observer);

        }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_SMS))
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.READ_SMS}, 1);
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.READ_SMS}, 1);
            }

        }
        else
        {
            /* do nothing */
            /* permission is granted */
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.SEND_SMS) ==  PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(this, "No Permission granted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
