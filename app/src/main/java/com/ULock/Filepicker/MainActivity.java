package com.ULock.Filepicker;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ULock.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.R.attr.content;
import static android.R.attr.path;
import static android.R.attr.textAllCaps;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {
    File file;
    private int progress = 0;
    private final int pBarMax = 60;
    private int MAX_ATTACHMENT_COUNT = 1000;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_vault);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        addThemToView();
        setAdapter();

    }
//method to pick photo
    public void pickPhotoClicked(View view) {

        MainActivityPermissionsDispatcher.onPickPhotoWithCheck(this);
    }

//method to pick document(currently disabled)
    public void pickDocClicked(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));

                    new MainActivity.CopyPics(photoPaths) {
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                       imageAdapter.getFromSdcard( imageAdapter.getPath());
                                    }
                            }, 1000);

                        }
                    }.execute();

                }
        break;
        }
    }

// To display the images andf folder in grid view.
    private void addThemToView() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        if (recyclerView != null) {
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

        }
    }

    private void setAdapter() {
        imageAdapter = new ImageAdapter(this);
        recyclerView.setAdapter(imageAdapter);
    }
// Method to pic photo from gallery and click picture from camera
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickPhoto() {
        int maxCount = MAX_ATTACHMENT_COUNT - docPaths.size() - photoPaths.size();
        if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT)
            Toast.makeText(this, getResources().getString(R.string.cannot_select_more_than) + MAX_ATTACHMENT_COUNT + getResources().getString(R.string.items), Toast.LENGTH_SHORT).show();
        else
            FilePickerBuilder.getInstance().setMaxCount(maxCount)

                    .enableCameraSupport(true)
                    .showGifs(true)
                    .showFolderView(true)
                    .pickPhoto(this);
    }

// (Not in use currently)For picing up file sif the user says
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickDoc() {

        int maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size() - docPaths.size();
        if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT)
            Toast.makeText(this, getResources().getString(R.string.cannot_select_more_than) + MAX_ATTACHMENT_COUNT + getResources().getString(R.string.items), Toast.LENGTH_SHORT).show();
        else
            FilePickerBuilder.getInstance().setMaxCount(maxCount)
                    .setSelectedFiles(docPaths)
                    .pickFile(this);
    }

//method to delete the pic
    private class deletePic extends AsyncTask<Void, Void, Void> {

        ArrayList<String> photoPaths1;
        public deletePic( ArrayList<String> photoPaths1){
            this.photoPaths1=photoPaths1;
        }

        @Override
        protected Void doInBackground(Void... params) {
                for (int i = 0; i < photoPaths1.size(); i++) {
                    File ff = new File(photoPaths1.get(i));
                    deleteRecursive(ff);
                }
            return null;
        }
    }
    //Method to delete file or folder even contents of folders.
    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

//method to refresh folder in background
    private class folderRefresh extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }
    }
//Method to  copy picked file from gallery
    private class CopyPics extends AsyncTask<Void, Void, Void> {
        ArrayList<String> photoPaths1;

        public CopyPics(ArrayList<String> photoPaths) {
            this.photoPaths1 = photoPaths;
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < photoPaths.size(); i++) {
                copyFileOrDirectory(photoPaths1.get(i));
                file = new File(photoPaths1.get(i));
                String[] projection = {MediaStore.Images.Media._ID};

                // Match on the file path
                String selection = MediaStore.Images.Media.DATA + " = ?";
                String[] selectionArgs = new String[]{file.getAbsolutePath()};

                // Query for the ID of the media matching the file path
                Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getContentResolver();
                Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
                if (c.moveToFirst()) {
                    // We found the ID. Deleting the item via the content provider will also remove the file
                    long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    contentResolver.delete(deleteUri, null, null);
                } else {

                }
                c.close();


            }
            return null;
        }
    }
// Method to copy selected file to gallery
    private class CopyToGallery extends AsyncTask<Void, Void, Void> {
        ArrayList<String> photoPaths1;

        public CopyToGallery(ArrayList<String> photoPaths) {
            this.photoPaths1 = photoPaths;
        }

        @Override
        protected Void doInBackground(Void... params) {
                for (int i = 0; i < photoPaths1.size(); i++) {
                    copyFileToGallery(photoPaths1.get(i));
                    File ff=new File(photoPaths1.get(i));
                    if (ff.delete()) {
                    } else {
                    }
                }
            return null;
        }
    }


//method to copy and move file form directory to the hidden folder

    public  void copyFileOrDirectory(String srcDir) {

        try {
            folder = new File(Environment.getExternalStorageDirectory() + "/.aa"+imageAdapter.getPath());

            if (!folder.exists()) {
                folder.mkdir();
            }


            File src = new File(srcDir);

            if (src.exists()) {
                File dst = new File(folder, src.getName());
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//Method to copy file
    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());

        } finally {
            if (source != null) {
                source.close();
            }

            if (destination != null) {
                destination.close();
            }
        }

    }
    //Method to copy file
    public void copyFile1(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());

        } finally {
            if (source != null) {
                source.close();
            }

            if (destination != null) {
                destination.close();
            }
        }
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA,destFile.getPath());

        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

    }
    // Method to copy file to gallery
    public  void copyFileToGallery(String srcDir) {

        try {
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!folder.exists()) {
                folder.mkdir();
            }


            File src = new File(srcDir);
            if (src.exists()) {
                File dst = new File(folder, src.getName());
                copyFile1(src, dst);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// Permission to access gallery files
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

//Toolbar icon button functionality
MenuItem newFolder,lockItem,deleteitem;
    boolean clickcheck=false;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vault_menu, menu);

        newFolder=menu.getItem(0);
        lockItem=menu.getItem(1);
        deleteitem=menu.getItem(2);
        showMenuItems();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
//Export function to export images from gallery vault to gallery
            case R.id.action_export:

                if(clickcheck){
                     ArrayList<HashMap<String, String>> list2 = imageAdapter.getF();
                    boolean dialogueCheck=false;
                    for (int i = 0; i < list2.size(); i++) {
                        if (list2.get(i).get("check").equals("1")) {
                            dialogueCheck=true;
                            break;
                        }
                        else{
                            dialogueCheck=false;
                        }
                    }
                    if(dialogueCheck){
                        tranferPics();
                    }
                    else{

                        Toast.makeText(this, getResources().getString(R.string.please_select_atleast_one_item_to_move), Toast.LENGTH_SHORT).show();
                    }}
                else{
                    clickcheck=true;
                    imageAdapter.setDeletecheck(false);
                    imageAdapter.setCheckvisibily(true);
                    imageAdapter.notifyDataSetChanged() ;

                    lockItem.setVisible(true);
                    newFolder.setVisible(false);
                    deleteitem.setVisible(false);
                    fab.setVisibility(View.GONE);

                }

                return true;
//Delete function to export images from gallery vault to gallery

            case R.id.action_delete:
                if(clickcheck){
                    ArrayList<HashMap<String, String>> list2 = imageAdapter.getF();
                    boolean dialogueCheck=false;
                    for (int i = 0; i < list2.size(); i++) {
                        if (list2.get(i).get("check").equals("1")) {
                        dialogueCheck=true;

                            break;
                        }
                        else{
                            dialogueCheck=false;
                        }
                    }
                    if(dialogueCheck){
                        deleteDialog();
                    }
                    else{

                        Toast.makeText(this, getResources().getString(R.string.please_select_atleast_one_item_to_delete), Toast.LENGTH_SHORT).show();
                }}
                else{
                    imageAdapter.setDeletecheck(true);
                    imageAdapter.setCheckvisibily(true);
                    imageAdapter.notifyDataSetChanged() ;
                    clickcheck=true;
                    imageAdapter.setDeletecheck(true);
                    deleteitem.setVisible(true);
                    lockItem.setVisible(false);
                    newFolder.setVisible(false);

                    fab.setVisibility(View.GONE);
                }
                return true;
//back arrow function
            case android.R.id.home:
                onBackPressed();
                imageAdapter.setCheckvisibily(false);
                fab.setVisibility(View.VISIBLE);
                return true;

// create new folder
            case R.id.action_newFolder:
                folderDialog();
                imageAdapter.setCheckvisibily(false);
                fab.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

        File folder;
    //method to create folder
    public void createfolder(){
        folder = new File(Environment.getExternalStorageDirectory() + "/.aa");
        boolean success = true;

        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {

            // Do something on success
            final String textname=input.getText().toString();
            if( new File(folder, m_Text = textname).exists()){
                Toast.makeText(MainActivity.this,getResources().getString(R.string.this_folder_already_exists), Toast.LENGTH_SHORT).show();

            }
            else{
                new folderRefresh(){
                    @Override
                    protected Void doInBackground(Void... params) {

                        new File(folder, m_Text = textname).mkdir();
                        return super.doInBackground(params);
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        imageAdapter.getFromSdcard(imageAdapter.getPath());
                        super.onPostExecute(aVoid);
                    }
                }.execute();
            }

        } else {
            // Do something else on failure
            Toast.makeText(MainActivity.this,getResources().getString(R.string.folder_not_created), Toast.LENGTH_SHORT).show();
        }
    }

    String m_Text = "";
     EditText input;
    //Dialog to input the name of the new folder
    private void editDialog() {

    AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);
//    builder.setTitle("Folder name");

    View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.edittext_dialog, (ViewGroup) findViewById(android.R.id.content), false);
    // Set up the input
     input = (EditText) viewInflated.findViewById(R.id.input);
    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
    builder.setView(viewInflated);

    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    });
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               createfolder();
            }
        });
    builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            Toast.makeText(MainActivity.this, getResources().getString(R.string.folder_not_created), Toast.LENGTH_SHORT).show();
        }
    });

    builder.show();
}


    AlertDialog alert3;
    // Dialog to ask the user if the user wants to create a new album folder or not.
    private void folderDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);

        builder.setMessage(getResources().getString(R.string.do_you_want_to_create_a_new_album_folder))
                .setCancelable(false)
                .setPositiveButton( getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    public void onClick(final DialogInterface dialog, int id) {
                        editDialog();
                    }
                })
                .setNegativeButton( getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showMenuItems();
                        imageAdapter.setCheckvisibily(false);
                        imageAdapter.refreshList();
                        clickcheck=false;
                        showMenuItems();
                        alert3.dismiss();
                        fab.setVisibility(View.VISIBLE);
                    }
                });

        //Creating dialog box
        alert3 = builder.create();
        alert3.setTitle(getResources().getString(R.string.album_folder));
        alert3.setIcon(R.drawable.ic_create_new_folder_white_24dp);
        alert3.show();
    }

AlertDialog alert2;
    //Dialog to confirm the user if he wants to delete the image
    private void deleteDialog(){
AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);

        builder.setMessage(getResources().getString(R.string.the_picture_will_be_completely_deleted_and_cannot_be_recovered))
                .setCancelable(false)
            .setPositiveButton( getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
            {
        public void onClick(final DialogInterface dialog, int id) {

            showMenuItems();
            ArrayList<String> list = new ArrayList<>();
            if (imageAdapter != null) {
                ArrayList<HashMap<String, String>> list2 = imageAdapter.getF();
                for (int i = 0; i < list2.size(); i++) {
                    if (list2.get(i).get("check").equals("1")) {
                        list.add(list2.get(i).get("image"));
                    }
                }
                new deletePic(list){
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        alert2.dismiss();
                        clickcheck=false;
                        imageAdapter.setDeletecheck(false);
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                imageAdapter.getFromSdcard(imageAdapter.getPath());
                            }
                        }, 1000);

                    }
                }.execute();
                fab.setVisibility(View.VISIBLE);
                imageAdapter.setCheckvisibily(false);
                imageAdapter.refreshList();
                clickcheck=false;
            }
        }
    })
            .setNegativeButton( getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            showMenuItems();
        imageAdapter.setCheckvisibily(false);
        imageAdapter.refreshList();
            clickcheck=false;
            alert2.dismiss();
            fab.setVisibility(View.VISIBLE);
        }
    });

    //Creating dialog box
    alert2 = builder.create();
        alert2.setTitle( getResources().getString(R.string.delete));
        alert2.setIcon(R.drawable.ic_delete_white_24dp);
        alert2.show();
}

    AlertDialog alert1;
    //Dialog to confirm the user if he wants to move images from the vault. to gallery
    private void tranferPics(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);
        builder.setMessage( getResources().getString(R.string.are_you_sure_you_want_to_move_it_out_of_vault))
                .setCancelable(false)

                .setPositiveButton( getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id) {
                        showMenuItems();
                        ArrayList<String> list = new ArrayList<>();
                        if (imageAdapter != null) {
                            ArrayList<HashMap<String, String>> list2 = imageAdapter.getF();
                            for (int i = 0; i < list2.size(); i++) {
                                if (list2.get(i).get("check").equals("1")) {
                                    list.add(list2.get(i).get("image"));
                                }
                            }

                            new CopyToGallery(list){
                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);
                                    alert1.dismiss();
                                    clickcheck=false;
                                    Handler handler=new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageAdapter.getFromSdcard(imageAdapter.getPath());

                                        }
                                    }, 1000);
                                }
                            }.execute();
                            fab.setVisibility(View.VISIBLE);
                            imageAdapter.setCheckvisibily(false);
                            imageAdapter.refreshList();
                            clickcheck=false;
                        }
                    }
                })
                .setNegativeButton( getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        imageAdapter.setCheckvisibily(false);
                        imageAdapter.refreshList();
                        clickcheck=false;
                        showMenuItems();
                        dialog.dismiss();
                        fab.setVisibility(View.VISIBLE);
                    }
                });


        //Creating dialog box
        alert1 = builder.create();
        alert1.setTitle(getResources().getString(R.string.move_it));
        alert1.setIcon(R.drawable.ic_lock_open_white_24dp);
        alert1.show();
    }
    //method for back arrow to go back
    @Override
    public void onBackPressed() {
        if(clickcheck==true){
            imageAdapter.refreshList();
            imageAdapter.setCheckvisibily(false);
            clickcheck=false;
            imageAdapter.setDeletecheck(false);
            final ProgressBar pBar = (ProgressBar) findViewById(R.id.progessbar);
            pBar.setMax(pBarMax);
            final Thread pBarThread = new Thread() {
                @Override
                public void run() {
                    try {
                        while(progress<=pBarMax) {
                            pBar.setProgress(progress);
                            sleep(1000);
                            ++progress;
                        }
                    }
                    catch(InterruptedException e) {
                    }
                }
            };

            pBarThread.start();
            showMenuItems();
        }
        else{if(imageAdapter.getPath().isEmpty()){  super.onBackPressed();}else{
            imageAdapter.setPath("");
            showMenuItems();
            imageAdapter.getFromSdcard(imageAdapter.getPath());
          }


          }

    }

    private void showMenuItems(){
        if(imageAdapter.getItemCount()==0){

            lockItem.setVisible(false);
            deleteitem.setVisible(false);
        }
        else
        {

            lockItem.setVisible(true);
            deleteitem.setVisible(true);
        }
        if(imageAdapter.getPath().isEmpty()){
         newFolder.setVisible(true);
         }
         else {
            newFolder.setVisible(false);
         }
    }

}