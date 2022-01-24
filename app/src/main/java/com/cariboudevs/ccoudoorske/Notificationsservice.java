package com.cariboudevs.ccoudoorske;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.cariboudevs.ccoudoorske.Utility.Notifications;
import com.cariboudevs.ccoudoorske.Utility.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Notificationsservice extends Service {
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference,databaseReferenceTimestamp;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    ArrayList<ContactsModal> contactsModalArrayList;
    Integer Service;
    Date dateTimeThen;
    private android.content.SharedPreferences sharedPreferences;
    Context context;
    String Timestamp;
    Query query;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void onDestroy() {
        Toast.makeText(this, "Ccout Service Stopped", Toast.LENGTH_LONG).show();
        //map string from shared preference to V

        Log.d("Service listener", "onDestroy");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = "Cool Classy Outdoors Admin Panel";
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Ccout Admin Panel")
                .setContentText(input)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        //do heavy work on a background thread

        Get_Notifications();
        //stopSelf();
        return START_NOT_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    public void Get_Notifications()
    {
        firebaseDatabase = FirebaseDatabase.getInstance();
        try {
            databaseReferenceTimestamp = firebaseDatabase.getReference("Devices Timestamp");
        }catch (Exception e)
        {
            Log.e("No Timestamp From Db",e.getMessage());
        }
        sharedPreferences=getSharedPreferences(SharedPreferences.SHARED_PREF, Context.MODE_PRIVATE);

        contactsModalArrayList = new ArrayList<>();


        mAuth = FirebaseAuth.getInstance();

        String FirstTimeNotification = sharedPreferences.getString(SharedPreferences.TIME_STAMP_LAST_NOTIFICATION, "TIME_STAMP_LAST_NOTIFICATION");

        if(FirstTimeNotification.equals("TIME_STAMP_LAST_NOTIFICATION"))
            {

           try {
               Timestamp = databaseReferenceTimestamp.child(Objects.requireNonNull(mAuth.getUid())).get().toString();
               Toast.makeText(this, Timestamp, Toast.LENGTH_LONG).show();
               sharedPreferences.edit()
                       .putString(SharedPreferences.TIME_STAMP_LAST_NOTIFICATION, Timestamp)
                       .apply();
           }
           catch (Exception e)
           {
               Log.e("No Timestamp From Db",e.getMessage());
           }

           }
       else
           {
           Timestamp = sharedPreferences.getString(SharedPreferences.TIME_STAMP_LAST_NOTIFICATION, "TIME_STAMP_LAST_NOTIFICATION");
           }

            databaseReference = firebaseDatabase.getReference("contacts-list");


            query= databaseReference.orderByChild("TimeStamp").limitToLast(1);

            query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //obviously necessary to reset index 0
                contactsModalArrayList.clear();
                contactsModalArrayList.add(snapshot.getValue(ContactsModal.class));
                ContactsModal contactsModal = contactsModalArrayList.get(0);

                //Toast.makeText(getBaseContext(),contactsModal.getServices(),Toast.LENGTH_LONG);
                Log.e("service",contactsModal.getServices());
                if(contactsModal.getServices().equals("Landscaping"))
                {
                    Service= (R.drawable.ccoutdoorske_balcony);
                }else  if(contactsModal.getServices().equals("kids Play Area"))
                {
                    Service= (R.drawable.ccoutdoorske_kids_play_area);
                } else   if(contactsModal.getServices().equals("Front Porch"))
                {
                    Service= (R.drawable.ccoutdoorske_front_porch);
                } else   if(contactsModal.getServices().equals("Car Shades/ Shade sails"))
                {
                    Service= (R.drawable.ccoutdoorske_shade);
                }else  if(contactsModal.getServices().equals("Gazebo"))
                {
                    Service= (R.drawable.ccoutdoorske_gazebo);
                }else  if(contactsModal.getServices().equals("Patios"))
                {
                    Service= (R.drawable.ccoutdoorske_patios);
                }else  if(contactsModal.getServices().equals("Pergola"))
                {
                    Service=(R.drawable.ccoutdoorske_pergola);
                }else  if(contactsModal.getServices().equals("Balcony"))
                {
                    Service= (R.drawable.ccoutdoorske_balcony);
                }else
                {
                    Service= (R.drawable.ccoutdoorske_paving);
                }

                try {
                    Date date = new Date(contactsModal.getTimeStamp());

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm");
                    String sDateTimeThen = simpleDateFormat.format(date);
                    dateTimeThen = simpleDateFormat.parse(sDateTimeThen);




                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //on below line we are hiding our progress bar.
                //call notifications class
                Notifications.build(getBaseContext())
                        /*
                         * Set notification title and content
                         * */
                        .setTitle("Request from "+contactsModal.getName())
                        .setContent("Request for "+contactsModal.getServices()+" design"+". Sent on: "+dateTimeThen.toString())
                        /*
                         * Set small icon from drawable resource
                         * */
                        .setSmallIcon(R.drawable.logo)
                        .setColor(R.color.black_shade_1)
                        .setImportance(Notifications.NotifyImportance.HIGH)
                        /*
                         * Set notification large icon from drawable resource or URL
                         * (make sure you added INTERNET permission to AndroidManifest.xml)
                         * */
                        .setLargeIcon(R.drawable.logo)
                        /*
                         * Circular large icon
                         * */
                        .largeCircularIcon()
                        .setChannelId(CHANNEL_ID)
                        .setChannelName(CHANNEL_ID+" Channel")

                        /*
                         * Add a picture from drawable resource or URL
                         * (INTERNET permission needs to be added to AndroidManifest.xml)
                         * */
                        .setPicture(Service)
                        .show(); // Show notification

                sharedPreferences.edit()
                        .putString(SharedPreferences.TIME_STAMP_LAST_NOTIFICATION, contactsModal.getTimeStamp().toString())
                        .apply();


                databaseReferenceTimestamp.child(Objects.requireNonNull(mAuth.getUid())).setValue(contactsModal.getTimeStamp().toString());



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //this method is called when new child is added we are notifying our adapter and making progress bar visibility as gone.

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //notifying our adapter when child is removed.


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Intent intents = new Intent(getBaseContext(),hello.class);
        //intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intents);
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d("servce listener", "onStart");
    }


}



