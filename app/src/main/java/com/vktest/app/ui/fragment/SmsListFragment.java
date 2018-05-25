package com.vktest.app.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.vktest.app.R;
import com.vktest.app.ui.adapter.SmsListAdapter;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by qati on 25.05.18.
 */

public class SmsListFragment extends BaseFragment {

    private final static int REQUEST_READ_SMS_PERMISSION = 10;

    @BindView(R.id.rvSms) RecyclerView rvSms;
    @BindView(R.id.tvEmptySms) TextView tvEmptySms;

    @Override
    public int bindLayout() {
        return R.layout.fragment_list_sms;
    }

    @Override
    protected void init() {
        rvSms.setHasFixedSize(true);
        rvSms.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (checkSmsPermission()) {
            showSmsInRV();
        }
    }

    private void showSmsInRV() {
        ArrayList<String> messages = loadSmsFromDevice();
        rvSms.setAdapter(new SmsListAdapter(messages));
        if (messages.isEmpty()) {
            tvEmptySms.setVisibility(View.VISIBLE);
        } else {
            tvEmptySms.setVisibility(View.GONE);
        }
    }


    private boolean checkSmsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_SMS},
                        REQUEST_READ_SMS_PERMISSION);

                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (REQUEST_READ_SMS_PERMISSION == requestCode) {
                System.out.println("REQUEST_READ_SMS_PERMISSION Permission Granted");
                showSmsInRV();
            }

        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (REQUEST_READ_SMS_PERMISSION == requestCode) {
                //todo
            }

        }
    }

    private ArrayList<String> loadSmsFromDevice() {
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = getActivity().getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, null, null, null);
        ArrayList<String> messages = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            int iterator = 0;
            while (cursor.moveToNext() && iterator++ < 30) {
                String address = cursor.getString(1);
                String body = cursor.getString(3);
                messages.add("Mobile number: " + address + "\n" + body);
                System.out.println("Mobile number: " + address);
                System.out.println("Text: " + body);
            }
        }
        return messages;

    }
}
