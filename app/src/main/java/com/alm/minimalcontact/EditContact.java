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
import com.alm.minimalcontact.Models.Dbhelper;
import com.alm.minimalcontact.Util.Util;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.constant.ImageProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditContact extends AppCompatActivity {


    public String encoded;
    private EditText editname, editemail, editphone;
    private ImageView editImage, pickImage;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        setTitle("Edit Contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
        initViews();

        //for keyboard hiding
        hideui();
        progressDialog = new ProgressDialog(this);

        //to set data on create

        int id = getIntent().getIntExtra("id", 1);
        //Toast.makeText(this, "editid= "+id, Toast.LENGTH_SHORT).show();
        Dbhelper dbhelper = new Dbhelper(EditContact.this);
        Contact contact = dbhelper.getContact(id);
        editname.setText(contact.getName());
        editemail.setText(contact.getEmail());
        editphone.setText(contact.getPhone());
        encoded = contact.getImage();

        if (contact.getImage() != null && !contact.getImage().isEmpty()) {

            Bitmap bitmap = Util.StringToBitMap(contact.getImage());
            editImage.setImageBitmap(bitmap);

        } else {
            String firstLetter = String.valueOf(contact.getName().charAt(0) + "");
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            int color = generator.getRandomColor();

            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(firstLetter, color); // radius in px
            editImage.setImageDrawable(drawable);
        }




        //for picking image
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Opening");
                progressDialog.show();
                ImagePickerMethod();

            }
        });


    }
    //method to pick image from gallery or camera
    public void ImagePickerMethod(){
        progressDialog.dismiss();
        ImagePicker.Companion.with(EditContact.this)
                .crop()
                .compress(1024)
                .maxResultSize(512,512)
                .provider(ImageProvider.BOTH)
                .start(101);

    }

    /// onactivyt result to get image to edit
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
                editImage.setImageBitmap(bitmap);



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

    //to create menu item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menusave, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id1 = item.getItemId();

        if (id1 == R.id.Save) {

            String username_vld = editname.getText().toString().trim();
            String useremail_vld = editemail.getText().toString().trim();
            String userphone_vld = editphone.getText().toString().trim();
            //for setting error if field is empty
            if (username_vld.trim().equals("")) {
                editname.setError("Please Enter Name");
            }
            if (useremail_vld.trim().equals("")) {
                editemail.setError("Please Enter Email");
            }
            if (userphone_vld.trim().equals("")) {
                editphone.setError("Please Enter Phone");
            }
            if (validate()) {
                if (isValidEmail(useremail_vld) == false) {
                    editemail.setError("Invalid Email-id");

                } else if (editname.getText().toString().length() < 3) {

                    editname.setError("Name must atleast three characters");

                } else if (editphone.getText().toString().length() < 10) {

                    editphone.setError("Mobile must be 10 digits long");

                } else {
                    editData();
                    MainActivity mainActivity = new MainActivity();
                    if(mainActivity.searchadapter!=null){
                        mainActivity.searchadapter.notifyDataSetChanged();
                    }

                    progressDialog.setMessage("Saving");
                    progressDialog.show();
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }

    //validations method
//validations method
    private Boolean validate() {

        Boolean result = false;

        String username_vld = editname.getText().toString().trim();
        String useremail_vld = editemail.getText().toString().trim();
        String userphone_vld = editphone.getText().toString().trim();


        if (username_vld.isEmpty() || useremail_vld.isEmpty() || userphone_vld.isEmpty()) {
            //Toast tst = Toast.makeText(this, "Please Enter All details", Toast.LENGTH_LONG);


        } else {
            result = true;
        }
        return result;

    }

    //email regex
    public boolean isValidEmail(final String email) {
        Pattern pattern;
        Matcher matcher;
        final String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        pattern = Pattern.compile(EmailPattern);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


    //to edit data
    private void editData() {
        int id = getIntent().getIntExtra("id", 0);

        Dbhelper dbhelper = new Dbhelper(EditContact.this);
        Contact contact = dbhelper.getContact(id);
        contact.setName(editname.getText().toString().trim());
        contact.setPhone(editphone.getText().toString().trim());
        contact.setEmail(editemail.getText().toString().trim());
        contact.setImage(encoded);

        //edit the contact
        Dbhelper db = new Dbhelper(this);
        db.editContact(id, contact);



        Toast.makeText(this, "Contact Edited Successfully", Toast.LENGTH_SHORT).show();

        finish();
        startActivity(new Intent(EditContact.this,MainActivity.class));// close this activity as oppose to navigating up

    }

    //to close this activity
    @Override
    public boolean onSupportNavigateUp() {


        finish();
        return false;
    }
















    //to hide keyboard on click
    public void hideui() {
        editname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        editemail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        editphone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

    }
    //TO HIDE KEYBOARD method.
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //views init
    private void initViews() {
        editname = findViewById(R.id.etEditName);
        editemail = findViewById(R.id.eteditEmail);
        editphone = findViewById(R.id.etEditPhone);
        editImage = findViewById(R.id.ivEditContactProfilePic);
        pickImage = findViewById(R.id.ivEditPickImage);
    }
}