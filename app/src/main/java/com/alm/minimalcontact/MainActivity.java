package com.alm.minimalcontact;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.alm.minimalcontact.Models.Contact;
import com.alm.minimalcontact.Models.ContactAdapter;
import com.alm.minimalcontact.Models.Dbhelper;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private TextView createContact;
    private RecyclerView rcvcontact;
    private EditText editTextSearch;
    public ContactAdapter searchadapter;
    private ArrayList<Contact> contactArrayList;
    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setUpUiViews();
        Dbhelper db = new Dbhelper(MainActivity.this);
        //swipe refresh layout
        swipe=findViewById(R.id.swipe1);
        getData();

        //to get data from sqllite
        contactArrayList = db.getContacts();

        //here jsed textwatcher in order to filter the recycler view with data as soon as the ddta is entered by user
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //method call from contact adapter
                searchadapter.filter(editable.toString().trim());
            }
        });

        //click event on create contact button
        createContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivity.this, CreateContact.class));


            }
        });

        //when swiped down refresh the layout in order to get the latest data
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();


            }
        });

    }

    //init views
    private void setUpUiViews() {
        createContact = findViewById(R.id.tvCreateContact);
        rcvcontact = findViewById(R.id.rcvContact);
        editTextSearch = findViewById(R.id.editTextSearch);

        rcvcontact.setLayoutManager(new LinearLayoutManager(this));
        rcvcontact.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }
    //fectching data
    private void getData() {

        searchadapter = new ContactAdapter(contactArrayList, MainActivity.this);

        //swipe.setRefreshing(true);




        rcvcontact.setAdapter(searchadapter);
        searchadapter.notifyDataSetChanged();

        swipe.setRefreshing(false);





    }



}