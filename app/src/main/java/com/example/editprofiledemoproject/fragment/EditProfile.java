package com.example.editprofiledemoproject.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.editprofiledemoproject.BuildConfig;
import com.example.editprofiledemoproject.R;
import com.example.editprofiledemoproject.listener.LocationListener;
import com.example.editprofiledemoproject.listener.ResponseListener;
import com.example.editprofiledemoproject.permission.CheckNetwork;
import com.example.editprofiledemoproject.permission.FetchLocation;
import com.example.editprofiledemoproject.request.UpdateData;
import com.example.editprofiledemoproject.utility.AppConstants;
import com.hbb20.CountryCodePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.example.editprofiledemoproject.utility.AppConstants.EMAIL_PATTERN;
import static com.example.editprofiledemoproject.utility.AppConstants.NAME_PATTERN;
import static com.example.editprofiledemoproject.utility.AppConstants.REQUEST_OPEN_CAMERA;
import static com.example.editprofiledemoproject.utility.AppConstants.REQUEST_OPEN_GALLERY;
import static com.example.editprofiledemoproject.utility.AppConstants.user_id;

/**
 * Created by sarabjjeet on 9/6/17.
 */

public class EditProfile extends Fragment implements View.OnClickListener, TextWatcher, LocationListener, ResponseListener {
    private EditText et_first_name, et_last_name, et_email, et_contact_number, et_address, et_company_name,
            et_zipcode, et_street_address;
    private CircleImageView circleImageView;
    private ImageView camera, calendar;
    private TextView update_button, txt_dob;
    private Spinner equipments_spinner;
    private CountryCodePicker ccp;
    private String firstname, lastname, email, location, profileimg, lat, lng, phone, pincode, address, dob, Equipment;
    private boolean validate = false;
    private Pattern patternemail, pattername;
    private Matcher matcher;
    String mCurrentPhotoPath;
    String mediaPath;
    private Calendar myCalendar = Calendar.getInstance();
    private ProgressDialog progressDialog;
   // Map<String, String> body;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_profile_fragment, container, false);
        patternemail = Pattern.compile(EMAIL_PATTERN);
        pattername = Pattern.compile(NAME_PATTERN);
        initView(view);
        fetchLocation();
        return view;
    }


    //INITIALISATION OF VIEW
    private void initView(View view) {
        et_first_name = (EditText) view.findViewById(R.id.et_first_name);
        et_last_name = (EditText) view.findViewById(R.id.et_last_name);
        et_email = (EditText) view.findViewById(R.id.et_email);
        et_contact_number = (EditText) view.findViewById(R.id.et_contact_number);
        et_address = (EditText) view.findViewById(R.id.et_address);
        et_company_name = (EditText) view.findViewById(R.id.et_company_name);
        et_zipcode = (EditText) view.findViewById(R.id.et_zipcode);
        et_street_address = (EditText) view.findViewById(R.id.et_street_address);
        circleImageView = (CircleImageView) view.findViewById(R.id.circleImageView);
        camera = (ImageView) view.findViewById(R.id.camera);
        calendar = (ImageView) view.findViewById(R.id.calendar);
        update_button = (TextView) view.findViewById(R.id.update_button);
        txt_dob = (TextView) view.findViewById(R.id.txt_dob);
        ccp = (CountryCodePicker) view.findViewById(R.id.ccp);
        equipments_spinner = (Spinner) view.findViewById(R.id.equipments_spinner);

        //implement click listener
        update_button.setOnClickListener(this);
        calendar.setOnClickListener(this);
        circleImageView.setOnClickListener(this);
        camera.setOnClickListener(this);

        //implement TextWatcherListener
        et_first_name.addTextChangedListener(this);
        et_last_name.addTextChangedListener(this);
        et_email.addTextChangedListener(this);
        et_address.addTextChangedListener(this);
        et_street_address.addTextChangedListener(this);
        et_company_name.addTextChangedListener(this);
        et_contact_number.addTextChangedListener(this);
        txt_dob.addTextChangedListener(this);
        et_zipcode.addTextChangedListener(this);
        txt_dob.addTextChangedListener(this);

        //body = new HashMap<>();

        //spinner adapter
        ArrayAdapter languageAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.equipment, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        equipments_spinner.setAdapter(languageAdapter);


        //To fetch COuntry Code
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                location=ccp.getDefaultCountryName();
                location = ccp.getSelectedCountryName();
                Log.e("location", location);
            }
        });


        //fetch selected
        equipments_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Equipment = equipments_spinner.getItemAtPosition(position).toString();
                System.out.println("Equipment ***********" + Equipment);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Wait...");

    }


    //VALIDATE DATA FILLED BY USER
    public void validateData() {
        firstname = et_first_name.getText().toString();
        lastname = et_last_name.getText().toString();
        email = et_email.getText().toString();
        phone = et_contact_number.getText().toString();
        address = et_address.getText().toString() + " " + et_street_address.getText().toString();
        pincode = et_zipcode.getText().toString();
        dob = txt_dob.getText().toString();


        if (TextUtils.isEmpty(et_first_name.getText().toString())) {
            et_first_name.setError(getString(R.string.fill_field));
            et_first_name.requestFocus();
        } else if (!validateName(firstname)) {
            et_first_name.setError("Characters Only");
            et_first_name.requestFocus();
        } else if (TextUtils.isEmpty(et_last_name.getText().toString())) {
            et_last_name.setError(getString(R.string.fill_field));
            et_last_name.requestFocus();
        } else if (!validateName(lastname)) {
            et_last_name.setError("Characters Only");
            et_last_name.requestFocus();
        } else if (TextUtils.isEmpty(et_email.getText().toString())) {
            et_email.setError(getString(R.string.fill_field));
            et_email.requestFocus();
        } else if (!validateEmail(email)) {
            et_email.setError(getString(R.string.invalid_email));
            et_email.requestFocus();
        } else if (TextUtils.isEmpty(et_contact_number.getText().toString())) {
            et_contact_number.setError(getString(R.string.fill_field));
            et_contact_number.requestFocus();
        } else if (et_contact_number.length() < 10) {
            et_contact_number.setError(getString(R.string.invalid_mobile));
            et_contact_number.requestFocus();
        } else if (TextUtils.isEmpty(et_address.getText().toString())) {
            et_address.setError(getString(R.string.fill_field));
            et_address.requestFocus();
        } else if (TextUtils.isEmpty(et_company_name.getText().toString())) {
            et_company_name.setError(getString(R.string.fill_field));
            et_company_name.requestFocus();
        } else if (TextUtils.isEmpty(et_zipcode.getText().toString())) {
            et_zipcode.setError(getString(R.string.fill_field));
            et_zipcode.requestFocus();
        } else if (et_zipcode.length() < 6) {
            et_zipcode.setError(getString(R.string.invalid_pincode));
            et_zipcode.requestFocus();
        } else if (TextUtils.isEmpty(et_street_address.getText().toString())) {
            et_street_address.setError(getString(R.string.fill_field));
            et_street_address.requestFocus();
        } else if (TextUtils.isEmpty(txt_dob.getText().toString())) {
            txt_dob.setError(getString(R.string.fill_field));
            txt_dob.requestFocus();
        } else if (TextUtils.isEmpty(profileimg)) {
            Toast.makeText(getActivity(), R.string.select_image, Toast.LENGTH_SHORT).show();
        } else {
            validate = true;
            if (validate) {
                if (CheckNetwork.isNetworkAvailable(getActivity())) {
                    progressDialog.show();
                    updateData();
                    progressDialog.dismiss();
                } else
                    CheckNetwork.showAlert(getString(R.string.connectivity_failure), getActivity());
            } else {
                validateData();
            }

        }
    }


    //Method to update data
    private void updateData() {
        buildParams(user_id,firstname, lastname, email, lat,lng,phone,location);
    }

    private void buildParams(String user_id,String firstname, String lastname, String email, String lat,String lng,
                             String phone,String location) {
        MultipartBody.Part imageFileBody = null;
        if (!profileimg.equals("")) {
            File imageFile = new File(profileimg);
            RequestBody requestBody = RequestBody.create(MediaType.parse(AppConstants.CONTENT_IMAGE), imageFile);
            imageFileBody = MultipartBody.Part.createFormData(AppConstants.KEY_PIC, imageFile.getName(), requestBody);
            Log.e("imageFileBody",imageFileBody+"");
        }
        Map<String,RequestBody> requestBodyMap = new HashMap<>();
        requestBodyMap.put("user_id", RequestBody.create(MediaType.parse(AppConstants.TEXT_PLAIN_TYPE), user_id));
        requestBodyMap.put("firstname", RequestBody.create(MediaType.parse(AppConstants.TEXT_PLAIN_TYPE), firstname));
        requestBodyMap.put("lastname", RequestBody.create(MediaType.parse(AppConstants.TEXT_PLAIN_TYPE), lastname));
        requestBodyMap.put("email", RequestBody.create(MediaType.parse(AppConstants.TEXT_PLAIN_TYPE), email));
        requestBodyMap.put("lat", RequestBody.create(MediaType.parse(AppConstants.TEXT_PLAIN_TYPE), ""+lat));
        requestBodyMap.put("lng", RequestBody.create(MediaType.parse(AppConstants.TEXT_PLAIN_TYPE), ""+lng));
        requestBodyMap.put("location", RequestBody.create(MediaType.parse(AppConstants.TEXT_PLAIN_TYPE), "It Park"));
        requestBodyMap.put("phone", RequestBody.create(MediaType.parse(AppConstants.TEXT_PLAIN_TYPE), phone));

        UpdateData updateData =  new UpdateData();
        updateData.updateData(requestBodyMap, imageFileBody,this,getActivity());
    }




        @Override
        public void onClick (View v){
            switch (v.getId()) {
                case R.id.calendar:
                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            // TODO Auto-generated method stub
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, monthOfYear);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            String myDateFormat = "yyyy-dd-MM";
                            SimpleDateFormat sdf = new SimpleDateFormat(myDateFormat);
                            txt_dob.setText(sdf.format(myCalendar.getTime()));
                        }

                    };

                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                    datePickerDialog.show();
                    break;
                case R.id.update_button:
                    validateData();
                    break;
                case R.id.circleImageView:
                    selectImage();
                    break;
                case R.id.camera:
                    activeCamera();
                    break;
            }
        }

        @Override
        public void beforeTextChanged (CharSequence s,int start, int count, int after){

        }

        @Override
        public void onTextChanged (CharSequence s,int start, int before, int count){
            et_first_name.setError(null);
            et_last_name.setError(null);
            et_email.setError(null);
            et_address.setError(null);
            et_contact_number.setError(null);
            et_street_address.setError(null);
            et_zipcode.setError(null);
            txt_dob.setError(null);
            et_company_name.setError(null);
        }

        @Override
        public void afterTextChanged (Editable s){

        }

    private boolean validateEmail(String email) {
        matcher = patternemail.matcher(email);
        return matcher.matches();

    }

    private boolean validateName(String name) {
        matcher = pattername.matcher(name);
        return matcher.matches();

    }

    public void fetchLocation() {
        FetchLocation fetchLocation = new FetchLocation(getActivity(), this);
        fetchLocation.currentLocation();
    }

    @Override
    public void currentLocation(String latitude, String langitude) {
        lat = latitude;
        lng = langitude;

    }


    public void selectImage() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_box);
        ImageView select_camera = (ImageView) dialog.findViewById(R.id.select_camera);
        ImageView select_gallery = (ImageView) dialog.findViewById(R.id.select_gallery);
        select_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeCamera();
                dialog.dismiss();

            }
        });
        select_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeGallery();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void activeCamera() {
        try {
            if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED)) {
                dispatchTakePictureIntent();

            } else {
                ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_OPEN_CAMERA);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "EditProfile");
        if (!storageDir.exists())
            storageDir.mkdirs();
        File image = File.createTempFile(
                imageFileName,     //prefix
                ".jpg",           //suffix
                storageDir       //directory
        );
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() throws IOException {
        Log.e("in", "dispatchTakePictureIntent");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                System.out.println("dispatchTakePictureIntent ==" + ex.toString());
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this.getActivity(), BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_OPEN_CAMERA);
            }
        }
    }

    public void activeGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_OPEN_GALLERY);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_OPEN_GALLERY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_OPEN_CAMERA:
                if (mCurrentPhotoPath == null) {
                    Log.e("MCurrentPath", "is null");
                } else {
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);

                    File file = new File(imageUri.getPath());
                    profileimg = String.valueOf(file);
                    Log.e("profileimg in camera", profileimg);
                    try {
                        InputStream ims = new FileInputStream(file);
                        circleImageView.setImageBitmap(BitmapFactory.decodeStream(ims));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    MediaScannerConnection.scanFile(getActivity(),
                            new String[]{imageUri.getPath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                }
                            });
                }
                break;
            case REQUEST_OPEN_GALLERY:
                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mediaPath = cursor.getString(columnIndex);
                    profileimg = mediaPath;
                    Log.e("profileimg from gallery", profileimg);
                    circleImageView.setImageBitmap(BitmapFactory.decodeFile(mediaPath));

                } catch (Exception e) {
                    Toast.makeText(this.getActivity(), R.string.select_photo, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_OPEN_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        dispatchTakePictureIntent();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_OPEN_CAMERA);
                }

                break;
            case REQUEST_OPEN_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                    openGalleryIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGalleryIntent, REQUEST_OPEN_GALLERY);

                } else {
                    ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_OPEN_GALLERY);
                }
                break;
        }
    }

    @Override
    public void onSuccess(String response, String message) {
        Toast.makeText(this.getActivity(), response + "\n" + message, Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();

    }

    @Override
    public void onError(String error, String message) {
        Toast.makeText(this.getActivity(), error + "\n" + message, Toast.LENGTH_SHORT).show();

    }
}
