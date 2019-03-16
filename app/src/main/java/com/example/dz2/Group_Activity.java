package com.example.dz2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Group_Activity extends AppCompatActivity {

    private String current_table_group_name = "";

    private Button btn_stadd;
    private Button btn_stdelete;
    private Button upd_btn;

    private TextView headlblst;
    private EditText add_del_stname;
    private ListView studList;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private List<String> DiscrTasks;
    private FirebaseUser user;

    ////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.action_news:
                //Toast.makeText(Group_Activity.this, "Новости", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Group_Activity.this, NewsActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
    // /////////////////////////////////////////////////////////////
    public void fill_list()
    {
        updateUI();
    }
    // /////////////////////////////////////////////////////////////
    public void add_smb()
    {
        try {
            if(!add_del_stname.getText().toString().isEmpty())
                mRef.child(user.getUid()).child("groups")
                        .child(current_table_group_name)
                        .child(add_del_stname.getText().toString()).setValue(add_del_stname.getText().toString());
            add_del_stname.setText("");
        }
        catch (Exception e)
        {
            Toast.makeText(Group_Activity.this,"Ошибка добавления",Toast.LENGTH_SHORT).show();
        }
    }
    public void delete_smb(String del_name)
    {
        try {
            if(!del_name.isEmpty())
                mRef.child(user.getUid()).child("groups")
                        .child(current_table_group_name)
                        .child(del_name).removeValue();
            add_del_stname.setText("");
        }
        catch (Exception e)
        {
            Toast.makeText(Group_Activity.this,"Ошибка удаления",Toast.LENGTH_SHORT).show();
        }
    }
    // /////////////////////////////////////////////////////////////
    public void go_to_page(String st_name)
    {
        Toast.makeText(this, st_name, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Student_Activity.class);
        intent.putExtra("stName", st_name );
        intent.putExtra("grName", current_table_group_name );
        startActivity(intent);
    }
    // /////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_);

        Intent intent = getIntent();
        current_table_group_name = intent.getStringExtra("tabName");

        headlblst = (TextView) findViewById(R.id.headerst);
        headlblst.setText(current_table_group_name);
        studList = (ListView)  findViewById(R.id.mainlistst);
        add_del_stname = (EditText)findViewById(R.id.add_del_stname);
        // /////////////////////////////////////////////////////////////
        btn_stadd = (Button) findViewById(R.id.butaddst);
        btn_stadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_smb();
            }
        });
        // /////////////////////////////////////////////////////////////
        upd_btn = (Button) findViewById(R.id.upd_btn);
        upd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fill_list();
            }
        });
        //////////////////////////////////////////////
        mRef= FirebaseDatabase.getInstance().getReference();
        user = mAuth.getInstance().getCurrentUser();
        DiscrTasks = new ArrayList<>();

        mRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //Toast.makeText(Group_Activity.this,"Обновление",Toast.LENGTH_SHORT).show();
                try {
                    if (!DiscrTasks.isEmpty())
                        DiscrTasks.clear();
                    for (DataSnapshot dsp : dataSnapshot.child("groups")
                            .child(current_table_group_name.toString()).getChildren())
                    {
                        DiscrTasks.add(String.valueOf(dsp.getKey()));
                    }
                    updateUI();
                }
                catch (Exception e)
                {
                    Toast.makeText(Group_Activity.this,"Сбой",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(Group_Activity.this,"Не получилось",Toast.LENGTH_SHORT).show();
            }
        });
        //////////////////////////////////////////////////
        studList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                go_to_page(studList.getItemAtPosition(position).toString());
            }
        });

        studList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    AlertDialog.Builder adb = new AlertDialog.Builder(Group_Activity.this);
                    adb.setTitle("Удалить?");
                    adb.setMessage("Вы точно желаете удалить " + studList.getItemAtPosition(position).toString() + "?");
                    final int positionToRemove = position;
                    adb.setNegativeButton("Не надо!", null);
                    adb.setPositiveButton("Ага", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            delete_smb(studList.getItemAtPosition(positionToRemove).toString());
                        }
                    });
                    adb.show();
                    return true;
                }
                catch (Exception e)
                {
                    Toast.makeText(Group_Activity.this,"Ошибка удаления",Toast.LENGTH_SHORT).show();
                }
                return  false;
            }
        });

    }
    //////////////////////////////////////////////////////
    private void updateUI()
    {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Group_Activity.this, android.R.layout.simple_list_item_1, DiscrTasks);
            studList.setAdapter(adapter);
        }
        catch (Exception e)
        {
            Toast.makeText(Group_Activity.this,"Сбой обновления",Toast.LENGTH_SHORT).show();
        }
    }
    //////////////////////////////////////////////////////////
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}
