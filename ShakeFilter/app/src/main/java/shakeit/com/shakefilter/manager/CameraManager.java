package shakeit.com.shakefilter.manager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import shakeit.com.shakefilter.utils.AndroidAPIUtils;

/**
 * Created by Camposbrunocampos on 04/05/2015.
 */
public class CameraManager {

    private static final String TAG = "CAMERA_MANAGER";
    private static final String ARG_IS_FRONT_CAMERA = "ARG_IS_FRONT_CAMERA";

    private static final int PICTURE_SIZE_MIN_WIDTH = 640;

    private int mDisplayOrientation;
    private int mLayoutOrientation;

    private int mFrontCameraId;
    private int mBackCameraId;
    private boolean mIsFrontCamera;

    private int mFocusAreaSize;
    private Matrix mMatrix;

    private boolean mTapToFocusEnabled;
    private boolean mIsBurstModeEnabled;

    private boolean mIsBurstModeRunning;

    private byte[] lastPhotoData;
    private int pictureSize;

    private final Object cameraSync = new Object();

    private Camera.Size mPreviewPictureSize;
    private Camera mCamera;

    private WeakReference<Activity> activityWeakReference;
    private CameraCallback mCallback;

    private Handler burstHandler = new Handler();

    Runnable burstRunnable = new Runnable() {
        @Override
        public void run() {
            takeBurstShot();
        }
    };

    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback(){
        @Override
        public void onShutter() {
            mCallback.onPhotoTaken();
        }
    };

    Camera.PictureCallback mPictureReadyCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mCamera.startPreview();
            new ProcessMomentImageTask(false).execute(data);
        }
    };

    private static int findCameraID(int facing) {
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == facing) return i;
        }
        return -1;
    }

    public CameraManager(Bundle savedInstanceState, Activity activity, CameraCallback callback) {
        activityWeakReference = new WeakReference<>(activity);
        mCallback = callback;

        mFrontCameraId = findCameraID(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mBackCameraId = findCameraID(Camera.CameraInfo.CAMERA_FACING_BACK);

        mMatrix = new Matrix();
        mFocusAreaSize = 50;

        mIsFrontCamera = savedInstanceState != null && savedInstanceState.getBoolean(ARG_IS_FRONT_CAMERA, false);
    }

    public void onSaveInstanceState(Bundle outState){
        outState.putBoolean(ARG_IS_FRONT_CAMERA, mIsFrontCamera);
    }

    public void openCamera(){
        try {
            mCamera = Camera.open(getCurrentCameraId());
        } catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "Error on opening camera", e);
        }
    }

    public void releaseCamera(){
        mCamera.release();
        mCamera = null;
    }

    public void startCameraPreview(SurfaceHolder holder) {
        synchronized (cameraSync) {
            setCameraDisplayOrientation();
            setupCamera();

            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (Exception exception) {
                Log.e(TAG, "Can't start camera preview", exception);
            }
        }
    }

    public void stopCameraPreview() {
        synchronized (cameraSync) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
            } catch (Exception exception) {
                Log.i(TAG, "Exception while stopping camera preview");
            }
        }
    }

    private boolean canTakePhoto(){
        return mCallback.canTakeMorePhotos() && mIsBurstModeRunning;
    }

    private synchronized void takeBurstShot() {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                mCallback.onPhotoTaken();

                new ProcessMomentImageTask(true).execute(lastPhotoData);

                if (canTakePhoto()) burstHandler.postDelayed(burstRunnable, 500);
                else mIsBurstModeRunning = false;
            }
        });
    }

    public synchronized void startBurstMode() {
        if(!mIsBurstModeRunning) {
            mIsBurstModeRunning = true;
            takeBurstShot();
        }
    }

    public void cancelBurstMode() {
        mIsBurstModeRunning = false;
        burstHandler.removeCallbacks(burstRunnable);
    }

    public void takePicture(){
        mCamera.takePicture(mShutterCallback, null, mPictureReadyCallback);
    }

    public int getPictureSize(){
        return pictureSize;
    }

    public void switchCamera(SurfaceHolder holder){
        stopCameraPreview();
        mCamera.release();

        mIsFrontCamera = !mIsFrontCamera;
        openCamera();
        startCameraPreview(holder);
    }

    public boolean isTapToFocusEnabled(){
        return mTapToFocusEnabled;
    }

    public boolean isBurstEnabled(){
        return mIsBurstModeEnabled;
    }

    private void setCameraDisplayOrientation() {
        if(activityWeakReference.get() == null) return;

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(getCurrentCameraId(), info);

        int rotation = activityWeakReference.get().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (mIsFrontCamera) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        mCamera.setDisplayOrientation(result);

        mDisplayOrientation = result;
        mLayoutOrientation = degrees;

        mMatrix = new Matrix();
        mMatrix.postRotate(result);
        mMatrix.postScale(mCallback.getPreviewWidth() / 2000f, mCallback.getPreviewHeight() / 2000f);
        mMatrix.postTranslate(mCallback.getPreviewWidth() / 2f, mCallback.getPreviewHeight() / 2f);
        mMatrix.invert(mMatrix);
    }

    private double getPreviewRatio(){
        return (double) mCallback.getPreviewWidth() / mCallback.getPreviewHeight();
    }

    private int getCurrentCameraId(){
        if(mIsFrontCamera) return mFrontCameraId;
        else return mBackCameraId;
    }

    private static boolean isPortrait(Camera.Size size){
        return (size.height > size.width);
    }

    private static double getPortraitRatio(Camera.Size size){
        if(isPortrait(size)) return (double) size.width / size.height;
        else return (double) size.height / size.width;
    }

    private List<Camera.Size> getScreenRatioPictureSizes(List<Camera.Size> sizes){
        List<Camera.Size> inRatioSizes = new ArrayList<>();

        double desiredPortraitRatio = getPreviewRatio();

        for (Camera.Size currentSize : sizes) {
            if(getPortraitRatio(currentSize) == desiredPortraitRatio) inRatioSizes.add(currentSize);
        }

        return inRatioSizes;
    }

    private static int getPortraitWidth(Camera.Size size){
        if(isPortrait(size)) return size.width;
        else return size.height;
    }

    private static List<Camera.Size> getSizesReverseOrderedByWidth(List<Camera.Size> sizes){
        Collections.sort(sizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                int lhsWidth = getPortraitWidth(lhs);
                int rhsWidth = getPortraitWidth(rhs);

                if (lhsWidth > rhsWidth) return -1;
                else if (lhsWidth == rhsWidth) return 0;
                else return 1;
            }
        });

        return sizes;
    }

    /***
     * Find the smallest picture size that is at least {#PICTURE_SIZE_MIN_WIDTH}
     * Or the largest available if none is at least {#PICTURE_SIZE_MIN_WIDTH}
     */
    private Camera.Size determineBestPictureSize(List<Camera.Size> sizes) {
        List<Camera.Size> orderedSizes = getSizesReverseOrderedByWidth(sizes);
        List<Camera.Size> minWidthSizes = new ArrayList<>();

        for(int i = 0; i < orderedSizes.size(); i++) {
            if(getPortraitWidth(orderedSizes.get(i)) >= PICTURE_SIZE_MIN_WIDTH) minWidthSizes.add(orderedSizes.get(i));
        }

        if(minWidthSizes.size() > 0) {
            List<Camera.Size> inRatioSizes = getScreenRatioPictureSizes(minWidthSizes);

            if(inRatioSizes.size() > 0) {
                return inRatioSizes.get(inRatioSizes.size() - 1);
            } else {
                return minWidthSizes.get(minWidthSizes.size() - 1);
            }

        } else {
            List<Camera.Size> inRatioSizes = getScreenRatioPictureSizes(orderedSizes);

            if(inRatioSizes.size() > 0) {
                return inRatioSizes.get(0);
            } else {
                return orderedSizes.get(0);
            }
        }
    }

    public int getPreviewWidth(){
        if(isPortrait(mPreviewPictureSize)) return mPreviewPictureSize.width;
        else return mPreviewPictureSize.height;
    }

    public int getPreviewHeight(){
        if(isPortrait(mPreviewPictureSize)) return mPreviewPictureSize.height;
        else return mPreviewPictureSize.width;
    }

    private void setupPreviewSize(Camera.Parameters parameters){
        mIsBurstModeEnabled = false;
        int previewFormat = parameters.getPreviewFormat();

        if(previewFormat == ImageFormat.NV21) {
            mPreviewPictureSize = determineBestPictureSize(parameters.getSupportedPreviewSizes());
            parameters.setPreviewSize(mPreviewPictureSize.width, mPreviewPictureSize.height);

            if (getPortraitWidth(mPreviewPictureSize) >= PICTURE_SIZE_MIN_WIDTH) {
                mIsBurstModeEnabled = true;
                pictureSize = mPreviewPictureSize.width;

                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        if (mIsBurstModeEnabled) lastPhotoData = data;
                    }
                });
            }
        }
    }

    private void setupPictureSize(Camera.Parameters parameters){
        Camera.Size bestPictureSize = determineBestPictureSize(parameters.getSupportedPictureSizes());
        pictureSize = bestPictureSize.width;
        parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);
    }

    private void setupFocusMode(Camera.Parameters parameters){
        List<String> focusModes = parameters.getSupportedFocusModes();

        if(AndroidAPIUtils.isPosIceCreamSandwich() && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mTapToFocusEnabled = true;

        } else if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mTapToFocusEnabled = true;
        } else {
            mTapToFocusEnabled = false;
        }
    }

    /**
     * Convert touch position x:y to {@link android.hardware.Camera.Area} position -1000:-1000 to 1000:1000.
     */
    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(mFocusAreaSize * coefficient).intValue();

        int left = clamp((int) x - areaSize / 2, 0, mCallback.getPreviewWidth() - areaSize);
        int top = clamp((int) y - areaSize / 2, 0, mCallback.getPreviewHeight() - areaSize);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        mMatrix.mapRect(rectF);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    private void setupCamera() {
        if(activityWeakReference.get() == null) return;

        Camera.Parameters parameters = mCamera.getParameters();

        setupPreviewSize(parameters);

        if(!mIsBurstModeEnabled){
            mCamera.setPreviewCallback(null);

            setupPictureSize(parameters);
        }

        setupFocusMode(parameters);

        mCamera.setParameters(parameters);

        mCallback.onSetupComplete();
    }

    public void focus(float x, float y){
        if (mCamera != null) {
            Rect focusRect = calculateTapArea(x, y, 1f);

            Camera.Parameters parameters = mCamera.getParameters();

            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 1000));

            parameters.setFocusAreas(focusAreas);

            if (parameters.getMaxNumMeteringAreas() > 0) {
                Rect meteringRect = calculateTapArea(x, y, 1.5f);

                List<Camera.Area> meteringAreas = new ArrayList<>();
                meteringAreas.add(new Camera.Area(meteringRect, 1000));

                parameters.setMeteringAreas(meteringAreas);
            }

            mCamera.cancelAutoFocus();
            mCamera.setParameters(parameters);
            mCamera.autoFocus(null);
        }
    }


    private class ProcessMomentImageTask extends AsyncTask<byte[], Void, Bitmap> {

        private boolean isFromPreview;

        public ProcessMomentImageTask(boolean isFromPreview){
            this.isFromPreview = isFromPreview;
        }

        private byte[] processYuvImage(byte[] data){
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, mPreviewPictureSize.width, mPreviewPictureSize.height, null);
            yuvImage.compressToJpeg(new Rect(0, 0, mPreviewPictureSize.width, mPreviewPictureSize.height), 50, byteStream);

            return byteStream.toByteArray();
        }

        @Override
        protected Bitmap doInBackground(byte[]... params) {
            byte[] data = params[0];
            if(isFromPreview) data = processYuvImage(data);

            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);

            int rotation = (mLayoutOrientation + mDisplayOrientation) % 360;

            if (rotation != 0) {
                Bitmap oldImage = image;

                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                if(mIsFrontCamera) matrix.postScale(1, -1);

                image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
                oldImage.recycle();
            }

            return mCallback.cropImage(image);
        }

        @Override
        protected void onPostExecute(Bitmap result){
            mCallback.onBitmapReady(result);
        }
    }

    public interface CameraCallback {
        int getPreviewWidth();
        int getPreviewHeight();

        void onSetupComplete();

        void onPhotoTaken();
        boolean canTakeMorePhotos();

        Bitmap cropImage(Bitmap bitmap);
        void onBitmapReady(Bitmap bitmap);
    }
}
