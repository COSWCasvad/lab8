package cosw.eci.edu.lab8;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String POST_MESSAGE= "bundlePost";
    public static final String POST_MESSAGE_OBJECT= "value";
    public static final int SELECT_IMAGE= 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    private final CharSequence[] dialogItems = {"Take picture", "Select picture"};
    private ImageView image;
    private EditText text;
    private Uri imageUri;
    private CheckedTextView imageCheck;
    private CheckedTextView messageCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.imageView);
        text = (EditText) findViewById(R.id.message);
        imageCheck = (CheckedTextView) findViewById(R.id.checkImage);
        messageCheck = (CheckedTextView) findViewById(R.id.checkMessage);
        image.setVisibility(View.INVISIBLE);

        messageCheck.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
        imageCheck.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);



        //listener on any change
        text.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.toString().length()>=20) {messageCheck.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);}
                else{messageCheck.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);}
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }


    public void onClickAddPhoto(View v){

        final DialogInterface.OnClickListener selectedListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                image.setVisibility(View.VISIBLE);
                imageCheck.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
                switch (which) {
                    case 0:
                        //take picture
                        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                            dispatchTakePictureIntent();
                        }
                        break;

                    case 1:
                        //select a picture
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
                        break;
                }
                dialog.dismiss();
            }
        };

        createSingleChoiceAlertDialog(MainActivity.this,"Select option", dialogItems, selectedListener,null).show();
    }

    public void onClickSave(View v){
        if(text.getText().toString().length()>=20 && image.getVisibility()==View.VISIBLE){
            //Create a new intent for the PostActivity created before.
            Intent intent = new Intent(this, PostActivity.class);
            //Create a Bundle object and add the Post object created to this bundle, then add the bundle to the intent as extras.
            Bundle bundle = new Bundle();
            bundle.putSerializable(POST_MESSAGE_OBJECT,new Post(text.getText().toString(),imageUri.toString()));
            intent.putExtra(POST_MESSAGE,bundle);
            //Start the new activity using the intent.
            startActivity(intent);
        }
        else{
            text.setError(getResources().getString(R.string.saveError));
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case SELECT_IMAGE:
                if(resultCode==RESULT_OK){
                    imageUri = data.getData();
                    image.setImageURI(null);
                    image.setImageURI(imageUri);
                    /*String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(imageUri,filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Drawable d= new BitmapDrawable(BitmapFactory.decodeFile(picturePath));*/

                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode==RESULT_OK){
                    /*Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    image.setImageBitmap(imageBitmap);*/
                    image.setImageURI(null);
                    image.setImageURI(imageUri);

                }
                break;
        }
    }

    @NonNull
    public static Dialog createSingleChoiceAlertDialog(@NonNull Context context, @Nullable String title,
                                                       @NonNull CharSequence[] items,
                                                       @NonNull DialogInterface.OnClickListener optionSelectedListener,
                                                       @Nullable DialogInterface.OnClickListener cancelListener )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( context, R.style.My_Dialog );
        builder.setItems( items, optionSelectedListener );
        if ( cancelListener != null )
        {
            builder.setNegativeButton( R.string.Cancel, cancelListener );
        }
        builder.setTitle( title );
        return builder.create();
    }

}
