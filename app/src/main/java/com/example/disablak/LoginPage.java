package com.example.disablak.skysofttaskone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LoginPage extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText email, password;

    private Button customGoogle, customFacebook;

    private LoginButton btn_loginFacebook;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth auth;
    private SignInButton btn_loginGoogle;
    private FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        CheckAtUser();

        if(user == null){
            setContentView(R.layout.activity_main);


            email = findViewById(R.id.email);
            password = findViewById(R.id.password);
            customGoogle = findViewById(R.id.customGoogle);
            customFacebook = findViewById(R.id.customFacebook);

            //google
            btn_loginGoogle = findViewById(R.id.loginGoogle);
            btn_loginGoogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 101);
                }
            });

            //facebook
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);
            btn_loginFacebook = findViewById(R.id.loginFacebook);
            callbackManager = CallbackManager.Factory.create();
            btn_loginFacebook.setReadPermissions(Arrays.asList("email"));
        }
        else {
            MakeToast("loading...");
            ToHome();
        }
    }


    public void onClickCustomFacebook(View view){
        if(view == customFacebook){
            btn_loginFacebook.performClick();
        }
    }

    public void onClickCustomGoogle(View view){
        if(view == customGoogle){
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 101);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = auth.getCurrentUser();
                            MakeToast("login successful");
                            Saving("google", user.getDisplayName(), user.getPhotoUrl().toString());
                            ToHome();
                        } else {
                            // If sign in fails, display a message to the user.
                            MakeToast("login unsuccessful");
                        }
                    }
                });
    }

    public void Login(View view) {
        final String PASSWORD_REGAX = "[a-zA-Z0-9]{8,20}+";
        final String EMAIL_REGAX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        if (email.getText().toString().matches(EMAIL_REGAX)) {
            if (password.getText().toString().matches(PASSWORD_REGAX)) {
                Saving("default", email.getText().toString(), "https://image.flaticon.com/icons/png/512/23/23228.png");
                ToHome();
            } else MakeToast("Wrong Password");
        } else MakeToast("Wrong Email");
    }

    public void LoginAsFacebook(View view){

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                MakeToast("user cancelled it");
            }

            @Override
            public void onError(FacebookException error) {
                MakeToast(error.getMessage());
            }
        });
    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user = auth.getCurrentUser();
                    Saving("facebook", user.getDisplayName(), user.getPhotoUrl().toString());
                    MakeToast("success sign in Facebook");
                    ToHome();
                }
                else {
                    MakeToast("error sign in Facebook");
                }
            }
        });
    }

    void CheckAtUser(){
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        String loginAs = settings.getString("loginAs", "null");
        if(loginAs.equals("default")){
            MakeToast("loading...");
            ToHome();
        }
    }

    void ToHome(){
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
        finish();
    }

    void MakeToast(String str) {
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
        toast.show();
    }

    void Saving(String loginAs, String name, String imageURL){
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("loginAs", loginAs);
        editor.putString("name", name);
        editor.putString("imageURL", imageURL);
        editor.apply();
    }
}
