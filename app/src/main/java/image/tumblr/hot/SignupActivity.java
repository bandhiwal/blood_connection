package image.tumblr.hot;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import image.tumblr.hot.databinding.ActivitySignUpBinding;


public class SignupActivity extends AppCompatActivity {

    private ActivitySignUpBinding mBinding;

    //firebase auth
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // [END get_storage_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();

        database = FirebaseDatabase.getInstance();

        mBinding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkForm(mBinding.etFirstName, mBinding.etLastName, mBinding.etEmail, mBinding.etMobile, mBinding.etPassword,mBinding.etRePassword)) {
                    if (!text(mBinding.etPassword).equals(text(mBinding.etRePassword))){
                        Toast.makeText(SignupActivity.this, "Password didn't match", Toast.LENGTH_SHORT).show();
                    }else {
                        signInAnonymously();
                    }
                }

            }
        });

        mBinding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mBinding.tvLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));

            }
        });
    }

    public boolean checkForm(EditText... views) {
        for (EditText editText : views) {
            if (editText.getText().toString().trim().isEmpty()) {
                editText.setError("Please enter " + editText.getHint());
                editText.requestFocus();
                return false;
            } else if (editText.getInputType() - 1 == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS &&
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches()) {
                editText.setError("Please enter valid email");
                editText.requestFocus();
                return false;
            } else if (editText.getInputType() - 1 == InputType.TYPE_TEXT_VARIATION_PASSWORD &&
                    editText.getText().toString().length() < 6) {
                editText.setError("Password should be minimum " + 6 + " characters");
                editText.requestFocus();

                return false;
            } else if (editText.getInputType() - 1 == InputType.TYPE_CLASS_PHONE &&
                    editText.getText().toString().length() < 10) {
                editText.setError("Mobile should be minimum " + 10 + " characters");
                editText.requestFocus();

                return false;
            }

        }
        return true;
    }

    private void signInAnonymously() {
        // Sign in anonymously. Authentication is required to read or write from Firebase Storage.
        //showProgressDialog();
        mAuth.signInAnonymously()
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d("", "signInAnonymously:SUCCESS");
                        //hideProgressDialog();
                        //updateUI(authResult.getUser());
                        writeNewUser();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("", "signInAnonymously:FAILURE", exception);
                        //hideProgressDialog();
                        //updateUI(null);
                        signInAnonymously();
                    }
                });
    }

    private void writeNewUser() {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        double time = (-System.currentTimeMillis());
        String key = mDatabase.child("users").push().getKey();

        User post = new User(time,text(mBinding.etFirstName),text(mBinding.etLastName),text(mBinding.etEmail),text(mBinding.etMobile)
        ,mBinding.spinner1.getSelectedItem().toString(),text(mBinding.etPassword));

        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + key, postValues);

        mDatabase.updateChildren(childUpdates);

        Toast.makeText(this, "Successfully added", Toast.LENGTH_SHORT).show();
        finish();
    }

    public String text(EditText editText) {

        return editText.getText().toString().trim();
    }

    public String text(TextView editText) {

        return editText.getText().toString().trim();
    }

}
