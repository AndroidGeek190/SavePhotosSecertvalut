package com.ULock.Filepicker;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ULock.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import droidninja.filepicker.views.SmoothCheckBox;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.FileViewHolder> {
    private  Context context;
    private int imageSize;
    boolean checkvisibily=false;
    boolean deletecheck = false;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        if(path!=null){return path;}
        else{return "";}

    }

    String path="";


    public ArrayList<HashMap<String,String >> f ;
    public ArrayList<HashMap<String,String >> imagesList ;// list of file paths
    File[] listFile;

    public ImageAdapter(Context context)
    {
        this.context = context;
        f = new ArrayList<HashMap<String,String >>();
        listFile=null;
        getFromSdcard("");
        setColumnNumber(context,3);
    }
//to get the column number(position) of the image
    private void setColumnNumber(Context context, int columnNum) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        imageSize = widthPixels / columnNum;
    }

    @Override
    public ImageAdapter.FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item_layout, parent, false);

        return new ImageAdapter.FileViewHolder(itemView);
    }
// To display image at there proper position
    @Override
    public void onBindViewHolder(final ImageAdapter.FileViewHolder holder, final int position) {
        String path = f.get(position).get("image");

        if(new File(path).isDirectory()){
            holder.imageView.setImageResource(R.drawable.folder_icon_24dp);
            holder.foldername.setVisibility(View.VISIBLE);
            holder.foldername.setText(new File(path).getName());
            if(deletecheck){
                if(checkvisibily){

                    holder.checkBox.setVisibility(View.VISIBLE);
                }
                else{

                    holder.checkBox.setVisibility(View.GONE);

                }
            }
            else{
                holder.checkBox.setVisibility(View.GONE);
            }

        } else {

             Glide.with(context).load(new File(path))
                .centerCrop()
                    .dontAnimate()
                    .thumbnail(0.5f)
                    .override(imageSize, imageSize)
                    .placeholder(droidninja.filepicker.R.drawable.image_placeholder)
                    .into(holder.imageView);
            holder.foldername.setVisibility(View.GONE);
            if(checkvisibily){

                holder.checkBox.setVisibility(View.VISIBLE);
            }
            else{

                holder.checkBox.setVisibility(View.GONE);

            }

        }


//setting the check box functionality to check and unchecked

        if(f.get(position).get("check").equals("1")){
            holder.checkBox.setChecked(true);
        }

        else{  holder.checkBox.setChecked(false);}
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(checkvisibily){
                    String checked="0";
                    if(holder.checkBox.isChecked())
                    {
                        holder.checkBox.setChecked(false);
                        checked="0";
                    }
                    else{
                        holder.checkBox.setChecked(true);
                        checked="1";
                    }
                    HashMap<String,String> map=new HashMap<>();
                    map.put("image",f.get(position).get("image"));
                    map.put("check",checked);
                    f.set(position,map);

                }
                else{
                    String path = f.get(position).get("image");
                    if(new File(path).isDirectory()){
                        getFromSdcard(new File(path).getName());
                    } else {
                        Intent intent = new Intent(context,ImageViewPager.class);
                        intent.putExtra("pos",position);
                        intent.putExtra("arraylist", getImagesForPager());
                        context. startActivity(intent);
                    }
            }
            }
        });
    }
private ArrayList<HashMap<String,String >> getImagesForPager(){
imagesList=new ArrayList<HashMap<String,String >>();
    for(int i=0;i<f.size();i++) {
        if(!new File(f.get(i).get("image")+path).isDirectory()){
        imagesList.add(f.get(i));
        }
    }
    return imagesList;
}
    @Override
    public int getItemCount() {
        return f.size();
    }



    public static class FileViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_photo)
        ImageView imageView;
        SmoothCheckBox checkBox;
        TextView foldername;


        public FileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            checkBox = (SmoothCheckBox) itemView.findViewById(R.id.checkbox);
            foldername = (TextView)itemView.findViewById(R.id.foldername);
            checkBox.setEnabled(false);
            foldername.setVisibility(View.GONE);
        }
    }
//To set path for vault folder
File folder;
    public void getFromSdcard(String foldername)
    {
        ((AppCompatActivity)context).invalidateOptionsMenu();
        String foldera = "/";

        if(foldername==null||foldername.isEmpty()){
        foldera="";
        }
        else{
            foldera="/"+foldername;
        }

        path= foldera;
        folder = new File(Environment.getExternalStorageDirectory(), ".aa"+foldera);
        if (folder.isDirectory())
        {
            listFile=null;
            listFile = folder.listFiles();

            f.clear();
            for (int i = 0; i < listFile.length; i++)
            {
                if(listFile[i].exists()){
                    HashMap<String,String> map=new HashMap<>();
                    map.put("image",listFile[i].getAbsolutePath());
                    map.put("check","0");
                    f.add(map);
                }

            }
            notifyDataSetChanged();
        }
    }
//to refresh the vault list after folder or image addition.
    public void refreshList(){
        for(int i=0;i<f.size();i++){
            HashMap<String,String> map=new HashMap<>();
            map.put("image",f.get(i).get("image"));
            map.put("check","0");
            f.set(i,map);
        }
        notifyDataSetChanged();
    }
    public void setCheckvisibily(boolean checkvisibily) {
        this.checkvisibily = checkvisibily;
    }
    public void setDeletecheck(boolean deletecheck) {
        this.deletecheck = deletecheck;
    }
    public ArrayList<HashMap<String, String>> getF() {
        return f;
    }
}
