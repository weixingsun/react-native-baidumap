package com.yiyang.reactnativebaidumap;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.views.text.ReactFontManager;

/**
 * Created by yiyang on 16/3/1.
 */
public class ReactMapMarker extends ReactMapFeature{
    private BaiduMap map;
    private Marker mMarker;
    private MarkerOptions mOptions;

    private String id;

    private Context mContext;

    public static BitmapDescriptor defaultIcon = BitmapDescriptorFactory.fromResource(R.drawable.location);


    private BitmapDescriptor iconBitmapDescriptor;
    private final DraweeHolder mLogoHolder;
    private DataSource<CloseableReference<CloseableImage>> dataSource;

    private final ControllerListener<ImageInfo> mLogoControllerListener =
            new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    CloseableReference<CloseableImage> imageReference = null;
                    try {
                        imageReference = dataSource.getResult();
                        if (imageReference != null) {
                            CloseableImage image = imageReference.get();
                            if (image != null && image instanceof CloseableStaticBitmap) {
                                CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap)image;
                                Bitmap bitmap = closeableStaticBitmap.getUnderlyingBitmap();
                                if (bitmap != null) {
                                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                    iconBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                                }
                            }
                        }
                    } finally {
                        dataSource.close();
                        if (imageReference != null) {
                            CloseableReference.closeSafely(imageReference);
                        }
                    }
                    update();
                }
            };

    public ReactMapMarker(Context context, BaiduMap map) {
        super(context);
        this.mContext = context;
        this.map = map;
        mLogoHolder = DraweeHolder.create(createDraweeHierarchy(), null);
        mLogoHolder.onAttach();
    }
    public ReactMapMarker(Context context) {
        super(context);
        this.mContext = context;
        mLogoHolder = DraweeHolder.create(createDraweeHierarchy(), null);
        mLogoHolder.onAttach();
    }

    public void setCoordinate(ReadableMap coordinate) {
        LatLng position = new LatLng(coordinate.getDouble("latitude"), coordinate.getDouble("longitude"));
        if (mOptions != null) {
            mOptions.position(position);
        }
        update();
    }

    public void buildMarker(ReadableMap annotation) throws Exception{
        if (annotation == null) {
            throw new Exception("marker annotation must not be null");
        }
        //id = Integer.parseInt(annotation.getString("id"));
        //id = annotation.getString("id");
        MarkerOptions options = new MarkerOptions();
        double latitude = annotation.getDouble("latitude");
        double longitude = annotation.getDouble("longitude");

        options.position(new LatLng(latitude, longitude));
        if (annotation.hasKey("draggable")) {

            boolean draggable = annotation.getBoolean("draggable");
            options.draggable(draggable);
        }

        if (annotation.hasKey("title")) {
            options.title(annotation.getString("title"));
        }

        options.icon(defaultIcon);
        this.mOptions = options;

        if (annotation.hasKey("image")) {
			ReadableMap img = annotation.getMap("image");
            if (img.hasKey("uri") ) {
				String imgUri = img.getString("uri");
				if(imgUri != null && imgUri.length() > 0){
					if (imgUri.startsWith("http://") || imgUri.startsWith("https://")) {
						ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imgUri)).build();
						ImagePipeline imagePipeline = Fresco.getImagePipeline();
						dataSource = imagePipeline.fetchDecodedImage(imageRequest,this);
						DraweeController controller = Fresco.newDraweeControllerBuilder()
								.setImageRequest(imageRequest)
								.setControllerListener(mLogoControllerListener)
								.setOldController(mLogoHolder.getController())
								.build();
						mLogoHolder.setController(controller);
					} else {
						this.mOptions.icon(getBitmapDescriptorByName(imgUri));
					}
				}
            }else if(img.hasKey("font")){  // vector font icons -> font:fa-bell-o:30:#dd00ee
                String font  = img.getString("font");
                String glyph = img.getString("glyph");
                Integer color = img.getInt("color");
                Integer size  = img.getInt("size");
                this.mOptions.icon(this.getIconForFont(font,glyph,size,color));  //"Ionicons","",34,-12942132
            }
        } else {
            options.icon(defaultIcon);
        }
        Bundle bundle = Arguments.toBundle(annotation);
	this.mMarker = (Marker)this.map.addOverlay(this.mOptions);
	this.mMarker.setExtraInfo(bundle);

    }

    private GenericDraweeHierarchy createDraweeHierarchy() {
        return new GenericDraweeHierarchyBuilder(this.mContext.getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setFadeDuration(0)
                .build();
    }

    //public void setId(int id) {this.id=id;}
    //public int getId() {return this.id;}
    //public void setIdentifier(String id) {this.id=id;}
    public String getIdentifier() {return this.id;}
    public Marker getMarker() {return this.mMarker;}
    public MarkerOptions getOptions() {return this.mOptions;}

    public void addToMap(BaiduMap map) {
        if (this.mMarker == null) {
            this.mMarker = (Marker)map.addOverlay(this.getOptions());
        }
    }

    private int getDrawableResourceByName(String name) {
        return this.mContext.getResources().getIdentifier(name, "drawable", this.mContext.getPackageName());
    }

    private BitmapDescriptor getBitmapDescriptorByName(String name) {
        //add support for react-native-vector-icons
        if(name.startsWith("file")){ //    /file:/data/data/com.share/cache/-vioav8_48@2x.png
            String newName = name.split(":")[1];
            return BitmapDescriptorFactory.fromPath(newName);
        }else{
            return BitmapDescriptorFactory.fromResource(getDrawableResourceByName(name));
        }
    }

    private BitmapDescriptor getIcon() {
        if (iconBitmapDescriptor != null) {
            return iconBitmapDescriptor;
        } else {
            return defaultIcon;
        }
    }

    public void update() {
        if (this.mMarker != null) {
            this.mMarker.setIcon(getIcon());
        } else if (this.mOptions != null){
            this.mOptions.icon(getIcon());
        }
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        // if children are added, it means we are rendering a custom marker
        //if (!(child instanceof AirMapCallout)) {
        //    hasCustomMarkerView = true;
        //}
        update();
    }

    @Override
    public Object getFeature() {
        return this.mMarker;
    }

    @Override
    public void removeFromMap(BaiduMap map) {
        this.mMarker.remove();
        this.mMarker = null;
    }
    BitmapDescriptor getIconForFont(String fontFamily, String glyph, Integer fontSize, Integer color){  //font:fa-bell-o:30:#dd00ee
        Context context = this.mContext; //getReactApplicationContext();
        float scale = context.getResources().getDisplayMetrics().density;
        int size = Math.round(fontSize*scale);
        Typeface typeface = ReactFontManager.getInstance().getTypeface(fontFamily, 0, context.getAssets());
        Paint paint = new Paint();
        paint.setTypeface(typeface);
        paint.setColor(color);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        Rect textBounds = new Rect();
        paint.getTextBounds(glyph, 0, glyph.length(), textBounds);
        Bitmap bitmap = Bitmap.createBitmap(textBounds.width(), textBounds.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(glyph, -textBounds.left, -textBounds.top, paint);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
