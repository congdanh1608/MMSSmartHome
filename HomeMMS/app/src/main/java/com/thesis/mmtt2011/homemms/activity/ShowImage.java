package com.thesis.mmtt2011.homemms.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.thesis.mmtt2011.homemms.R;

import java.io.File;

public class ShowImage extends AppCompatActivity {

    public static String IMAGE_PATH = "IMAGE_PATH";
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        ImageView imageView = (ImageView) findViewById(R.id.img_attach);
        Intent intent = getIntent();
        String path = intent.getStringExtra(IMAGE_PATH);
        showImage(path);

    }

    private void showImage(String imagePath) {
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(bmp);
        }
    }
}
