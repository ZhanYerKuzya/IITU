package com.nmn.app.iitu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends BaseAdapter {

    private Context context;
    private List<String> newsTitle;
    private List<String> newsShort;
    private LayoutInflater layoutInflater;

    public NewsAdapter(Context context, List<String> newsTitle, List<String> newsShort) {
        this.context = context;
        this.newsTitle = newsTitle;
        this.newsShort = newsShort;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return newsTitle.size();
    }

    @Override
    public Object getItem(int position) {
        return newsTitle.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.news_itmes, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.newstitle.setText(newsTitle.get(position));
        viewHolder.newsshort.setText(newsShort.get(position));

        return convertView;
    }

    private class ViewHolder{
        private TextView newstitle, newsshort;

        public ViewHolder(View view){

            newstitle = (TextView) view.findViewById(R.id.newsTitle);
            newsshort = (TextView) view.findViewById(R.id.newsShort);
        }
    }
}
