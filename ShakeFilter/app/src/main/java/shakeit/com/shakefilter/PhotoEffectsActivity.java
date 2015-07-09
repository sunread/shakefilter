package shakeit.com.shakefilter;

import android.content.Context;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
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

/**
 * Created by camposbrunocampos on 05/07/15.
 */
public class PhotoEffectsActivity extends ActionBarActivity {

    ImageView mImageView;
    Drawable drawable;
    ProgressBar progressBar;
    File file;
    Bitmap bmp;
    Button button;
    Vibrator vib;

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            if (mAccel > 12) {
                progressBar.setVisibility(View.VISIBLE);
                Random r = new Random();
                int i1 = r.nextInt(5);
                Log.d("PHOTO_EFFECTS_ACTIVITY", "number is = "+ i1);
                switch (i1){

                    case 1:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new ToonFilterTransformation(PhotoEffectsActivity.this))
                                .into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Filter applied successfully. ", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Failure when applying filter", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;
                    case 2:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new PixelationFilterTransformation(PhotoEffectsActivity.this))
                                .into(mImageView,new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Filter applied successfully.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Failure when applying filter", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;
                    case 3:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new ContrastFilterTransformation(PhotoEffectsActivity.this))
                                .into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                            Toast toast = Toast.makeText(getApplicationContext(), "Filter applied successfully.", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                            Toast toast = Toast.makeText(getApplicationContext(), "Failure when applying filter", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                        break;
                    case 4:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new BrightnessFilterTransformation(PhotoEffectsActivity.this))
                                .into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Filter applied successfully.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Failure when applying filter", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;
                    case 5:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new GrayscaleTransformation())
                                .into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Filter applied GrayscaleTransformation successfully.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Failure when applying GrayscaleTransformation filter", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;
                    default:
                        Picasso.with(PhotoEffectsActivity.this).load(file)
                                .transform(new GrayscaleTransformation())
                                .into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Filter applied successfully.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Failure when applying GrayscaleTransformation filter", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                        break;


                }

//                mImageView.setImageDrawable(drawable);


            }


        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

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
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        mImageView.setImageBitmap(bmp);

        file = new File(Environment.getExternalStorageDirectory().getPath()+"/image2.jpg");
        try
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
            ostream.close();
        }
        catch (Exception e)
        {
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
}

