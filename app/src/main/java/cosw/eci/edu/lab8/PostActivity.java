package cosw.eci.edu.lab8;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

public class PostActivity extends AppCompatActivity implements ShowFragment.OnFragmentInteractionListener {
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Intent intent = getIntent();
        //Obtain the object
        Post p=(Post) intent.getBundleExtra(MainActivity.POST_MESSAGE).getSerializable(MainActivity.POST_MESSAGE_OBJECT);
        //Show fragment
        FrameLayout fl = (FrameLayout) findViewById(R.id.fragment_container);
        ShowFragment sf = new ShowFragment();
        sf.setMessage(p.getMessage());
        sf.setUri(Uri.parse(p.getImageUri()));
        showFragment(sf,false);

        Fragment NewPostFragment = new Fragment();
    }

    public void showFragment(android.support.v4.app.Fragment fragment, boolean addToBackStack)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        String tag = fragment.getClass().getSimpleName();
        if ( addToBackStack )
        {
            transaction.addToBackStack( tag );
        }
        transaction.replace( R.id.fragment_container, fragment, tag );
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
