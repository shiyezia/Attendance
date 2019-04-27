package com.example.attendance;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.attendance.cameras.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

public class camera extends AppCompatActivity {

    private static final String TAG = "camera";

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    public MTCNN mtcnn;
    public Facenet facenet;
    private CameraView mCameraView;
    private Handler mBackgroundHandler;
    long lastModirTime;
    private CameraView.Callback mCallback = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
        }

        @Override
        public void onPreviewFrame(final byte[] data, final Camera camera) {
            if (System.currentTimeMillis() - lastModirTime <= 200 || data == null || data.length == 0 ) {
                return;
            }
            Log.i(TAG, "onPreviewFrame " + (data == null ? null : data.length));
            getBackgroundHandler().post(new FaceThread(data, camera));
            lastModirTime = System.currentTimeMillis();
        }
    };


    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mtcnn=new MTCNN(getAssets());
        mCameraView = findViewById(R.id.camera);
        facenet=new Facenet(getAssets());
        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ConfirmationDialogFragment
                    .newInstance("获取相机权限失败",
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION,
                            "没有相机权限，app不能为您进行脸部检测")
                    .show(getSupportFragmentManager(), "");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "获取到拍照权限",
                            Toast.LENGTH_SHORT).show();
                }
                // No need to start camera here; it is handled by onResume
                break;
        }
    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    public static class ConfirmationDialogFragment extends DialogFragment {

        private static final String ARG_MESSAGE = "message";
        private static final String ARG_PERMISSIONS = "permissions";
        private static final String ARG_REQUEST_CODE = "request_code";
        private static final String ARG_NOT_GRANTED_MESSAGE = "not_granted_message";

        public static ConfirmationDialogFragment newInstance(String message,
                                                             String[] permissions, int requestCode, String notGrantedMessage) {
            ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            args.putStringArray(ARG_PERMISSIONS, permissions);
            args.putInt(ARG_REQUEST_CODE, requestCode);
            args.putString(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Bundle args = getArguments();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(args.getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String[] permissions = args.getStringArray(ARG_PERMISSIONS);
                                    if (permissions == null) {
                                        throw new IllegalArgumentException();
                                    }
                                    ActivityCompat.requestPermissions(getActivity(),
                                            permissions, args.getInt(ARG_REQUEST_CODE));
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(),
                                            args.getString(ARG_NOT_GRANTED_MESSAGE),
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                    .create();
        }

    }


    private class FaceThread implements Runnable {
        private byte[] mData;
        private ByteArrayOutputStream mBitmapOutput;
        private Matrix mMatrix;
        private Camera mCamera;

        public FaceThread(byte[] data, Camera camera) {
            mData = data;
            mBitmapOutput = new ByteArrayOutputStream();
            mMatrix = new Matrix();
            int mOrienta = mCameraView.getCameraDisplayOrientation();
            mMatrix.postRotate(mOrienta * -1);
            mMatrix.postScale(-1, 1);//默认是前置摄像头，直接写死 -1 。
            mCamera = camera;
        }

        @Override
        public void run() {
            Log.i(TAG, "thread is run");
            Bitmap bitmap = null;
            Bitmap roteBitmap  = null ;
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                int width = parameters.getPreviewSize().width;
                int height = parameters.getPreviewSize().height;

                YuvImage yuv = new YuvImage(mData, parameters.getPreviewFormat(), width, height, null);
                mData = null ;
                yuv.compressToJpeg(new Rect(0, 0, width, height), 100, mBitmapOutput);

                byte[] bytes = mBitmapOutput.toByteArray();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;//必须设置为565，否则无法检测
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                mBitmapOutput.reset();
                roteBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mMatrix, false);
                Vector<Box> boxes=mtcnn.detectFaces(roteBitmap,40);
                for (int i=0;i<boxes.size();i++) Utils.drawBox(bitmap,boxes.get(i),1+bitmap.getWidth()/500 );
                //Log.i("box","[*]boxNum"+boxes.size());
                Rect rect=boxes.get(0).transform2Rect();
                int margin=20; //20这个值是facenet中设置的。自己应该可以调整。
                Utils.rectExtend(roteBitmap,rect,margin);
                Utils.drawRect(roteBitmap,rect,1+bitmap.getWidth()/100 );
                Bitmap face=Utils.crop(roteBitmap,rect);
                FaceFeature ff=facenet.recognizeImage(face);
                //ff.getFeature();
               // Log.i("ff","you"+ff.getdata(ff.getFeature()));
               // Bitmap face1=Utils.crop(bitmap,rect);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mMatrix = null ;
                if (bitmap != null) {
                    bitmap.recycle();
                }
                if (roteBitmap != null) {
                    roteBitmap.recycle();
                }

                if (mBitmapOutput != null) {
                    try {
                        mBitmapOutput.close();
                        mBitmapOutput = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

