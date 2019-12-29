package com.example.rogueapk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView getapp =(TextView) findViewById(R.id.getapp);
        getapp.setMovementMethod(LinkMovementMethod.getInstance());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Scanning for rogue apps.", Toast.LENGTH_SHORT).show();

                try {
                    // Looks for the text file containing all the package names exposed as rogue apk's.
                    @SuppressLint("WorldReadableFiles") InputStream fIn = getApplicationContext().getResources().getAssets()
                            .open("rogueapk.txt", MODE_WORLD_READABLE);

                    BufferedReader input = new BufferedReader(new InputStreamReader(fIn));
                    String line;
                    while ((line = input.readLine()) != null) {
                        isPackageInstalled(getApplicationContext(), line);
                    }

                } catch (Exception e) {
                    e.getMessage();
                }
                Toast.makeText(getApplicationContext(), "Finished scanning.", Toast.LENGTH_SHORT).show();
            }

        });

    }


    public void isPackageInstalled(final Context context, String packageName) {
        Log.d("[Debug]", "Starting to look for " + packageName);
        final PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            Log.d("(Debug)", packageName + " not installed.");
        } else {
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            Toast.makeText(context, "Found: " + packageName, Toast.LENGTH_LONG).show();

            ArrayList rogueapps = new ArrayList();
            rogueapps.add(packageName);

            for (int i = 0; i < rogueapps.size(); i++) {
                final String uninstallapp = (String) rogueapps.get(i);
                Log.d("(Debug)", "Uninstall this --- " + uninstallapp);

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Alert! " + uninstallapp + " found.");
                alertDialog.setMessage("This app has been verified to be one of more than 100 rogue apps to have been identified, which hijack your phone" +
                        ", use up its processing power, and drain its battery life. Press OK to initiate an uninstall.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                Intent intent = new Intent("android.intent.action.DELETE");
                                intent.setData(Uri.parse("package:" + uninstallapp));
                                startActivity(intent);

                            }
                        });
                alertDialog.show();

            }
            Toast.makeText(getApplicationContext(), "Finished scanning..", Toast.LENGTH_SHORT).show();
        }

    }
}
