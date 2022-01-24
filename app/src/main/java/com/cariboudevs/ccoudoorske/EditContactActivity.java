package com.cariboudevs.ccoudoorske;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cariboudevs.ccoudoorske.Utility.SnackBarHelper;
import com.cariboudevs.ccoudoorske.Utility.SnackBarHelper_Warning;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;



public class EditContactActivity extends AppCompatActivity {

    //creating variables for our edit text, firebase database, database reference, course rv modal,progress bar.
    private TextInputEditText NameEdt, EmailEdt, NumberEdt, LocationEdt, ServiceEdt, DateEdt,Descriptionedt;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,databaseReference_no_child;
    ContactsModal contactsModal;
    private ProgressBar loadingPB;
    //creating a string for our course id.
    private String contactID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        //initializing all our variables on below line.
        Button addCourseBtn = findViewById(R.id.idBtnAddContact);
        NameEdt = findViewById(R.id.idEdtName);
        EmailEdt = findViewById(R.id.idEdtEmail);
        NumberEdt = findViewById(R.id.idEdtNumber);
        LocationEdt = findViewById(R.id.idEdtLocation);
        ServiceEdt = findViewById(R.id.idEdtService);
        DateEdt = findViewById(R.id.idEdtDate);
        Descriptionedt = findViewById(R.id.idEdtDescription);
        loadingPB = findViewById(R.id.idPBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //on below line we are getting our modal class on which we have passed.
        contactsModal = getIntent().getParcelableExtra("contact");
        Button deleteCourseBtn = findViewById(R.id.idBtnDeleteContact);

        if (contactsModal != null) {
            //on below line we are setting data to our edit text from our modal class.
            NameEdt.setText(contactsModal.getName());
            EmailEdt.setText(contactsModal.getEmail());
            NumberEdt.setText(contactsModal.getMobile_Number());
            LocationEdt.setText(contactsModal.getLocation());
            ServiceEdt.setText(contactsModal.getServices());
            DateEdt.setText(contactsModal.getDate());
            contactID = contactsModal.getContactId();
            Snackbar snack = Snackbar.make(EditContactActivity.this.findViewById(android.R.id.content),contactID,      Snackbar.LENGTH_LONG);
            SnackBarHelper.configSnackbar(EditContactActivity.this, snack);
            snack.show();
        }
        else
        {
            Snackbar snack = Snackbar.make(EditContactActivity.this.findViewById(android.R.id.content),"Empty",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Warning.configSnackbar(EditContactActivity.this, snack);
            snack.show();
        }
        databaseReference_no_child = firebaseDatabase.getReference("contacts-list");
        //to get data key value by iteration



        //on below line we are initialing our database reference and we are adding a child as id.
        databaseReference = firebaseDatabase.getReference("contacts-list").child(contactID);
        //on below line we are adding click listener for our add course button.
        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on below line we are making our progress bar as visible.
                loadingPB.setVisibility(View.VISIBLE);
                //on below line we are getting data from our edit text.
                String Name = NameEdt.getText().toString();
                String Email = EmailEdt.getText().toString();
                String Number = NumberEdt.getText().toString();
                String Location = LocationEdt.getText().toString();
                String Service = ServiceEdt.getText().toString();
                String Date = DateEdt.getText().toString();
                String Description = Descriptionedt.getText().toString();
                //on below line we are creating a map for passing a data using key and value pair.
                Map<String, Object> map = new HashMap<>();
                map.put("Name", Name);
                map.put("email", Email);
                map.put("mobileNumber", Number);
                map.put("Location", Location);
                map.put("Services", Service);
                map.put("Date", Date);
                map.put("Description", Description);
                map.put("ContactId", contactID);
                //map.put("courseId", contactID);

                //on below line we are calling a database reference on add value event listener and on data change method
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //making progress bar visibility as gone.
                        loadingPB.setVisibility(View.GONE);
                        //adding a map to our database.
                        databaseReference.updateChildren(map);
                        //on below line we are displaying a toast message.
                        Toast.makeText(EditContactActivity.this, "Course Updated..", Toast.LENGTH_SHORT).show();
                        //opening a new activity after updating our coarse.
                        startActivity(new Intent(EditContactActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //displaying a failure message on toast.
                        Toast.makeText(EditContactActivity.this, "Fail to update course..", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //adding a click listener for our delete course button.
        deleteCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling a method to delete a course.
                deleteCourse();
            }
        });

    }

    private void deleteCourse() {
        //on below line calling a method to delete the course.
        databaseReference.removeValue();
        //displaying a toast message on below line.
        Toast.makeText(this, "Course Deleted..", Toast.LENGTH_SHORT).show();
        //opening a main activity on below line.
        startActivity(new Intent(EditContactActivity.this, MainActivity.class));
    }
}