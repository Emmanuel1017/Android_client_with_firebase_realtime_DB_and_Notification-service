package com.cariboudevs.ccoudoorske;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cariboudevs.ccoudoorske.Utility.SharedPreferences;
import com.cariboudevs.ccoudoorske.Utility.SnackBarHelper;
import com.cariboudevs.ccoudoorske.Utility.appupdater.AppUpdater;
import com.cariboudevs.ccoudoorske.Utility.appupdater.enums.UpdateFrom;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;





public class MainActivity extends AppCompatActivity implements ContactsAdapter.CourseClickInterface {

    private static final int MY_PERMISSIONS_RECEIVE_BOOT_COMPLETED = 0;
    //creating variables for fab, firebase database, progress bar, list, adapter,firebase auth, recycler view and relative layout.
    private FloatingActionButton addCourseFAB;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private RecyclerView contactRV;
    private FirebaseAuth mAuth;
    private ProgressBar loadingPB;
    private ArrayList<ContactsModal> contactsModalArrayList;
    private ContactsAdapter contactsAdapter;
    private LinearLayout homeRL,CardLinearLayout;
    private ImageView Back,Menu;
    private int Position_Clicked;
    private PowerMenu powerMenu;
    private Intent intent,intent2;
    private final int CODE =1;
    private String callPhoneNumber ="";
    private ValueEventListener valueEventListener;
    private android.content.SharedPreferences sharedPreferences;
    int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initializing all our variables.
        contactRV = findViewById(R.id.idRVCourses);
        homeRL = findViewById(R.id.idRLBSheet);
        loadingPB = findViewById(R.id.idPBLoading);
        addCourseFAB = findViewById(R.id.idFABAddCourse);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        contactsModalArrayList = new ArrayList<>();
        //on below line we are getting database reference.
        databaseReference = firebaseDatabase.getReference("contacts-list");
        Back=findViewById(R.id.back_button_Main);
        Menu=findViewById(R.id.button_Menu);
        sharedPreferences=getSharedPreferences(SharedPreferences.SHARED_PREF, Context.MODE_PRIVATE);




        AppUpdater appUpdater = new AppUpdater(this)
         .setTitleOnUpdateAvailable("Update available")
                .setContentOnUpdateAvailable("Check out the latest version available of my app!")
                .setTitleOnUpdateNotAvailable("Update not available")
                .setContentOnUpdateNotAvailable("No update available. Check for updates again later!")
                .setButtonUpdate("Update now?")

                .setButtonDismiss("Maybe later")
                .setUpdateFrom(UpdateFrom.XML)
	            .setButtonDoNotShowAgain("Huh, not interested")
	             .setIcon(R.drawable.logo) // Notification icon
                .setCancelable(false) // Dialog could not be dismissable
                .showEvery(5);
           appUpdater.start();


        checkPermissionboot();
        permissionsGranted();

        String First_Time=sharedPreferences.getString(SharedPreferences.FIRST_TIME_PERMISSION, "FIRST_TIME_PERMISSION");

        if(First_Time=="FIRST_TIME_PERMISSION") {
            //take user to settings

            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Permission")
                    .setCustomImage(R.drawable.logo)
                    .setContentText("Permission is needed to be able to receive notifications.Please allow Auto Start permission to continue.Click OK to allow the permission.")
                    .setConfirmText("OK")
                    .setConfirmButtonTextColor(getResources().getColor(R.color.teal_200))
                    .setCancelButtonTextColor(getResources().getColor(R.color.red_btn_bg_color))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sharedPreferences.edit()
                                    .putString(SharedPreferences.FIRST_TIME_PERMISSION, "NOT_THE_FIRST_TIME_PERMISSION")
                                    .apply();
                            sDialog.dismissWithAnimation();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .show();


        }else
        {
            Log.i("PermissionFirst","Not First Time");
        }



        //on below line adding a click listener for our floating action button.
        addCourseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opening a new activity for adding a course.
                Intent i = new Intent(MainActivity.this, AddContactActivity.class);
                startActivity(i);
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.this.onBackPressed();

            }
        });

        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //powermenu anchor
                powerMenu.showAsDropDown(view); // view is an anchor

            }
        });


         firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("contacts-list");






        powerMenu = new PowerMenu.Builder(MainActivity.this)
                // .addItemList(list) //
                .addItem(new PowerMenuItem("Log Out",  R.drawable.log_out))
                .addItem(new PowerMenuItem("Exit",  R.drawable.exit)) // add an item.

                .setAnimation(MenuAnimation.ELASTIC_TOP_RIGHT) // Animation start point (TOP | LEFT)
                .setIconSize(24)
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setTextColor(ContextCompat.getColor(MainActivity.this, R.color.purple_700))
                .setTextGravity(Gravity.CENTER)
                .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setTextSize(15)
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setSelectedMenuColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_blue_dark))
                .setSelectedTextColor(Color.WHITE) // sets the color of the selected item text.
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .build();



        //on below line initializing our adapter class.
        contactsAdapter = new ContactsAdapter(contactsModalArrayList, this, this::onCourseClick);
        //setting layout malinger to recycler view on below line.  LinearLayoutManager.VERTICAL,true to set new items be added at index 0 inverse layout
        contactRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));
        //setting adapter to recycler view on below line.
        contactRV.setAdapter(contactsAdapter);
        //on below line calling a method to fetch courses from database.
        getContacts();



    }

    //poewer menu item
    private final OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
        @Override
        public void onItemClick(int position, PowerMenuItem item) {
            // Toast.makeText(getBaseContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            powerMenu.setSelectedPosition(position); // change selected item
            if (item.getTitle().equals("Exit")){
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Exit?")
                        .setContentText("Are you Sure!")
                        .setConfirmText("Yes,Exit!")
                        .setCancelText("Cancel")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                MainActivity.this.onBackPressed();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();

            }else if (item.getTitle().equals("Log Out")){
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Log Out?")
                        .setContentText("Are you Sure!")
                        .setConfirmText("Yes,Log Out!")
                        .setCancelText("Cancel")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                mAuth.signOut();
                                Toast.makeText(getApplicationContext(), "User Logged Out", Toast.LENGTH_LONG).show();
                                //on below line we are opening our login activity.
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(i);
                                MainActivity.this.finish();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();



            }


            powerMenu.dismiss();
        }
    };


    //check if service is running
    public boolean isServiceRunning(String serviceClassName){
        final ActivityManager activityManager = (ActivityManager) MainActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }


    private void permissionsGranted() {

        /**
         do some stuff at startup, like creating some directories if not already present
         **/

        try{
            if(isServiceRunning(Notificationsservice.class.getName()))
            {
                Log.i("Service listener","Service Running already");
            }else {
                //Toast.makeText(MainActivity.this, Notificationsservice.class.getSimpleName(), Toast.LENGTH_LONG).show();
                //start listning service
                //Toast.makeText(MainActivity.this, "Started service /***********/", Toast.LENGTH_LONG).show();
                intent = new Intent(MainActivity.this, Notificationsservice.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    MainActivity.this.startForegroundService(intent);
                    //Toast.makeText(MainActivity.this, "Started service o main", Toast.LENGTH_LONG).show();
                } else {
                    MainActivity.this.startService(intent);
                    //Toast.makeText(MainActivity.this, "Started service o main", Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception ex) {

            Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }










    public void checkPermissionboot() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            // do this if permisisons have not yet been granted
            Log.d("permission", "NOT granted");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    MY_PERMISSIONS_RECEIVE_BOOT_COMPLETED);
        }else{
            // if the permissions have already been granted do the following
            Log.d("permission", "granted");
            permissionsGranted();
        }
    }



    //on detry restart service bg
    //@Override
    ////protected void onDestroy() {
        //stopService(mServiceIntent);
       // Intent broadcastIntent = new Intent();
        //broadcastIntent.setAction("restartservice");
        //broadcastIntent.setClass(this, Restarter.class);
        //this.sendBroadcast(broadcastIntent);
       // super.onDestroy();
    //}


    private void getContacts() {
        //on below line clearing our list.
        contactsModalArrayList.clear();
        //to sort by date based on timestamp
        Query query = databaseReference.orderByChild("TimeStamp");
        //on below line we are calling add child event listener method to read the data.3
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //on below line we are hiding our progress bar.
                loadingPB.setVisibility(View.GONE);
                //adding snapshot to our array list on below line.

                contactsModalArrayList.add(snapshot.getValue(ContactsModal.class));
                contactRV.scrollToPosition(contactsModalArrayList.size() - 1);
                //notifying our adapter that data has changed.
                contactsAdapter.notifyDataSetChanged();
                //scroll to top  item

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //this method is called when new child is added we are notifying our adapter and making progress bar visibility as gone.
                loadingPB.setVisibility(View.GONE);
                contactRV.scrollToPosition(contactsModalArrayList.size() - 1);
                //contactRV.scrollToPosition(contactsModalArrayList.size() - 1);
                contactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //notifying our adapter when child is removed.
                contactsAdapter.notifyDataSetChanged();
                //contactRV.scrollToPosition(contactsModalArrayList.size() - 1);
                loadingPB.setVisibility(View.GONE);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //notifying our adapter when child is moved.
                contactsAdapter.notifyDataSetChanged();
                contactRV.scrollToPosition(contactsModalArrayList.size() - 1);
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCourseClick(int position) {
        //calling a method to display a bottom sheet on below line.
        displayBottomSheet(contactsModalArrayList.get(position));
        Position_Clicked = contactsModalArrayList.indexOf(contactsModalArrayList.get(position));
        //Toast.makeText(MainActivity.this,position, Toast.LENGTH_LONG).show();
       // Log.e("position", String.valueOf(position));
        Log.e("position", String.valueOf(Position_Clicked));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //adding a click listner for option selected on below line.
        int id = item.getItemId();
        switch (id) {
            case R.id.idLogOut:
                //displaying a toast message on user logged out inside on click.
                Toast.makeText(getApplicationContext(), "User Logged Out", Toast.LENGTH_LONG).show();
                //on below line we are signing out our user.
                mAuth.signOut();
                //on below line we are opening our login activity.
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //on below line we are inflating our menu file for displaying our menu options.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void displayBottomSheet(ContactsModal modal) {
        //on below line we are creating our bottom sheet dialog.
        final BottomSheetDialog bottomSheetTeachersDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        //on below line we are inflating our layout file for our bottom sheet.
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, homeRL);
        //setting content view for bottom sheet on below line.
        bottomSheetTeachersDialog.setContentView(layout);
        //on below line we are setting a cancelable
        bottomSheetTeachersDialog.setCancelable(false);
        bottomSheetTeachersDialog.setCanceledOnTouchOutside(true);
        //calling a method to display our bottom sheet.
        bottomSheetTeachersDialog.show();
        //on below line we are creating variables for our text view and image view inside bottom sheet
        //and initialing them with their ids.
        TextView Name = layout.findViewById(R.id.idName);
        TextView Email = layout.findViewById(R.id.idEmail);
        TextView MobileNumber = layout.findViewById(R.id.idNumber);
        TextView Location = layout.findViewById(R.id.idLocation);
        TextView services = layout.findViewById(R.id.idServices);
        TextView Date = layout.findViewById(R.id.idDate);
        TextView Description = layout.findViewById(R.id.idDescription);
        ImageView Logo = layout.findViewById(R.id.idlogo);
        ImageView EmailContact =layout.findViewById(R.id.Emailcontact);
        ImageView Whatsapp =layout.findViewById(R.id.whatsapp);
        ImageView MakeCall =layout.findViewById(R.id.PhoneCall);

        TextView From = layout.findViewById(R.id.RequestForm);
        //on below line we are setting data to different views on below line.
        Name.setText(modal.getName());
        Email.setText(modal.getEmail());
        MobileNumber.setText(modal.getMobile_Number());
        Location.setText(modal.getLocation());
        services.setText(modal.getServices());
        Date.setText(modal.getDate());
        Description.setText(modal.getDescription());
        From.append(" " + modal.getName());


        Logo.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.user_icons_7));

        //Picasso.get().load(modal.getCourseImg()).into(Logo);
        Button DeleteContact = layout.findViewById(R.id.delete_contact);

       // Toast.makeText(getApplicationContext(),String.valueOf(position),Toast.LENGTH_SHORT).show();


        Whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Sanitized_Number= modal.getMobile_Number().replaceFirst("0","+254");
                String message ="Hi,Thank you for contacting us. Regarding your request for the design of "+modal.getServices();
                boolean installed = appInstalledOrNot("com.whatsapp");
                if (installed){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+Sanitized_Number+ "&text="+message));
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, "Whats app not installed on your device", Toast.LENGTH_SHORT).show();
                }

            }
            });


        MakeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                callPhoneNumber=modal.getMobile_Number();
                checkPermission(Manifest.permission.CALL_PHONE,
                        CODE);



            }
        });



        EmailContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.parse("mailto:"
                            + modal.getEmail()
                            + "?subject=" + "Re: Your request For Outdoors design of" +modal.getServices() + "&body=" + "We received Your Request for the site visit on "+modal.getDate()+" for the request to design "+modal.getServices()+". This reply email is To confirm the request and inform you we shall get back to you with more details regarding the request.\nThank You.\n\n Regards Cool Classy Outdoors Ke");
                    intent.setData(data);
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    Snackbar snack = Snackbar.make(MainActivity.this.findViewById(android.R.id.content),"No Email Apllication Installed!",      Snackbar.LENGTH_LONG);
                    SnackBarHelper.configSnackbar(MainActivity.this, snack);
                    snack.show();
                }


            }
        });




        //adding click listener for our view button on below line.
        DeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on below line calling a method to delete the course.
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Delete?")
                        .setContentText("Are you Sure!")
                        .setConfirmText("Yes!")
                        .setCancelText("Cancel")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                try {
                                    firebaseDatabase.getReference("contacts-list").child(modal.getContactId()).removeValue();
                                    String id = modal.getContactId();
                                    int position = -1;
                                    for (int i = 0; i < contactsModalArrayList.size(); i++) {
                                        if (contactsModalArrayList.get(i).getContactId().equals(id)) {
                                            position = i;
                                            // break;  // uncomment to get the first instance
                                        }
                                    }
                                    if(Position_Clicked==position)
                                    {
                                        contactsModalArrayList.remove(Position_Clicked);
                                    }
                                    contactsAdapter.notifyDataSetChanged();
                                }
                                catch (Exception e)
                                {
                                    Log.e("Delete item error","Error");
                                }
                                //displaying a toast message on below line.
                                //Toast.makeText(MainActivity.this, "Deleted..", Toast.LENGTH_SHORT).show();
                                Snackbar snack = Snackbar.make(MainActivity.this.findViewById(android.R.id.content),"Deleted",Snackbar.LENGTH_SHORT);
                                SnackBarHelper.configSnackbar(MainActivity.this, snack);
                                snack.show();
                                bottomSheetTeachersDialog.dismiss();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();


            }
        });

    }


    // Function to check and request permission
    public void checkPermission(String permission, int requestCode)
    {
        ///// Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else{
            intent2 = new Intent(Intent.ACTION_CALL);
            intent2.setData(Uri.parse("tel:" +callPhoneNumber));
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent2);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Call Permission Granted", Toast.LENGTH_SHORT).show();

                intent2 = new Intent(Intent.ACTION_CALL);
                intent2.setData(Uri.parse("tel:" + callPhoneNumber));
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);

            } else if (requestCode == MY_PERMISSIONS_RECEIVE_BOOT_COMPLETED) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                    permissionsGranted();
                } else {
                    // possibly prompt why permissions are required and try again
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setTitleText("Notifications!")
                            .setCustomImage(R.drawable.logo)
                            .setContentText("Permission is needed to be able to receive notifications.Please allow permission.")
                            .setConfirmText("Reallow")
                            .setConfirmButtonTextColor(getResources().getColor(R.color.main_green_color))
                            .setCancelButtonTextColor(getResources().getColor(R.color.red_btn_bg_color))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    checkPermissionboot();
                                }
                            })
                            .show();

                }
            }
        }
    }


    //Create method appInstalledOrNot
    private boolean appInstalledOrNot(String url){
        PackageManager packageManager =getPackageManager();
        boolean app_installed;
        try {
            packageManager.getPackageInfo(url,PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }catch (PackageManager.NameNotFoundException e){
            app_installed = false;
        }
        return app_installed;
    }



}