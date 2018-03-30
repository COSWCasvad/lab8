package cosw.eci.edu.lab8;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewPostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPostFragment extends Fragment{

    public static final int SELECT_IMAGE= 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    private final CharSequence[] dialogItems = {"Take picture", "Select picture"};
    private ImageView image;
    private EditText text;
    private Uri imageUri;
    private CheckedTextView imageCheck;
    private CheckedTextView messageCheck;
    private Button addPhoto;
    private Button save;



    private OnFragmentInteractionListener mListener;

    public NewPostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_post, container,    false);
        // Inflate the layout for this fragment
        image = (ImageView) rootView.findViewById(R.id.imageView);
        text = (EditText) rootView.findViewById(R.id.message);
        imageCheck = (CheckedTextView) rootView.findViewById(R.id.checkImage);
        messageCheck = (CheckedTextView) rootView.findViewById(R.id.checkMessage);
        addPhoto = (Button) rootView.findViewById(R.id.addPhoto);
        save = (Button) rootView.findViewById(R.id.save);
        image.setVisibility(View.INVISIBLE);

        messageCheck.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
        imageCheck.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);


        //button listeners
        save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                onClickSave(view);
            }
        });
        addPhoto.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                onClickAddPhoto(view);
            }
        });
        //listener on any change
        text.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.toString().length()>=20) {messageCheck.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);}
                else{messageCheck.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);}
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


        return rootView;
    }


    public void onClickAddPhoto(View v){

        final DialogInterface.OnClickListener selectedListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                image.setVisibility(View.VISIBLE);
                imageCheck.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
                switch (which) {
                    case 0:
                        //take picture
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
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

        createSingleChoiceAlertDialog(getActivity() ,"Select option", dialogItems, selectedListener,null).show();
    }

    public void onClickSave(View v){
        if(text.getText().toString().length()>=20 && image.getVisibility()==View.VISIBLE){
            //Create a new intent for the PostActivity created before.
            Intent intent = new Intent(getActivity(), PostActivity.class);
            //Create a Bundle object and add the Post object created to this bundle, then add the bundle to the intent as extras.
            Bundle bundle = new Bundle();
            bundle.putSerializable(MainActivity.POST_MESSAGE_OBJECT,new Post(text.getText().toString(),imageUri.toString()));
            intent.putExtra(MainActivity.POST_MESSAGE,bundle);
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
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case SELECT_IMAGE:
                if(resultCode==getActivity().RESULT_OK){
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
                if(resultCode==getActivity().RESULT_OK){
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


    //AUTOGENERATED CODE


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewPostFragment newInstance(String param1, String param2) {
        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


}
