package com.example.a202sgi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mrecyclerView;
    private FloatingActionButton mfab;

    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private ProgressDialog loader;

    private String key = "";
    //visualize to data
    private String task;
    private String details;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //initialise the variables
        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("To-Do-List Homepage");

        mrecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(linearLayoutManager);


        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        mReference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);


        mfab = findViewById(R.id.fab);
        //we want to know the input file in the previous layout
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTask();
            }
        });
    }

    private void createTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);


        View myView = layoutInflater.inflate(R.layout.input_data,null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);
        //by touching outside does not make it disappear
        final EditText task = myView.findViewById(R.id.etTaskName);
        final EditText details = myView.findViewById(R.id.etTaskDetails);
        Button save = myView.findViewById(R.id.btnSave);
        Button cancel = myView.findViewById(R.id.btnCancel);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mTask = task.getText().toString().trim();
                String mDetails = details.getText().toString().trim();
                String id = mReference.push().getKey();

                String date = DateFormat.getDateInstance().format(new Date());

                if (TextUtils.isEmpty(mTask)){
                    task.setError("Task name is required!");
                    return;
                }
                if (TextUtils.isEmpty(mDetails)){
                    task.setError("Task details are required");
                    return;
                }else{
                    loader.setMessage("Adding your data");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();


                    Model model = new Model (mTask,mDetails,id,date);
                    //we need to create an object of Model class
                    //if id and date put inside onClick, cause red lines
                    mReference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //inform the user that their data has been successfully retrieved
                                Toast.makeText(HomeActivity.this, "Task has been created!", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }else{
                                String error = task.getException().toString();
                                //give user the error message
                                Toast.makeText(HomeActivity.this, "Sorry, fail to save created task" + error, Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }

                        }
                    });

                }
            }
        });
        dialog.show();
        //and to show the dialog
    }//end of createTask();

    //so now we need to create Firebase Datbase Recycler Adapter
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(mReference,Model.class)
                .build();

        FirebaseRecyclerAdapter<Model,myViewHolder> adapter = new FirebaseRecyclerAdapter<Model, myViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myViewHolder holder, final int position, @NonNull final Model model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDetails(model.getDetails());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //we want to fetch the data from the particular task and paste it to update data
                        key = getRef(position).getKey();
                        task = model.getTask();
                        details = model.getDetails();

                        updateTask();
                    }

                });
            }

            @NonNull
            @Override
            public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from (parent.getContext()).inflate(R.layout.retrieved_data
                        ,parent,false);
                return new myViewHolder(view);


            }
        };

        //set the adapter to the recycler view
        mrecyclerView.setAdapter(adapter);
        adapter.startListening();
    }



    //create an Adapter class that will help us to retrieve the data
    public static class myViewHolder extends  RecyclerView.ViewHolder{
        View mView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            //we need to set
            mView = itemView;
        }

        //getters and setters so that we can able to use text views that we have been in retrieved layout
        public void setTask(String task){
            TextView taskTextView = mView.findViewById(R.id.tvTask);
            //we are going to set the text view
            taskTextView.setText(task);
        }

        public void setDetails (String details){
            TextView detailsTextView = mView.findViewById(R.id.tvDetails);
            detailsTextView.setText(details);
        }

        public void setDate (String date){
            TextView dateText = mView.findViewById(R.id.dateTv);
            dateText.setText(date);
        }
    }

    public void updateTask(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_data,null);
        //TODO:Research about this
        myDialog.setView(view);

        final AlertDialog dialog = myDialog.create();
        //EditText fields in update data
        final EditText mTask = view.findViewById(R.id.updateTask);
        final EditText mDetails = view.findViewById(R.id.updateDetails);
        //set the edited task to the task that was originally there

        mTask.setText(task);
        mTask.setSelection(task.length());

        mDetails.setText(details);
        mDetails.setSelection(details.length());

        Button mbtnDel = view.findViewById(R.id.btnDel);
        Button mbtnUpdate = view.findViewById(R.id.btnUpdate);

        mbtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task = mTask.getText().toString().trim();
                details = mDetails.getText().toString().trim();

                //get the date
                String date = DateFormat.getDateInstance().format(new Date());

                Model model = new Model(task,details,key,date);
                //an object of the model class -- give the parameters


                mReference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(HomeActivity.this, "Update done!", Toast.LENGTH_SHORT).show();
                        }else{
                            String error = task.getException().toString();
                            Toast.makeText(HomeActivity.this, "Sorry, update fail" + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //update the particular task
                dialog.dismiss();
            }
        });

        mbtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(HomeActivity.this, "Deletion done!", Toast.LENGTH_SHORT).show();
                        }else{
                            String error = task.getException().toString();
                            Toast.makeText(HomeActivity.this, "Sorry, deletion fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //delete the particular task
                dialog.dismiss();
            }
        });

        dialog.show();;

    }//end of updateTask()

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                mAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}