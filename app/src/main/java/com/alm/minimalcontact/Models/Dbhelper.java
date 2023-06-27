package com.alm.minimalcontact.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Dbhelper extends SQLiteOpenHelper {

    public static final String DB_NAME= "address_book";
    public static final int DB_VERSION = 1;

    //Default constructor.
    public Dbhelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createContactTable="CREATE TABLE CONTACTS (" +
                "ID INTEGER primary key AUTOINCREMENT," +
                "NAME TEXT," +
                "EMAIL TEXT," +
                "PHONE TEXT," +
                "USERIMAGE TEXT" +
                ")";
        db.execSQL(createContactTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE CONTACTS");
        this.onCreate(db);
    }

    //to create contact
    public void createContact(Contact contact){
        ContentValues contentValues=new ContentValues();
        contentValues.put("NAME",contact.getName());
        contentValues.put("EMAIL",contact.getEmail());
        contentValues.put("PHONE",contact.getPhone());
        contentValues.put("USERIMAGE",contact.getImage());

     //   byte[] image = cursor.getBlob(1);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("CONTACTS", null, contentValues);

    }

    //to get array to show in recyler view
    public ArrayList<Contact> getContacts(){

        ArrayList<Contact> contactArrayList=new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor c=sqLiteDatabase.rawQuery("Select * from Contacts",null);
        if(c.moveToFirst()){
            do{
                Contact contact=new Contact();
                contact.setId(c.getInt(c.getColumnIndex("ID")));
                contact.setName(c.getString(c.getColumnIndex("NAME")));
                contact.setEmail(c.getString(c.getColumnIndex("EMAIL")));
                contact.setPhone(c.getString(c.getColumnIndex("PHONE")));
                contact.setImage(c.getString(c.getColumnIndex("USERIMAGE")));
                contactArrayList.add(contact);

            }while(c.moveToNext());
        }return contactArrayList;


    }

    //to get single  contact details
    public Contact getContact(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM CONTACTS WHERE ID=?", new String[]{Integer.toString(id)});
        Contact contact = new Contact();
        if(c.moveToFirst()){
            contact.setName(c.getString(c.getColumnIndex("NAME")));
            contact.setPhone(c.getString(c.getColumnIndex("PHONE")));
            contact.setEmail(c.getString(c.getColumnIndex("EMAIL")));
            contact.setImage(c.getString(c.getColumnIndex("USERIMAGE")));
            return contact;
        }

        return null;
    }


    //edit method
    public void editContact(int id, Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", contact.getName());
        contentValues.put("PHONE", contact.getPhone());
        contentValues.put("EMAIL", contact.getEmail());
        contentValues.put("USERIMAGE",contact.getImage());
        db.update("CONTACTS",contentValues,"id=?",new String[]{Integer.toString(id)});
    }


    //delete method
    public void deleteContact(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("CONTACTS","id=?",new String[]{Integer.toString(id)});

        //db.rawQuery("DELETE FROM CONTACTS WHERE ID=?",new String[]{Integer.toString(id)});
    }



}
