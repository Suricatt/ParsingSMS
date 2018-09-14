package com.example.budjetics.parsingsms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static java.lang.Thread.currentThread;

public class MainActivity extends AppCompatActivity {

        ArrayList<String> test;
        ArrayAdapter<String> adapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            // TODO rewrite required permission and test for API less then API 23
            checkPermission();

            //!ПЛОХОЙ КОД
            test = new ArrayList<String>();
            ListView listView = (ListView) findViewById(R.id.list);
            adapter= new ArrayAdapter<String>(this,   android.R.layout.simple_list_item_1, test);
            listView.setAdapter(adapter);

            //TODO разобраться с потоками. Возможно будет перенести в Sheduler.io
            SmsObserver smsSource = new SmsObserver(new Handler(), getContentResolver(), new Date());
            smsSource.getSource()
                .subscribe(s->{
                   test.add(s[0]);
                   adapter.notifyDataSetChanged();
                });
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
