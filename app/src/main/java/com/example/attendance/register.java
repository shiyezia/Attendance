package com.example.attendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class register extends AppCompatActivity {
    private ClearEditText Sno;
    private ClearEditText name;
    private ClearEditText cls;
    private ClearEditText age;
    private ClearEditText password;
    private Button getface;
    private Button register;
    private student student;
    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Sno=(ClearEditText)findViewById(R.id.Sno);
        name=(ClearEditText)findViewById(R.id.name);
        age=(ClearEditText)findViewById(R.id.age);
        cls=(ClearEditText)findViewById(R.id.cls);
        password=(ClearEditText)findViewById(R.id.password);
        getface=(Button)findViewById(R.id.face);
        register=(Button)findViewById(R.id.register);
        dao=new DAO(register.this);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a=Integer.parseInt(age.getText().toString());
                int b=Integer.parseInt(Sno.getText().toString());
                student=new student(name.getText().toString(),
                        a, b,
                        cls.getText().toString(),
                        password.getText().toString());
                        dao.insertS(student);
            }
        });
        getface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(register.this,camera.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dao.close();
    }
}
