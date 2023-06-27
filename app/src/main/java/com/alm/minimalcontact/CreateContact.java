package com.alm.minimalcontact;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alm.minimalcontact.Models.Contact;
import com.alm.minimalcontact.Models.ContactAdapter;
import com.alm.minimalcontact.Models.Dbhelper;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.constant.ImageProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateContact extends AppCompatActivity {

    private EditText name, email, phone;
    private ImageView chooseimage, profilePic;

    public String encoded;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        setTitle("Create new contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
        progressDialog=new ProgressDialog(this);

        SetUpUiViews();
        hideui();

        //click event on camera image in order to start the image picker

        chooseimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                progressDialog.setMessage("Opening");
                progressDialog.show();


                //image picker method is clicked
                ImagePickerMethod();

            }
        });

    }

    //init views

    private void SetUpUiViews() {
        name = findViewById(R.id.etCreateName);
        email = findViewById(R.id.etCreateEmail);
        phone = findViewById(R.id.etCreatePhone);
        chooseimage = findViewById(R.id.ivpickImage);
        profilePic = findViewById(R.id.ivCreateContact);
    }

    //to hide keyboard whenever user clicks outside of the input field
    public void hideui() {
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

    }
    //TO HIDE KEYBOARD.
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    //to create a action bar button and set click event on it in order to save the contact

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menusave, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //if save button is clicked this block of code will exectue
        if (id == R.id.Save) {


            String username_vld = name.getText().toString().trim();
            String useremail_vld = email.getText().toString().trim();
            String userphone_vld = phone.getText().toString().trim();
            //for setting error message if input fields are empty
            if (username_vld.trim().equals("")) {
                name.setError("Please Enter Name");
            }
            if (useremail_vld.trim().equals("")) {
                email.setError("Please Enter Email");
            }
            if (userphone_vld.trim().equals("")) {
                phone.setError("Please Enter Phone");
            }

            //here it will check for validation if input fields are not empty then
            //it will further validate it using custom validations
            //where email has to be correct\
            //name must be atleast 3 characters
            //phone must be 10 digits long.
            if (validate()) {


                if (isValidEmail(useremail_vld) == false) {
                    email.setError("Invalid Email-id");

                } else if (name.getText().toString().length() < 3) {

                    name.setError("Name must atleast three characters");

                } else if (phone.getText().toString().length() < 10) {

                    phone.setError("Mobile must be 10 digits long");

                }
                //if all validations are proper than it will show a progress dailog and contact would be saved to sqllite
                else {
                    progressDialog.setMessage("Saving");
                    progressDialog.show();
                    setData();
                    //progressDialog.hide();
                }

            }


        }
        return super.onOptionsItemSelected(item);
    }

    //method to pick image from gallery or camera
    public void ImagePickerMethod(){
        ImagePicker.Companion.with(CreateContact.this)
                .crop()
                .compress(1024)
                .maxResultSize(512,512)
                .provider(ImageProvider.BOTH)
                .start(101);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        //here if an image is picked data would be passed to uri object
        if (resultCode == RESULT_OK && data!=null) {

            Uri uri = data.getData();
            try {
                //here getting bitmap value from uri as bitma are more efficient then uri
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //here quality can be changed accordingly
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

                //here the picked image is set to image view
                profilePic.setImageBitmap(bitmap);



                //this code will now convert the bitmap to string
                //as images cannot be stored as image in sqllite
                //they must be converted to string
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        //if no image is selected then toast will be shown
        else {
            Toast.makeText(this, "No image is selected", Toast.LENGTH_SHORT).show();
        }
    }

    //TO set data
    //TO store data to sqllite
    private void setData() {
        Contact contact = new Contact();
        contact.setName(name.getText().toString().trim());
        contact.setPhone(phone.getText().toString().trim());
        contact.setEmail(email.getText().toString().trim());
        contact.setImage(encoded);

        //save the contact
        Dbhelper db = new Dbhelper(this);
        db.createContact(contact);
        Toast.makeText(this, "Contact Saved Successfully", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(CreateContact.this,MainActivity.class));
        finish();
    }

    //to validate the user input
    private Boolean validate() {

        Boolean result = false;

        String username_vld = name.getText().toString().trim();
        String useremail_vld = email.getText().toString().trim();
        String userphone_vld = phone.getText().toString().trim();


        if (username_vld.isEmpty() || useremail_vld.isEmpty() || userphone_vld.isEmpty()) {
            //Toast tst = Toast.makeText(this, "Please Enter All details", Toast.LENGTH_LONG);


        } else {
            result = true;
        }
        return result;

    }


    //regex used to properly validate the email id entered by user
    public boolean isValidEmail(final String email) {
        Pattern pattern;
        Matcher matcher;
        final String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        pattern = Pattern.compile(EmailPattern);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


    //to go back to previous activity
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up

        startActivity(new Intent(CreateContact.this,MainActivity.class));

        return false;
    }

}