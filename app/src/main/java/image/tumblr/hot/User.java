package image.tumblr.hot;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class User implements Serializable {

    public String firstName;
    public String lastName;
    public String email;
    public String mobile;
    public String bloodGroup;
    public String password;
    public double time;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public User(double time, String firstName, String lastName, String email, String mobile, String bloodGroup, String password) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.bloodGroup = bloodGroup;
        this.time = time;
        this.password = password;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("email", email);
        result.put("mobile", mobile);
        result.put("bloodGroup", bloodGroup);
        result.put("password", password);
        result.put("time", time);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
