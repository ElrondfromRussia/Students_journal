package com.example.dz2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewsActivity extends AppCompatActivity {

    private Button butadd;
    private Button butupd;
    private TextView headernam;
    private EditText new_new;
    private ListView newslistst;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private List<MyNew> DiscrTasks;
    private FirebaseUser user;

    public void add_smth()
    {
        try {
            if(!new_new.getText().toString().isEmpty()) {
                Date data = new Date();
                mRef.child("news").child(data.toString()).child("user").setValue(user.getEmail());
                mRef.child("news").child(data.toString()).child("text").setValue(new_new.getText().toString());
            }
            new_new.setText("");
        }
        catch (Exception e)
        {
            Toast.makeText(NewsActivity.this,"Ошибка добавления",Toast.LENGTH_SHORT).show();
        }
    }
    ///////////////////////////////////////////////////////////
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                try {
                    LayoutInflater li = LayoutInflater.from(NewsActivity.this);
                    View promptsView = li.inflate(R.layout.redact_new, null);
                    //Log.d("BLET", "1");
                    final int positionToRedact = info.position;

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            NewsActivity.this);

                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);

                    final MyNew mnew1 = (MyNew) newslistst.getItemAtPosition(positionToRedact);
                    userInput.setText(mnew1.getNewText().toString());

                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            if(mnew1.getUserMail().toString().equals(user.getEmail().toString()))
                                                mRef.child("news").child(mnew1.getDate().toString()).child("text").setValue(userInput.getText().toString());
                                            else
                                                Toast.makeText(NewsActivity.this,"Хммм... Это чужая новость!",Toast.LENGTH_SHORT).show();
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                catch (Exception e)
                {
                    Toast.makeText(NewsActivity.this,"Ничего не изменить...",Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.delete:
                try {
                    AlertDialog.Builder adb = new AlertDialog.Builder(NewsActivity.this);
                    adb.setTitle("Удалить?");
                    adb.setMessage("Вы точно желаете удалить эту новость?");
                    final int positionToRemove = info.position;
                    adb.setNegativeButton("Не надо!", null);
                    adb.setPositiveButton("Ага", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MyNew mnew = (MyNew)newslistst.getItemAtPosition(positionToRemove);
                            if(mnew.getUserMail().toString().equals(user.getEmail().toString()))
                                mRef.child("news").child(mnew.getDate().toString()).removeValue();
                            else
                                Toast.makeText(NewsActivity.this,"Хммм... Это чужая новость!",Toast.LENGTH_SHORT).show();
                        }
                    });
                    adb.show();
                    return true;
                }
                catch (Exception e)
                {
                    Toast.makeText(NewsActivity.this,"Ошибка удаления",Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    /////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        headernam = (TextView) findViewById(R.id.header_news);
        newslistst = (ListView)  findViewById(R.id.newslist);
        registerForContextMenu(newslistst);
        new_new = (EditText)findViewById(R.id.new_name);

        // /////////////////////////////////////////////////////////////
        butadd = (Button) findViewById(R.id.btn_addnew);
        butadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_smth();
            }
        });

        butupd = (Button) findViewById(R.id.btn_updatenews);
        butupd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
            }
        });

        mRef= FirebaseDatabase.getInstance().getReference();
        user = mAuth.getInstance().getCurrentUser();
        DiscrTasks = new ArrayList<>();

        mRef.child("news").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //Toast.makeText(NewsActivity.this,"Обновление",Toast.LENGTH_SHORT).show();
                try {
                    if (!DiscrTasks.isEmpty())
                        DiscrTasks.clear();
                    for (DataSnapshot dsp : dataSnapshot.getChildren())
                    {
                        MyNew mnew = new MyNew(String.valueOf(dsp.getKey()),
                                dsp.child("user").getValue().toString(),
                                dsp.child("text").getValue().toString());
                        DiscrTasks.add(mnew);
                    }
                    updateUI();
                }
                catch (Exception e)
                {
                    Toast.makeText(NewsActivity.this,"Сбой",Toast.LENGTH_SHORT).show();
                }
        }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(NewsActivity.this,"Не получилось",Toast.LENGTH_SHORT).show();
            }
        });
    }
    //////////////////////////////////////////////////////
    private void updateUI()
    {
        try {
            newslistst.setAdapter(new CustomListAdapter(this, DiscrTasks));
        }
        catch (Exception e)
        {
            Toast.makeText(NewsActivity.this,"Сбой обновления",Toast.LENGTH_SHORT).show();
        }
    }
    //////////////////////////////////////////////////////////
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}
