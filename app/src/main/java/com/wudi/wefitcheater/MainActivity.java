package com.wudi.wefitcheater;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button button;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) this.findViewById(R.id.input);
        button = (Button) this.findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int step;
                try {
                    String text = editText.getText().toString();
                    step = Integer.valueOf(text);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.fail), Toast.LENGTH_LONG).show();
                    return;
                }

                AsyncTask<Integer, Void, Boolean> task = new AsyncTask<Integer, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Integer... params) {
                        try {
                            FileOutputStream test = openFileOutput("test", Context.MODE_PRIVATE);
                            test.write("Test".getBytes());
                            test.close();

                            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "am force-stop com.tencent.mm; cp /data/data/com.tencent.mm/MicroMsg/stepcounter.cfg /data/data/com.wudi.wefitcheater/files/stepcounter.cfg; chmod 777 /data/data/com.wudi.wefitcheater/files/stepcounter.cfg"});
                            process.waitFor();

                            FileInputStream input = openFileInput("stepcounter.cfg");
                            ObjectInputStream inputStream = new ObjectInputStream(input);
                            Map map = (Map) inputStream.readObject();
                            inputStream.close();
                            input.close();

                            map.put(201, params[0]);
                            map.put(204, System.currentTimeMillis());

                            FileOutputStream file2 = openFileOutput("stepcounter.cfg", 2);
                            ObjectOutputStream outputStream = new ObjectOutputStream(file2);
                            outputStream.writeObject(map);
                            outputStream.close();
                            file2.close();

                            process = Runtime.getRuntime().exec(new String[]{"su", "-c", "cp -f /data/data/com.wudi.wefitcheater/files/stepcounter.cfg /data/data/com.tencent.mm/MicroMsg/stepcounter.cfg"});
                            process.waitFor();
                        } catch (Exception e) {
                            return false;
                        }

                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if (!aBoolean) {
                            Toast.makeText(MainActivity.this,getResources().getString(R.string.fail),Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this,getResources().getString(R.string.success),Toast.LENGTH_LONG).show();
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                            startActivity(launchIntent);
                        }
                    }
                };

                task.execute(step);
            }
        });
    }
}
