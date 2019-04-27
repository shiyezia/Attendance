package com.example.attendance;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private FloatingActionButton action_a;
    private FloatingActionButton action_b;
    private FloatingActionButton action_c;
    private ListView listView;
    private SQLiteDatabase db;
    List<Map<String,String>> data=new ArrayList<Map<String, String>>();
    public void init(int Sno) {
        DbHelper dbhelper = new DbHelper(this);
        db = dbhelper.getWritableDatabase();
        String sql="select course from elective where Sno=?";
        Cursor cursor = db.rawQuery(sql, new String[]{Sno+" "});
        while (cursor.moveToNext()){
            Map<String, String> courselist = new HashMap<String, String>();
            courselist.put("course",cursor.getString(cursor.getColumnIndex("course")));
            data.add(courselist);
        }
    }

    public void showlist(String Sno){

        SimpleAdapter adapter= new SimpleAdapter(LoginActivity.this,
                data,
                R.layout.course,
                new String[]{"course"},
                new int[]{R.id.text});
        listView.setAdapter(adapter);
    }

    public void insert(int Sno1,String course){
        ContentValues cv = new ContentValues();
        cv.put("Sno",Sno1);
        cv.put("course",course);
        db.insert("elective",null,cv);
        Map<String, String> courselist = new HashMap<String, String>();
        courselist.put("course",course);
        data.add(courselist);
    }

    public void delete(String Sno2,String course){
        db.delete("elective","Sno=? and course=?",new String[]{Sno2,course});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        action_a = (FloatingActionButton) findViewById(R.id.action_a);
        action_b = (FloatingActionButton) findViewById(R.id.action_b);
        action_c = (FloatingActionButton) findViewById(R.id.action_c);
        listView =(ListView) findViewById(R.id.listview);
        Intent intent = getIntent();
        final int Sno = intent.getIntExtra("Sno",1);
        init(Sno);
        final String SSno=Integer.toString(Sno);
        showlist(SSno);
        action_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(LoginActivity.this);
                new AlertDialog.Builder(LoginActivity.this).setTitle("请输入课程名称")
                        .setIcon(android.R.drawable.sym_def_app_icon)
                        .setView(et)
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        insert(Sno,et.getText().toString());
                        showlist(SSno);
                    }
                }).setNegativeButton("取消",null).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("确定删除?");
                builder.setTitle("提示");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String,String> map=(HashMap<String,String>)listView.getItemAtPosition(position);
                        String Course=map.get("course");
                        delete(SSno,Course);
                        data.remove(position);
                        showlist(SSno);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                });
                builder.create().show();
                return false;
            }
        });
    }

}
