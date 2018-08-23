package com.ULock.Filepicker;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.ULock.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageViewPager extends AppCompatActivity {
	// Declare Variable
	int position;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the view from view_pager.xml
		setContentView(R.layout.vault_view_pager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		getSupportActionBar().setDisplayShowTitleEnabled(false);


		Intent intent1 = getIntent();
		ArrayList<HashMap<String, String>> arl = (ArrayList<HashMap<String, String>>) intent1.getSerializableExtra("arraylist");
		System.out.println("...serialized data.."+arl);
		position = intent1.getExtras().getInt("pos");

		ImageAdapter imageAdapter = new ImageAdapter(this);
		List<ImageView> images = new ArrayList<ImageView>();

		// Retrieve all the images
        for (int i = 0; i < arl.size(); i++) {
			ImageView imageView = new ImageView(this);
			Glide.with(this).load(new File(arl.get(i).get("image")))
//					.centerCrop()
					.dontAnimate()
					.thumbnail(0.5f)
					.placeholder(droidninja.filepicker.R.drawable.image_placeholder)
					.into(imageView);

			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			images.add(imageView);
		}

		// Set the images into ViewPager
		ImagePagerAdapter pageradapter = new ImagePagerAdapter(images);
		ViewPager viewpager = (ViewPager) findViewById(R.id.pager1);
		viewpager.setAdapter(pageradapter);
		// Show images following the position
		viewpager.setCurrentItem(position);
	}
	//method to set functionality to toolbar items
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case android.R.id.home:
				onBackPressed();
				return true;
//            case R.id.action_share:
//
//                btnChooseShareClick();
//                imageAdapter.setCheckvisibily(true);
//                imageAdapter.notifyDataSetChanged() ;
//                return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}

}