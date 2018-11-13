package image.tumblr.hot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

/**
 * Created by Dev on 6/8/2017.
 */

public class FullImageActivity extends AppCompatActivity {
    ProgressDialog dialog;
    private TouchImageView mFullImage;
    private FloatingActionButton mButtonDelete;
    private TextView mTvProgress;
    Post postItemData;
    private DisplayImageOptions options;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        getSupportActionBar().setTitle("Full Image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if (getIntent() != null) {
            postItemData = (Post) intent.getSerializableExtra("url");
        }
        mFullImage = (TouchImageView) findViewById(R.id.image_full);
        mButtonDelete = (FloatingActionButton) findViewById(R.id.floating_button_delete);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);



        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoader imageLoader = ImageLoader.getInstance();


        imageLoader.displayImage(postItemData.url, mFullImage, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

                dialog = new ProgressDialog(FullImageActivity.this);
                dialog.show();
                dialog.setMessage("Loading...");
                dialog.setCancelable(false);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mTvProgress.setVisibility(View.GONE);
                mFullImage.setImageBitmap(loadedImage);
                mFullImage.setZoom(1);
                dialog.dismiss();

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                Log.e("TAG", "onProgressUpdate: " + current + "   " + total);
                mTvProgress.setText(String.valueOf(Math.round(current / total) * 100));
            }
        });


        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });

    }

    private void deleteItem() {
        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

// Create a reference to the file to delete
        StorageReference desertRef = storageRef.child("photos").child(postItemData.imageId);

// Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                deleteFromRealTimeDatabase();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.e(TAG, "onFailure: " + exception);
                if (exception instanceof StorageException) {
                    deleteFromRealTimeDatabase();
                }
            }
        });
    }

    private void deleteFromRealTimeDatabase() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("photos").child(postItemData.key).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(FullImageActivity.this, "deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

