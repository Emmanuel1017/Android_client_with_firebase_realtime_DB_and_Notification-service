package com.cariboudevs.ccoudoorske;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.cariboudevs.ccoudoorske.Utility.ConnectionChecker;
import com.cariboudevs.ccoudoorske.Utility.SharedPreferences;
import com.cariboudevs.ccoudoorske.Utility.SnackBarHelper_Error;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class RegisterActivity extends AppCompatActivity {

    //cretaing variables for edit text and textview, firebase auth, button and progress bar.
    private EditText userNameEdt, passwordEdt, confirmPwdEdt;
    private TextInputLayout PasswordrptT,PasswordT,EmailT;
    private TextView loginTV,LoginTv2;
    private Button registerBtn;
    private FirebaseAuth mAuth;
    private ProgressBar loadingPB;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ConnectionChecker utils;
    String userID,Passwordrpt,Password,Email;
    private SweetAlertDialog pDialog;
    private android.content.SharedPreferences sharedPreferences;
    private Map<String ,Object> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //initializing all our variables.
        userNameEdt = findViewById(R.id.edit_register_email);
        passwordEdt = findViewById(R.id.edit_register_password);
        loadingPB = findViewById(R.id.idPBLoading);
        confirmPwdEdt = findViewById(R.id.edit_register_password2);
        loginTV = findViewById(R.id.Txt_register_login);
        LoginTv2 = findViewById(R.id.Txt_login_from_register);
        registerBtn = findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        PasswordrptT =  findViewById(R.id.textInputLayoutPassword2);
        EmailT =  findViewById(R.id.textInputLayoutEmail);
        PasswordT =  findViewById(R.id.textInputLayoutPassword);
        sharedPreferences=getSharedPreferences(SharedPreferences.SHARED_PREF, Context.MODE_PRIVATE);
        utils = new ConnectionChecker(this);
        ImageView Back =findViewById(R.id.back_button_register);

        firebaseDatabase = FirebaseDatabase.getInstance();


        //text watcher instatination
        confirmPwdEdt.addTextChangedListener(RegisterTextWatcher);
        userNameEdt.addTextChangedListener(RegisterTextWatcher);
        passwordEdt.addTextChangedListener(RegisterTextWatcher);

        //adding on click for login tv.
        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opening a login activity on clicking login text.
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RegisterActivity.this.onBackPressed();
                finish();

            }
        });
        //adding on click for login tv.
        LoginTv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opening a login activity on clicking login text.
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
            }
        });
        //adding click listener for register button.
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerBtn.setEnabled(false);
                if(Check_Fields())
                {
                    if(Is_Network_Present())
                    {
                       Register_Using_Firebase();
                    }
                    else
                    {
                        registerBtn.setEnabled(true);
                    }
                }else
                {
                    registerBtn.setEnabled(true);
                }
            }
        });
    }




    private boolean  Check_Fields()
    {
        String Passwordrpt = confirmPwdEdt.getText().toString().trim();
        String  Email = userNameEdt.getText().toString().trim();
        String Password = passwordEdt.getText().toString().trim();
        if (TextUtils.isEmpty(Passwordrpt) &&TextUtils.isEmpty(Email) && TextUtils.isEmpty(Password) ) {
            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            confirmPwdEdt.startAnimation(animShake);
            userNameEdt.startAnimation(animShake);
            passwordEdt.startAnimation(animShake);
            PasswordT.startAnimation(animShake);
            EmailT.startAnimation(animShake);
            PasswordT.startAnimation(animShake);
            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"Fill all the fields to continue!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            snack.show();
            confirmPwdEdt.setError("Required");
            userNameEdt.setError("Enter email");
            passwordEdt.setError("Enter password");
            return false;
        }
        else if (TextUtils.isEmpty(Passwordrpt) && !Patterns.EMAIL_ADDRESS.matcher(userNameEdt.getText().toString()).matches() && TextUtils.isEmpty(Password) ) {
            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            confirmPwdEdt.startAnimation(animShake);
            userNameEdt.startAnimation(animShake);
            passwordEdt.startAnimation(animShake);
            PasswordrptT.startAnimation(animShake);

            EmailT.startAnimation(animShake);
            PasswordT.startAnimation(animShake);

            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"Enter a valid Email and password to continue!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            snack.show();
            confirmPwdEdt.setError("Required");
            userNameEdt.setError("Enter a valid email");
            passwordEdt.setError("Enter password");
            return false;
        }
        else if (Passwordrpt.length()<6 && !Patterns.EMAIL_ADDRESS.matcher(userNameEdt.getText().toString()).matches() &&Password.length() < 6 ) {
            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            confirmPwdEdt.startAnimation(animShake);
            userNameEdt.startAnimation(animShake);
            passwordEdt.startAnimation(animShake);
            PasswordrptT.startAnimation(animShake);
            EmailT.startAnimation(animShake);
            PasswordT.startAnimation(animShake);

            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"Enter a valid Email and password(6 characters long atleast)!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            snack.show();
            confirmPwdEdt.setError("Required");
            userNameEdt.setError("Enter a valid  email");
            passwordEdt.setError("Enter a valid password");
            return false;
        }
        else if (Passwordrpt.length()<6 &&Password.length() < 6 ) {
            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            confirmPwdEdt.startAnimation(animShake);
            passwordEdt.startAnimation(animShake);
            PasswordrptT.startAnimation(animShake);
            PasswordT.startAnimation(animShake);

            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"Password should be atleast 6 characters long!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            snack.show();
            confirmPwdEdt.setError("Required to match password");
            passwordEdt.setError("Password required to be 6 characters or more ");
            return false;
        }
        else if (Passwordrpt.length()<6 && !Patterns.EMAIL_ADDRESS.matcher(userNameEdt.getText().toString()).matches() ) {
            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            confirmPwdEdt.startAnimation(animShake);
            userNameEdt.startAnimation(animShake);
            PasswordrptT.startAnimation(animShake);
            EmailT.startAnimation(animShake);

            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"Enter a Email and password!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            snack.show();
            confirmPwdEdt.setError("Enter a valid  password");
            userNameEdt.setError("Enter a valid email");
            return false;
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(userNameEdt.getText().toString()).matches() &&Password.length() < 6 ) {
            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            userNameEdt.startAnimation(animShake);
            passwordEdt.startAnimation(animShake);
            EmailT.startAnimation(animShake);
            PasswordT.startAnimation(animShake);

            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"Enter a valid Email and password!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            snack.show();
            userNameEdt.setError("Enter a valid  email");
            passwordEdt.setError("Enter a valid password");
            return false;
        }
        else if (TextUtils.isEmpty(Email)) {

            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            userNameEdt.startAnimation(animShake);
            EmailT.startAnimation(animShake);
            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"Email cannot be empty!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            snack.show();
            userNameEdt.setError("Enter email to continue");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(userNameEdt.getText().toString()).matches()) {
            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"Invalid email!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            snack.show();
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            userNameEdt.startAnimation(animShake);
            EmailT.startAnimation(animShake);

            userNameEdt.setError("Enter a valid Email!");
            boolean isEmailValid = false;
            return false;
        } else if (TextUtils.isEmpty(Password)) {

            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            passwordEdt.startAnimation(animShake);
            PasswordT.startAnimation(animShake);

            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"Enter a valid  password!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            snack.show();
            passwordEdt.setError("Password invalid");
            Boolean isPasswordValid = false;
            return false;
        }

        else if (Password.length() < 6) {
            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"Password should be atleast 6 characters!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            passwordEdt.startAnimation(animShake);
            PasswordT.startAnimation(animShake);

            snack.show();
            passwordEdt.setError("Password atlaest 6 characters long");
            Boolean isPasswordValid = false;
            return false;
        } else if (TextUtils.isEmpty(Passwordrpt)) {

            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            confirmPwdEdt.startAnimation(animShake);
            PasswordrptT.startAnimation(animShake);

            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"Retype password to continue!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            snack.show();
            confirmPwdEdt.setError("Cannot be empty");
            return false;
        } else
        if (Passwordrpt.length()<6) {
            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            confirmPwdEdt.startAnimation(animShake);
            PasswordrptT.startAnimation(animShake);

            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"Password mismatch!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            snack.show();
            confirmPwdEdt.setError("Password mismatch");
            return false;
        }
        else
        if (!Password.equals(Passwordrpt)) {
            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            confirmPwdEdt.startAnimation(animShake);
            PasswordrptT.startAnimation(animShake);
            passwordEdt.startAnimation(animShake);
            PasswordT.startAnimation(animShake);

            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"Password mismatch try again!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            snack.show();
            confirmPwdEdt.setError("Password mismatch");
            passwordEdt.setError("Password mismatch");
            return false;
        }


        return true;
    }

    private boolean Is_Network_Present()
    {
        //check wifi internet state
        if (utils.haveNetworkConnection()) {

            return true;

        } else {

            Snackbar snack = Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),"No internet connection.  make sure your internet is connected to continue!",  Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(RegisterActivity.this, snack);
            snack.show();
            return false;

        }
    }


    //check email availability
    private void Register_Using_Firebase(){
        pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Creating  account please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        // Check availability of the email.
        mAuth.fetchSignInMethodsForEmail(userNameEdt.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                try {
                    Log.d("REGISTER", "" + task.getResult().getSignInMethods().size());
                    if (task.getResult().getSignInMethods().size() == 0) {
                        // email not existed
                        String Password2 = confirmPwdEdt.getText().toString().trim();
                        String Email = userNameEdt.getText().toString().trim();
                        String Password = passwordEdt.getText().toString().trim();


                        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("Register", "signInWithCredential:success");

                                    userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy"+"-"+"MM"+"-"+"dd"+" "+"HH:mm:ss", Locale.getDefault());
                                    String currentDateandTime = sdf.format(new Date());

                                    sharedPreferences.edit()
                                            .putString(SharedPreferences.PROFILE_STATE, "com.stansand.stansand.PROFILE_STATE")
                                            .apply();

                                    sharedPreferences.edit()
                                            .putString(SharedPreferences.PROFILE_DP, "com.stansand.stansand.PROFILE_DP")
                                            .apply();



                                    sharedPreferences.edit()
                                            .putString(SharedPreferences.TIME, currentDateandTime)
                                            .apply();

                                    sharedPreferences.edit()
                                            .putString(SharedPreferences.UID, userID)
                                            .apply();

                                    sharedPreferences.edit()
                                            .putString(SharedPreferences.EMAIL, Email)
                                            .apply();

                                    databaseReference = firebaseDatabase.getReference("Devices Timestamp");
                                    databaseReference.child(Objects.requireNonNull(mAuth.getUid())).setValue(String.valueOf(System.currentTimeMillis()));


//no syc yet till edit data//end of mapping update

                                    Toast.makeText(RegisterActivity.this,"Registered "+mAuth.getCurrentUser().getEmail()+".",Toast.LENGTH_LONG);

                                    pDialog.dismiss();
                                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(i);
                                    //intent edit with Name
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("Register", "signInWithCredential:failure", task.getException());
                                    pDialog.dismiss();
                                    final SweetAlertDialog CC = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.WARNING_TYPE);
                                    CC.setTitleText("Failed");
                                    CC.setContentText("Check network!");
                                    CC.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                                    CC.setCancelable(false);
                                    CC.show();
                                    pDialog.dismiss();
                                    registerBtn.setEnabled(true);
                                }
                            }
                        });


                    } else {
                        // email existed
                        final SweetAlertDialog CC = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                        CC.setCustomImage(R.drawable.ic_email);
                        CC.setTitleText("Email already in use");
                        CC.setContentText("Email has already been registered");
                        CC.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        CC.setCancelable(false);
                        CC.show();
                        pDialog.dismiss();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }






    @Override
    public void onResume() {
        super.onResume();
        //text watcher instatination
        confirmPwdEdt.addTextChangedListener(RegisterTextWatcher);
        userNameEdt.addTextChangedListener(RegisterTextWatcher);
        passwordEdt.addTextChangedListener(RegisterTextWatcher);

    }

    @Override public void onPause() {
        super.onPause();
        confirmPwdEdt.removeTextChangedListener(RegisterTextWatcher);
        userNameEdt.removeTextChangedListener(RegisterTextWatcher);
        passwordEdt.removeTextChangedListener(RegisterTextWatcher);
    }


    //set drawables color in text view whooah!!!!
    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }



    //text watcher begin
    private final TextWatcher RegisterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            Text_watcher();

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Text_watcher();

        }
        @Override
        public void afterTextChanged(Editable s) {

            Text_watcher();

        }
    }; //text watcher end

    private void Text_watcher() {
        String useremail = userNameEdt.getText().toString().trim();
        String passwordInput = passwordEdt.getText().toString().trim();
        String passwordrepinput = confirmPwdEdt.getText().toString().trim();

        if (!useremail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(userNameEdt.getText().toString()).matches()) {
            final EditText text = findViewById(R.id.edit_register_email);
            text.setTextColor(getResources().getColor(R.color.black_shade_2));
            text.setHintTextColor(getResources().getColor(R.color.black_shade_2));
            setTextViewDrawableColor(text, R.color.black_shade_2);
        } else {
            final EditText text = findViewById(R.id.edit_register_email);
            text.setTextColor(getResources().getColor(R.color.black));
            text.setHintTextColor(getResources().getColor(R.color.black));
            setTextViewDrawableColor(text, R.color.cardview_dark_background);

        }

        // compare two passwords visually to user using unlock icon
        if (passwordrepinput.equals(passwordInput) && (passwordInput.length() > 5)) {
            final EditText text = findViewById(R.id.edit_register_password2);
            final EditText text2 = findViewById(R.id.edit_register_password);

            // set drawables oder left,top,end , right
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_unlocked_1, 0, 0, 0);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                text2.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_unlocked_1, 0, 0, 0);
            }

            text.setTextColor(getResources().getColor(R.color.black_shade_2));
            text2.setTextColor(getResources().getColor(R.color.black_shade_2));
            text.setHintTextColor(getResources().getColor(R.color.black_shade_2));
            text2.setHintTextColor(getResources().getColor(R.color.black_shade_2));
            setTextViewDrawableColor(text, R.color.black_shade_2);
            setTextViewDrawableColor(text2, R.color.black_shade_2);
        } else {
            final EditText text = findViewById(R.id.edit_register_password);
            final EditText text2 = findViewById(R.id.edit_register_password2);
            text.setTextColor(getResources().getColor(R.color.black));
            text2.setTextColor(getResources().getColor(R.color.black));
            text.setHintTextColor(getResources().getColor(R.color.black));
            // set drawables oder left,top,end , right
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_locked_4, 0, 0, 0);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                text2.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_locked_4, 0, 0, 0);
            }
            setTextViewDrawableColor(text, R.color.cardview_dark_background);
            setTextViewDrawableColor(text2, R.color.cardview_dark_background);

        } //end of password compare


        if (!(passwordInput.length() < 6)) {
            final EditText text = findViewById(R.id.edit_register_password);
            text.setTextColor(getResources().getColor(R.color.black_shade_2));
            text.setHintTextColor(getResources().getColor(R.color.black_shade_2));
            setTextViewDrawableColor(text, R.color.black_shade_2);
        } else {
            final EditText text = findViewById(R.id.edit_register_password);
            text.setTextColor(getResources().getColor(R.color.black));
            text.setHintTextColor(getResources().getColor(R.color.black));
            setTextViewDrawableColor(text, R.color.cardview_dark_background);

        }
        if (!(passwordrepinput.length() < 6)) {
            final EditText text = findViewById(R.id.edit_register_password2);
            text.setTextColor(getResources().getColor(R.color.black_shade_2));
            text.setHintTextColor(getResources().getColor(R.color.black_shade_2));
            setTextViewDrawableColor(text, R.color.black_shade_2);
        } else {
            final EditText text = findViewById(R.id.edit_register_password2);
            text.setTextColor(getResources().getColor(R.color.black));
            text.setHintTextColor(getResources().getColor(R.color.black));
            setTextViewDrawableColor(text, R.color.cardview_dark_background);

        }
    }

}