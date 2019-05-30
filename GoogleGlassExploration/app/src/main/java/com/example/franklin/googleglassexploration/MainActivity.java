/*
 *@Authors: Franklin Kenghagho Kenfack
 *          Xiaojun Lan
 *          Wei Qiu
 *          Weihua Wu
 *@Date: 21.07.2016
 *@Goal: Human Motion Tracking System On Smart Glass Using built-in Sensors
 */


package com.example.franklin.googleglassexploration;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;


import java.io.*;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import android.widget.Toast;
import android.os.PowerManager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity implements SensorEventListener {





    //graphics variable
    private Calendar cal;
    private CardScrollView mCardScroller;
    private View mView;
    private CardBuilder card;
    private Bitmap SAVE;
    private Canvas NOW;
    private Paint PAINT;
    private float UX;
    private float UY;
    private float RX;
    private float RY;
    private float PX;
    private float PY;
    private float THICKNESS;


    //sensors
    private boolean FINISH=true;
    private boolean FINISH1=false;
    private SensorManager sensorManager;
    private Sensor acc_sensor;
    private Sensor rv_sensor;
    private ArrayList<String[]> sensorData;
    private int SAMPLE_LENGTH;
    private int CURRENT_INDEX;
    private float[] PREVROTATION={1.0f,0.0f,0.0f,0.0f,1.0f,0.0f,0.0f,0.0f,1.0f}; //identity rotation
    private float[][] MAINROTATION;
    private float[] CURRENTROTATION;
    private float[] CURRENT_ANGLES={0.0f,0.0f,0.0f};
    private boolean BEGIN1;
    private boolean BEGIN2;
    private   int SYNCHR;
    private   int SAMPLE_RATE;
    private int STEP;
    private int QUEUE;

    private int PREVIOUS_SIZE;
    private int DIVISOR;




    /**Metrics for kinematics*/

    private float DISTANCE;//in m
    private float SPEED;  //in m/s
    private float SPEEDX;  //in m/s
    private float SPEEDY;  //in m/s
    private float SPEEDZ;  //in m/s
    private float POSITIONX; //X-coordinate
    private float POSITIONY; //Y-coordinate
    private float POSITIONZ; //Z-coordinate
    private float TIMESPENT; //time spentin s
    private float SENSIBILITY;//radius of the tracked region for the time spent;
    private float SCALER;     //scale factor for the step reconstruction
    private float SALPHA;     //the smoothing factor
    private float ERRORX;     //x-offset
    private float ERRORY;     //y-offset
    private float ERRORZ;     //z-offset
    private float LOWLIMITZ;  //epsaz as defined in the report
    private float LOWLIMITX;  //epsax
    private float LOWLIMITY;  //epsay
    private float ZERO_RATE;  //rate as defined in the report
    private float LOWLIMITSPEED; //epsv as defined in the report
    private int PULSELENGTH;     // window's size as defined in the report
    private float THRESHOLD;    //threshold for derivability as defined in the report
    private int ZERO_RADIUS;    //radius as defined in the report
    private float FORWARD_PULSE_ERROR; //ED as defined in the report
    private float BACKWARD_PULSE_ERROR; //EU as defined in the report
    private String HEAD_MESSAGE;        //user message to display on the GUI

    //centroid for forward steps as defined in the report(sin)
    private float[] FORWARD_PULSE_CENTROID={ -1.22464680e-16f,  -1.04528463e-01f,  -2.07911691e-01f,
            -3.09016994e-01f,  -4.06736643e-01f,  -5.00000000e-01f,
            -5.87785252e-01f,  -6.69130606e-01f,  -7.43144825e-01f,
            -8.09016994e-01f,  -8.66025404e-01f,  -9.13545458e-01f,
            -9.51056516e-01f,  -9.78147601e-01f,  -9.94521895e-01f,
            -1.00000000e+00f,  -9.94521895e-01f,  -9.78147601e-01f,
            -9.51056516e-01f,  -9.13545458e-01f,  -8.66025404e-01f,
            -8.09016994e-01f,  -7.43144825e-01f,  -6.69130606e-01f,
            -5.87785252e-01f,  -5.00000000e-01f,  -4.06736643e-01f,
            -3.09016994e-01f,  -2.07911691e-01f,  -1.04528463e-01f,
            4.44089210e-15f,   1.04528463e-01f,   2.07911691e-01f,
            3.09016994e-01f,   4.06736643e-01f,   5.00000000e-01f,
            5.87785252e-01f,   6.69130606e-01f,   7.43144825e-01f,
            8.09016994e-01f,   8.66025404e-01f,   9.13545458e-01f,
            9.51056516e-01f,   9.78147601e-01f,   9.94521895e-01f,
            1.00000000e+00f,   9.94521895e-01f,   9.78147601e-01f,
            9.51056516e-01f,   9.13545458e-01f,   8.66025404e-01f,
            8.09016994e-01f,   7.43144825e-01f,   6.69130606e-01f,
            5.87785252e-01f,   5.00000000e-01f,   4.06736643e-01f,
            3.09016994e-01f,   2.07911691e-01f,   1.04528463e-01f};

    //centroid for backward steps as defined in the report(-sin)
    private float[] BACKWARD_PULSE_CENTROID={  1.22464680e-16f,   1.04528463e-01f,   2.07911691e-01f,
            3.09016994e-01f,   4.06736643e-01f,   5.00000000e-01f,
            5.87785252e-01f,   6.69130606e-01f,   7.43144825e-01f,
            8.09016994e-01f,   8.66025404e-01f,   9.13545458e-01f,
            9.51056516e-01f,   9.78147601e-01f,   9.94521895e-01f,
            1.00000000e+00f,   9.94521895e-01f,   9.78147601e-01f,
            9.51056516e-01f,   9.13545458e-01f,   8.66025404e-01f,
            8.09016994e-01f,   7.43144825e-01f,   6.69130606e-01f,
            5.87785252e-01f,   5.00000000e-01f,   4.06736643e-01f,
            3.09016994e-01f,   2.07911691e-01f,   1.04528463e-01f,
            -4.44089210e-15f,  -1.04528463e-01f,  -2.07911691e-01f,
            -3.09016994e-01f,  -4.06736643e-01f,  -5.00000000e-01f,
            -5.87785252e-01f,  -6.69130606e-01f,  -7.43144825e-01f,
            -8.09016994e-01f,  -8.66025404e-01f,  -9.13545458e-01f,
            -9.51056516e-01f,  -9.78147601e-01f,  -9.94521895e-01f,
            -1.00000000e+00f,  -9.94521895e-01f,  -9.78147601e-01f,
            -9.51056516e-01f,  -9.13545458e-01f,  -8.66025404e-01f,
            -8.09016994e-01f,  -7.43144825e-01f,  -6.69130606e-01f,
            -5.87785252e-01f,  -5.00000000e-01f,  -4.06736643e-01f,
            -3.09016994e-01f,  -2.07911691e-01f,  -1.04528463e-01f};








    /*
 * SENSOR LISTENER FUNCTION: collects the data from different sensors and store them into a particular format in array sensorData
 */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //record sensor data
        if (event.sensor == acc_sensor) {
            String e[] = {Long.toString(event.timestamp), Float.toString(event.values[0]), Float.toString(event.values[1]), Float.toString(event.values[2]), Float.toString(CURRENT_ANGLES[1]), Float.toString(CURRENT_ANGLES[2]), Float.toString(CURRENT_ANGLES[0])};
            sensorData.add(e);

        } else {
            if (event.sensor == rv_sensor) {
                if (BEGIN2) {//access only after calibration
                    float[] rv = {event.values[0], event.values[1], event.values[2]};// read quaternion from sensor
                    sensorManager.getRotationMatrixFromVector(CURRENTROTATION, rv);//get rotation matrix from quaternion
                    if (BEGIN1) {
                        for (int i = 0; i < CURRENTROTATION.length; i++)
                            PREVROTATION[i] = CURRENTROTATION[i];   //fix the initial quaternion(calibration)
                        BEGIN1 = false;

                    }
                    //already accessed

                    sensorManager.getAngleChange(CURRENT_ANGLES, CURRENTROTATION, PREVROTATION); //difference of rotations

                }else
                if(SYNCHR==0){
                    BEGIN2=true;
                    sensorData.clear();
                    HEAD_MESSAGE="Ok!Started";//alert the user that the calibration terminated
                }else{
                    SYNCHR--;
                }
            }
        }
        if((sensorData.size()>PREVIOUS_SIZE) && ((sensorData.size()%SAMPLE_LENGTH)==0)&& BEGIN2){
            PREVIOUS_SIZE+=SAMPLE_LENGTH;                 //personalized queueing of events
            QUEUE++;
        }

       if((QUEUE>0) && FINISH && BEGIN2) {
            FINISH=false;
            QUEUE--;
           //free the data stream that was just processed to avoid memory overflow
           if(FINISH1) {
               FINISH1=false;
               for (int i = 0; i < SAMPLE_LENGTH; i++)
                   sensorData.remove(0);
               PREVIOUS_SIZE-=SAMPLE_LENGTH;
           }
            new GUI_Updater().execute(1.0f);    // send a finite stream to dead-reckoning module for kinematics computation
        }
    }



    /*
     * SENSOR LISTENER FUNCTION: not used
     */

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    /*
     * SYSTEM STARTER FUNCTION: used to start and initialize the system at launch
     */

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //sensor variables initalization
        CURRENTROTATION=new float[9];
        MAINROTATION=new float[3][3];
        CURRENT_INDEX=0;
        BEGIN1=true;
        BEGIN2=false;
        SYNCHR=800;
        DIVISOR=1;
        QUEUE=0;
        PREVIOUS_SIZE=0;
        SAMPLE_LENGTH=60;
        SAMPLE_RATE=50;//50Hz- SENSOR_DELAY_GAME
        sensorData=new ArrayList<String[]>();

        //Initialize kinematics-related variables
        SCALER=2.0f;
        DISTANCE=0.0f;
        SPEED=0.0f;
        SPEEDX=0.0f;
        SPEEDY=0.0f;
        SPEEDZ=0.0f;
        POSITIONX=0.0f;
        POSITIONY=0.0f;
        POSITIONZ=0.0f;
        TIMESPENT=0.0f;
        SENSIBILITY=0.05f;
        ERRORX= 0.0507093771434f;
        ERRORY=0.2560571075031f;
        ERRORZ=0.0461532642256f;
        LOWLIMITZ=0.1f;
        LOWLIMITX=0.5f;
        LOWLIMITY=0.5f;
        LOWLIMITSPEED=0.01f;
        ZERO_RATE=0.8f;
        SALPHA=00.60f;

        PULSELENGTH=4;
        THRESHOLD=5.0f;
        ZERO_RADIUS=PULSELENGTH;
        FORWARD_PULSE_ERROR=5.15f;
        BACKWARD_PULSE_ERROR=4.6f;
        STEP=0;

        //Initialize GUI-related variables
        cal=Calendar.getInstance();
        HEAD_MESSAGE="Wait for sync...";
        //set drawing parameter
        THICKNESS=3.0f;
        UX=100;
        UY=170;
        RX=0.2f;
        RY=9.0f/50.0f;
        PX=100.0f;
        PY=170.0f;
        card= buildView();
        mView=card.getView();
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return mView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView;
            }

            @Override
            public int getPosition(Object item) {
                if (mView.equals(item)) {
                    return 0;
                }
                return AdapterView.INVALID_POSITION;
            }
        });
        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Plays disallowed sound to indicate that TAP actions are not supported.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.DISALLOWED);
            }
        });
        setContentView(mCardScroller);

        //sensors creation
        sensorManager=(SensorManager)getSystemService(this.getApplicationContext().SENSOR_SERVICE);
        acc_sensor=sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        rv_sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR );


    }

    @Override
    protected void onResume() {
        super.onResume();
       //activate GUI on resume
        mCardScroller.activate();

        //sensors registration to listeners
        sensorManager.registerListener(this,acc_sensor,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this,rv_sensor,SensorManager.SENSOR_DELAY_GAME);
        //test to display on GUI
        card.setText("Distance:  "+DISTANCE+"m\n"+
                "Speed:     "+SPEED+" m/s\n"+
                "Position:  ("+POSITIONX+","+POSITIONZ+")\n"+
                "Time Spent: "+TIMESPENT+"s"
        );


        //Trajectory of the pedestrian as Image saving and then insertion in the GUI

        SAVE = Bitmap.createBitmap(220,320, Config.ARGB_8888);
        PAINT = new Paint();
        PAINT.setStyle(Paint.Style.FILL);
        PAINT.setColor(Color.GRAY);
        NOW = new Canvas(SAVE);
        NOW.drawPaint(PAINT);
        PAINT.setTextSize(PAINT.getTextSize()*2);

        //current path
        PAINT.setColor(Color.BLACK);
        NOW.drawRect(new Rect(0,80,200,260), PAINT);
        PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
        PAINT.setStrokeWidth(THICKNESS);
        PAINT.setColor(Color.BLUE);
        NOW.drawLine(0,170,200,170,PAINT);
        NOW.drawLine(100,80,100,260,PAINT);

        //origin
        PAINT.setColor(Color.WHITE);
        NOW.drawCircle(100,170,THICKNESS,PAINT);
        //current position
        PAINT.setStyle(Paint.Style.FILL);
        PAINT.setColor(Color.YELLOW);
        NOW.drawCircle(100,170,THICKNESS,PAINT);

        //label-information

        //Head Message
        PAINT.setColor(Color.GRAY);
        NOW.drawRect(new Rect(15,50,200,80), PAINT);
        // PAINT.setTextSize(PAINT.getTextSize()*2);
        PAINT.setColor(Color.YELLOW);
        NOW.drawText(HEAD_MESSAGE,15,50
                ,PAINT);

        NOW.drawText("CURRENT PATH",15,285
                ,PAINT);
        Drawable currentTrack = new BitmapDrawable(getResources(), SAVE);
        card.setIcon(currentTrack);


        //screen dimensioning
        mView=card.getView();
        mView.setScaleX(0.95f);
        mView.setScaleY(0.95f);
        mView.setBackgroundColor(Color.argb(208,150,150,150));
        setContentView(mView);

    }

    @Override
    protected void onPause() {

        //free resources on pause
        mCardScroller.deactivate();
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    /**
     * Builds a Glass styled "Hello World!" view using the {@link CardBuilder} class.
     */
    private CardBuilder buildView() {
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.COLUMNS);

        //Metrics
        card.setText("Distance:  "+DISTANCE+"m\n"+
                "Speed:     "+SPEED+" m/s\n"+
                "Position:  ("+POSITIONX+","+POSITIONZ+")\n"+
                "Time Spent: "+TIMESPENT+"s"
        );

        //other parameters
        card.setFootnote("Step count: "+STEP);
        card.setTimestamp("Power: "+getBatteryLevel()+"%");
        return card;
    }







    /////////////////////////////////////////////////////////////////////////////////////////////////
    ////               SIGNAL PROCESSING                                                         ////
    ////                                                                                         ////
    ////                                                                                         ////
    /////////////////////////////////////////////////////////////////////////////////////////////////


    /*
     *  Get battery level
     */

    public float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }




    /*
     * Update path on display given new target position (x,y)
     */
    public void updatePath(float x, float y){
        //convert in cm
        x=x*100.0f;
        y=y*100.0f;
        //convert into device coordinate
        x=x*RX + UX;
        y=340-(y*RY+ UY);
        //drawing

        //Head Message
        PAINT.setColor(Color.GRAY);
        NOW.drawRect(new Rect(15,20,200,80), PAINT);
        PAINT.setColor(Color.YELLOW);

        // PAINT.setTextSize(PAINT.getTextSize()*2);
        NOW.drawText(HEAD_MESSAGE,15,50
                ,PAINT);
        //expected path
        //set color
        PAINT.setColor(Color.RED);
        NOW.drawLine(PX,PY,x,y,PAINT);
        //cancel previous position
        NOW.drawCircle(PX,PY,THICKNESS,PAINT);
        //current position
        PAINT.setStyle(Paint.Style.FILL);
        PAINT.setColor(Color.YELLOW);
        NOW.drawCircle(x,y,THICKNESS,PAINT);
        //draw origin
        //origin
        PAINT.setColor(Color.WHITE);
        NOW.drawCircle(100,170,THICKNESS,PAINT);
        //update position
        PX=x;
        PY=y;
        //update GUI
        Drawable currentTrack = new BitmapDrawable(getResources(), SAVE);
        card.setIcon(currentTrack);

        mView=card.getView();
        mView.setScaleX(0.95f);
        mView.setScaleY(0.95f);
        mView.setBackgroundColor(Color.argb(208,150,150,150));
        setContentView(mView);


    }




    /*
     * Low-pass filter : Finite Impulse Response for smoothing the signal
     * acc input signal
     * SALPHA filter constant
     */
    private void  iir(float[] acc, float SALPHA){
        int n=acc.length;
        //x[0] does not change
        for(int i=1;i<n;i++ ) {
            if(Math.abs(acc[i])!=0.0f)
                acc[i] = SALPHA * acc[i] + (1 - SALPHA) * acc[i - 1];

        }
    }



    /*
     * Convert relative timestamp in nanoseconds to seconds
     * timestamp: array of timestamps
     */
    private void nanoTosecond(float[] timestamp){
        float RATIO=1000000000.0f;
        int n=timestamp.length;
        for(int i=n-1;i>=0;i--)
            timestamp[i]=(timestamp[i]-timestamp[0])/RATIO;
    }


    /*
     * ZeroShift Function: To remove the initial offset from the input signal
     * Offset:(ERRORX,ERRORY,ERRORZ)
     * acc: input acceleration
     */

    public void zeroShift(float[] acc, float offset){
        int n=acc.length;
        for(int i=0;i<n;i++){
            if(Math.abs(acc[i])<=offset)
                acc[i]=0.0f;
            else
            if(acc[i]<0.0f)
                acc[i]+=offset;
            else
            if(acc[i]>0.0f)
                acc[i]-=offset;

        }

    }


    /*
     * Explicit removal of extremly bad outliers from sensors data
     * acc: input acceleration
     * THRESHOLD: Max change rate of the signal to ensure its continuity.
     * timestamp: timestamps of pulses in seconds
     */
    public void removeOutliers(float[] acc, float threshold,float[] timestamp){
        int n=acc.length;
        if(n>1){
            for(int i=1;i<n;i++){
                if((Math.abs((acc[i]-acc[i-1])/(timestamp[i]-timestamp[i-1]))>threshold)){
                    //outlier detected
                    //overcome the issue
                    if(acc[i]>=acc[i-1]){
                        acc[i]=threshold*((timestamp[i]-timestamp[i-1]))+acc[i-1];
                    }else
                        acc[i]=-threshold*((timestamp[i]-timestamp[i-1]))+acc[i-1];

                }
            }

        }
    }


     /*
      *  squarewise reconstruction of a segment of the signal stream
      *  acc: the full input acceleration stream
      *  i,j: delimiters of the segment in the stream
      *  lowlimit: the lower bound of the input acceleration
      *  zero_rate: the sufficient density of zero within a segment to zero the segment
      */

    public void squarewiseSignalReconstruction(float[] acc,int i, int j,float lowlimit,float zero_rate){
        int n=acc.length;
        if((i<=j) && (j<n) && (i>-1)){
            float nber_zero=0.0f;
            for(int k=i;k<=j;k++){
                if(Math.abs(acc[k])<=lowlimit)
                    nber_zero++;
            }
            if((nber_zero/(j-i+1))>=zero_rate){
                //zero the segment
                for(int k=i;k<=j;k++){
                    acc[i]=0.0f;
                }
            }else{
                //assess the overall sign of the segment
                float maxmin;
                float sign=0.0f;
                for(int k=i;k<=j;k++){
                    sign+=acc[k];
                }
                if(sign>=0.0f){
                    //the segment is generally positive
                    //maximize the segment
                    maxmin=acc[i];
                    for(int k=i+1;k<=j;k++)
                        maxmin=Math.max(maxmin,acc[k]);
                }else{
                    //minimize the signal
                    maxmin=acc[i];
                    for(int k=i+1;k<=j;k++)
                        maxmin=Math.min(maxmin,acc[k]);
                }
                //update the segment
                for(int k=i;k<=j;k++){
                    acc[k]=maxmin;
                }

            }
        }
    }


    /*
     *  apply squarewise reconstruction for all segment of a given signal stream
     *  acc: full signal stream
     *  lowlimit: the lower bound of the signal stream
     *  zero_rate: sufficient rate of zero within a zegment to zero it
     *  segment_length: the length of a segment
     */
    public void fullSquarewiseSignalReconstruction(float[] acc,int segment_length,float lowlimit,float zero_rate){
        int n=acc.length;
        int length_last_segment=n%segment_length;
        int nber_segment=(n-length_last_segment)/segment_length;
        int k=0;
        for(int i=1;i<=nber_segment;i++){
            squarewiseSignalReconstruction(acc,k,k+segment_length-1,lowlimit, zero_rate);
            k=k+segment_length;
        }
        k=nber_segment*segment_length;
        squarewiseSignalReconstruction(acc,k,n-1,lowlimit, zero_rate);
    }


    /*
     * Explicit detecion of pauses/still phases.
     * acc: acceleration stream
     * lowlimitspeed: the lower bound of acceptable speed
     * i: is the system at timestamp indexed by i paused?
     * radius: in this case the neighboorhood of i must be paused as well.
     */

    public boolean isPaused(float[] acc,float lowlimitspeed,int i, int radius){
        int n=acc.length;
        int start=i-radius+1;  //start neighboorhood
        int end=i+radius-1;    //end neighboorhood
        while(start<=end){
            if ((start < n) && (start > -1))
                if (Math.abs(acc[start]) > lowlimitspeed)
                    return false;
            start = start + 1;
        }
        return true;
    }



    /*
     * compute the distance between two points/vectors
     */
    float distance(float v1[],float[] v2){
        float dist=0.0f;
        int n=v1.length;
        for(int i=0;i<n;i++)
            dist+=Float.valueOf(String.valueOf((Math.pow(Double.valueOf(String.valueOf(v1[i]-v2[i])),2.0))));
        return Float.valueOf(String.valueOf((Math.sqrt(Double.valueOf(String.valueOf(dist))))));
    }



    /*
     * realize zero velocity compensation and return the number of steps done
     * acc: full acceleration stream
     * accU: backward pulse centroid (-sin(x))
     * accD: forward pulse centroid  (sin(x))
     * backward_pulse_error: how near is a stream segment to what it should ideally be for positive move?
     * forward_pulse_error: how near is a stream segment to what it should ideally be for negative move?
     */
    public int zeroVelocityCompensation(float[] acc,float[] accU,float backward_pulse_error,float[] accD, float forward_pulse_error){
        int step=0;
        int startZero=0;
        int n=acc.length;
        float[] _acc=new float[accU.length];
        int i=0;
        int j=accU.length+i-1;



        while(j<n) {
            float max=Math.abs(acc[i]);
            for(int k=i+1;k<=j;k++)
                max=Math.max(max,Math.abs(acc[k]));
            if(max==0.0f)
                max=1.0f;
            for(int k=i;k<=j;k++)
                _acc[k-i]=acc[k]/max;          //segment normalization to match sine

            float distUp=distance(_acc,accU);

            float distDown=distance(_acc,accD);


            if ((distUp<=backward_pulse_error)&&((distUp<=distDown) || (distDown>forward_pulse_error))) {
                //scaled signal reconstruction
                for(int k=i;k<=j;k++)
                    acc[k]=SCALER*max *accU[k-i];
                //incremment step number
                step = step + 1;
                i = j;
            }
            else {
                if (distDown<=forward_pulse_error) {
                    //scaled signal reconstruction
                    for (int k = i; k <= j; k++)
                        acc[k] = SCALER* max * accD[k - i];
                    //incremment step number
                    step = step + 1;
                    i = j;
                }
            }
            i = i + 1;
            j = accU.length + i - 1;

        }
      /*  for(int k=startZero;k<n;k++)
            acc[k]=0.0f;*/
        return step;
    }

    /*
     * convert double to float
     */
    float getDF(double value){
        return Float.valueOf(String.valueOf(value));
    }

    /*
     * convert float to double
     */
    double getFD(float value){
        return Double.valueOf(String.valueOf(value));
    }

    /*
     * compute rotation matrix from an intrinsec rotation first about z of tetaz
     * , about x of tetax and about y of tetay
     */

    public void getRotationMatrix(float[][] mainrotation, float tetax,float tetay, float tetaz){
        mainrotation[0][0]=getDF(Math.cos(getFD(tetay))*Math.cos(getFD(tetaz))-Math.sin(getFD(tetax))*Math.sin(getFD(tetay))*Math.sin(getFD(tetaz)));
        mainrotation[0][1]=getDF(Math.cos(getFD(tetax))*Math.sin(getFD(tetaz)));
        mainrotation[0][2]=getDF(Math.cos(getFD(tetaz))*Math.sin(getFD(tetay))+Math.sin(getFD(tetax))*Math.sin(getFD(tetaz))*Math.cos(getFD(tetay)));

        mainrotation[1][0]=getDF(-Math.sin(getFD(tetaz))*Math.cos(getFD(tetay))-Math.sin(getFD(tetax))*Math.cos(getFD(tetaz))*Math.sin(getFD(tetay)));
        mainrotation[1][1]=getDF(Math.cos(getFD(tetax))*Math.cos(getFD(tetaz)));
        mainrotation[1][2]=getDF(-Math.sin(getFD(tetaz))*Math.sin(getFD(tetay))+Math.sin(getFD(tetax))*Math.cos(getFD(tetaz))*Math.cos(getFD(tetay)));

        mainrotation[2][0]=getDF(-Math.cos(getFD(tetax))*Math.sin(getFD(tetay)));
        mainrotation[2][1]=getDF(-Math.sin(getFD(tetax)));
        mainrotation[2][2]=getDF(Math.cos(getFD(tetax))*Math.cos(getFD(tetay)));

    }

    /*
     * Matrix-Vector multiplication
     * This function is specialized to handle only 3x3 matrix-product
     */
    public void matrixProduct(float[][] rotation, float[] vector, float[] answer){

        answer[0]=rotation[0][0]*vector[0]+rotation[0][1]*vector[1]+rotation[0][2]*vector[2];
        answer[1]=rotation[1][0]*vector[0]+rotation[1][1]*vector[1]+rotation[1][2]*vector[2];
        answer[2]=rotation[2][0]*vector[0]+rotation[2][1]*vector[1]+rotation[2][2]*vector[2];

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    ////                              DEAD RECKONING                                             ////
    ////                                                                                         ////
    ////                                                                                         ////
    /////////////////////////////////////////////////////////////////////////////////////////////////




    //  For updating the GUI
    private class GUI_Updater extends AsyncTask<Float, Void, String[]>{
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */

        @Override
        protected String[] doInBackground(Float... params) {

            //update kinematics
            String[] answer=new String[4+2*SAMPLE_LENGTH];

            float timeStamp[]=new float[SAMPLE_LENGTH];     //timestamp

            float Ax[]=new float[SAMPLE_LENGTH];           //X-Acceleration
            float Ay[]=new float[SAMPLE_LENGTH];           //Y-Acceleration
            float Az[]=new float[SAMPLE_LENGTH];           //Z-Acceleration

            float tetax[]=new float[SAMPLE_LENGTH];       //X-Angle
            float tetay[]=new float[SAMPLE_LENGTH];       //Y-Angle
            float tetaz[]=new float[SAMPLE_LENGTH];       //Z-Angle

            float racc[]=new float[3];
            float acc[]=new float[3];
            float speedX;
            float speedY;
            float speedZ;
            float posXK;
            float posYK;
            float posZK;
            float DX;
            float DY;
            float DZ;
            float dist=0.0f;
            float isStep=0;
            String event[];
            //feature extraction
            for(int i=CURRENT_INDEX;i<CURRENT_INDEX+SAMPLE_LENGTH;i++){
                event=sensorData.get(i);
                timeStamp[i-CURRENT_INDEX]=Float.valueOf(event[0]);
                Ax[i-CURRENT_INDEX]=Float.valueOf(event[1]);
                Ay[i-CURRENT_INDEX]=Float.valueOf(event[2]);
                Az[i-CURRENT_INDEX]=Float.valueOf(event[3]);
                tetax[i-CURRENT_INDEX]=Float.valueOf(event[4]);
                tetay[i-CURRENT_INDEX]=Float.valueOf(event[5]);
                tetaz[i-CURRENT_INDEX]=Float.valueOf(event[6]);
                //  sensorData.remove(CURRENT_INDEX); //free memory
            }
            //relative timestamps in seconds
            nanoTosecond(timeStamp);

            //zero shift
            zeroShift(Ax,ERRORX);
            zeroShift(Ay,ERRORY);
            zeroShift(Az,ERRORZ);

            //explicit removal of outliers
            removeOutliers(Ax,THRESHOLD,timeStamp);
            removeOutliers(Ay,THRESHOLD,timeStamp);
            removeOutliers(Az,THRESHOLD,timeStamp);

            //squarewise signal reconstruction
            fullSquarewiseSignalReconstruction(Ax,PULSELENGTH,LOWLIMITX, ZERO_RATE);
            fullSquarewiseSignalReconstruction(Ay,PULSELENGTH,LOWLIMITY, ZERO_RATE);
            fullSquarewiseSignalReconstruction(Az,PULSELENGTH,LOWLIMITZ, ZERO_RATE);

            //signal smoothing
            iir(Ax,SALPHA);
            iir(Ay,SALPHA);
            iir(Az,SALPHA);

            //zero velocity compensation
            isStep=Math.max(
                    zeroVelocityCompensation(Ax,BACKWARD_PULSE_CENTROID,BACKWARD_PULSE_ERROR,FORWARD_PULSE_CENTROID, FORWARD_PULSE_ERROR),
                    Math.max(
                            zeroVelocityCompensation(Ay,BACKWARD_PULSE_CENTROID,BACKWARD_PULSE_ERROR,FORWARD_PULSE_CENTROID, FORWARD_PULSE_ERROR),
                            zeroVelocityCompensation(Az,BACKWARD_PULSE_CENTROID,BACKWARD_PULSE_ERROR,FORWARD_PULSE_CENTROID, FORWARD_PULSE_ERROR))
            );


                //update Step
                STEP += isStep;

           /* //Angle smoothing
            iir(tetax,ALPHA);
            iir(tetay,ALPHA);
            iir(tetaz,ALPHA);
           */
                //Rotation Matrix computation
                for (int i = 0; i < tetax.length; i++) {
                    getRotationMatrix(MAINROTATION, tetax[i], tetay[i], tetaz[i]);
                    acc[0] = Ax[i];
                    acc[1] = Ay[i];
                    acc[2] = Az[i];
                    matrixProduct(MAINROTATION, acc, racc);
                    Ax[i] = racc[0];
                    Ay[i] = racc[1];
                    Az[i] = racc[2];
                }
                //speed & displacement initialization
                //Since we are working in 2D we disabled all computation around the third axis Y. It increases the response time
                speedX = SPEEDX;
                // speedY=SPEEDY;
                speedZ = SPEEDZ;
                posXK = POSITIONX;
                // posYK=POSITIONY;
                posZK = POSITIONZ;
                answer[4] = Float.toString(posXK);
                answer[5] = Float.toString(posZK);
                for (int i = 0; i < SAMPLE_LENGTH - 1; i++) {
                   if (isPaused(Ax, LOWLIMITSPEED, i, ZERO_RADIUS))
                        SPEEDX = 0.0f;
                    else
                        SPEEDX = speedX + (timeStamp[i + 1] - timeStamp[i]) * (Ax[i]);
                    //   if(isPaused(Ay,LOWLIMITSPEED,i,ZERO_RADIUS))
                    //       SPEEDY=0.0f;
                    //   else
                    //         SPEEDY=speedY+(timeStamp[i+1]-timeStamp[i])*(Ay[i]);

                  if (isPaused(Az, LOWLIMITSPEED, i, ZERO_RADIUS))
                        SPEEDZ = 0.0f;
                    else
                        SPEEDZ = speedZ + (timeStamp[i + 1] - timeStamp[i]) * (Az[i]);

                    DX = 0.5f * Ax[i] * (timeStamp[i + 1] - timeStamp[i]) * (timeStamp[i + 1] - timeStamp[i]) + (timeStamp[i + 1] - timeStamp[i]) * (speedX);
                    //   DY=0.5f*Ay[i]*(timeStamp[i+1]-timeStamp[i])*(timeStamp[i+1]-timeStamp[i])+(timeStamp[i+1]-timeStamp[i])*(speedY);
                    DZ = 0.5f * Az[i] * (timeStamp[i + 1] - timeStamp[i]) * (timeStamp[i + 1] - timeStamp[i]) + (timeStamp[i + 1] - timeStamp[i]) * (speedZ);

                    POSITIONX = posXK + DX;
                    // POSITIONY=posYK+DY;
                    POSITIONZ = posZK - DZ;//conversion to app's coordinate system


                    dist += Float.valueOf(String.valueOf((Math.sqrt(Double.valueOf(String.valueOf(DX * DX + DZ * DZ))))));
                    //actualize
                    speedX = SPEEDX;
                    //  speedY=SPEEDY;
                    speedZ = SPEEDZ;
                    posXK = POSITIONX;
                    //   posYK=POSITIONY;
                    posZK = POSITIONZ;
                    answer[(i + 1) * 2 + 4] = Float.toString(posXK);
                    answer[(i + 1) * 2 + 5] = Float.toString(posZK);

                }

            //2D speed
            SPEED=Float.valueOf(String.valueOf((Math.sqrt(Double.valueOf(String.valueOf(SPEEDX*SPEEDX+SPEEDZ*SPEEDZ))))));
            if(dist>SENSIBILITY)
                TIMESPENT=0.0f;
            else
                TIMESPENT+=timeStamp[SAMPLE_LENGTH-1];
            //distance
            DISTANCE+=dist;
            BigDecimal distC = new BigDecimal(Float.toString(DISTANCE));
            distC = distC.setScale(2, BigDecimal.ROUND_HALF_UP);

            BigDecimal sp= new BigDecimal(Float.toString(SPEED));
            sp = sp.setScale(2, BigDecimal.ROUND_HALF_UP);

            BigDecimal posx = new BigDecimal(Float.toString(POSITIONX));
            posx = posx.setScale(2, BigDecimal.ROUND_HALF_UP);

            BigDecimal posy = new BigDecimal(Float.toString(POSITIONY));
            posy = posy.setScale(2, BigDecimal.ROUND_HALF_UP);

            BigDecimal posz = new BigDecimal(Float.toString(POSITIONZ));
            posz = posz.setScale(2, BigDecimal.ROUND_HALF_UP);

            BigDecimal time = new BigDecimal(Float.toString(TIMESPENT));
            time = time.setScale(2, BigDecimal.ROUND_HALF_UP);
            //update current index
            //CURRENT_INDEX+=(SAMPLE_LENGTH);
            answer[0]="Distance:  "+distC.floatValue()+"m\n"+
                    "Speed:     "+sp.floatValue()+"m/s\n"+

                    "Position:  ("+posx.floatValue()+","+posz.floatValue()+")\n"+
                    "Time Spent: "+time.floatValue()+"s";
            answer[1]=String.valueOf(STEP);
            answer[2]=String.valueOf((new BigDecimal(Float.toString(getBatteryLevel()))).setScale(1, BigDecimal.ROUND_HALF_UP));
            answer[3]=HEAD_MESSAGE;
            return answer;

        }
        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void  onPostExecute(String[] answer ) {
            card.setText(answer[0]);
            card.setFootnote("Step count: "+answer[1]);
            card.setTimestamp("Power: "+answer[2]+"%");
            //update user path
            //i=0

            for(int i=SAMPLE_LENGTH-2
                ;i<SAMPLE_LENGTH-1;i++) {
                updatePath(Float.parseFloat(answer[(i+1)*2+4]),Float.parseFloat(answer[(i+1)*2+5]));
            }

            mView=card.getView();
            mView.setScaleX(0.95f);
            mView.setScaleY(0.95f);
            mView.setBackgroundColor(Color.argb(208,150,150,150));
            setContentView(mView);
            FINISH=true;
            FINISH1=true;
        }



    }

}
