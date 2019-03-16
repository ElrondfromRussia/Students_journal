package com.example.dz2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private String current_table_group_name = "";

    private Button btn_add;
    private Button btn_update;
    private Button btn_delete;

    private TextView headlbl;
    private EditText add_del_name;
    private ListView mainList;

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
                //Toast.makeText(MainActivity.this, "Новости", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
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
    public void add_smth()
    {
        try {
            if(!add_del_name.getText().toString().isEmpty())
                mRef.child(user.getUid()).child("groups").child(add_del_name.getText().toString()).setValue(add_del_name.getText().toString());
            add_del_name.setText("");
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this,"Ошибка добавления",Toast.LENGTH_SHORT).show();
        }
    }
    public void delete_smth(String del_name)
    {
        try {
            if(!del_name.isEmpty())
                mRef.child(user.getUid()).child("groups").
                        child(del_name.toString()).removeValue();
            add_del_name.setText("");
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this,"Ошибка удаления",Toast.LENGTH_SHORT).show();
        }
    }
    // /////////////////////////////////////////////////////////////
    public void go_to_page(String gr_name, int pos)
    {
        Toast.makeText(this, gr_name, Toast.LENGTH_SHORT).show();
        current_table_group_name = gr_name;

        Intent intent = new Intent(this, Group_Activity.class);
        intent.putExtra("tabName", current_table_group_name );
        startActivity(intent);
    }
    // /////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headlbl = (TextView) findViewById(R.id.header);
        mainList = (ListView)  findViewById(R.id.mainlist);
        add_del_name = (EditText)findViewById(R.id.add_del_name);

        // /////////////////////////////////////////////////////////////
        btn_add = (Button) findViewById(R.id.butadd);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_smth();
            }
        });
        // /////////////////////////////////////////////////////////////
        btn_update = (Button) findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fill_list();
            }
        });

        //////////////////////////////////////////////////
        mRef= FirebaseDatabase.getInstance().getReference();
        user = mAuth.getInstance().getCurrentUser();
        DiscrTasks = new ArrayList<>();

        mRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //Toast.makeText(MainActivity.this,"Обновление",Toast.LENGTH_SHORT).show();
                try {
                    if (!DiscrTasks.isEmpty())
                        DiscrTasks.clear();
                    for (DataSnapshot dsp : dataSnapshot.child("groups").getChildren())
                    {
                        DiscrTasks.add(String.valueOf(dsp.getKey()));
                    }
                    updateUI();
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this,"Сбой",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(MainActivity.this,"Не получилось",Toast.LENGTH_SHORT).show();
            }
        });
        //////////////////////////////////////////////////

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                go_to_page(mainList.getItemAtPosition(position).toString(), position);
            }
        });

        mainList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                    adb.setTitle("Удалить?");
                    adb.setMessage("Вы точно желаете удалить " + mainList.getItemAtPosition(position).toString() + "?");
                    final int positionToRemove = position;
                    adb.setNegativeButton("Не надо!", null);
                    adb.setPositiveButton("Ага", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            delete_smth(mainList.getItemAtPosition(positionToRemove).toString());
                        }
                    });
                    adb.show();
                    return true;
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this,"Ошибка удаления",Toast.LENGTH_SHORT).show();
                }
                return  false;
            }
        });

    }
    /////////////////////////////////////////////////////////////////
    private void updateUI()
    {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, DiscrTasks);
            mainList.setAdapter(adapter);
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this,"Сбой обновления",Toast.LENGTH_SHORT).show();
        }
    }
    /////////////////////////////////////////////////////////////////
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}
