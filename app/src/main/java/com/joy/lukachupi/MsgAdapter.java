package com.joy.lukachupi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MsgAdapter extends FirestoreRecyclerAdapter<MsgModel, MsgAdapter.MsgHolder>{

    public MsgAdapter(@NonNull FirestoreRecyclerOptions<MsgModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MsgHolder holder, int position, @NonNull MsgModel model) {
        holder.textData.setText(model.getData());
        Date date = model.getTime().toDate();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
        holder.textTime.setText(dateFormat.format(date));
    }

    @NonNull
    @Override
    public MsgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg,
                parent,false);
        return new MsgHolder(v);
    }

    static class MsgHolder extends RecyclerView.ViewHolder {

        TextView textData;
        TextView textTime;
        ImageView imageMsgShare;

        public MsgHolder(@NonNull final View itemView) {
            super(itemView);
            textData = itemView.findViewById(R.id.textMsg);
            textTime = itemView.findViewById(R.id.textTime);
            imageMsgShare = itemView.findViewById(R.id.imageMsgShare);

            imageMsgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file = new File(Environment.getExternalStorageDirectory() + "/.lukachupi/");
                    if (file.exists()){
                        textData.setDrawingCacheEnabled(true);
                        Bitmap bitmap = textData.getDrawingCache();
                        try {
                            String imgPath = Environment.getExternalStorageDirectory().toString() + "/.lukachupi/img_share.jpg";
                            bitmap.compress(Bitmap.CompressFormat.JPEG,95,new FileOutputStream(imgPath));
                            Uri imgUri = Uri.parse(imgPath);
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_TEXT,
                                    "Someone send me this secret message, do you want to try ? Download the app now https://play.google.com/store/apps/details?id=com.joy.lukachupi");
                            shareIntent.putExtra(Intent.EXTRA_STREAM,imgUri);
                            shareIntent.setType("image/jpg");
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            itemView.getContext().startActivity(Intent.createChooser(shareIntent,"send"));

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
