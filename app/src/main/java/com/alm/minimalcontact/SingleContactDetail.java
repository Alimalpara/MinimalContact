package com.alm.minimalcontact;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alm.minimalcontact.Models.Contact;
import com.alm.minimalcontact.Models.Dbhelper;
import com.alm.minimalcontact.Util.Util;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.Objects;

public class SingleContactDetail extends AppCompatActivity {

    TextView textView,textView4,textView5;
    ImageView imageView,call,email,message;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_contact_detail);

        setTitle("Contacts");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initviews();

        //to call when call logo is clicked
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String calling=textView5.getText().toString().trim();
                dialContactPhone(calling);

            }
        });

        //to send email
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();

            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });


        //to send messages



    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
    private void getData(){
        id = getIntent().getIntExtra("id",0);
        // Toast.makeText(this, "id= "+id, Toast.LENGTH_SHORT).show();

        Dbhelper dbhelper=new Dbhelper(SingleContactDetail.this);
        Contact contact=dbhelper.getContact(id);
        textView.setText(contact.getName());
        textView4.setText(contact.getEmail());
        textView5.setText(contact.getPhone());

        //to set imageview to image if exists
        if(contact.getImage()!=null && !contact.getImage().isEmpty()){

            Bitmap bitmap= Util.StringToBitMap(contact.getImage());
            imageView.setImageBitmap(bitmap);



        }
        //else setting the first letter of name as image
        else {
            String firstLetter = String.valueOf(contact.getName().charAt(0)+"");
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            int color = generator.getRandomColor();
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(firstLetter.toUpperCase(), color); // radius in px
            imageView.setImageDrawable(drawable);
        }


    }


    //calling method
    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    //to send email method
    private void sendEmail(){
        String email=textView4.getText().toString().trim();

        //using intent
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + email));
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My email's subject");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "My email's body");


        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SingleContactDetail.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }

    //to send message method
    private void sendMessage(){
        String message=textView5.getText().toString().trim();

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"+message));
        // sendIntent.putExtra("sms_body", message);
        startActivity(sendIntent);


    }




    //init views
    public void initviews(){
        textView=findViewById(R.id.tvName);
        textView4=findViewById(R.id.tvEmail);
        textView5=findViewById(R.id.tvPhone);
        imageView=findViewById(R.id.ivSingleContact);
        call=findViewById(R.id.ivCall);
        email=findViewById(R.id.ivemaaail);
        message=findViewById(R.id.ivMessage);
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id1 = item.getItemId();

        if (id1 == R.id.edit) {
            Intent intent = new Intent(SingleContactDetail.this,EditContact.class);
            intent.putExtra("id", id);
            startActivity(intent);
            finish();

            //Toast.makeText(this, "editid= "+id, Toast.LENGTH_SHORT).show();

        }

        //for dailog box
        if(id1==R.id.delete) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SingleContactDetail.this);
            builder1.setMessage("Delete this contact?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Delete",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //here getting id from intent and passing it to delete method
                            id = getIntent().getIntExtra("id",0);
                            Dbhelper dbhelper=new Dbhelper(SingleContactDetail.this);
                            Contact contact=dbhelper.getContact(id);
                            Dbhelper db = new Dbhelper(SingleContactDetail.this);
                            db.deleteContact(id);
                            MainActivity mainActivity =new MainActivity();
                            mainActivity.searchadapter.notifyDataSetChanged();

                            finish();


                        }
                    });

            builder1.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up

        startActivity(new Intent(SingleContactDetail.this,MainActivity.class));
        return false;
    }
}