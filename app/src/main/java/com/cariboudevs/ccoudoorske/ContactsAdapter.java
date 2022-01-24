package com.cariboudevs.ccoudoorske;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cariboudevs.ccoudoorske.Utility.TimeAgo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    //creating variables for our list, context, interface and position.
    private final ArrayList<ContactsModal> contactsModalArrayList;
    private final Context context;
    private final CourseClickInterface courseClickInterface;
    int lastPos = -1;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    //creating a constructor.
    public ContactsAdapter(ArrayList<ContactsModal> contactsModalArrayList, Context context, CourseClickInterface courseClickInterface) {
        this.contactsModalArrayList = contactsModalArrayList;
        this.context = context;
        this.courseClickInterface = courseClickInterface;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating our layout file on below line.
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //setting data to our recycler view item on below line.
        ContactsModal contactsModal = contactsModalArrayList.get(position);
        holder.Name.setText(contactsModal.getName());
        holder.Email.setText(contactsModal.getEmail());
        holder.Mobilenumber.setText(contactsModal.getMobile_Number());
        holder.Location.setText(contactsModal.getLocation());
        holder.Services.setText(contactsModal.getServices());
        holder.Date.setText(contactsModal.getDate());
        holder.Description.setText(contactsModal.getDescription());
        holder.RequestFrom.setText( contactsModal.getName());





        try {

            Handler handler = new Handler();
// Define the code block to be executed
            Runnable runnableCode = new Runnable() {
                @Override
                public void run() {
                    // runs every munite for effective timestamp yeeeeah
            // change string  milliseconds to date
                    try {
                        Date date = new Date(contactsModal.getTimeStamp());

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
                        String sDateTimeThen = simpleDateFormat.format(date);


                        Date dateTimeThen = simpleDateFormat.parse(sDateTimeThen);

                        //sanitize to time ago from helper class %TimeAgo
                        TimeAgo timeAgo = new TimeAgo();
                        String result = timeAgo.getTimeAgo(dateTimeThen);

                        //sanitize time to time ago

                        holder.TimeAgo.setText(result);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                    handler.postDelayed(this, 30000);
                }
            };
// Start the initial runnable task by posting through the handler
            new Thread(() -> {
                    handler.post(runnableCode);
                    // handler.post(runnableCode);
            }).start();



        } catch (Exception e) {
            e.printStackTrace();
        }
       // Picasso.get().load(contactsModal.getCourseImg()).into(holder.courseIV);
        if(position == 0)
        {
            holder.courseIV.setImageDrawable((context.getResources().getDrawable(R.drawable.user_icons_6)));
        }
        else if(position % 4 ==0)
        {
            holder.courseIV.setImageDrawable((context.getResources().getDrawable(R.drawable.user_icons_5)));
        }
        else
        if(position % 2 == 0)
        {
            holder.courseIV.setImageDrawable((context.getResources().getDrawable(R.drawable.user_icons_1)));
        }
        else if(position % 3 ==0)
        {
            holder.courseIV.setImageDrawable((context.getResources().getDrawable(R.drawable.user_icons_2)));
        }

        else if(position % 5 ==0)
        {
            holder.courseIV.setImageDrawable((context.getResources().getDrawable(R.drawable.user_icons_3)));
        }
        else {
            holder.courseIV.setImageDrawable((context.getResources().getDrawable(R.drawable.user_icons_4)));
        }




        //adding animation to recycler view item on below line.
        setAnimation(holder.itemView, position);
        holder.CardLinearLayOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseClickInterface.onCourseClick(position);
            }
        });
    }

    private void setAnimation(View itemView, int position) {
        if (position > lastPos) {
            //on below line we are setting animation.
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }

    @Override
    public int getItemCount() {
        return contactsModalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //creating variable for our image view and text view on below line.
        private final ImageView courseIV;
        private final LinearLayout CardLinearLayOut;
        private final TextView Name;
        private final TextView Email;
        private final TextView Mobilenumber;
        private final TextView Location;
        private final TextView Services;
        private final TextView Date;
        private final TextView Description;
        private final TextView RequestFrom;
        private final TextView TimeAgo;
        private  Date dateTimeThen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //initializing all our variables on below line.
            courseIV = itemView.findViewById(R.id.idIconLogo);
            Name = itemView.findViewById(R.id.idName);
            Email = itemView.findViewById(R.id.idEmail);
            Mobilenumber = itemView.findViewById(R.id.idNumber);
            Location = itemView.findViewById(R.id.idLocation);
            Services = itemView.findViewById(R.id.idServices);
            Date = itemView.findViewById(R.id.idDate);
            Description = itemView.findViewById(R.id.idDescription);
            CardLinearLayOut=itemView.findViewById(R.id.idItemCardLinearlayout);
            RequestFrom = itemView.findViewById(R.id.RequestFromWho);
            TimeAgo = itemView.findViewById(R.id.idTimeStamp);
        }
    }

    //creating a interface for on click
    public interface CourseClickInterface {
        void onCourseClick(int position);
    }
}
