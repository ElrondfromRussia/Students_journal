package com.example.dz2;

import android.content.Intent;
import android.support.annotation.NonNull;
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

public class Student_Activity extends AppCompatActivity {

    private String current_table_group_name = "";
    private String current_table_student_name = "";

    private Button butaddmark;
    private Button butapply;
    private  Button clear_btn;

    private TextView headernam;
    private EditText ch_studname;
    private EditText mark_t;
    private ListView markslistst;

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
        switch (id) {
            case R.id.action_news:
                //Toast.makeText(Student_Activity.this, "Новости", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Student_Activity.this, NewsActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
    // /////////////////////////////////////////////////////////////
    ///////////////////////////
    public void clear_marks()
    {
        try {
                mRef.child(user.getUid()).child("groups")
                        .child(current_table_group_name)
                        .child(current_table_student_name)
                        .child("marks").removeValue();
        }
        catch (Exception e)
        {
            Toast.makeText(Student_Activity.this,"Ошибка удаления",Toast.LENGTH_SHORT).show();
        }
    }
    // ////////////////////////
    public void fill_list()
    {
        updateUI();
    }
    // /////////////////////////////////////////////////////////////
    public void add_smth()
    {
        try {
            if(!mark_t.getText().toString().isEmpty())
                mRef.child(user.getUid()).child("groups")
                        .child(current_table_group_name)
                        .child(current_table_student_name)
                        .child("marks")
                        .child(mark_t.getText().toString()).setValue(mark_t.getText().toString());
            mark_t.setText("");
        }
        catch (Exception e)
        {
            Toast.makeText(Student_Activity.this,"Ошибка добавления",Toast.LENGTH_SHORT).show();
        }
    }
    public void apply_smth()
    {
        String new_name = ch_studname.getText().toString();
        try {
            if(!new_name.isEmpty()) {
                ArrayList<String> marks = new ArrayList<>();
                for (int i = 0; i < markslistst.getAdapter().getCount(); i++)
                {
                    marks.add(markslistst.getAdapter().getItem(i).toString());
                }

                mRef.child(user.getUid()).child("groups")
                        .child(current_table_group_name)
                        .child(current_table_student_name).removeValue();

                mRef.child(user.getUid()).child("groups")
                        .child(current_table_group_name)
                        .child(new_name).setValue(new_name);

                for(String mr : marks)
                {
                    mRef.child(user.getUid()).child("groups")
                            .child(current_table_group_name)
                            .child(new_name)
                            .child("marks")
                            .child(mr).setValue(mr);
                }

                current_table_student_name = new_name;

                headernam.setText(new_name);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(Student_Activity.this,"Ошибка изменения",Toast.LENGTH_SHORT).show();
        }
    }
    // /////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_);

        Intent intent = getIntent();
        current_table_group_name = intent.getStringExtra("grName");
        current_table_student_name = intent.getStringExtra("stName");

        headernam = (TextView) findViewById(R.id.headernam);
        headernam.setText(current_table_student_name);
        markslistst = (ListView)  findViewById(R.id.markslistst);
        ch_studname = (EditText)findViewById(R.id.ch_studname);
        ch_studname.setText(current_table_student_name);
        mark_t = (EditText)findViewById(R.id.mark_t);
        // /////////////////////////////////////////////////////////////
        butaddmark = (Button) findViewById(R.id.butaddmark);
        butaddmark = (Button) findViewById(R.id.butaddmark);
        butaddmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_smth();
            }
        });
        butapply = (Button) findViewById(R.id.butapply);
        butapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply_smth();
            }
        });
        clear_btn = (Button)findViewById(R.id.clear_btn);
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear_marks();
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
                //Toast.makeText(Student_Activity.this,"Обновление",Toast.LENGTH_SHORT).show();
                try {
                    if (!DiscrTasks.isEmpty())
                        DiscrTasks.clear();
                    for (DataSnapshot dsp : dataSnapshot.child("groups")
                            .child(current_table_group_name.toString())
                            .child(current_table_student_name)
                            .child("marks")
                            .getChildren())
                    {
                        DiscrTasks.add(String.valueOf(dsp.getKey()));
                    }
                    updateUI();
                }
                catch (Exception e)
                {
                    Toast.makeText(Student_Activity.this,"Сбой",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(Student_Activity.this,"Не получилось",Toast.LENGTH_SHORT).show();
            }
        });

        markslistst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
    //////////////////////////////////////////////////////
    private void updateUI()
    {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Student_Activity.this, android.R.layout.simple_list_item_1, DiscrTasks);
            markslistst.setAdapter(adapter);
        }
        catch (Exception e)
        {
            Toast.makeText(Student_Activity.this,"Сбой обновления",Toast.LENGTH_SHORT).show();
        }
    }
    //////////////////////////////////////////////////////////
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}
