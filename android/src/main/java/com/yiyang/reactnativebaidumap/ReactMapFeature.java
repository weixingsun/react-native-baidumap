package com.yiyang.reactnativebaidumap;

import android.content.Context;

import com.facebook.react.views.view.ReactViewGroup;
import com.baidu.mapapi.map.BaiduMap;

public abstract class ReactMapFeature extends ReactViewGroup {
    public ReactMapFeature(Context context) {
        super(context);
    }

    public abstract void addToMap(BaiduMap map);

    public abstract void removeFromMap(BaiduMap map);

    public abstract Object getFeature();
}
