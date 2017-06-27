package com.example.android.newsapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by evi on 25. 6. 2017.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    public static final String LOG_TAG = NewsAdapter.class.getName();

    public NewsAdapter(Activity context, ArrayList<News> news) {
        super(context, 0, news);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        // Find the news article at the given position in the list of news
        News currentNews = getItem(position);

        // Displaying title
        TextView titleView = (TextView) listItemView.findViewById(R.id.title_view);
        String title = currentNews.getTitle();
        titleView.setText(title);

        // Displaying section
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section_view);
        String section = currentNews.getSection();
        sectionView.setText(section);

        // Displaying date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date_view);
        String dateOriginal = currentNews.getDate();
        String dateString = dateOriginal.substring(0,9);
        Date dateParsed = parseDate(dateString);
        String date = formatDate(dateParsed);
        dateView.setText(date.toString());

        // Displaying time
        TextView timeView = (TextView) listItemView.findViewById(R.id.time_view);
        String timeString = dateOriginal.substring(11,18);
        Date timeParsed = parseTime(timeString);
        String time = formatTime(timeParsed);
        timeView.setText(time.toString());

return listItemView;
}


    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseTime(String time) {
        try {
            return new SimpleDateFormat("HH:mm:ss").parse(time);
        } catch (ParseException e) {
            return null;
        }
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private String formatTime(Date timeObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(timeObject);
    }
}
