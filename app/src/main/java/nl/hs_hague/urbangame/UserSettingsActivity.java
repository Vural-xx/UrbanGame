package nl.hs_hague.urbangame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.facebook.FacebookSdk.getApplicationContext;
import static nl.hs_hague.urbangame.R.string.profile;

public class UserSettingsActivity extends AppCompatActivity implements ReauthDialog.ReauthDialogListener {

    FirebaseUser fbUser;
    Button confirmButton;
    ImageView Profile_Picture;
    StorageReference mStorageReference;
    ReauthDialog newFragment;
    private static final int GALLERY_INTENT = 2;
    private static  String Password="";
    private boolean reauth_done=false;
    private boolean fill_pass=true;

    EditText Username, Email, Pass, Pass2;
    TextView pass,pass2,reauth;
    String newUsername, newEmail, newPass, newPass2;
    Uri myuri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        confirmButton = (Button)findViewById(R.id.btnConfirm);
        Profile_Picture = (ImageView) findViewById(R.id.profileImage);
        mStorageReference = FirebaseStorage.getInstance().getReference();

        Username = (EditText) findViewById(R.id.etUsername);
        Email = (EditText) findViewById(R.id.etEmail);
        Pass = (EditText) findViewById(R.id.etPass);
        Pass2 = (EditText) findViewById(R.id.etPass2);
        pass = (TextView) findViewById(R.id.textView5);
        pass2 = (TextView) findViewById(R.id.textView6);
        reauth= (TextView) findViewById(R.id.reauth);

                                                    //if face load facebook data and unable views
        if(fbUser!=null){
                if(fbUser.getDisplayName()==null)
                    Username.setText(fbUser.getEmail());
                else
                    Username.setText(fbUser.getDisplayName());

               if (fbUser.getPhotoUrl()!=null) //cuando no tenga imagen
               {Profile_Picture.setImageURI(fbUser.getPhotoUrl());
                   System.out.println(fbUser.getPhotoUrl().toString());
               }
               else//pone la de default
                   Profile_Picture.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

               Email.setText(fbUser.getEmail());
               Email.setEnabled(false);
               Pass.setVisibility(View.INVISIBLE);
               Pass2.setVisibility(View.INVISIBLE);
               pass.setVisibility(View.INVISIBLE);
               pass2.setVisibility(View.INVISIBLE);
        }
        else {finish();}


        reauth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoticeDialog();
            }
        });


        confirmButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        if (fbUser!=null) {

                            newUsername = Username.getText().toString();
                            newEmail = Email.getText().toString();
                            newPass = Pass.getText().toString();
                            newPass2 = Pass2.getText().toString();

                            if(myuri!=null)
                            {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(myuri)
                                        .build();
                                fbUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UserSettingsActivity.this, "Image updated", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            } //else no hubo cambio de foto

                            if(!newUsername.isEmpty()){
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(newUsername)
                                        .build();

                                fbUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(UserSettingsActivity.this, "Username updated", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }

                            if(reauth_done && !newEmail.isEmpty() && newEmail.contains("@"))  //need reauthentication
                            {
                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(fbUser.getEmail(), Password);
                                fbUser.reauthenticate(credential);
                                fbUser.updateEmail(newEmail)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(UserSettingsActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });}

                            if(!newPass.isEmpty()) { //need reauthentication
                                if (newPass.equals(newPass2) && newPass.length()>5) {
                                       AuthCredential credential = EmailAuthProvider
                                              .getCredential(fbUser.getEmail(), Password);
                                    fbUser.reauthenticate(credential);
                                    fbUser.updatePassword(newPass)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(UserSettingsActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    fill_pass=true;
                                } else
                                {Toast.makeText(UserSettingsActivity.this, "Error verify password", Toast.LENGTH_SHORT).show();
                                  fill_pass=false; }

                            } else fill_pass=true;

                        }//user null
                        if(fill_pass)
                           finish();
                    }
                });

        Profile_Picture.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, GALLERY_INTENT);
                    }
                });
    }


    public void showNoticeDialog ()
    {
        newFragment = new ReauthDialog();
        newFragment.show(getSupportFragmentManager(), "dialog_reauth");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, final String password) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(fbUser.getEmail(), password);

            fbUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                reauth.setVisibility(View.INVISIBLE);
                                Pass.setVisibility(View.VISIBLE);
                                Pass2.setVisibility(View.VISIBLE);
                                pass.setVisibility(View.VISIBLE);
                                pass2.setVisibility(View.VISIBLE);
                                Email.setEnabled(true);
                                Password = password;
                                reauth_done=true;
                            }else
                                Toast.makeText(getApplicationContext(), "No reauthentication, wrong password", Toast.LENGTH_SHORT).show();
                        }
                    });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
             myuri = intent.getData();
            StorageReference filepath = mStorageReference.child("UserPhotos").child(myuri.getLastPathSegment());
            filepath.putFile(myuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Profile_Picture.setImageURI(myuri);
                    System.out.println(myuri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), R.string.user_photo_upload_fail, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
