package com.occs.ldsoft.occs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by yeliu on 15/8/30.
 */
public class PersonInfoFragment extends Fragment {

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int TAKE_PICTURE_WITH_CAMERA = 2;
    private static final int SELECT_SINGLE_PICTURE = 3;
    private static final int REQUEST_CROPIMAGE = 4;

    private ListView personInfoListView;
    private Toolbar toolbar;
    private String[] personInfoAry;
    private String[] personInfoTempAry;
    private ImageButton backBtn;
    private ImageButton editBtn;
    private boolean isEditing = false;
    private static final String TAG = "PersonInfoFragment";
    private PersonInfoAdapter adapter;
    private boolean isLoadingWeb;
    private String key;
    private String username;
    private String birthday;
    private static final String DIALOG_DATE = "date";
    private Bitmap photoImage;
    private TextView infoTextView;
    private ImageButton photoBtn;
    private String photoStr;
    private String[] infoAry;
    private String[] noEditAry;

    public static final String IMAGE_TYPE = "image/*";
    private Uri fileUri;

    InputStream inputStream;

    private ArrayList<InfoOnFocusListener> listenerList = new ArrayList<InfoOnFocusListener>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.person_info_layout, container, false);
        personInfoListView = (ListView) v.findViewById(R.id.person_info_listview);
        toolbar = (Toolbar) v.findViewById(R.id.person_info_bar);
        backBtn = (ImageButton) v.findViewById(R.id.person_info_bar_backbtn);
        editBtn = (ImageButton) v.findViewById(R.id.person_info_bar_editbtn);
        infoTextView = (TextView) v.findViewById(R.id.drawer_name_txt);

        Person p = Person.getPerson();
        String typeStr = p.getTypeNameFromInt(p.getTypeNumber());
        if (typeStr.contains("个人")){
            personInfoAry = getPersonStringAry();
            personInfoTempAry = getPersonStringAry();
            infoAry = Tools.PERSONINFOLIST;
            noEditAry = Tools.PERSONINFONOEDIT;
            editBtn.setVisibility(View.VISIBLE);
        } else {
            personInfoAry = getCompanyStringAry();
            personInfoTempAry = getCompanyStringAry();
            infoAry = Tools.COMPANYINFOLIST;
            noEditAry = Tools.COMPANYINFONOEDIT;
            editBtn.setVisibility(View.INVISIBLE);
        }

        int contentColor = p.getColorFromType(p.getTypeNameFromInt(p.getTypeNumber()));
        editBtn.setBackgroundResource(android.R.drawable.ic_menu_edit);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditBtnSrc(null, false);
                adapter.notifyDataSetChanged();
            }
        });
        key = p.getKey();
        username = p.getName();
        birthday = p.getBirthday();

        toolbar.setBackgroundColor(getResources().getColor(contentColor));
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditBtnSrc(getActivity(),true);
            }
        });
        // Set the adapter for the list view
        adapter = new PersonInfoAdapter(getActivity(),R.layout.person_info_photo_layout,
                R.layout.person_info_listview_cell, infoAry, personInfoAry);
        personInfoListView.setAdapter(adapter);
        personInfoListView.setBackgroundColor(getResources().getColor(R.color.white));

        return v;
    }

    private String[] getPersonStringAry(){
        Person p = Person.getPerson();
        return new String[]{
                p.getName(),p.getRealname(), p.getNickname(), p.getSex(), p.getIdno(), p.getBirthday(), p.getAddress(),
                p.getEducollege(), p.getMobile(), p.getEmail(), p.getQq(), p.getWeixin(), p.getTjid()
        };
    }

    private String[] getCompanyStringAry(){
        Person p = Person.getPerson();
        return new String[]{
                p.getName(),p.getRealname(), p.getNickname(), p.getIndustry(), p.getAddress(),
                p.getMobile(), p.getUrl(), p.getOrgcode(), p.getSummary()
        };
    }

    private void setEditBtnSrc(Activity finishActivity, boolean isDialog) {
        if (!isEditing) {
            if (isDialog){
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
                return;
            }
            isEditing = !isEditing;
            Tools.showToastMid(getActivity(), "请编辑蓝色项目");
            editBtn.setBackgroundResource(android.R.drawable.ic_menu_save);
        } else {
            for (int i=0; i<personInfoAry.length; i++){
                if (!personInfoAry[i].equals(personInfoTempAry[i])){
                    if (isDialog){
                        dialog("账户管理", "资料有修改需要保存吗？", finishActivity);
                    }else {
                        isEditing = !isEditing;
                        savePersonInfoEdit();
                        editBtn.setBackgroundResource(android.R.drawable.ic_menu_edit);
                    }
                    return;
                }
            }
            if (!isDialog){
                editBtn.setBackgroundResource(android.R.drawable.ic_menu_edit);
                isEditing = !isEditing;
            }else{
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

//////////////////////////////  dialog sets here  //////////////////////////////

    protected void dialog(String title, String msg, final Activity finishActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isEditing = !isEditing;
                savePersonInfoEdit();
                editBtn.setBackgroundResource(android.R.drawable.ic_menu_edit);
                if (finishActivity != null) {
                    finishActivity.finish();
                }
            }

        });
        builder.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isEditing = !isEditing;
                dialog.dismiss();
                cancelPersonInfoEdit();
                editBtn.setBackgroundResource(android.R.drawable.ic_menu_edit);
                if (finishActivity != null) {
                    finishActivity.finish();
                }
            }

        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    protected void sexDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("您的性别");
        final int index = Tools.getIndexInStringArray("性别", infoAry);
        builder.setPositiveButton("女", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                if (index != -1)
                    personInfoAry[index] = "女";
                dialog.dismiss();
                adapter.notifyDataSetChanged();
            }

        });
        builder.setNegativeButton("男", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (index != -1)
                    personInfoAry[index] = "男";
                dialog.dismiss();
                adapter.notifyDataSetChanged();
            }

        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                for (InfoOnFocusListener listener : listenerList){
                    if (listener.strID.equals("性别")){
                        listener.initCurView();
                    }
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    protected void photoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("请选择头像来源");
        builder.setPositiveButton("照相机", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
//                Intent i = new Intent(getActivity(), PersonCameraActivity.class);
//                startActivityForResult(i,REQUEST_PHOTO);
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = Tools.getOutputMediaFileUri(Tools.MEDIA_TYPE_IMAGE); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, TAKE_PICTURE_WITH_CAMERA);
            }

        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        builder.setNeutralButton("手机相册", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setType(IMAGE_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.select_picture)), SELECT_SINGLE_PICTURE);
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        builder.create().show();
    }



//////////////////////////////////////////     在这里存储person    ///////////////////////////////////////

    public void savePersonInfoEdit(){
        for (int i = 0; i < personInfoAry.length; i++) {
            if (!personInfoAry[i].equals(personInfoTempAry[i])) {
                personInfoTempAry[i] = personInfoAry[i];
                Person.changeProp(infoAry[i], personInfoAry[i], getActivity().getApplicationContext());
            }
        }
        if (Tools.isNetworkConnected(getActivity()) && !UploadUserInfo.isLoadingWeb){
            Log.v(TAG,Person.personToJsonString());
            new UploadUserInfo(Person.personToJsonString()).execute();
        }
        adapter.notifyDataSetChanged();
    }

    public void cancelPersonInfoEdit(){
        for (int i = 0; i < personInfoAry.length; i++) {
            personInfoAry[i] = personInfoTempAry[i];
        }
        adapter.notifyDataSetChanged();
    }

    public class PersonInfoAdapter extends ArrayAdapter<String> {
        String label[] = null;
        String info[] = null;
        Context context;
        int layoutResourceId;
        int photoLayoutResourceId;

        public PersonInfoAdapter(Context context, int photoLayoutResourceId,int layoutResourceId, String[] label, String[] info) {
            super(context, layoutResourceId, label);
            this.layoutResourceId = layoutResourceId;
            this.photoLayoutResourceId = photoLayoutResourceId;
            this.context = context;
            this.label = label;
            this.info = info;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View headRow = null;
            View row = null;
            RowHolder holder = null;
            if (position == 0){
                if (headRow == null){
                    headRow = convertView;
                    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                    headRow = inflater.inflate(photoLayoutResourceId, parent, false);
                    final ImageView photo = (ImageView) headRow.findViewById(R.id.drawer_person_photo_detail);
                    if (Person.getPerson().getAvatar() != "") {
                        fileUri = Tools.getOutputMediaFileUri(Tools.MEDIA_TYPE_IMAGE);
                        try {
                            photoImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileUri);
                            photo.setImageBitmap(photoImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        photo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d(TAG, "show big photo");
                            }
                        });
                    }
                    photoBtn = (ImageButton) headRow.findViewById(R.id.drawer_person_photo_editbtn);
                    if (photoImage != null){
                        photo.setImageBitmap(photoImage);
                    }
                    infoTextView = (TextView) headRow.findViewById(R.id.drawer_name_txt);
                    infoTextView.setText(personInfoAry[0]);
                    PackageManager pm = getActivity().getPackageManager();
                    boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                            pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                            Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD ||
                            Camera.getNumberOfCameras() > 0;
                    if (hasCamera){
                        photoBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                photoDialog();
                            }
                        });
                    }
                }
                return headRow;
            }else{
                if (row == null) {
                    row = convertView;
                    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                    row = inflater.inflate(layoutResourceId, parent, false);
                    holder = new RowHolder();
                    holder.labelTxt = (TextView) row.findViewById(R.id.person_info_label);
                    holder.infoTxt = (EditText) row.findViewById(R.id.person_info_text);

                    holder.infoTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            info[position] = editable.toString();
                        }
                    });

                    row.setTag(holder);
                } else {
                    holder = (RowHolder) row.getTag();
                }
                String ll = label[position];
                String ii = info[position];
                holder.labelTxt.setText(ll);
                holder.infoTxt.setText(ii);

                if (ll.equals("QQ")){
                    holder.infoTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

                if(ll.contains("地址") || ll.contains("简介")){
                    holder.infoTxt.setSingleLine(false);
                }

                final RowHolder finalHolder = holder;

                InfoOnFocusListener listener = new InfoOnFocusListener(finalHolder);
                listenerList.add(listener);
                holder.infoTxt.setOnFocusChangeListener(listener);

                if (isEditing) {
                    holder.infoTxt.setFocusable(true);
                    holder.infoTxt.setFocusableInTouchMode(true);
                    holder.infoTxt.setClickable(true);

                    holder.infoTxt.setBackgroundColor(getResources().getColor(R.color.lightlightblue));
                    holder.labelTxt.setBackgroundColor(getResources().getColor(R.color.lightlightblue));

                    for (String item : noEditAry) {
                        if (item.equals(ll)) {
                            holder.infoTxt.setFocusable(false);
                            holder.infoTxt.setFocusableInTouchMode(false);
                            holder.infoTxt.setClickable(false);

                            holder.infoTxt.setBackgroundColor(getResources().getColor(R.color.white));
                            holder.labelTxt.setBackgroundColor(getResources().getColor(R.color.white));
                        }
                    }
                } else {
                    holder.infoTxt.setFocusable(false);
                    holder.infoTxt.setFocusableInTouchMode(false);
                    holder.infoTxt.setClickable(false);

                    holder.infoTxt.setBackgroundColor(getResources().getColor(R.color.white));
                    holder.labelTxt.setBackgroundColor(getResources().getColor(R.color.white));
                }

                return row;
            }
        }
    }
    ///////////////////////////////////////  Activity Result  /////////////////////////////////////////////

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DATE){
            if(resultCode == Activity.RESULT_OK){
                Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
                int index = Tools.getIndexInStringArray("生日",infoAry);
                if (index != -1)
                    personInfoAry[index] = df.format(date);
                adapter.notifyDataSetChanged();
            }
            for (InfoOnFocusListener listener : listenerList){
                if (listener.strID.equals("生日") || listener.strID.equals("性别")){
                    listener.initCurView();
                }
            }
        }
        if (requestCode == SELECT_SINGLE_PICTURE) {
            if (resultCode == Activity.RESULT_OK){
                Uri selectedImageUri = data.getData();
                Intent i = new Intent(getActivity(),PhotoCroperActivity.class);
                i.putExtra("imageUri",selectedImageUri.toString());
                startActivityForResult(i, REQUEST_CROPIMAGE);
            }else {
                Log.d(PersonInfoFragment.class.getSimpleName(), "Failed to get intent data, result code is " + resultCode);
            }
            // original code
//                String selectedImagePath = getPath(selectedImageUri);
//                selectedImagePreview.setImageURI(selectedImageUri);
        }

        if (requestCode == REQUEST_PHOTO) {

        }

        if (requestCode == TAKE_PICTURE_WITH_CAMERA){
            if (resultCode == Activity.RESULT_OK) {
                Intent i = new Intent(getActivity(),PhotoCroperActivity.class);
                i.putExtra("imageUri",fileUri.toString());
                startActivityForResult(i, REQUEST_CROPIMAGE);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

        if (requestCode == REQUEST_CROPIMAGE){
            if (resultCode == Activity.RESULT_OK){
                byte[] byteArray = data.getByteArrayExtra("image");

                File imgFile = Tools.getOutputMediaFile(Tools.MEDIA_TYPE_IMAGE);
                photoImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                adapter.notifyDataSetChanged();
                photoStr = Tools.encodeTobase64(byteArray);
                final ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("keyword",WebLinkStatic.KEYWORD));
                nameValuePairs.add(new BasicNameValuePair("username",Person.getPerson().getName()));
                nameValuePairs.add(new BasicNameValuePair("imgInfo",photoStr));
                nameValuePairs.add(new BasicNameValuePair("imgName",Person.getPerson().getName()
                        + Person.getPerson().getMobile() + ".jpg"));

                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try{
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost httppost = new HttpPost(WebLinkStatic.UPLOADAVATAR);
                            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                            HttpResponse response = httpclient.execute(httppost);
                            String the_string_response = convertResponseToString(response);
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                }
                            });

                        }catch(Exception e){
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                }
                            });
                            System.out.println("Error in http connection "+e.toString());
                        }
                    }
                });
                t.start();


                Tools.saveAvatarIcon(imgFile, photoImage);
            }
        }
    }

    public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException{

        String res = "";
        StringBuffer buffer = new StringBuffer();
        inputStream = response.getEntity().getContent();
        final int contentLength = (int) response.getEntity().getContentLength(); //getting content length…..
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
            }
        });

        if (contentLength < 0){
        }
        else{
            byte[] data = new byte[512];
            int len = 0;
            try
            {
                while (-1 != (len = inputStream.read(data)) )
                {
                    buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                inputStream.close(); // closing the stream…..
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            res = buffer.toString();     // converting stringbuffer to string…..

            final String finalRes = res;
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                }
            });
            //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
        }
        return res;
    }

    public String  performPostCall(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private class UploadUserInfo extends WebFunctionHelper.FetchUploadUserInfo {

        public UploadUserInfo(String str) {
            super(str);
        }
    }

    static class RowHolder {
        TextView labelTxt;
        EditText infoTxt;
    }

    private class InfoOnFocusListener implements View.OnFocusChangeListener {

        private String strID;
        private View curView;
        private RowHolder holder;
        private Date date;

        public InfoOnFocusListener(RowHolder holder) {
            this.strID = holder.labelTxt.getText().toString();
            this.holder = holder;
        }

        public void initCurView(){
            this.curView = null;
        }

        public void setCellhighlighted(){
            for(InfoOnFocusListener listener : listenerList) {
                String lableStr = listener.holder.labelTxt.getText().toString();
                if (!Arrays.asList(noEditAry).contains(lableStr)) {
                    listener.holder.labelTxt.setBackgroundColor(getResources().getColor(R.color.lightlightblue));
                    listener.holder.infoTxt.setBackgroundColor(getResources().getColor(R.color.lightlightblue));
                }
            }
            holder.labelTxt.setBackgroundColor(getResources().getColor(R.color.personinfo_cell_hightlight));
            holder.infoTxt.setBackgroundColor(getResources().getColor(R.color.personinfo_cell_hightlight));
        }

        @Override
        public void onFocusChange(View view, boolean isFocus) {
            if (isFocus){
                setCellhighlighted();
            }
            if (isFocus && curView == null) {
                curView = view;

                String titleStr = holder.labelTxt.getText().toString();
                switch (titleStr) {
                    case "生日":
                        view.clearFocus();
                        if (holder.infoTxt.getText().toString().equals("")){
                            date = new Date(0);
                        } else {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
                            try {
                                date = format.parse(holder.infoTxt.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        DatePickerFragment dialog = DatePickerFragment.newInstance(date);
                        dialog.setTargetFragment(PersonInfoFragment.this, REQUEST_DATE);
                        dialog.show(fm, DIALOG_DATE);
                        break;
                    case "性别":
                        view.clearFocus();
                        sexDialog();
                        break;
                }
            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */

    public String getPath(Uri uri) {

        // just some safety built in
        if( uri == null ) {
            // perform some logging or show user feedback
            Toast.makeText(getActivity().getApplicationContext(), R.string.msg_failed_to_get_picture, Toast.LENGTH_LONG).show();
            Log.d(PersonInfoFragment.class.getSimpleName(), "Failed to parse image path from image URI " + uri);
            return null;
        }

        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here, thanks to the answer from @mad indicating this is needed for
        // working code based on images selected using other file managers
        return uri.getPath();
    }


//    /**
//     * helper to scale down image before display to prevent render errors:
//     * "Bitmap too large to be uploaded into a texture"
//     */
//    private void displayPicture(String imagePath, ImageView imageView) {
//
//        // from http://stackoverflow.com/questions/22633638/prevent-bitmap-too-large-to-be-uploaded-into-a-texture-android
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 4;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//        int height = bitmap.getHeight(), width = bitmap.getWidth();
//
//        if (height > 1280 && width > 960){
//            Bitmap imgbitmap = BitmapFactory.decodeFile(imagePath, options);
//            imageView.setImageBitmap(imgbitmap);
//        } else {
//            imageView.setImageBitmap(bitmap);
//        }
//    }
}
