package com.occs.ldsoft.occs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.hardware.Camera.Size;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by Yale on 2015/9/5.
 */
public class PersonCameraFragment extends Fragment {

    private static final String TAG = "PersonCameraFragment";
    public static final String EXTRA_PHOTO_FILENAME = "com.test.ldsoft.criminallntent.photo_filename";
    private View mProgressContainer;
    private SurfaceView mSurfaceView;
    private Camera mCamera;

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            String filename = UUID.randomUUID().toString() + ".jpg";
            FileOutputStream os = null;
            boolean success = true;

            try {
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(bytes);
            } catch (FileNotFoundException e) {
                success = false;
                e.printStackTrace();
            } catch (IOException e) {
                success = false;
                e.printStackTrace();
            } finally {
                try {
                    if (os != null){
                        os.close();
                    }
                } catch (IOException e) {
                    success = false;
                    e.printStackTrace();
                }
            }

            if (success) {
                Intent i = new Intent();
                i.putExtra(EXTRA_PHOTO_FILENAME,filename);
                getActivity().setResult(Activity.RESULT_OK, i);
            }else{
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.person_camera_fragment, container, false);

        Button takePicBtn = (Button) v.findViewById(R.id.person_camera_btn);
        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCamera != null) {
                    mCamera.takePicture(mShutterCallback, null, mJpegCallback);
                }
            }
        });

        mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback(){

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if (mCamera != null){
                        mCamera.setPreviewDisplay(surfaceHolder);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int w, int h) {
                if (mCamera != null){
                    Camera.Parameters parameters = mCamera.getParameters();
                    Size s = getBestSupportSize(parameters.getSupportedPreviewSizes(), w, h);
                    parameters.setPreviewSize(s.width, s.height);
                    s = getBestSupportSize(parameters.getSupportedPictureSizes(),w,h);
                    parameters.setPictureSize(s.width, s.height);
                    mCamera.setParameters(parameters);
                    try {
                        mCamera.setDisplayOrientation(90);
                        mCamera.startPreview();
                    }catch (Exception e){
                        Log.e(TAG, "Could not start preview", e);
                        mCamera.release();
                        mCamera = null;
                    }
                }else{
                    return;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (mCamera != null){
                    mCamera.stopPreview();
                }
            }
        });
        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        return v;
    }

    @TargetApi(9)
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(0);
        }else{
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    private Size getBestSupportSize(List<Size> sizes, int width, int height) {
        Size bestSize = sizes.get(0);
        int lagestArea = bestSize.width * bestSize.height;
        for (Size s : sizes){
            int area = s.width * s.height;
            if (area > lagestArea){
                bestSize = s;
                lagestArea = area;
            }
        }

        return bestSize;
    }
}
