package image.tumblr.hot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.List;

import image.tumblr.hot.Custom.VerticalViewPager;


/**
 * Created by laxmikant bolya on 29-01-2017.
 */

public class NewsVerticalAdapter extends PagerAdapter {

    Activity activity;
    List<Post> arrNewsData;
    ImageLoader imageLoader;

    private static final int PERMISSION_REQUEST_CODE = 1;


    public NewsVerticalAdapter(Activity activity, List<Post> arrNewsData) {

        this.activity = activity;
        this.arrNewsData = arrNewsData;


      //  imageLoader = AppController.getInstance().getImageLoader();
    }


    @Override
    public Object instantiateItem(View collection, final int position) {
        final ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.news_vertical_adapter, null);

        final Post model = arrNewsData.get(position);
        holder.internetUna = (RelativeLayout) layout.findViewById(R.id.internetUna);
        holder.image = (ImageView) layout.findViewById(R.id.image);
      //  holder.mainWord = (TextView) layout.findViewById(R.id.mainWord);
        holder.meaniniTv = (TextView) layout.findViewById(R.id.meaniniTv);
        holder.description = (TextView) layout.findViewById(R.id.description);
     //   holder.totalPage = (TextView) layout.findViewById(R.id.totalPage);
    //    holder.currentPage = (TextView) layout.findViewById(R.id.currentPage);
      //  holder.sourceImage = (ImageView) layout.findViewById(R.id.sourceImage);
       // holder.share = (ImageView) layout.findViewById(R.id.share);
        holder.goUrl = (TextView) layout.findViewById(R.id.goUrl);
        holder.mStarCount = (TextView) layout.findViewById(R.id.post_num_stars);
        holder.starView = (ImageView) layout.findViewById(R.id.star);


        Picasso.get().load(model.url).
                into(holder.image);

        if (model.stars.containsKey(getUid())) {
            holder.starView.setImageResource(R.drawable.star_fill_icon);
        } else {
            holder.starView.setImageResource(R.drawable.start_icon);
        }


        Log.e("image", model.url + "");
       // holder.image.setImageUrl(model.getUrlToImagae(), imageLoader);
        holder.description.setText(model.description);
        holder.meaniniTv.setText(model.title);
      //  holder.mainWord.setText(sourceModel.getNamePaper());


        holder.mStarCount.setText(String.valueOf(model.starCount));

        holder.starView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference globalPostRef = FirebaseDatabase.getInstance().getReference().child("photos").child(model.key);

                onStarClicked(globalPostRef);
            }

        });

        holder.goUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity,FullStoryActivity.class);
                intent.putExtra("title",model.title);
                intent.putExtra("full_story",model.fullStory);
                intent.putExtra("url",model.url);
                activity.startActivity(intent);

                DatabaseReference globalPostRef = FirebaseDatabase.getInstance().getReference().child("photos").child(model.key);
                onView(globalPostRef);
            }
        });


        ((VerticalViewPager) collection).addView(layout);
        return layout;
    }

    @Override
    public int getCount() {
        return arrNewsData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    class ViewHolder {
        RelativeLayout internetUna;
        ImageView image;
        TextView goUrl;
        ImageView sourceImage;
        TextView  meaniniTv, description;
        ImageView share;
        TextView mStarCount;
        ImageView starView;
    }

    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((VerticalViewPager) collection).removeView((View) view);
    }

    public void shareIntent() {
        Intent Intent1 = new Intent(Intent.ACTION_SEND);
        /**Capture the image of screen */
        View v1 = activity.getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap b = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        Intent1.setType("*/*");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        Uri imageUri = null;
        try {
            String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(),
                    b, "Title", null);
            imageUri = Uri.parse(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent1.putExtra(Intent.EXTRA_SUBJECT, "");

        Intent1.putExtra(Intent.EXTRA_STREAM, imageUri);
        //  Intent1.setDataAndType(imageUri,"*/*");
        Intent1.setType("image/jpeg");
        Intent1.putExtra(Intent.EXTRA_TEXT, "Get Daily Updated and summarized News at " + "\n" +
                "https://play.google.com/store/apps/details?id=" + activity.getPackageName() + "&hl=en");
        activity.startActivity(Intent.createChooser(Intent1, "Share this News with"));

    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void onView(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                final Post p = mutableData.getValue(Post.class);

                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (!p.views.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.viewCount = p.viewCount + 1;
                    p.views.put(getUid(), true);

                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed

            }
        });
    }

    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                final Post p = mutableData.getValue(Post.class);

                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);


                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed

            }
        });
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            //Toast.makeText(getApplicationContext(),"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

}

