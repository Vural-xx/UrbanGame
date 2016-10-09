package nl.hs_hague.urbangame;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UserSettingsActivity extends AppCompatActivity {

    FirebaseAuth mAuthListener;
    FirebaseUser fbUser;
    Button confirmButton;
    Button uImgButton;
    private StorageReference mStorageReference;
    private static final int GALLERY_INTENT = 2;




    EditText etUsername, etEmail, etPass, etPass2;

    String newUsername, newEmail, newPass, newPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        confirmButton = (Button)findViewById(R.id.btnConfirm);
        uImgButton = (Button) findViewById(R.id.btnUploadImage);



        confirmButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        if (fbUser != null) {



                            etUsername = (EditText) findViewById(R.id.etUsername);
                            etEmail = (EditText) findViewById(R.id.etEmail);
                            etPass = (EditText) findViewById(R.id.etPass);
                            etPass2 = (EditText) findViewById(R.id.etPass2);

                            newUsername = etUsername.getText().toString();
                            newEmail = etEmail.getText().toString();
                            newPass = etPass.getText().toString();
                            newPass2 = etPass2.getText().toString();



                            if(!newUsername.isEmpty()){

                                UserProfileChangeRequest fbUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(newUsername)
                                        .build();

                                fbUser.updateProfile(fbUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("profileUpdateUsername", "User profile updated.");
                                                    //Toast.makeText(getApplicationContext(), fbUser.getDisplayName(), Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                                

                            }

                            if(!newEmail.isEmpty() && newEmail.contains("@")){



                                fbUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("email update", "User email address updated.");
                                        }
                                    }
                                });
                            }

                            if(!newPass.equals("") && newPass.equals(newPass2)){
                                fbUser.updatePassword(newPass);

                            }
                            Toast.makeText(getApplicationContext(), R.string.profile_updated, Toast.LENGTH_SHORT).show();



                            // User is signed in
                        } else {
                            // No user is signed in
                        }

                    }
                });

        uImgButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, GALLERY_INTENT);
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            Uri uri = intent.getData();
            StorageReference filepath = mStorageReference.child("UserPhotos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), R.string.user_photo_uploaded, Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), R.string.user_photo_upload_fail, Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

    public void UpdateEmail(String _email){
        fbUser.updateEmail(_email);

    }
}
