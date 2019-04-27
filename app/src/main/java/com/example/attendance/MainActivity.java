package com.example.attendance;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ClearEditText username;
    private ClearEditText passward;
    private Button login1;
    private Button register;
    private String user;
    private String pass;
    private DAO dao;


    student s= new student("tangzhihao",18,26810821,"物联网1501","520");
    student s2= new student("zhangsan",18,28113,"物联网1501","qwerqwrq");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username=(ClearEditText)findViewById(R.id.username);
        passward=(ClearEditText)findViewById(R.id.password);
        login1=(Button)findViewById(R.id.login);
        register=(Button)findViewById(R.id.register);
        dao=new DAO(MainActivity.this);

        //insert(s);insert(s2);
        //db.delete("students",null,null);
        //db.execSQL("update sqlite_sequence set seq=0 where name='students'");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,register.class);
                startActivity(intent);
            }
        });
        login1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user=username.getText().toString();
                pass=passward.getText().toString();
                Cursor cursor=dao.login(user);
                cursor.moveToFirst();
                if(cursor.getCount()<1){
                    Toast ts =Toast.makeText(MainActivity.this,"用户不存在",Toast.LENGTH_LONG
                    );
                    ts.show();
                }
                else if(cursor.getString(cursor.getColumnIndex("passward")).equals(pass)){
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    intent.putExtra("Sno",cursor.getInt(cursor.getColumnIndex("Sno")));
                    startActivity(intent);
                }
                else {
                    Toast ts =Toast.makeText(MainActivity.this,"密码错误",Toast.LENGTH_LONG
                    );
                    ts.show();
                }
                cursor.close();
            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dao.close();
    }
}
