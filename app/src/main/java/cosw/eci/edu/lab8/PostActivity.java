package cosw.eci.edu.lab8;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class PostActivity extends AppCompatActivity {
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Intent intent = getIntent();
        //Obtain the object
        Post p=(Post) intent.getBundleExtra(MainActivity.POST_MESSAGE).getSerializable(MainActivity.POST_MESSAGE_OBJECT);
        //
        imageUri = Uri.parse(p.getImageUri());
        System.out.println("----------------------------IMPRIMIENDO DESDE NEW ACTIVITY----------------------");
        System.out.println(p.getImageUri());
        System.out.println(p.getMessage());

        Fragment NewPostFragment = new Fragment();
    }

}
