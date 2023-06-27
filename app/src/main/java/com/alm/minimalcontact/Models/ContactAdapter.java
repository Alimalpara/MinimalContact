package com.alm.minimalcontact.Models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.alm.minimalcontact.EditContact;
import com.alm.minimalcontact.MainActivity;
import com.alm.minimalcontact.R;
import com.alm.minimalcontact.SingleContactDetail;
import com.alm.minimalcontact.Util.Util;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private ArrayList<Contact> data;
    private ArrayList<Contact> filterData;
    private Context context;
    private int id;

    public ContactAdapter(ArrayList<Contact> data, Context context) {
        this.filterData = data;
        this.data = data;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,  final int position) {
        final Contact contact = data.get(position);

        holder.tvName.setText(contact.getName());



        //if image is stored
        if (contact.getImage() != null && !contact.getImage().isEmpty()) {

            Bitmap bitmap = Util.StringToBitMap(contact.getImage());
            holder.image.setImageBitmap(bitmap);

            //holder.image.setImageBitmap();


        } //if there is no image first letter would be used
        else {
            String firstLetter = String.valueOf(contact.getName().charAt(0) + "");
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            int color = generator.getRandomColor();

            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(0)
                    .height(500)
                    .width(500)
                    .endConfig()
                    .buildRound(firstLetter.toUpperCase(), color); // radius in px
            holder.image.setImageDrawable(drawable);
        }
        //click event on recylcer view items
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleContactDetail.class);
                intent.putExtra("id", contact.getId());
                context.startActivity(intent);


            }

        });

        //long click event in recylcer view item so that the pop up is showed
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu popup = new PopupMenu(context, holder.itemView);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.pop_up_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        int id1 = item.getItemId();
                        if (id1 == R.id.delete1) {

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            builder1.setMessage("Delete this contact?");

                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Delete",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {


                                            id = contact.getId();
                                            Dbhelper dbhelper = new Dbhelper(context);
                                            Contact contact = dbhelper.getContact(id);
                                            Dbhelper db = new Dbhelper(context);
                                            db.deleteContact(id);
                                            data.remove(position);
                                            notifyItemRemoved(holder.getAdapterPosition());


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
                        if (id1 == R.id.edit1) {
                            //  context.startActivity(new Intent(context,EditContact.class));
                            id = contact.getId();

                            Intent intent = new Intent(context, EditContact.class);
                            intent.putExtra("id", id);
                            context.startActivity(intent);
                            MainActivity mainActivity = new MainActivity();
                            if(mainActivity.searchadapter!=null){
                                mainActivity.searchadapter.notifyItemChanged(holder.getAdapterPosition());
                            }


                        }
                        return true;
                    }
                });

                popup.show(); //sho
                return false;
            }


        });
       // notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();



    }

    //to filter the data in order to search
    public void filter(String text) {
        data = new ArrayList<Contact>();
        for (Contact c : filterData) {
            if (c.getName().toLowerCase().contains(text.toLowerCase()) || c.getPhone().startsWith(text)) {
                data.add(c);
            }
        }
        notifyDataSetChanged();
    }

    //extra method too test the name
    public String[] getNameArray() {
        String[] names = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            Contact c = data.get(i);
            names[i] = c.getName();

        }
        return names;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvContactName);
            image = itemView.findViewById(R.id.imageView);


        }
    }





}


