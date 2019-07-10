package com.summer.school.retrofitcourse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.summer.school.retrofitcourse.model.EmployeeData;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<EmployeeData> employeeList;
    private EmployeeApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 1. Create and setup the RecyclerView
        recyclerView = findViewById(R.id.my_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        employeeList = new ArrayList<>();
        mAdapter = new MyListAdapter(employeeList, new MyListAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(int position) {
                // Add a delete action when long pressing an item in the list
                if (position < employeeList.size()) {
                    displayConfirmDialog(employeeList.get(position));
                }
                return false;
            }
        });
        recyclerView.setAdapter(mAdapter);


        // 2. Build the API Service
        apiService = (new Retrofit.Builder())
                // The GsonConverterFactory is used to transform Employee Data to Json and back
                .addConverterFactory(GsonConverterFactory.create())
                // Sets the base URL of the api that will be used
                .baseUrl("http://dummy.restapiexample.com/api/v1/")
                .build()
                .create(EmployeeApiService.class);

        // 3. Load at start the current list of employees
        getAllEmployees();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with the create employee action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    // This is an auto-generated method when creating the project for setting up a menu in the upper left corner
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // In this part you can add another screen or dialog to make the API URL customizable
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // This method makes an asynchronous GET call to the server in order to get the list of employees
    private void getAllEmployees() {
        Call<List<EmployeeData>> employeeCall = apiService.getUserDetails();
        employeeCall.enqueue(new Callback<List<EmployeeData>>() {
            @Override
            public void onResponse(Call<List<EmployeeData>> call, Response<List<EmployeeData>> response) {
                if (response != null && response.body() != null) {
                    employeeList.clear();
                    employeeList.addAll(response.body());
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<EmployeeData>> call, Throwable t) {
                Toast.makeText(
                        MainActivity.this,
                        getString(R.string.communication_error),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    // Creates and displays a dialog for the user if he wants to confirm the delete action or not
    private void displayConfirmDialog(final EmployeeData employeeData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure_label))
                .setPositiveButton(getString(R.string.confirm_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEmployee(employeeData.getId());
                    }
                })
                .setNegativeButton(getString(R.string.cancel_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    // Makes an asynchronous delete request to the server
    private void deleteEmployee(int employeeId) {
        Call<ResponseBody> responseCallback = apiService.deleteEmployeeById(employeeId);
        responseCallback.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Get the employee list again to validate if the entry was deleted
                getAllEmployees();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(
                        MainActivity.this,
                        getString(R.string.communication_error),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
