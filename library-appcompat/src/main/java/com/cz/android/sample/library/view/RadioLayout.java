package com.cz.android.sample.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cz.android.sample.library.appcompat.R;


/**
 * @author Created by cz
 * @date 2020-01-27 18:59
 * @email bingo110@126.com
 */
public class RadioLayout extends RadioGroup {

    private OnCheckedChangeListener listener;

    public RadioLayout(Context context) {
        this(context,null);
    }

    public RadioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadioLayout);


        setRadioArray(a.getTextArray(R.styleable.RadioLayout_radio_items));
        a.recycle();
    }

    /**
     * set radio group array.
     * @param textArray
     */
    public void setRadioArray(CharSequence[] textArray) {
        removeAllViews();
        Context context = getContext();
        if(null!=textArray){
            for(int i=0;i<textArray.length;i++){
                CharSequence text=textArray[i];
                RadioButton appCompatCheckBox = new RadioButton(context);
                appCompatCheckBox.setId(i);
                appCompatCheckBox.setText(text);
                appCompatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(null!=listener){
                            int index = indexOfChild(compoundButton);
                            listener.onCheckedChanged(compoundButton,index,b);
                        }
                    }
                });
                addView(appCompatCheckBox);
            }
        }
    }


    /**
     * 获得选中的位置
     */
    public int getCheckedPosotion(){
        return getCheckedRadioButtonId();
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener){
        this.listener=listener;
    }

    /**
     * interface responsible for receiving compoundButton's check change event
     */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(CompoundButton compoundButton, int index, boolean b);
    }
}
