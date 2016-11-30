package com.yiyang.reactnativebaidumap;

import android.graphics.Color;
import android.view.View;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ReactMarkerManager extends ViewGroupManager<ReactMapMarker> {

    private static final int SHOW_INFO_WINDOW = 1;
    private static final int HIDE_INFO_WINDOW = 2;

    public ReactMarkerManager() {
    }

    @Override
    public String getName() {
        return "AIRMapMarker";
    }

    @Override
    public ReactMapMarker createViewInstance(ThemedReactContext context) {
        return new ReactMapMarker(context);
    }

    @ReactProp(name = "coordinate")
    public void setCoordinate(ReactMapMarker view, ReadableMap map) {
        view.setCoordinate(map);
    }
    /*
    @ReactProp(name = "identifier")
    public void setIdentifier(ReactMapMarker view, String id) {
        //view.setId(Integer.parseInt(id));
        view.setIdentifier(id);
    }

    
    @ReactProp(name = "title")
    public void setTitle(ReactMapMarker view, String title) {
        view.setTitle(title);
    }

    @ReactProp(name = "identifier")
    public void setIdentifier(ReactMapMarker view, String identifier) {
        view.setIdentifier(identifier);
    }

    @ReactProp(name = "description")
    public void setDescription(ReactMapMarker view, String description) {
        view.setSnippet(description);
    }

    @ReactProp(name = "anchor")
    public void setAnchor(ReactMapMarker view, ReadableMap map) {
        // should default to (0.5, 1) (bottom middle)
        double x = map != null && map.hasKey("x") ? map.getDouble("x") : 0.5;
        double y = map != null && map.hasKey("y") ? map.getDouble("y") : 1.0;
        view.setAnchor(x, y);
    }

    @ReactProp(name = "calloutAnchor")
    public void setCalloutAnchor(ReactMapMarker view, ReadableMap map) {
        // should default to (0.5, 0) (top middle)
        double x = map != null && map.hasKey("x") ? map.getDouble("x") : 0.5;
        double y = map != null && map.hasKey("y") ? map.getDouble("y") : 0.0;
        view.setCalloutAnchor(x, y);
    }

    @ReactProp(name = "image")
    public void setImage(ReactMapMarker view, @Nullable String source) {
        view.setImage(source);
    }

    @ReactProp(name = "pinColor", defaultInt = Color.RED, customType = "Color")
    public void setPinColor(ReactMapMarker view, int pinColor) {
        float[] hsv = new float[3];
        Color.colorToHSV(pinColor, hsv);
        // NOTE: android only supports a hue
        view.setMarkerHue(hsv[0]);
    }

    @ReactProp(name = "rotation", defaultFloat = 0.0f)
    public void setMarkerRotation(ReactMapMarker view, float rotation) {
        view.setRotation(rotation);
    }

    @ReactProp(name = "flat", defaultBoolean = false)
    public void setFlat(ReactMapMarker view, boolean flat) {
        view.setFlat(flat);
    }

    @ReactProp(name = "draggable", defaultBoolean = false)
    public void setDraggable(ReactMapMarker view, boolean draggable) {
        view.setDraggable(draggable);
    }

    @Override
    @ReactProp(name = "zIndex", defaultFloat = 0.0f)
    public void setZIndex(ReactMapMarker view, float zIndex) {
      super.setZIndex(view, zIndex);
      int integerZIndex = Math.round(zIndex);
      view.setZIndex(integerZIndex);
    }
    */
    @Override
    public void addView(ReactMapMarker parent, View child, int index) {
        //if (child instanceof AirMapCallout) {
        //    parent.setCalloutView((AirMapCallout) child);
        //} else {
            super.addView(parent, child, index);
            parent.update();
        //}
    }

    @Override
    public void removeViewAt(ReactMapMarker parent, int index) {
        super.removeViewAt(parent, index);
        parent.update();
    }

    @Override
    @Nullable
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "showCallout", SHOW_INFO_WINDOW,
                "hideCallout", HIDE_INFO_WINDOW
        );
    }

    @Override
    public void receiveCommand(ReactMapMarker view, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case SHOW_INFO_WINDOW:
                //((Marker) view.getFeature()).showInfoWindow();
                break;

            case HIDE_INFO_WINDOW:
                //((Marker) view.getFeature()).hideInfoWindow();
                break;
        }
    }

    @Override
    @Nullable
    public Map getExportedCustomDirectEventTypeConstants() {
        Map<String, Map<String, String>> map = MapBuilder.of(
                "onPress", MapBuilder.of("registrationName", "onPress"),
                "onCalloutPress", MapBuilder.of("registrationName", "onCalloutPress"),
                "onDragStart", MapBuilder.of("registrationName", "onDragStart"),
                "onDrag", MapBuilder.of("registrationName", "onDrag"),
                "onDragEnd", MapBuilder.of("registrationName", "onDragEnd")
        );

        map.putAll(MapBuilder.of(
                "onDragStart", MapBuilder.of("registrationName", "onDragStart"),
                "onDrag", MapBuilder.of("registrationName", "onDrag"),
                "onDragEnd", MapBuilder.of("registrationName", "onDragEnd")
        ));

        return map;
    }
    /*
    @Override
    public LayoutShadowNode createShadowNodeInstance() {
        // we use a custom shadow node that emits the width/height of the view
        // after layout with the updateExtraData method. Without this, we can't generate
        // a bitmap of the appropriate width/height of the rendered view.
        return new SizeReportingShadowNode();
    }

    @Override
    public void updateExtraData(ReactMapMarker view, Object extraData) {
        // This method is called from the shadow node with the width/height of the rendered
        // marker view.
        HashMap<String, Float> data = (HashMap<String, Float>) extraData;
        float width = data.get("width");
        float height = data.get("height");
        view.update((int) width, (int) height);
    }
    */
}
