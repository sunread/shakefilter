package shakeit.com.shakefilter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import shakeit.com.shakefilter.manager.CameraManager;
import shakeit.com.shakefilter.view.CameraSurfaceView;


public class MainActivity extends ActionBarActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }

        mImageView = (ImageView)findViewById(R.id.image_view);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent in1 = new Intent(this, PhotoEffectsActivity.class);
            in1.putExtra("image",byteArray);
            startActivity(in1);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements CameraManager.CameraCallback{
        Button takePhotoButton;
        public PlaceholderFragment() {
        }
        CameraManager mCameraManager;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            CameraSurfaceView cameraView = (CameraSurfaceView) rootView.findViewById(R.id.camera_preview);
            mCameraManager = new CameraManager(savedInstanceState, getActivity(), this);
            takePhotoButton = (Button)rootView.findViewById(R.id.take_photo_button);
            takePhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCameraManager.takePicture();
                    takePhotoButton.setEnabled(false);
                }
            });
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();

            mCameraManager.openCamera();
//            mPreview.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPause() {
            super.onPause();

            mCameraManager.stopCameraPreview();
//            mPreview.setVisibility(View.INVISIBLE);
            mCameraManager.releaseCamera();
        }

        @Override
        public int getPreviewWidth() {
            return 0;
        }

        @Override
        public int getPreviewHeight() {
            return 0;
        }

        @Override
        public void onSetupComplete() {

        }

        @Override
        public void onPhotoTaken() {

        }

        @Override
        public boolean canTakeMorePhotos() {
            return false;
        }

        @Override
        public Bitmap cropImage(Bitmap bitmap) {
            return null;
        }

        @Override
        public void onBitmapReady(Bitmap bitmap) {

        }
    }
}
