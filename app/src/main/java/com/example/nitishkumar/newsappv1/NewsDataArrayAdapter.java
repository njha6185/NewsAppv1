package com.example.nitishkumar.newsappv1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;

public class NewsDataArrayAdapter extends ArrayAdapter<NewsData>{
    public NewsDataArrayAdapter(Context context, List<NewsData> objects) {
        super(context, 0, objects);
    }
/****************************** update UI from data*****************/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }
        NewsData currentNewsdata = getItem(position);

        TextView headingText = (TextView)listItemView.findViewById(R.id.headingTextView);
        TextView dateText = (TextView)listItemView.findViewById(R.id.dateTextView);
        TextView newsTypeText = (TextView)listItemView.findViewById(R.id.newsType);
        TextView authorText = (TextView)listItemView.findViewById(R.id.authorTextView);
        ImageView newsThumbnailImage = (ImageView)listItemView.findViewById(R.id.thumbnailImageView);

        headingText.setText(currentNewsdata.getNewsHeading());
        dateText.setText(currentNewsdata.getNewsDate());
        newsTypeText.setText(currentNewsdata.getNewsType());
        authorText.setText(currentNewsdata.getNewsAuthor());

        RequestOptions options = new RequestOptions()
                .centerCrop().placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(getContext()).load(currentNewsdata.getNewsThumbNailImageUrl()).apply(options).into(newsThumbnailImage);
        return listItemView;
    }
}