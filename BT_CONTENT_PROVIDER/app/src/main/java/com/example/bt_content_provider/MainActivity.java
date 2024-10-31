package com.example.bt_content_provider;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends Activity implements OnClickListener{

    Button btnshowallcontact;
    Button btnaccesscalllog;
    Button btnaccessmediastore;
    Button btnaccessbookmarks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kiểm tra quyền READ_CONTACTS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }

        btnshowallcontact=(Button) findViewById(R.id.btnshowallcontact);
        btnshowallcontact.setOnClickListener(this);

        // Kiểm tra quyền READ_CALL_LOG
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, 2);
        }
        btnaccesscalllog=(Button) findViewById(R.id.btnaccesscalllog);
        btnaccesscalllog.setOnClickListener(this);

        // Kiểm tra quyền READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        }
        btnaccessmediastore=(Button) findViewById(R.id.btnmediastore);
        btnaccessmediastore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        if(v==btnshowallcontact)
        {
            intent=new Intent(this, ShowAllContactActivity.class);
            startActivity(intent);
        }
        else if(v==btnaccesscalllog)
        {
            accessTheCallLog();
        }
        else if(v==btnaccessmediastore)
        {
            accessMediaStore();
        }
        else if(v==btnaccessbookmarks)
        {

        }

    }
    /**
     * hàm lấy danh sách lịch sử cuộc gọi
     * với thời gian nhỏ hơn 30 giây và sắp xếp theo ngày gọi
     */
    public void accessTheCallLog()
    {
        String [] projection=new String[]{
                Calls.DATE,
                Calls.NUMBER,
                Calls.DURATION
        };
        Cursor c=getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                Calls.DURATION+"<?",new String[]{"30"},
                Calls.DATE +" Asc");
        c.moveToFirst();
        String s="";
        while(c.isAfterLast()==false){
            for(int i=0;i<c.getColumnCount();i++){
                s+=c.getString(i)+" - ";
            }
            s+="\n";
            c.moveToNext();
        }
        c.close();
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
    /**
     * hàm đọc danh sách các Media trong SD CARD
     */
    public void accessMediaStore()
    {
        String []projection={
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.MIME_TYPE
        };
        CursorLoader loader=new CursorLoader
                (this, Media.EXTERNAL_CONTENT_URI,
                        projection, null, null, null);
        Cursor c=loader.loadInBackground();
        c.moveToFirst();
        String s="";
        while(!c.isAfterLast()){
            for(int i=0;i<c.getColumnCount();i++){
                s+=c.getString(i)+" - ";
            }
            s+="\n";
            c.moveToNext();
        }
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        c.close();
    }
    /**
     * hàm đọc danh sách Bookmark trong trình duyệt
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: // READ_CONTACTS
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Quyền được cấp, thực hiện thao tác liên quan
                    Toast.makeText(this, "Permission for contacts granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission for contacts denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2: // READ_CALL_LOG
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission for call logs granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission for call logs denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3: // READ_EXTERNAL_STORAGE
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission for media storage granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission for media storage denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}
