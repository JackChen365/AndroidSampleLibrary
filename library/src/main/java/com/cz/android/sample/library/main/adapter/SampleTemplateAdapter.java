package com.cz.android.sample.library.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cz.android.sample.api.item.Demonstrable;
import com.cz.android.sample.api.item.RegisterItem;
import com.cz.android.sample.library.R;

import java.util.List;

/**
 * @author Created by cz
 * @date 2020-01-27 21:42
 * @email bingo110@126.com
 */
public class SampleTemplateAdapter extends BaseAdapter {
    private final LayoutInflater layoutInflater;
    private List<Demonstrable> demonstrableList;

    public SampleTemplateAdapter(Context context, List<Demonstrable> demonstrableList) {
        this.layoutInflater=LayoutInflater.from(context);
        this.demonstrableList = demonstrableList;
    }

    @Override
    public int getCount() {
        return demonstrableList.size();
    }

    @Override
    public Demonstrable getItem(int i) {
        return demonstrableList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(null==view){
            view=layoutInflater.inflate(R.layout.sample_category_list_item,viewGroup,false);
        }
        TextView sampleTitle=view.findViewById(R.id.sampleTitle);
        TextView sampleDescription=view.findViewById(R.id.sampleDescription);
        View sampleArrowView=view.findViewById(R.id.sampleArrowView);
        Demonstrable demonstrable = getItem(i);
        sampleTitle.setText(demonstrable.getTitle());
        sampleDescription.setText(demonstrable.getDescription());
        sampleArrowView.setVisibility(demonstrable instanceof RegisterItem?View.GONE:View.VISIBLE);
        return view;
    }
}
