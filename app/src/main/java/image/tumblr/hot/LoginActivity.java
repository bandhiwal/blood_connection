package image.tumblr.hot;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import image.tumblr.hot.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mBinding;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("", "createUser:onComplete:" + task.isSuccessful());

                    }
                });
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mBinding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, SignupActivity.class));

            }
        });


        mBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkForm(mBinding.etEmail)) {
                    if (mBinding.etPassword.getVisibility() == View.VISIBLE && !checkForm(mBinding.etPassword)) {
                        return;
                    }
                    login();
                }
                /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();*/
            }
        });


        mBinding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void login() {

        Query postsQuery = mDatabase.child("users").orderByChild("time");
        postsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    if (user.email.equals(text(mBinding.etEmail))) {
                        if (user.password.equals(text(mBinding.etPassword))) {
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                            return;
                        } else {
                            Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_LONG).show();
                            return;
                        }
                    } else {

                    }

                }
                Toast.makeText(LoginActivity.this, "User doesn't exist", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("", "");
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

    public String text(EditText editText) {

        return editText.getText().toString().trim();
    }

    public String text(TextView editText) {

        return editText.getText().toString().trim();
    }

}
