package com.qstech.pierglass.bean;

import com.bigkoo.pickerview.model.IPickerViewData;

/**
 * Created by admin on 2016/9/21.
 */

public class PickerViewData implements IPickerViewData {
    private String content;

    public PickerViewData(String content) {
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getPickerViewText() {
        return content;
    }
}
