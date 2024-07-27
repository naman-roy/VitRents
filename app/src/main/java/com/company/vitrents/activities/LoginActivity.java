package com.company.vitrents.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.company.vitrents.MyUtils;
import com.company.vitrents.R;
import com.company.vitrents.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private static final String TAG = "Login_TAG";

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private GoogleSignInClient mgoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth=FirebaseAuth.getInstance();

        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mgoogleSignInClient= GoogleSignIn.getClient(this,gso);

        binding.skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.loginGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginGoogleLogin();

            }
        });

        binding.loginEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,LoginEmailActivity.class));
            }
        });
    }
    private void beginGoogleLogin(){
        Log.d(TAG, "beginGoogleLogin: ");

        Intent googleSignInIntent = mgoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(googleSignInIntent);
    }

    private ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG,"onActivityResult: ");

                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data=result.getData();
                        Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);

                        try {

                            GoogleSignInAccount account=task.getResult(ApiException.class);
                            Log.d(TAG, "onActivityResult: AccountID: "+account.getId());
                            firebaseAuthWithGoogleAccount(account.getIdToken());


                        } catch (Exception e){
                            Log.e(TAG, "onActivityResult: ",e);
                        }
                    }else {
                        Log.d(TAG, "onActivityResult: Cancelled");
                        MyUtils.showToast(LoginActivity.this,"Cancelled");
                    }
                }
            }
    );

    private void firebaseAuthWithGoogleAccount(String idToken){
        Log.d(TAG, "firebaseAuthWithGoogleAccount: idToken: "+idToken);

        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if(authResult.getAdditionalUserInfo().isNewUser()){
                            Log.d(TAG, "onSuccess: New User, Account Created");
                            updateUserInfoDb();
                        }else{
                            Log.d(TAG, "onSuccess: Existing User, Logged In");

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finishAffinity();
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }
    private void updateUserInfoDb(){
        Log.d(TAG, "updateUserInfoDb: ");

        progressDialog.setMessage("Saving User Info");
        progressDialog.show();

        long timestamp = MyUtils.timestamp();
        String registeredUid=firebaseAuth.getUid();
        String registeredUserEmail=firebaseAuth.getCurrentUser().getEmail();
        String name=firebaseAuth.getCurrentUser().getDisplayName();

        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("timestamp",timestamp);
        hashMap.put("uid",registeredUid);
        hashMap.put("email",registeredUserEmail);
        hashMap.put("name",name);
        hashMap.put("phoneCode","");
        hashMap.put("phoneNumber","");
        hashMap.put("profileImageUrl","");
        hashMap.put("dob","");
        hashMap.put("userType",MyUtils.USER_TYPE_GOOGLE);
        hashMap.put("token","");

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(registeredUid).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Log.d(TAG, "onSuccess: User Info Saved");
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finishAffinity();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        progressDialog.dismiss();
                        MyUtils.showToast(LoginActivity.this,"Failed to save due to"+e.getMessage());
                    }
                });

    }

}