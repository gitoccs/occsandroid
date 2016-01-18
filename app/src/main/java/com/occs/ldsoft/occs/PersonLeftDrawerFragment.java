package com.occs.ldsoft.occs;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.occs.ldsoft.occs.WebFunctionHelper.FetchLogoutTask;
import static java.security.AccessController.getContext;

/**
 * Created by yeliu on 15/7/30.
 */
public class PersonLeftDrawerFragment extends Fragment {

    private static final int PERSONINFO = 1;
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private LinearLayout mDrawerLinear;
    private View drawerPersonInfo;
    private ActionBarDrawerToggle mDrawerToggle;
    private int contentColor;
    private ImageView photoMain;
    private ImageView photoMask;
    public TextView gongdanAll;
    public TextView gongdanBidding;
    public TextView gongdanOnGoing;
    public TextView gongdanFinish;
    public TextView oCoinTextView;
    public ToggleButton toggleButton;

    private static final String TAG = "PersonLeftDrawerFragment";
    private boolean isLoadingWeb = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentColor = getArguments().getInt("color");
    }

    public static PersonLeftDrawerFragment newInstance(int contentColor) {
        Bundle args = new Bundle();
        args.putInt("color", contentColor);

        PersonLeftDrawerFragment fragment = new PersonLeftDrawerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERSONINFO){
            if (resultCode == Activity.RESULT_OK){
                Log.i(TAG, "**************  update avatar  **************");
                try {
                    updatePersonIcon();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.person_left_drawer, container, false);

        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLinear = (LinearLayout) v.findViewById(R.id.person_linear);
        mDrawerLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        mDrawerLinear.setBackgroundColor(getResources().getColor(R.color.white));
        mDrawerList = (ListView) v.findViewById(R.id.left_drawer_list);
        drawerPersonInfo = (View) v.findViewById(R.id.drawer_person_info_view);
        drawerPersonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PersonInfoActivity.class);
                startActivityForResult(i, PERSONINFO);
            }
        });
        LinearLayout linear = (LinearLayout) v.findViewById(R.id.left_drawer_top);
        linear.setBackgroundColor(getResources().getColor(contentColor));
        TextView nameTxt = (TextView) v.findViewById(R.id.person_drawer_name);
        nameTxt.setText(Person.getPerson().getName());
        Button logoutBtn = (Button) v.findViewById(R.id.drawer_cell_logout);
        photoMain = (ImageView) v.findViewById(R.id.drawer_person_photo_main);
        photoMask = (ImageView) v.findViewById(R.id.drawer_person_photo_main_mask);

//        toggleButton = (ToggleButton) v.findViewById(R.id.slinece_mode_togglebutton);
//        toggleButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                toggleChecked(toggleButton.isChecked());
//            }
//        });
//        checkToggleBtn();

        switch (contentColor){
            case R.color.btn_orange_normal:
                photoMask.setImageResource(R.drawable.personphotomask_orange);
                break;
            case R.color.btn_cyan_normal:
                photoMask.setImageResource(R.drawable.personphotomask_cyan);
                break;
            case R.color.btn_blue_normal:
                photoMask.setImageResource(R.drawable.personphotomask_blue);
                break;
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FetchLogoutGo.isLoadingWeb && Tools.isNetworkConnected(getActivity())) {
                    new FetchLogoutGo(PersonLeftDrawerFragment.this).execute();
                }
            }
        });

        // Set the adapter for the list view
        mDrawerList.setAdapter(new DrawerAdapter(getActivity(), R.layout.drawer_list_item, mPlanetTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                TabViewContainerActivity activity = (TabViewContainerActivity) getActivity();
                activity.selectItem(position);
            }
        });

        gongdanAll = (TextView) v.findViewById(R.id.drawer_person_gongdan_all);
        gongdanBidding = (TextView) v.findViewById(R.id.drawer_person_gongdan_bidding);
        gongdanOnGoing = (TextView) v.findViewById(R.id.drawer_person_gongdan_ongoing);
        gongdanFinish = (TextView) v.findViewById(R.id.drawer_person_gongdan_finished);
        oCoinTextView = (TextView) v.findViewById(R.id.drawer_person_ocoin_txt);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Person.getPerson().getAvatar().equals("")){
            try {
                updatePersonIcon();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void toggleChecked(Boolean isChecked){
        if (isChecked){
            Tools.setRingerMode(getActivity().getApplicationContext(), AudioManager.RINGER_MODE_SILENT);
            Log.d(TAG,"AudioManager.RINGER_MODE_SILENT");
        }else{
            Tools.setRingerMode(getActivity().getApplicationContext(),AudioManager.RINGER_MODE_NORMAL);
            Log.d(TAG, "AudioManager.MODE_NORMAL");
        }
    }

    public void checkToggleBtn(){
        toggleButton.setChecked(Tools.isPhoneSilent(getActivity().getApplicationContext()));
    }

    public void updatePersonIcon() throws IOException {
        String avatarPath = Person.getPerson().getAvatar();
        if(avatarPath.equals(""))
            return;
        if (avatarPath.split(File.separator)[0].equals("http:")){
            new FetchAvatarTask().execute();
        } else {
            Uri imageUri = Uri.parse(avatarPath);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            photoMain.setImageBitmap(bitmap);
        }
    }

    public class FetchAvatarTask extends WebFunctionHelper.FetchImageTask {

        @Override
        public void successFunction(Bitmap bitmap) {
            File file = Tools.getOutputMediaFile(Tools.MEDIA_TYPE_IMAGE);
            Tools.saveAvatarIcon(file, bitmap);
            Person.getPerson().setAvatar(Tools.getOutputMediaFileUri(Tools.MEDIA_TYPE_IMAGE).toString());
            photoMain.setImageBitmap(bitmap);
        }

        @Override
        public String getBitmap() {
            return Person.getPerson().getAvatar();
        }
    }

    public class DrawerAdapter extends ArrayAdapter<String> {
        String data[] = null;
        Context context;
        int layoutResourceId;

        public DrawerAdapter(Context context, int layoutResourceId, String[] data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            RowHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new RowHolder();
                holder.imgIcon = (ImageView) row.findViewById(R.id.drawer_cell_image);
                holder.txtTitle = (TextView) row.findViewById(R.id.drawer_cell_txt);

                row.setTag(holder);
            } else {
                holder = (RowHolder) row.getTag();
            }

            String str = data[position];
            holder.txtTitle.setText(str);
            holder.imgIcon.setImageResource(R.drawable.back_02);

            return row;
        }
    }

    static class RowHolder {
        ImageView imgIcon;
        TextView txtTitle;
    }

    private class FetchLogoutGo extends FetchLogoutTask {

        public FetchLogoutGo(Fragment f) {
            super(f);
        }
    }
}
