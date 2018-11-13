package image.tumblr.hot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FullStoryActivity extends AppCompatActivity {

    private TextView mTvTitle,mTvFullStory;
    private ImageView imageNews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_story);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvFullStory = (TextView) findViewById(R.id.tv_full_story);
        imageNews = (ImageView) findViewById(R.id.image);

        if (this.getIntent()!=null){
            mTvTitle.setText(this.getIntent().getStringExtra("title"));
            mTvFullStory.setText(this.getIntent().getStringExtra("full_story"));


            Picasso.get().load(this.getIntent().getStringExtra("url")).
                    into(imageNews);
        }
    }
}
