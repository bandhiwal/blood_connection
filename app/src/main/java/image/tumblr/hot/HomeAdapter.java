package image.tumblr.hot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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

import java.util.ArrayList;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private final ArrayList<Post> mValues;
    private Context mContext;

    Bitmap imageBitmap;
    public HomeAdapter(Context context, ArrayList<Post> items) {
        mValues = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
       // holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);





        String url = mValues.get(position).url.replace("\"","");
        holder.mContentView.setText(mValues.get(position).description);
        holder.mTextDuration.setText(mValues.get(position).title);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(url, holder.mImageView);
        if (holder.mImageView.getWidth()>0)
        Picasso.get().load(url).resize(holder.mImageView.getWidth(),holder.mImageView.getHeight()).fit().centerCrop().into(holder.mImageView);
        if (holder.mImageView.getWidth()>0) {
            Picasso.get().load(url).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    holder.mImageView.setImageBitmap(bitmap);
                    imageBitmap = bitmap;
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }


                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,FullImageActivity.class).putExtra("url",mValues.get(position)));
                DatabaseReference globalPostRef = FirebaseDatabase.getInstance().getReference().child("photos").child(mValues.get(position).key);
                onView(globalPostRef);

            }
        });
        if (mValues.get(position).stars.containsKey(getUid())) {
            holder.starView.setImageResource(R.drawable.star_fill_icon);
        } else {
            holder.starView.setImageResource(R.drawable.start_icon);
        }

        if (mValues.get(position).views.containsKey(getUid())) {
            holder.viewImage.setImageResource(R.drawable.graph_icon);
        } else {
            holder.viewImage.setImageResource(R.drawable.graph_icon);
        }
        holder.mStarCount.setText(String.valueOf(mValues.get(position).starCount));
        holder.mViewCount.setText(String.valueOf(mValues.get(position).viewCount));
        holder.starView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference globalPostRef = FirebaseDatabase.getInstance().getReference().child("photos").child(mValues.get(position).key);

                onStarClicked(globalPostRef);
            }

        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mContentView;
        public final TextView mTextDuration;
        public final TextView mStarCount,mViewCount;
        public final ImageView starView,viewImage;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.thumnailImage);
            mContentView = (TextView) view.findViewById(R.id.videolisttext);
            mTextDuration = (TextView) view.findViewById(R.id.tv_duration);
            mStarCount = (TextView) view.findViewById(R.id.post_num_stars);
            starView = (ImageView) itemView.findViewById(R.id.star);
            mViewCount = (TextView) view.findViewById(R.id.post_num_views);
            viewImage = (ImageView) itemView.findViewById(R.id.view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
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

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
