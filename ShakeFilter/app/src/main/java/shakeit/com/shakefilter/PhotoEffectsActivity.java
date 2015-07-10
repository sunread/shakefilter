package shakeit.com.shakefilter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import jp.co.cyberagent.android.gpuimage.*;
import jp.wasabeef.picasso.transformations.*;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.gpu.BrightnessFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.ContrastFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.PixelationFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.ToonFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.InvertFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.KuwaharaFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.SketchFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.SwirlFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.VignetteFilterTransformation;

/**
 * Created by camposbrunocampos on 05/07/15.
 */
public class PhotoEffectsActivity extends ActionBarActivity {

    ImageView mImageView;
    Drawable drawable;
    ProgressBar progressBar;
    File fileOriginal;
    File fileFiltered;

    Bitmap bmp;
    Button button;
    Vibrator vib;

    float [] history = new float[2];
    String [] direction = {"NONE","NONE"};


    boolean pressed;

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private static Toast toast;

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {


            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];



            float xChange = history[0] - x;
            float yChange = history[1] - y;
            history[0] = x;
            history[1] = y;



            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter

            final String successMessage = " filter applied successfully.";
            final String errorMessage = "Failure when applying filter";

            if (mAccel > 12) {
                File file;
                if(pressed){
                    fileFiltered = new File(Environment.getExternalStorageDirectory().getPath()+"/filteredImage.jpg");
                    file = fileFiltered;
                }else{
                    fileOriginal = new File(Environment.getExternalStorageDirectory().getPath()+"/originalImage.jpg");
                    file = fileOriginal;
                }

                progressBar.setVisibility(View.VISIBLE);
                Random r = new Random();
                int randomNumber = r.nextInt(12);
                Log.d("PHOTO_EFFECTS_ACTIVITY", "number is = "+ randomNumber);
                switch (randomNumber){
                // Toon filter
                    case 0:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new ToonFilterTransformation(PhotoEffectsActivity.this))
                                .into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        filterApplied( "Toon"+successMessage);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        if(toast != null)
                                            toast.cancel();
                                        toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;
                    // Pixelation filter
                    case 1:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new PixelationFilterTransformation(PhotoEffectsActivity.this, 2))
                                .into(mImageView,new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        filterApplied("Pixelation"+successMessage);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        if(toast != null)
                                            toast.cancel();
                                        toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;
                    // Contrast filter
                    case 2:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new ContrastFilterTransformation(PhotoEffectsActivity.this))
                                .into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            filterApplied( "Contrast"+successMessage);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                            if(toast != null)
                                toast.cancel();
                            toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                        break;
                    // Kuwahara filter
                    case 3:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new KuwaharaFilterTransformation(PhotoEffectsActivity.this))
                                .into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        filterApplied("Kuwahara"+successMessage);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        if(toast != null)
                                            toast.cancel();
                                        toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;
                    // Grayscale filter
                    case 4:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new GrayscaleTransformation())
                                .into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        filterApplied("Grayscale"+successMessage);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        if(toast != null)
                                            toast.cancel();
                                        toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;
                    // Sepia filter
                    case 5:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new SepiaFilterTransformation(PhotoEffectsActivity.this))
                                .into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        filterApplied( "Sepia"+successMessage);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        if(toast != null)
                                            toast.cancel();
                                        toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;
                    // Invert filter
                    case 6:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new InvertFilterTransformation(PhotoEffectsActivity.this))
                                .into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        filterApplied("Sketch"+successMessage);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        if(toast != null)
                                            toast.cancel();
                                        toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;
                    // Sketch filter
                    case 7:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new SketchFilterTransformation(PhotoEffectsActivity.this))
                                .into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                       filterApplied("Sketch"+successMessage);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        if(toast != null)
                                            toast.cancel();
                                        toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;
                    // Swirl filter
                    case 8:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new SwirlFilterTransformation(PhotoEffectsActivity.this))
                                .into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        filterApplied( "Swirl"+successMessage);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        if(toast != null)
                                            toast.cancel();
                                        toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;
                    // Vignette filter
                    case 9:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new VignetteFilterTransformation(PhotoEffectsActivity.this))
                                .into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                       filterApplied("Vignette"+successMessage);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        if(toast != null)
                                            toast.cancel();
                                        toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;
                }
            }


        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public void filterApplied(String filterName){
        progressBar.setVisibility(View.GONE);
        bmp = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
        if(pressed) {
            Log.d("PHOTO_EFFECTS_ACTIVITY", "IS PRESSING SCREEN");
            saveImage(((BitmapDrawable)mImageView.getDrawable()).getBitmap());
        }

        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(getApplicationContext(), filterName, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_effect);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        progressBar = (ProgressBar)findViewById(R.id.progress);
        button = (Button)findViewById(R.id.button);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                invert(bmp);
                Vibrator vib = (Vibrator) PhotoEffectsActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                vib.vibrate(500);
                mImageView.setImageBitmap(bmp);
                return true;
            }
        });


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        mImageView = (ImageView)findViewById(R.id.image_view);
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        pressed = true;
                        saveImage(((BitmapDrawable) mImageView.getDrawable()).getBitmap());
                        break;

                    case MotionEvent.ACTION_MOVE:
                        //User is moving around on the screen
                        break;

                    case MotionEvent.ACTION_UP:
                        pressed = false;
                        break;
                }
                return pressed;            }
        });



        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        mImageView.setImageBitmap(bmp);

        fileOriginal = new File(Environment.getExternalStorageDirectory().getPath()+"/originalImage.jpg");
        fileFiltered = new File(Environment.getExternalStorageDirectory().getPath()+"/filteredImage.jpg");
        try
        {
            fileOriginal.getParentFile().mkdirs();
            fileOriginal.createNewFile();
            fileFiltered.getParentFile().mkdirs();
            fileFiltered.createNewFile();
            FileOutputStream ostream = new FileOutputStream(fileOriginal);
            bmp.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
            FileOutputStream ostream2 = new FileOutputStream(fileFiltered);
            bmp.compress(Bitmap.CompressFormat.JPEG, 75, ostream2);
            ostream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void saveImage(Bitmap bit){
        try
        {
            fileFiltered = new File(Environment.getExternalStorageDirectory().getPath()+"/filteredImage.jpg");

            FileOutputStream ostream = new FileOutputStream(fileFiltered);
            bit.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
            ostream.close();
            Log.e("PHOTO_EFFECTS_ACTIVITTY","image saved ");
        }
        catch (Exception e)
        {
            Log.e("PHOTO_EFFECTS_ACTIVITTY","failure when saving image");
            e.printStackTrace();
        }
    }

    public void putOverlay(Bitmap bitmap, Bitmap overlay, int x , int y) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawPoint(x, y, paint);
        mImageView.setImageBitmap(bitmap);
    }



    public Bitmap invert(Bitmap src) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        Random r = new Random();
        int x = r.nextInt(width);
        int y = r.nextInt(height);
        // scan through all pixels
        for(int i = 0; i < 500; i++) {
//            for(int y = 0; y < height; ++y) {
                // get pixel color
                x = r.nextInt(width);
                y = r.nextInt(height);
                pixel = src.getPixel(x, y);
                // get color on each channel
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // set new pixel color to output image
                bmOut.setPixel(x, y, Color.argb(A, 255-R, 255-G, 255-B));
            }
//        }

        bmp = src.copy(bmOut.getConfig(), true);
        Canvas canvasBmp = new Canvas( bmp );
        canvasBmp.drawBitmap(bmOut,0,0,null);
        // return final image
        return bmOut;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                try {
                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/finalImage.jpg");

                    FileOutputStream ostream = new FileOutputStream(file);
                    bmp = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                    ostream.close();
                    Log.e("PHOTO_EFFECTS_ACTIVITTY", "image saved ");
                    if (toast != null) {
                        toast = Toast.makeText(getApplicationContext(), "Arquivo salvo em " + Environment.getExternalStorageDirectory().getPath() + "/image2.jpg", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (Exception e) {
                    Log.e("PHOTO_EFFECTS_ACTIVITTY", "failure when saving image");
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

