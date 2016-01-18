package com.occs.ldsoft.occs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by yeliu on 15/9/8.
 */
public class PhotoCroperFragment extends Fragment {
    private static final String TAG = "PhotoCroperFragment";
    private TouchImageView imageView;
    private Button yesBtn;
    private Button noBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.photo_croper_layout, container, false);
        imageView = (TouchImageView) v.findViewById(R.id.photo_croper_imageview);
        imageView.setDrawingCacheEnabled(true);
        imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        imageView.layout(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());

        yesBtn = (Button) v.findViewById(R.id.photo_croper_yesbtn);
        noBtn = (Button) v.findViewById(R.id.photo_croper_nobtn);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.buildDrawingCache(true);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bmp = Bitmap.createBitmap(imageView.getDrawingCache(),
                        0,(imageView.getHeight()-imageView.getWidth())/2,imageView.getWidth(),imageView.getWidth());

                bmp.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] byteArray = stream.toByteArray();
                imageView.setDrawingCacheEnabled(false);

                Intent in1 = new Intent();
                in1.putExtra("image", byteArray);
                getActivity().setResult(Activity.RESULT_OK, in1);
                getActivity().finish();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        Uri imageUri = Uri.parse(getActivity().getIntent().getExtras().getString("imageUri"));
        try {
            Bitmap bitmap = new UserPicture(imageUri, getActivity().getContentResolver(), 1080, 1920).getBitmap();
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        PhotoSquare square = new PhotoSquare(getActivity());
//        View layout = v.findViewById(R.id.photo_croper_relative);
//        ((RelativeLayout)layout).addView(square);

        return v;
    }
}
