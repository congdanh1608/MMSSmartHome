package com.thesis.mmtt2011.homemms.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.activity.MainActivity;
import com.thesis.mmtt2011.homemms.activity.ShowImage;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xpie on 12/22/2015.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    public static final String IMAGE_PATH = "IMAGE_PATH";

    List<String> imagesContent;
    static Context context;

    public ImageAdapter(Context context, ArrayList<String> images){
        this.context = context;
        imagesContent = images;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view_item, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(v);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.bindImage(imagesContent.get(position));
    }

    @Override
    public int getItemCount() {
        return imagesContent.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView imageView;
        String imgPath;

        public ImageViewHolder(View imgView) {
            super(imgView);
            imageView = (ImageView)imgView.findViewById(R.id.imageview_content);
            imgView.setOnClickListener(this);
        }

        public void bindImage(String imagePath) {
            this.imgPath = imagePath;
            File imgFile = new File(imagePath);
            if(imgFile.exists()) {
                Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(bmp);
            }
        }

        @Override
        public void onClick(View v) {
            //show image full content
            Intent intent = new Intent(context, ShowImage.class);
            intent.putExtra(IMAGE_PATH, imgPath);
            context.startActivity(intent);
        }
    }
}
