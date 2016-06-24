package com.yiyang.reactnativebaidumap;

import android.util.Log;
import android.view.*;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter; 

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yiyang on 16/2/29.
 */
public class ReactMapView implements BaiduMap.OnMapStatusChangeListener, BaiduMap.OnMarkerClickListener {

    private MapView mMapView;
    private BaiduMapViewManager mManager;

    private LocationClient mLocationClient;

    private ReactMapMyLocationConfiguration mConfiguration;

    private boolean autoZoomToSpan;

    public boolean isAutoZoomToSpan() {
        return autoZoomToSpan;
    }

    public void setAutoZoomToSpan(boolean autoZoomToSpan) {
        this.autoZoomToSpan = autoZoomToSpan;
    }

    private List<ReactMapMarker> mMarkers = new ArrayList<ReactMapMarker>();
    private List<String> mMarkerIds = new ArrayList<String>();

    private List<ReactMapOverlay> mOverlays = new ArrayList<ReactMapOverlay>();
    private List<String> mOverlayIds = new ArrayList<String>();

    public ReactMapView(MapView mapView, BaiduMapViewManager manager) {
        this.mMapView = mapView;
	this.mManager = manager;
        mapView.getMap().setOnMapStatusChangeListener(this);  
        mapView.getMap().setOnMarkerClickListener(this);  
    }

    public void onMapStatusChangeStart(MapStatus status) {
        //updateMapState();
    }
    public void onMapStatusChangeFinish(MapStatus status) {
        LatLng center = status.target;
        //System.out.println("lat:"+center.latitude+", lng:"+center.longitude);
        WritableMap region = Arguments.createMap();
        region.putDouble("latitude",center.latitude);
        region.putDouble("longitude",center.longitude);
        region.putDouble("zoom",status.zoom);
        region.putDouble("latitudeDelta",Math.abs(status.bound.northeast.latitude-status.bound.southwest.latitude));
        region.putDouble("longitudeDelta",Math.abs(status.bound.northeast.longitude-status.bound.southwest.longitude));
        WritableMap event = Arguments.createMap();
        event.putMap("region", region);
        //reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("regionChange", event);
        ReactContext reactContext = (ReactContext)this.mMapView.getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(this.mMapView.getId(), "topChange", event);
        //mManager.pushEvent(this.mMapView, "topChange", event);
    }
    public void onMapStatusChange(MapStatus status) {
        //updateMapState();
    }

    public boolean onMarkerClick(Marker marker) {
        System.out.println("onMarkerClick:");
        WritableMap event = Arguments.createMap();
        event.putDouble("latitude", marker.getPosition().latitude);
        event.putDouble("longitude",marker.getPosition().longitude);
        event.putString("title",marker.getTitle());
        //event.putString("id",marker.getId());
        ReactContext reactContext = (ReactContext)this.mMapView.getContext();
	//WritableMap event = Arguments.createMap();
	//event.putMap("event", event);
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(this.mMapView.getId(), "onMarkerPress", event);
        //return true;
	return false; // returning false opens the callout window, if possible
    }

    public BaiduMap getMap() {
        return this.mMapView.getMap();
    }

    public void setOverlays(List<ReactMapOverlay> overlays) {
        List<String> newOverlayIds = new ArrayList<String>();
        List<ReactMapOverlay> overlaysToDelete = new ArrayList<ReactMapOverlay>();
        List<ReactMapOverlay> overlaysToAdd = new ArrayList<ReactMapOverlay>();

        for (ReactMapOverlay overlay :
                overlays) {
            if (overlay instanceof ReactMapOverlay == false) {
                continue;
            }

            newOverlayIds.add(overlay.getId());

            if (!mOverlayIds.contains(overlay.getId())) {
                overlaysToAdd.add(overlay);
            }
        }

        for (ReactMapOverlay overlay :
                this.mOverlays) {
            if (overlay instanceof ReactMapOverlay == false) {
                continue;
            }

            if (!newOverlayIds.contains(overlay.getId())) {
                overlaysToDelete.add(overlay);
            }
        }

        if (!overlaysToDelete.isEmpty()) {
            for (ReactMapOverlay overlay :
                    overlaysToDelete) {
                overlay.getPolyline().remove();
                this.mOverlays.remove(overlay);
            }
        }

        if (!overlaysToAdd.isEmpty()) {
            for (ReactMapOverlay overlay:
                    overlaysToAdd) {
                if (overlay.getOptions() != null) {
                    overlay.addToMap(this.getMap());
                    this.mOverlays.add(overlay);
                }
            }
        }

        this.mOverlayIds = newOverlayIds;

    }

    public void setMarker(List<ReactMapMarker> markers) {

        List<String> newMarkerIds = new ArrayList<String>();
        List<ReactMapMarker> markersToDelete = new ArrayList<ReactMapMarker>();
        List<ReactMapMarker> markersToAdd = new ArrayList<ReactMapMarker>();

        for (ReactMapMarker marker :
                markers) {
            if (marker instanceof ReactMapMarker == false) {
                continue;
            }

            newMarkerIds.add(marker.getId());

            if (!mMarkerIds.contains(marker.getId())) {
                markersToAdd.add(marker);
            }
        }

        for (ReactMapMarker marker :
                this.mMarkers) {
            if (marker instanceof ReactMapMarker == false) {
                continue;
            }

            if (!newMarkerIds.contains(marker.getId())) {
                markersToDelete.add(marker);
            }
        }

        if (!markersToDelete.isEmpty()) {
            for (ReactMapMarker marker :
                    markersToDelete) {
                marker.getMarker().remove();
                this.mMarkers.remove(marker);
            }
        }

        if (!markersToAdd.isEmpty()) {
            for (ReactMapMarker marker :
                    markersToAdd) {
                if (marker.getOptions() != null) {
                    marker.addToMap(this.getMap());
                    this.mMarkers.add(marker);
                }
            }
        }

        this.mMarkerIds = newMarkerIds;
    }


    public void onMapLoaded() {
        if (this.autoZoomToSpan) {
            this.zoomToSpan();
        }
    }

    public void zoomToSpan(List<ReactMapMarker> markers, List<ReactMapOverlay> overlays) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        boolean hasBuilded = false;
        if (markers != null && markers.size() > 0) {
            for (ReactMapMarker marker:
                 markers) {
                if (marker != null && marker.getOptions() != null) {
                    LatLng location = marker.getOptions().getPosition();
                    builder.include(location);
                    hasBuilded = true;
                }
            }
        }
        if (overlays != null && overlays.size() > 0) {
            for (ReactMapOverlay overlay :
                    overlays) {
                if (overlay != null && overlay.getOptions() != null) {
                    for (LatLng location :
                            overlay.getOptions().getPoints()) {
                        builder.include(location);
                        hasBuilded = true;
                    }
                }
            }
        }
        if (hasBuilded) {
            this.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));
        }
    }

    public void zoomToSpan() {
        this.zoomToSpan(this.mMarkers, this.mOverlays);
    }

    public void setShowsUserLocation(boolean showsUserLocation) {
        if (getMap() == null) {
            return;
        }
        if (showsUserLocation != getMap().isMyLocationEnabled()) {
            getMap().setMyLocationEnabled(showsUserLocation);
            if (showsUserLocation && mLocationClient == null) {
                mLocationClient = new LocationClient(mMapView.getContext());
                BaiduLocationListener listener = new BaiduLocationListener(mLocationClient, new BaiduLocationListener.ReactLocationCallback() {
                    @Override
                    public void onSuccess(BDLocation bdLocation) {

                        float radius = 0;
                        if (mConfiguration != null && mConfiguration.isShowAccuracyCircle()) {
                            radius = bdLocation.getRadius();
                        }
                        MyLocationData locData = new MyLocationData.Builder()
                                .accuracy(radius)
                                .latitude(bdLocation.getLatitude())
                                .longitude(bdLocation.getLongitude())
                                .build();
                        if (getMap().isMyLocationEnabled()) {

                            getMap().setMyLocationData(locData);
                        }
                    }

                    @Override
                    public void onFailure(BDLocation bdLocation) {
                        Log.e("RNBaidumap", "error: " + bdLocation.getLocType());
                    }
                });
                mLocationClient.setLocOption(getLocationOption());
                mLocationClient.registerLocationListener(listener);
                mLocationClient.start();
            } else if (showsUserLocation) {
                if (mLocationClient.isStarted()) {
                    mLocationClient.requestLocation();
                } else {
                    mLocationClient.start();
                }
            } else if (mLocationClient != null) {
                if (mLocationClient.isStarted()) {
                    mLocationClient.stop();
                }
            }
        }
    }

    public void setConfiguration(ReactMapMyLocationConfiguration configuration) {
        this.mConfiguration = configuration;
        this.mConfiguration.setConfigurationUpdateListener(new ReactMapMyLocationConfiguration.ConfigurationUpdateListener() {
            @Override
            public void onConfigurationUpdate(ReactMapMyLocationConfiguration aConfiguration) {
                if (getMap() != null) {
                    getMap().setMyLocationConfigeration(aConfiguration.getConfiguration());
                }
            }
        });
        if (getMap() != null) {
            getMap().setMyLocationConfigeration(configuration.getConfiguration());
        }
    }

    private LocationClientOption getLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(1000);
        option.setCoorType("bd09ll");
        return option;
    }
}
