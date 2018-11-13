package image.tumblr.hot;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import java.lang.reflect.Array;
import java.security.PrivateKey;
import java.util.ArrayList;

import image.tumblr.hot.databinding.ActivityDonorBinding;

public class DonorActivity extends AppCompatActivity {

    private ActivityDonorBinding mBinding;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ArrayList<User> list;
    RecyclerViewArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_donor);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        list = new ArrayList<>();
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        mBinding.recyclerView.setHasFixedSize(true);

        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("", "createUser:onComplete:" + task.isSuccessful());

                    }
                });
        mDatabase = FirebaseDatabase.getInstance().getReference();


        mBinding.spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    findDonor();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mBinding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void findDonor() {

        Query postsQuery = mDatabase.child("users").orderByChild("time");
        postsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    if (user.bloodGroup.equals(mBinding.spinner1.getSelectedItem().toString())) {
                        list.add(user);
                        setAdapter();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("", "");
            }
        });

    }
    private void setAdapter() {
        adapter = new RecyclerViewArrayAdapter(list, null);
        adapter.setEmptyTextView(mBinding.tvEmptyView, R.string.default_empty_list_info);
        mBinding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
