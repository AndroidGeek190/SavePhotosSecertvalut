package com.ULock;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ULock.Prefhelper.Prefshelper;
import com.ULock.util.TrialCheck;
import com.ULock.view.ContentView;
import com.ULock.view.MainView;
import com.ULock.view.SplashView;

import java.io.File;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
// variable declaration
  private static final String TAG = "MainActivity";
  private static final boolean DO_XML = false;
  public boolean startingActivity = false;

  int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1000;

  private boolean sentToSettings = false;
    //storage and permission declaration
  private SharedPreferences permissionStatus;
  private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
  private static final int REQUEST_PERMISSION_SETTING = 101;
  String[] permissionsRequired = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.WRITE_EXTERNAL_STORAGE};

  private ViewGroup mMainView;
  private SplashView mSplashView;
 
  private Handler mHandler = new Handler();
  private View mContentView;
  Prefshelper prefshelper;
  String serv;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
      permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
      if (android.support.v4.app.ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED
              && android.support.v4.app.ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED){
          proceedAfterPermission();
      }
      else{Permission();}

  }

//to retrieve all the apps for home screen
  private void startLoadingData(){
    // finish "loading data" in a random time between 1 and 3 seconds
    Random random = new Random();
    mHandler.postDelayed(new Runnable(){
      @Override
      public void run(){
        onLoadingDataEnded();
      }
    }, 1000 + random.nextInt(10));

  }
  
  private void onLoadingDataEnded(){
    Context context = getApplicationContext();
    // now that our data is loaded we can initialize the content view
    mContentView = new ContentView(context);
    // add the content view to the background
    mMainView.addView(mContentView, 0);


    // start the splash animation
    mSplashView.splashAndDisappear(new SplashView.ISplashListener(){
      @Override
      public void onStart(){
        // log the animation start event
        if(BuildConfig.DEBUG){

        }
        
      }
      
      @Override
      public void onUpdate(float completionFraction){
        // log animation update events
        if(BuildConfig.DEBUG){

        }
      }
      
      @Override
      public void onEnd(){
        // log the animation end event
        if(BuildConfig.DEBUG){

        }
        // free the view so that it turns into garbage
        mSplashView = null;
        if(!DO_XML){
          // if inflating from code we will also have to free the reference in MainView as well
          // otherwise we will leak the View, this could be done better but so far it will suffice
          ((MainView) mMainView).unsetSplashView();
        }

      }
    });
  }

  //to end up all other task when
  @Override
  protected void onUserLeaveHint()
  {
    if(startingActivity)
    {
      // Reset boolean for next time
      startingActivity = false;
    }
    else
    {
      // User is exiting to another application, do what you want here

      finish();
    }
  }

  @Override
  public void startActivity(Intent intent)
  {
    startingActivity = true;
    super.startActivity(intent);
  }
//to start another activity when ever check is complete
  @Override
  public void startActivityForResult(Intent intent, int requestCode)
  {
    startingActivity = true;
    super.startActivityForResult(intent, requestCode);
  }
  //creating a hidden folder to store the current time and date ,to keep a check to the 14 days trail period.
    private void proceedAfterPermission() {
        prefshelper=new Prefshelper(this);

        try{
          File  folder = new File(Environment.getExternalStorageDirectory() + "/.aaa");
            boolean success = true;

            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (success) {
            File data = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/.aaa" + File.separator);
            File file = new File(data, "ulockconfiga.txt");
            if(!file.exists())
            {
                file.createNewFile();
                TrialCheck tc=new TrialCheck();
                tc.writeFile(new Date().getTime());
            }
        }}
        catch(Exception e){
            e.printStackTrace();
        }

//condition to check and provide trial period
        try{
            TrialCheck tc=new TrialCheck();
            long apptime=tc.readFile();
            long todaytime=new Date().getTime();
            long date1 = tc.findDaysDiff(apptime,todaytime);
            int sum= (int)date1;
            int sum1 = 14-sum;
            if((int)sum1<=14 &&  sum1 >=0 ){
                if(!prefshelper.getIInAppPurchased().equalsIgnoreCase("1")) {
                    prefshelper.isIInAppPurchased("1");//fot test purpose only
                    prefshelper.storePackageType("unlimited");//fot test purpose only
                }
            }
            else{
                prefshelper.isIInAppPurchased("");//fot test purpose only
                prefshelper.storePackageType("");//fot test purpose only
            }
        }catch (Exception e){}

       
        serv=getIntent().getStringExtra("data");

        // change the DO_XML variable to switch between code and xml
    if(DO_XML){
      // inflate the view from XML and then get a reference to it
      setContentView(R.layout.main_activity);
      mMainView = (ViewGroup) findViewById(R.id.main_view);
      mSplashView = (SplashView) findViewById(R.id.splash_view);


    } else {
      // create the main view and it will handle the rest
      mMainView = new MainView(getApplicationContext());
      mSplashView = ((MainView) mMainView).getSplashView();
      setContentView(mMainView);

    }

    // pretend like we are loading data
    startLoadingData();


    }

//interface is for receiving the results for permission requests.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                proceedAfterPermission();
            } else if (android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[0])
                    || android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[1])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyAlertDialogStyle);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        android.support.v4.app.ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }


//to check if the user has given permission to the app manually
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (android.support.v4.app.ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }


// to request for permission for external storage and camera
    public void Permission() {

        if (android.support.v4.app.ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || android.support.v4.app.ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED){
            if (android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[0])
                    || android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[1])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyAlertDialogStyle);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        android.support.v4.app.ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyAlertDialogStyle);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant  Camera and Storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                android.support.v4.app.ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }


            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else

        {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }

    }

}