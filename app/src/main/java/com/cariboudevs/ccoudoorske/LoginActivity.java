package com.cariboudevs.ccoudoorske;

import android.content.Context;
import android.content.Intent;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.cariboudevs.ccoudoorske.Utility.SnackBarHelper_Error;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    //creating variable for edit text, textview, button, progress bar and firebase auth.
    private EditText userNameEdt, passwordEdt;
    private Button loginBtn;
    private TextView newUserTV,Forgot;
    private FirebaseAuth mAuth;
    private ProgressBar loadingPB;
    private TextInputLayout EmailT,passwordT;
    private String email,password;
    ImageView Back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initializing all our variables.
        userNameEdt = findViewById(R.id.edit_login_email);
        passwordEdt = findViewById(R.id.edit_login_password);
        loginBtn = findViewById(R.id.btn_login);
        newUserTV = findViewById(R.id.Txt_login_signup);
        mAuth = FirebaseAuth.getInstance();
        //loadingPB = findViewById(R.id.idPBLoading);
        EmailT = findViewById(R.id.textInputLayoutEmail);
        passwordT = findViewById(R.id.textInputLayoutPassword);
        Back =findViewById(R.id.back_button_login);
        Forgot=findViewById(R.id.Txt_forgotpassword);
        //adding click listner for our new user tv.
        newUserTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on below line opening a login activity.
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginActivity.this.onBackPressed();


            }
        });
        Forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Opening new user registration activity using intent on button click.
                Intent intent = new Intent(LoginActivity.this, Reset.class);
                startActivity(intent);

            }
        });

        hideKeyboard();
        //text watcher instantiation
        userNameEdt.addTextChangedListener(loginTextWatcher);
        passwordEdt.addTextChangedListener(loginTextWatcher);

        //adding on click listener for our login button.
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setEnabled(false);
                loginBtn.setText("Logging in...");
                 email = userNameEdt.getText().toString();
                 password = passwordEdt.getText().toString();
                 if(FieldsValid())
                 {
                     login();
                 }
                 else
                 {
                     loginBtn.setEnabled(true);
                     loginBtn.setText("Log in");
                 }
            }
        });

    }

    private void login()
    {
        try {
            mAuth.signOut();
        }catch (Exception g)
        {
            Log.e("mauth sign out ",g.getMessage());
        }

        //on below line we are calling a sign in method and passing email and password to it.
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //on below line we are checking if the task is succes or not.
                if (task.isSuccessful()) {
                    //on below line we are hiding our progress bar.
                    // loadingPB.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Login Successful..", Toast.LENGTH_SHORT).show();
                    //on below line we are opening our mainactivity.
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    finish();
                } else {
                    //hiding our progress bar and displaying a toast message.
                    //loadingPB.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Please enter valid user credentials..", Toast.LENGTH_SHORT).show();
                    loginBtn.setEnabled(true);
                    loginBtn.setText("Log in");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //in on start method checking if the user is already sign in.
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //if the user is not null then we are opening a main activity on below line.
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            this.finish();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        userNameEdt.addTextChangedListener(loginTextWatcher);
        passwordEdt.addTextChangedListener(loginTextWatcher);
    }


    @Override public void onPause() {
        super.onPause();
        userNameEdt.removeTextChangedListener(loginTextWatcher);
        passwordEdt.removeTextChangedListener(loginTextWatcher);
    }

    //hide keyboard
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private boolean FieldsValid()
    {
        if (TextUtils.isEmpty(email)&&TextUtils.isEmpty(password))
        {
            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            userNameEdt.startAnimation(animShake);
            passwordEdt.startAnimation(animShake);
            EmailT.startAnimation(animShake);
            passwordT.startAnimation(animShake);

            userNameEdt.setError("Email Required");
            passwordEdt.setError("password cannot be empty");
            Snackbar snack = Snackbar.make(LoginActivity.this.findViewById(android.R.id.content),"Enter email and password to continue!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(LoginActivity.this, snack);
            snack.show();
            return false;

        }
        else if (TextUtils.isEmpty(email)&&passwordEdt.getText().length() < 2)
        {
            //animate textbox

            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            userNameEdt.startAnimation(animShake);
            passwordEdt.startAnimation(animShake);
            EmailT.startAnimation(animShake);
            passwordT.startAnimation(animShake);

            userNameEdt.setError("Email Required");
            passwordEdt.setError("password Is invalid");

            Snackbar snack = Snackbar.make(LoginActivity.this.findViewById(android.R.id.content),"Enter email and a valid password to continue!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(LoginActivity.this, snack);
            snack.show();
            return false;

        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(userNameEdt.getText().toString()).matches()&&TextUtils.isEmpty(password))
        {
            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            userNameEdt.startAnimation(animShake);
            passwordEdt.startAnimation(animShake);
            EmailT.startAnimation(animShake);
            passwordT.startAnimation(animShake);

            userNameEdt.setError("Invalid Email");
            passwordEdt.setError("password cannot be empty");

            Snackbar snack = Snackbar.make(LoginActivity.this.findViewById(android.R.id.content),"Enter a valid email and password to continue!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(LoginActivity.this, snack);
            snack.show();
            return false;

        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(userNameEdt.getText().toString()).matches()&&passwordEdt.getText().length() < 2)
        {
            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            userNameEdt.startAnimation(animShake);
            passwordEdt.startAnimation(animShake);
            EmailT.startAnimation(animShake);
            passwordT.startAnimation(animShake);

            userNameEdt.setError("Email is Invalid");
            passwordEdt.setError("password Is Invalid");

            Snackbar snack = Snackbar.make(LoginActivity.this.findViewById(android.R.id.content),"Enter a valid email and a valid password to continue!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(LoginActivity.this, snack);
            snack.show();
            return false;
        }
        else if (TextUtils.isEmpty(email)) {

            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            userNameEdt.startAnimation(animShake);
            EmailT.startAnimation(animShake);



            userNameEdt.setError("Invalid email");
            Snackbar snack = Snackbar.make(LoginActivity.this.findViewById(android.R.id.content),"Email cannot be empty!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(LoginActivity.this, snack);
            snack.show();

            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(userNameEdt.getText().toString()).matches()) {

            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            userNameEdt.startAnimation(animShake);
            EmailT.startAnimation(animShake);

            userNameEdt.setError("Invalid email");
            Snackbar snack = Snackbar.make(LoginActivity.this.findViewById(android.R.id.content),"Enter a valid email!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(LoginActivity.this, snack);
            snack.show();

            return false;
        }
        else if (TextUtils.isEmpty(password)) {
            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            passwordEdt.startAnimation(animShake);
            passwordT.startAnimation(animShake);

            passwordEdt.setError("password cannot be empty");
            Snackbar snack = Snackbar.make(LoginActivity.this.findViewById(android.R.id.content),"Enter password!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(LoginActivity.this, snack);
            snack.show();


            return false;
        }

        else if (passwordEdt.getText().length() < 2) {
            //animate textbox
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake_textbox);
            passwordEdt.startAnimation(animShake);
            passwordT.startAnimation(animShake);

            passwordEdt.setError("Invalid passsword");
            Snackbar snack = Snackbar.make(LoginActivity.this.findViewById(android.R.id.content),"Invalid password!",      Snackbar.LENGTH_LONG);
            SnackBarHelper_Error.configSnackbar(LoginActivity.this, snack);
            snack.show();


            return false;

        }
        else{

            return true;
        }

    }

    //text watcher begin
    private final TextWatcher loginTextWatcher = new TextWatcher() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            Text_watcher();

        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Text_watcher();

        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void afterTextChanged(Editable s) {

            Text_watcher();

        }
    }; //text watcher end



    //set drawables color in text view whooah!!!!
    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void Text_watcher() {
        String useremail = userNameEdt.getText().toString().trim();
        String passwordInput = passwordEdt.getText().toString().trim();
        if  (!useremail.isEmpty()  && Patterns.EMAIL_ADDRESS.matcher(useremail).matches()){
            EditText text = findViewById(R.id.edit_login_email);
            text.setTextColor(getResources().getColor(R.color.black_shade_2));
            text.setHintTextColor(getResources().getColor(R.color.black_shade_2));
            setTextViewDrawableColor(text, R.color.black_shade_2);
        }else
        {
            EditText text = findViewById(R.id.edit_login_email);
            text.setTextColor(getResources().getColor(R.color.black));
            text.setHintTextColor(getResources().getColor(R.color.black));
            setTextViewDrawableColor(text, R.color.cardview_dark_background);

        }
        if(!(passwordInput.length() <6)){
            EditText text = findViewById(R.id.edit_login_password);
            text.setTextColor(getResources().getColor(R.color.black_shade_2));
            text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_unlocked_1, 0, 0, 0);
            text.setHintTextColor(getResources().getColor(R.color.black_shade_2));
            setTextViewDrawableColor(text, R.color.black_shade_2);
        }else
        {
            EditText text = findViewById(R.id.edit_login_password);
            text.setTextColor(getResources().getColor(R.color.black));
            text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_locked_4, 0, 0, 0);
            text.setHintTextColor(getResources().getColor(R.color.black));
            setTextViewDrawableColor(text, R.color.cardview_dark_background);

        }

    }


}