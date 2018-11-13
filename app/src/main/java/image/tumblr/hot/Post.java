package image.tumblr.hot;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post implements Serializable{

    public String key;
    public String description;
    public String fullStory;
    public String title;
    public String url;
    public String imageId;
    public double time;

    public int starCount = 0;
    public int viewCount =0;
    public Map<String, Boolean> stars = new HashMap<>();
    public Map<String, Boolean> views = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(double time, String key, String title, String url, String description, String imageId, String fullStory) {

        this.key = key;
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageId = imageId;
        this.time = time;
        this.fullStory = fullStory;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("description", description);
        result.put("title", title);
        result.put("url", url);
        result.put("fullStory", fullStory);
        result.put("imageId", imageId);
        result.put("time", time);
        result.put("starCount", starCount);
        result.put("viewCount", viewCount);
        result.put("stars", stars);
        result.put("views", views);


        return result;
    }
    // [END post_to_map]

}
// [END post_class]
