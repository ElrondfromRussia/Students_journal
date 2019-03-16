package com.example.dz2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class CustomListAdapter  extends BaseAdapter {

    private List<MyNew> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListAdapter(Context aContext,  List<MyNew> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.me_list_item_layout, null);
            holder = new ViewHolder();
            holder.DateView = (TextView) convertView.findViewById(R.id.DateView);
            holder.UserView = (TextView) convertView.findViewById(R.id.UserView);
            holder.TexView = (TextView) convertView.findViewById(R.id.TexView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MyNew mnew = this.listData.get(position);
        holder.DateView.setText(mnew.getDate());
        holder.UserView.setText(mnew.getUserMail());
        holder.TexView.setText(mnew.getNewText());

        return convertView;
    }

    static class ViewHolder {
        TextView DateView;
        TextView UserView;
        TextView TexView;
    }

}
