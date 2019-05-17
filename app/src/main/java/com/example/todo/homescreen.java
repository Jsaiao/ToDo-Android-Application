package com.example.todo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class homescreen extends AppCompatActivity {

    RecyclerView r;
    FloatingActionButton fab;

    private View popupInputDialogView = null;
    private EditText taskName = null;
    private EditText taskDescription = null;
    private CheckBox status = null;
    private ImageView saveUserDataButton = null;
    private ImageView cancelUserDataButton = null;

    final List<Task> lstTask = new ArrayList<>() ;
    final int spanCount = 2; // 2 columns
    final int spacing = 40; // 40px
    final boolean includeEdge = true;

    final String ADD_TASK_URL = "http://192.168.31.122:80/todo/addTask.php";
    final String GET_ALL_TASK_URL = "http://192.168.31.122:80/todo/getTask.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        Bundle bundle = getIntent().getExtras();
        final String username = bundle.getString("username");

        displayTask(GET_ALL_TASK_URL,username);

        initMainActivityControls();



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(homescreen.this);
                // Set title, icon, can not cancel properties.
                alertDialogBuilder.setTitle("Add Task");
                //alertDialogBuilder.setIcon(R.drawable.ic_launcher_background);
                alertDialogBuilder.setCancelable(false);

                initPopupViewControls();

                alertDialogBuilder.setView(popupInputDialogView);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                saveUserDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String TaskName = taskName.getText().toString();
                        final String TaskDescription = taskDescription.getText().toString();
                        String TaskStatus = "0";
                        if(status.isChecked())
                            TaskStatus = "1";

                      //  Toast.makeText(homescreen.this,TaskName+"\n"+TaskDescription+"\n"+TaskStatus+"\n"+username,Toast.LENGTH_LONG).show();

                        if(TaskName.length()>=1)
                        {
                            final String finalTaskStatus = TaskStatus;
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_TASK_URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //Toast.makeText(homescreen.this, response, Toast.LENGTH_LONG).show();

                                    Snackbar.make(view, response, Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                    try {
                                        //TODO: Display Message if User is properly registered

                                    } catch (Exception e) {
                                        Toast.makeText(homescreen.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(homescreen.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("taskname", TaskName);
                                    params.put("taskdescription", TaskDescription);
                                    params.put("username", username);
                                    params.put("status", finalTaskStatus);
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(homescreen.this);
                            requestQueue.add(stringRequest);
                            alertDialog.cancel();
                        }
                        else
                        {
                            Toast.makeText(homescreen.this,"Please enter Task Name",Toast.LENGTH_LONG).show();
                        }
                    }
                });

                cancelUserDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });


            }
        });
    }

    private void initMainActivityControls()
    {
        if(fab == null)
        {
            fab = findViewById(R.id.fab);
        }
        /*
        if(userDataListView == null)
        {
            userDataListView = (ListView)findViewById(R.id.listview_user_data);
        }*/
    }

    private void initPopupViewControls()
    {
        // Get layout inflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(homescreen.this);

        // Inflate the popup dialog from a layout xml file.
        popupInputDialogView = layoutInflater.inflate(R.layout.addtask__popup, null);

        // Get user input edittext and button ui controls in the popup dialog.
        taskName = (EditText) popupInputDialogView.findViewById(R.id.et_tName);
        taskDescription = (EditText) popupInputDialogView.findViewById(R.id.et_tDescription);
        status = (CheckBox)popupInputDialogView.findViewById(R.id.cb_status) ;
      //  emailEditText = (EditText) popupInputDialogView.findViewById(R.id.email);
        saveUserDataButton = popupInputDialogView.findViewById(R.id.bt_save);
        cancelUserDataButton = popupInputDialogView.findViewById(R.id.bt_cancel);

        // Display values from the main activity list view in user input edittext.
       // initEditTextUserDataInPopupDialog();
    }

   void displayTask(String GET_ALL_TASK_URL, final String username)
   {
       StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_ALL_TASK_URL, new Response.Listener<String>() {
           @Override
           public void onResponse(String response) {
             //  Toast.makeText(homescreen.this, response, Toast.LENGTH_LONG).show();
               System.out.println("xavi: "+response);

               try {

                  // Toast.makeText(homescreen.this,"response: "+response,Toast.LENGTH_LONG).show();

                   JSONArray array = new JSONArray(response);
                   for (int i = 0; i < array.length(); i++)
                   {
                       JSONObject serverData = array.getJSONObject(i);
                       String Taskid = serverData.getString("TaskId");
                       String TaskName = serverData.getString("TaskName");
                       String TaskDescription = serverData.getString("TaskDescription");
                       String status = serverData.getString("status");

                       // System.out.println(Taskid+"\n"+TaskName+"\n"+TaskDescription+"\n"+status+"\n");

                       lstTask.add(new Task(TaskName,TaskDescription,status,Taskid));

                   }

               }
               catch (Exception e) {
                   Toast.makeText(homescreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
               }

               RecyclerView myrv = (RecyclerView) findViewById(R.id.recyclerview_id1);
               RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(homescreen.this,lstTask);
               myrv.setLayoutManager(new GridLayoutManager(homescreen.this,2));
               myrv.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
               myrv.setAdapter(myAdapter);

           }
       },
               new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       Toast.makeText(homescreen.this, error.getMessage(), Toast.LENGTH_LONG).show();
                   }
               }) {
           @Override
           protected Map<String, String> getParams() {
               Map<String, String> params = new HashMap<String, String>();
               params.put("username", username);
               return params;
           }
       };
       RequestQueue requestQueue = Volley.newRequestQueue(homescreen.this);
       requestQueue.add(stringRequest);


   }

}