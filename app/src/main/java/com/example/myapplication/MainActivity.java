package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<CharSequence> appList = null;

    private ArrayAdapter<CharSequence> appAdapter = null;

    private Spinner appSpinner = null;

    private List<CharSequence> userList = new ArrayList<>();

    private ArrayAdapter<CharSequence> userAdapter = null;

    private Spinner userSpinner = null;

    JSONObject appUser;

    private String currentApp = null;

    TextView log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        log = findViewById(R.id.textView7);
        appSpinner = (Spinner) super.findViewById(R.id.spinner);
        appSpinner.setPrompt("请选择您的应用:");
        userSpinner = super.findViewById(R.id.spinner2);
        userSpinner.setPrompt("请选择您的用户名");
        userAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, userList);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(userAdapter);
        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String encrypyPass = appUser.getJSONObject(currentApp).getString((String) userSpinner.getItemAtPosition(position));
                EditText encryptPassEditText = findViewById(R.id.textView13);
                encryptPassEditText.setText(encrypyPass);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        appList = new ArrayList<CharSequence>();
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        if (preferences.contains("HighScore")) {
            appUser = JSON.parseObject(preferences.getString("HighScore", "{}"));
            appList.addAll(appUser.keySet());
        } else {
            appUser = new JSONObject();
        }
        appAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, appList);
        appAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        appSpinner.setAdapter(appAdapter);
        appSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                JSONObject users = appUser.getJSONObject((String) appSpinner.getItemAtPosition(position));
                currentApp = (String) appSpinner.getItemAtPosition(position);
                userList.clear();
                userList.addAll(users.keySet());
                userAdapter = new ArrayAdapter<CharSequence>(parent.getContext(), android.R.layout.simple_spinner_item, userList);
                userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                userSpinner.setAdapter(userAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                userSpinner.removeAllViews();
                userList.clear();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        EditText editText = (EditText) findViewById(R.id.editText);
        String appName = editText.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.editText2);
        String userName = editText2.getText().toString();
        EditText editText3 = (EditText) findViewById(R.id.editText3);
        String password = editText3.getText().toString();
        EditText editText4 = (EditText) findViewById(R.id.editText4);
        String secretKey = editText4.getText().toString();

        String encryptPass = encrypt(password, secretKey);

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        if (preferences.contains("HighScore")) {
            appUser = JSON.parseObject(preferences.getString("HighScore", "{}"));
        }
        JSONObject users;
        if (appUser.containsKey(appName)) {
            users = appUser.getJSONObject(appName);
        } else {
            users = new JSONObject();
            appUser.put(appName, users);
        }
        users.put(userName, encryptPass);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("HighScore", appUser.toJSONString());
        editor.apply();

        appList.clear();
        appList.addAll(appUser.keySet());
        appAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, appList);
        appAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        appSpinner.setAdapter(appAdapter);

        userList.clear();
        userAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, userList);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(userAdapter);
    }

    public void decryptClick(View v) {
        EditText encryptPass = findViewById(R.id.textView13);
        EditText decryptPass = findViewById(R.id.textView10);
        String encryptPassStr = encryptPass.getText().toString();
        if (encryptPassStr == null || encryptPassStr.trim().length() == 0) {
            decryptPass.setText("hello");
            return;
        }

        EditText keyText = findViewById(R.id.keyword);
        String key = keyText.getText().toString();
        if (key == null || key.trim().length() == 0) {
            decryptPass.setText("no hello");
            return;
        }

        String decryptPassStr = decrypt(encryptPassStr, key);
        decryptPass.setText(decryptPassStr);
    }

    public void writeToFile(View view) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
            } else {
                writeFile();
            }
        } else {
            log.setText("sdk < 6.0");
        }
    }

    public void importFromFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data.getData() != null) {
            if (data.getData().getScheme().equals("content")) {
                String s = data.getData().getPath();  //文件路径
                s = s.substring(1);
                log.setText("path:" + s);
                String filPath = Environment.getExternalStoragePublicDirectory("").getPath() + s.substring(s.indexOf("/"));
                File file = new File(filPath);
                log.setText("path:" + s);
                InputStream instream = null;
                try {
                    instream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    log.setText(e.getMessage());
                }
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    try {
                        String line = buffreader.readLine();
                        if (line != null) {
                            JSONObject jsonObject = JSON.parseObject(line);
                            for (Map.Entry entry : jsonObject.entrySet()) {
                                String app = (String) entry.getKey();
                                if (app == null || app.trim().length() == 0) {
                                    continue;
                                }
                                Object value = entry.getValue();
                                if (value instanceof JSONObject) {
                                    for (Map.Entry user : ((JSONObject) value).entrySet()) {
                                        String userName = (String) user.getKey();
                                        String pass = (String) user.getValue();
                                        if (userName == null || userName.trim().length() == 0 ||
                                                pass == null || pass.trim().length() == 0) {
                                            continue;
                                        }

                                        if (appUser.containsKey(app)) {
                                            appUser.getJSONObject(app).put(userName, pass);
                                        } else {
                                            JSONObject userObjct = new JSONObject();
                                            userObjct.put(userName, pass);
                                            appUser.put(app, userObjct);
                                        }
                                    }
                                }
                            }
                            SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("HighScore", appUser.toJSONString());
                            editor.apply();
                            appList.clear();
                            appList.addAll(appUser.keySet());
                            appAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, appList);
                            appAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            appSpinner.setAdapter(appAdapter);

                            userList.clear();
                            userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userList);
                            userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            userSpinner.setAdapter(userAdapter);
                            log.setText("import success");
                        }
                    } catch (Exception e) {
                        log.setText("failed parse " + e.getMessage());
                    }
                    try {
                        instream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                log.setText("scheme not contet");
            }
        } else {
            log.setText("data is null");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i : grantResults) {
                if (i != 0) {
                    log.setText("权限不够");
                    return;
                }
            }
        }
        writeFile();
    }

    private void writeFile() {

        String filPath = Environment.getExternalStoragePublicDirectory("").getPath() + "/Download/passFile/";
        log.setText(filPath);
        String fileName = "passOut.txt";
        File dir = new File(filPath);
        if (!(dir.exists())) {
            dir.mkdirs();
        }


        File file = new File(filPath + fileName);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                log.setText("创建文件失败" + filPath + e.getMessage());
                return;
            }
        } else {
            file.delete();
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                log.setText("创建文件失败" + filPath + e.getMessage());
                return;
            }
        }

        String appText = JSON.toJSONString(appUser);
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rwd");
        } catch (FileNotFoundException e) {
            log.setText("文件不存在");
            return;
        }
        try {
            raf.seek(0);
            raf.write(appText.getBytes());
            raf.close();
        } catch (IOException e) {
            log.setText("写文件失败" + e.getMessage());
        }

        log.setText("写入成功：" + filPath);
    }

    private String encrypt(String password, String secretKey) {
        //计算密文   0：密文为原始字符
        // 1：密文为原始字符+密钥字符后的结果
        // 2：密文为原始字符-密钥字符后的结果
        // 3：密文为密钥字符-原始字符后的结果
        int keyLength = secretKey.length();
        StringBuilder sp = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            int secInt = Integer.parseInt(String.valueOf(secretKey.charAt(i % keyLength)));
            if ((int) password.charAt(i) + secInt > 31 &&
                    (int) password.charAt(i) + secInt < 127) {
                sp.append('1');
                sp.append((char) ((int) password.charAt(i) + secInt));
            } else if ((int) password.charAt(i) - secInt > 31 &&
                    (int) password.charAt(i) - secInt < 127) {
                sp.append('2');
                sp.append((char) ((int) password.charAt(i) - secInt));
            } else if (secInt - (int) password.charAt(i) > 31 &&
                    secInt - (int) password.charAt(i) < 127) {
                sp.append('3');
                sp.append((char) (secInt - (int) password.charAt(i)));
            } else {
                sp.append('0');
                sp.append(password.charAt(i));
            }
        }

        return sp.toString();
    }


    private String decrypt(String encryptpass, String secretKey) {
        //计算密文   0：密文为原始字符
        // 1：密文为原始字符+密钥字符后的结果
        // 2：密文为原始字符-密钥字符后的结果
        // 3：密文为密钥字符-原始字符后的结果
        StringBuilder temppassword = new StringBuilder();
        int keyIndex = 0;
        int keyLength = secretKey.length();
        for (int i = 0; i < encryptpass.length(); i++) {
            char encryptType = encryptpass.charAt(i);
            int keyInt = Integer.parseInt(String.valueOf(secretKey.charAt(keyIndex % keyLength)));
            switch (encryptType) {
                //0：密文为原始字符
                // 1：密文为原始字符+密钥字符后的结果
                // 2：密文为原始字符-密钥字符后的结果
                // 3：密文为密钥字符-原始字符后的结果
                case '0': {
                    temppassword.append(String.valueOf(encryptpass.charAt(i + 1)));
                    break;
                }
                case '1': {
                    temppassword.append((char) ((int) encryptpass.charAt(i + 1) - keyInt));
                    break;
                }
                case '2': {
                    temppassword.append((char) ((int) encryptpass.charAt(i + 1) + keyInt));
                    break;
                }
                case '3': {
                    temppassword.append((char) (keyInt - (int) encryptpass.charAt(i + 1)));
                    break;
                }
                default: {
                    temppassword.append("failed to decrypt");
                    return temppassword.toString();
                }
            }
            i++;
            keyIndex++;
        }
        return temppassword.toString();
    }
}
