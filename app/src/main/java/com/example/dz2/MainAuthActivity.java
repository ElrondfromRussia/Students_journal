package com.example.dz2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainAuthActivity extends AppCompatActivity  implements View.OnClickListener{

    private Button btn_sign;
    private Button btn_registr;

    private EditText email;
    private EditText password;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //avto signing
    public void auto_auth()
    {
        SharedPreferences prefs;
        prefs = getSharedPreferences("login_and_email_buffer", Context.MODE_PRIVATE);
        String em = prefs.getString("email", "");
        String pas = prefs.getString("passw", "");
        email.setText(em);
        password.setText(pas);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_auth);

        email = (EditText) findViewById(R.id.mail);
        password = (EditText)findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();
        //Слушать на состояние пользователя
        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    Toast.makeText(MainAuthActivity.this, "Пользователь существует", Toast.LENGTH_SHORT).show();

                } else
                {
                    Toast.makeText(MainAuthActivity.this, "Вас еще нет", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // /////////////////////////////////////////////////////////////
        btn_sign = (Button) findViewById(R.id.btn_sign);
        btn_registr = (Button) findViewById(R.id.btn_registry);

        btn_sign.setOnClickListener(this);
        btn_registr.setOnClickListener(this);

        //Toast.makeText(MainAuthActivity.this,"Inited",Toast.LENGTH_SHORT).show();
        auto_auth();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_sign:
            {
                if(!email.getText().toString().isEmpty())
                    Signing(email.getText().toString(),password.getText().toString());
                else
                    Toast.makeText(MainAuthActivity.this,"Введите логин для входа",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btn_registry:
            {
                if(!email.getText().toString().isEmpty())
                    Registration(email.getText().toString(),password.getText().toString());
                else
                    Toast.makeText(MainAuthActivity.this,"Введите логин для регистрации",Toast.LENGTH_SHORT).show();
                break;
            }
            default:
            {
                Toast.makeText(MainAuthActivity.this,"Ошибка id",Toast.LENGTH_SHORT).show();
                break;
            }

        }
    }
//
    public  void Signing(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    SharedPreferences prefs = MainAuthActivity.this.getSharedPreferences("login_and_email_buffer", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("email", MainAuthActivity.this.email.getText().toString());
                    editor.putString("passw", MainAuthActivity.this.password.getText().toString());
                    editor.commit();

                    Toast.makeText(MainAuthActivity.this, "Авторизация успешна", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainAuthActivity.this,MainActivity.class));
                }
                else
                    Toast.makeText(MainAuthActivity.this,"Ошибка авторизации",Toast.LENGTH_SHORT).show();
            }
        });

    }
    public  void Registration(String email,String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful()) {
                    Toast.makeText(MainAuthActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                    Signing(MainAuthActivity.this.email.getText().toString(), MainAuthActivity.this.password.getText().toString());
                }
                else
                    Toast.makeText(MainAuthActivity.this,"Ошибка регистрации",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
