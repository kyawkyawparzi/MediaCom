package pmt.kyawkyaw.myapp.mediacom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import pmt.kyawkyaw.myapp.mediacom.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.CustomViewHolder> {

    String myposition;
    private final List<String> mFileList;
    private final Context myContext;

    public GalleryAdapter(Context context, List<String> fileList) {
        myContext = context;
        mFileList = fileList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_image_show, parent, false);
        return new GalleryAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {
        Glide
                .with(myContext)
                .load(mFileList.get(position))
                .into(holder.imageResource);

        final int itemPosition = holder.getAdapterPosition();
        holder.imageResource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myposition=mFileList.get(itemPosition);
                Toast.makeText(myContext, mFileList.get(itemPosition), Toast.LENGTH_SHORT).show();
//                holder.hide_layout.setVisibility(View.VISIBLE);
//                ((callmethod_pic_request_code)myContext).showImage();
            }
        });
    }
    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageResource;
        CustomViewHolder(View itemView) {
            super(itemView);
            imageResource =itemView.findViewById(R.id.send_image_show);
        }
    }
}
