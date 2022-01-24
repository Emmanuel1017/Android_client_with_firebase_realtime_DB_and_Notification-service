package com.cariboudevs.ccoudoorske;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.cariboudevs.ccoudoorske.Utility.ConnectionChecker;
import com.cariboudevs.ccoudoorske.Utility.SnackBarHelper;
import com.cariboudevs.ccoudoorske.Utility.SnackBarHelper_Error;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Reset extends AppCompatActivity {
    Button Reset,Resend;
    ProgressBar Loading;
    EditText EmailE;
    TextView Instruction,Resendtext;
    ConnectionChecker utils;
    String email;
    ImageView Back;
    LinearLayout Textlinear;
    LottieAnimationView Lotti;
    private FirebaseAuth auth;
    private TextInputLayout EmailT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        //declare connection checker
        utils = new ConnectionChecker(this);


        Textlinear =findViewById(R.id.didnt_receive_linear);
        Back =findViewById(R.id.back_buttonreset);
        Resend =findViewById(R.id.btn_resend);
        Reset =findViewById(R.id.btn_reset);
        Loading =findViewById(R.id.progressBarreset);
        Resendtext =findViewById(R.id.didnt_receive);
        EmailE =findViewById(R.id.edit_reset_email);
        Instruction=findViewById(R.id.Instructionsreset);
        auth = FirebaseAuth.getInstance();
        Lotti=findViewById(R.id.animationViewreset2);
        EmailT=findViewById(R.id.textInputLayoutEmailReset);



        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Reset.this.onBackPressed();
                finish();


            }
        });



        Resendtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Instruction.setText("Forgot your password. Enter you Email below and click the reset button. A link will be sent on your email inbox where you will use to reset your password and set a new one.");
                Instruction.setTextColor(Color.BLACK);

                Lotti.setAnimation(R.raw.forgot_password);
                Lotti.playAnimation();
                Lotti.setRepeatMode(LottieDrawable.RESTART);
                Lotti.setRepeatCount(100000);

                EmailE.setText("");
                EmailE.setVisibility(View.VISIBLE);
                EmailT.setVisibility(View.VISIBLE);

                Reset.setVisibility(View.VISIBLE);

                Resend.setVisibility(View.GONE);
                Textlinear.setVisibility(View.GONE);


            }
        });


        Resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Loading.setVisibility(View.VISIBLE);
                if (utils.haveNetworkConnection()) {
                    reset();
                } else {
                    Instruction.setText("Forgot your password. Enter you Email below and click the reset button. A link will be sent on your email inbox where you will use to reset your password and set a new one.");
                    Instruction.setTextColor(Color.BLACK);


                    EmailE.setVisibility(View.VISIBLE);
                    EmailT.setVisibility(View.VISIBLE);
                    Reset.setVisibility(View.VISIBLE);


                    Resend.setVisibility(View.GONE);
                    Textlinear.setVisibility(View.GONE);

                    Snackbar snack = Snackbar.make(Reset.this.findViewById(android.R.id.content),"No internet connection. Make sure your internet is connected to continue.!",      Snackbar.LENGTH_LONG);
                    SnackBarHelper_Error.configSnackbar(Reset.this, snack);
                    snack.show();
                    Loading.setVisibility(View.INVISIBLE);

                }

            }
        });



        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check connection using class


                email = EmailE.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {

                    final Animation animShake = AnimationUtils.loadAnimation(Reset.this, R.anim.shake_textbox);
                    EmailE.startAnimation(animShake);

                    Snackbar snack = Snackbar.make(Reset.this.findViewById(android.R.id.content),"Enter your registered email id to continue",      Snackbar.LENGTH_LONG);
                    SnackBarHelper.configSnackbar(Reset.this, snack);
                    snack.show();

                    EmailE.setError("Enter a valid  email");

                    return;
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //animate textbox
                    final Animation animShake = AnimationUtils.loadAnimation(Reset.this, R.anim.shake_textbox);
                    EmailE.startAnimation(animShake);


                    Snackbar snack = Snackbar.make(Reset.this.findViewById(android.R.id.content),"Enter a valid Email and password!",      Snackbar.LENGTH_LONG);
                    SnackBarHelper_Error.configSnackbar(Reset.this, snack);
                    snack.show();

                    EmailE.setError("Enter a valid  email");

                    return;
                }

                Loading.setVisibility(View.VISIBLE);
                if (utils.haveNetworkConnection()) {
                    reset();
                    Reset.setText("Please Wait...");
                } else {
                    Snackbar snack = Snackbar.make(Reset.this.findViewById(android.R.id.content),"No internet connection. Make sure your internet is connected to continue.!",      Snackbar.LENGTH_LONG);
                    SnackBarHelper_Error.configSnackbar(Reset.this, snack);
                    snack.show();
                    Loading.setVisibility(View.INVISIBLE);


                }



            }

        });



    }





    public void reset (){

        auth.fetchSignInMethodsForEmail(EmailE.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                try {
                    Log.d("Reset Activity", "" + task.getResult().getSignInMethods().size());
                    if (task.getResult().getSignInMethods().size() == 0) {
                        // email not existed

                        Snackbar snack = Snackbar.make(Reset.this.findViewById(android.R.id.content), "You cannot reset password as email is not registered.Check email if correct or registered!", Snackbar.LENGTH_LONG);
                        SnackBarHelper_Error.configSnackbar(Reset.this, snack);
                        snack.show();
                        Loading.setVisibility(View.INVISIBLE);
                        Reset.setText("Reset");

                    } else {
                        //email exists
                        Acrualreset();
                    }
                }
                catch (Exception e){
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



    public void Acrualreset(){





        auth.sendPasswordResetEmail(email)

                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Instruction.setText("Reset link sent successfuly to your email :"+email);
                            Instruction.setTextColor(R.color.purple_700);

                            EmailE.setVisibility(View.GONE);
                            EmailT.setVisibility(View.GONE);
                            Reset.setVisibility(View.GONE);

                            Lotti.setAnimation(R.raw.robothappy);
                            Lotti.playAnimation();
                            Lotti.setRepeatMode(LottieDrawable.RESTART);
                            Lotti.setRepeatCount(10000);


                            Resend.setVisibility(View.VISIBLE);
                            Textlinear.setVisibility(View.VISIBLE);

                            final SweetAlertDialog CC = new SweetAlertDialog(Reset.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                            CC.setCustomImage(R.drawable.ic_sent_mail);
                            CC.setTitleText("Reset link sent.");
                            CC.setContentText("Link to reset sent on " + email);
                            CC.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                            CC.setCancelable(false);
                            CC.show();

                            Snackbar snack = Snackbar.make(Reset.this.findViewById(android.R.id.content),"We have sent you instructions to reset your password!"+ "To your Email : " + email,      Snackbar.LENGTH_LONG);
                            SnackBarHelper.configSnackbar(Reset.this, snack);
                            snack.show();
                            Loading.setVisibility(View.INVISIBLE);
                            Reset.setText("Reset");

                        } else {
                            Snackbar snack = Snackbar.make(Reset.this.findViewById(android.R.id.content),"Failed to send reset email.Try again later!",      Snackbar.LENGTH_LONG);
                            SnackBarHelper_Error.configSnackbar(Reset.this, snack);
                            snack.show();
                            Loading.setVisibility(View.INVISIBLE);
                            Reset.setText("Reset");

                        }


                    }
                });
    }
}