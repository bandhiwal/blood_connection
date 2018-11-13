package image.tumblr.hot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

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

import java.util.ArrayList;
import java.util.Collections;

import image.tumblr.hot.Custom.VerticalViewPager;
import image.tumblr.hot.Custom.ZoomOutPageTransformer;

public class HomeActivity extends AppCompatActivity {

    //private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String TAG = this.getClass().getSimpleName();
    ArrayList<Post> list = new ArrayList<>();
    //HomeAdapter adapter;
    ProgressBar progressBar;
    FloatingActionButton mFloatingButton;
    VerticalViewPager verticalpager;
    NewsVerticalAdapter newsVerticalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //   mRecyclerView = (RecyclerView) findViewById(R.id.list);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        mFloatingButton = (FloatingActionButton) findViewById(R.id.floating_button);
        verticalpager = (VerticalViewPager) findViewById(R.id.verticalpager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);


        tabLayout.addTab(tabLayout.newTab().setText("Recent"),true);
        tabLayout.addTab(tabLayout.newTab().setText("Most Viewed"));
        tabLayout.addTab(tabLayout.newTab().setText("Trending"));


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        verticalpager.setClipToPadding(false);
        verticalpager.setPadding(0, 5, 0, 5);
        verticalpager.setPageMargin(10);

        verticalpager.setPageTransformer(true, new ZoomOutPageTransformer());


        newsVerticalAdapter = new NewsVerticalAdapter(HomeActivity.this, list);
        verticalpager.setAdapter(newsVerticalAdapter);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());

                    }
                });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query postsQuery = mDatabase.child("photos").orderByChild("time");
        postsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Post post = snap.getValue(Post.class);
                    list.add(post);
                }
                progressBar.setVisibility(View.GONE);
                newsVerticalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ImageActivity.class));
            }
        });

    }
    private void setCurrentTabFragment(int tabPosition)
    {
        Query postsQuery=null;
        boolean reverse= false;
        switch (tabPosition)
        {

            case 0 :
                 postsQuery = mDatabase.child("photos").orderByChild("time");
                 reverse=false;
                break;
            case 1 :
                 postsQuery = mDatabase.child("photos").orderByChild("viewCount");
                 reverse=true;
                break;
            case 2 :
                 postsQuery = mDatabase.child("photos").orderByChild("starCount");
                 reverse=true;
                break;
                default:
                    break;

        }
        changeData(postsQuery,reverse);
    }
    private void changeData(Query postsQuery, final boolean reverse){
        //Query postsQuery = mDatabase.child("photos").orderByChild("time");
        postsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Post post = snap.getValue(Post.class);
                    list.add(post);
                }
                progressBar.setVisibility(View.GONE);
                if (reverse){
                    Collections.reverse(list);
                }
                newsVerticalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_donor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(HomeActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            return true;
        }
        if (item.getItemId() == R.id.menu_apply) {
            startActivity(new Intent(HomeActivity.this,DonorActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
