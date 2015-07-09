package shakeit.com.shakefilter.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import shakeit.com.shakefilter.manager.CameraManager;
import shakeit.com.shakefilter.utils.AndroidAPIUtils;

/**
 * Created by Rodrigo on 04/05/2015.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private CameraManager mCameraManager;

    private void initializeHolder(){
        mHolder = getHolder();
        if(AndroidAPIUtils.isPreHoneycomb()) mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public CameraSurfaceView(Context context) {
        super(context);
        initializeHolder();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeHolder();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeHolder();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeHolder();
    }

    public void setCameraManager(CameraManager cameraManager){
        mCameraManager = cameraManager;
        mHolder.addCallback(this);
    }

    /*
     * Surface Callback methods
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(mCameraManager == null) return;
        mCameraManager.startCameraPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mHolder == null) return;

        mCameraManager.stopCameraPreview();
        mCameraManager.startCameraPreview(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}


}
