package cosw.eci.edu.lab8;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by carlos.sanchez-v on 3/24/2018.
 */

public class Post implements Serializable {

    String message;
    String imageUri;

    public Post(){}

    public Post(String message,String imageUri){
        this.message=message;
        this.imageUri=imageUri;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
