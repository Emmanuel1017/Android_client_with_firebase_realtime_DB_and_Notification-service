package com.cariboudevs.ccoudoorske;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;


public class AddContactActivity extends AppCompatActivity {

    //creating variables for our button, edit text,firebase database, database refrence, progress bar.
    private Button addCourseBtn;
    private TextInputEditText NameEdt, EmailEdt, numberEdt, LocationEdt, ServiceEdt, DateEdt, DescriptionEdt;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar loadingPB;
    private String contactID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        //initializing all our variables.
        addCourseBtn = findViewById(R.id.idBtnAddCourse);
        NameEdt = findViewById(R.id.idEdtName);
        EmailEdt = findViewById(R.id.idEdtEmail);
        numberEdt = findViewById(R.id.idEdtNumber);
        ServiceEdt = findViewById(R.id.idEdtService);
        LocationEdt = findViewById(R.id.idEdtLocation);
        DateEdt = findViewById(R.id.idEdtDate);
        DescriptionEdt = findViewById(R.id.idEdtDescription);
        loadingPB = findViewById(R.id.idPBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //on below line creating our database reference.
        databaseReference = firebaseDatabase.getReference("contacts-list");
        //adding click listener for our add course button.
        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                loadingPB.setVisibility(View.VISIBLE);
                //getting data from our edit text.



                StringBuilder alphabetbuilder = new StringBuilder();



                String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                Random rnd = new Random();

                for (int i=0; i<15; i++)
                {
                    //get random number from alphabet and numbers string alphabet
                    alphabetbuilder.append( alphabet.charAt(rnd.nextInt(alphabet.length())));
                }




                String Name = NameEdt.getText().toString();
                String Email = EmailEdt.getText().toString();
                String Number = numberEdt.getText().toString();
                String Service =ServiceEdt.getText().toString();
                String Location = LocationEdt.getText().toString();
                String Date = DateEdt.getText().toString();
                String Description = DescriptionEdt.getText().toString();
                contactID = alphabetbuilder.toString();
                Long timestamp = System.currentTimeMillis();
                //on below line we are passing all data to our modal class.
                ContactsModal contactsModal= new ContactsModal(Name, Email, Number, Service, Location, Date, Description,contactID,timestamp);
                //on below line we are calling a add value event to pass data to firebase database.
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //on below line we are setting data in our firebase database.
                        databaseReference.child(contactID).setValue(contactsModal);
                        //displaying a toast message.
                        Toast.makeText(AddContactActivity.this, " Submitted..", Toast.LENGTH_SHORT).show();
                        //starting a main activity.
                        startActivity(new Intent(AddContactActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //displaying a failure message on below line.
                        Toast.makeText(AddContactActivity.this, "Failed..", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}