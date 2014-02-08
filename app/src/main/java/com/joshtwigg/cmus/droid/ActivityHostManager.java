package com.joshtwigg.cmus.droid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by josh on 31/01/14.
 */
public class ActivityHostManager extends Activity implements IReceiveHost {
    private static final int REQUEST_CODE = 100;
    private ListView _hostView;
    private ArrayAdapter<String> _hostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_manager);
        _hostAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        _hostView = (ListView) findViewById(R.id.hostList);
        _hostView.setAdapter(_hostAdapter);
        _hostAdapter.add(getResources().getString(R.string.add_new));
        _hostView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int position, long id) {
                String address = adapter.getItemAtPosition(position).toString();
                if (address == getResources().getString(R.string.add_new)) address = "";
                ActivityHostManager.this.startActivityForResult(ActivityHostDialog.getStartIntent(ActivityHostManager.this, address), REQUEST_CODE);
            }
        });
        ArrayList<String> hosts = Storage.getSavedHosts(this);
        Log.d(getClass().getSimpleName(), "no of hosts received{" + hosts.size() + "}.");
        for (String s : hosts) _hostAdapter.add(s);
        _hostAdapter.notifyDataSetChanged();
        Util.runSearchHosts(this, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getClass().getSimpleName(), String.format("Activity returned result {%s} {%s}", requestCode, resultCode));
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) finish();
        else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void receiveHost(final String hostAddress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < _hostAdapter.getCount(); ++i) {
                    if (_hostAdapter.getItem(i).equals(hostAddress)) return;
                }
                _hostAdapter.add(hostAddress);
                _hostAdapter.notifyDataSetChanged();
            }
        });
    }

    public static void Show(Context context) {
        Intent intent = new Intent(context, ActivityHostManager.class);
        context.startActivity(intent);
    }
}