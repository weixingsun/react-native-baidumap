'use strict';

import React, {Component, PropTypes} from "react";
import { EdgeInsetsPropType,findNodeHandle,Image,NativeMethodsMixin,Platform,requireNativeComponent,StyleSheet,processColor,ColorPropType,UIManager,View, } from 'react-native';

import deprecatedPropType from 'react-native/Libraries/Utilities/deprecatedPropType';
import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';


const RCTBaiduMapConstants = UIManager.RCTBaiduMap.Constants;

type Event = Object;

export type MAAnnotationDragState = $Enum<{
  idle: string;
  starting: string;
  dragging: string;
  canceling: string;
  ending: string;
}>;
// class Fuck extends React.Component {
//     render() {
//         return <View></View>;
//     }
// }
const BaiduMapView= React.createClass({

  mixins: [NativeMethodsMixin],

  propTypes: {
    ...View.propTypes,
    /**
     * Used to style and layout the `MapView`.  See `StyleSheet.js` and
     * `ViewStylePropTypes.js` for more info.
     */
    style: View.propTypes.style,

    /**
     * If `true` the app will ask for the user's location and display it on
     * the map. Default value is `false`.
     *
     * **NOTE**: on iOS, you need to add the `NSLocationWhenInUseUsageDescription`
     * key in Info.plist to enable geolocation, otherwise it will fail silently.
     */
    showsUserLocation: React.PropTypes.bool,

    userLocationViewParams: React.PropTypes.shape({
        /**
         * 定位图标XY轴偏移量(屏幕坐标)
         */
        offsetX: React.PropTypes.number,
        offsetY: React.PropTypes.number,
        /**
         * 精度圈是否显示
         */
        showAccuracyCircle: React.PropTypes.bool,
        /**
         * 精度圈 填充颜色
         */
        accuracyCircleFillColor: ColorPropType,
        /**
         * 精度圈 边框颜色
         */
        accuracyCircleStrokeColor: ColorPropType,
        /**
         * 跟随态旋转角度是否生效
         */
        rotateAngleValid: React.PropTypes.bool,
        /**
         * 定位图标名称，需要将该图片放到 mapapi.bundle/images 目录下
         */
        image: Image.propTypes.source

    }),

    autoZoomToSpan: React.PropTypes.bool,

    /**
     * If `true` the map will follow the user's location whenever it changes.
     * Note that this has no effect unless `showsUserLocation` is enabled.
     * Default value is `true`.
     * @platform ios
     */
    followUserLocation: React.PropTypes.bool,

    /**
     * If `false` points of interest won't be displayed on the map.
     * Default value is `true`.
     * @platform ios
     */
    showsPointsOfInterest: React.PropTypes.bool,

    /**
     * If `false` compass won't be displayed on the map.
     * Default value is `true`.
     * @platform ios
     */
    showsCompass: React.PropTypes.bool,

    /**
     * If `false` the user won't be able to pinch/zoom the map.
     * Default value is `true`.
     */
    zoomEnabled: React.PropTypes.bool,

    /**
     * When this property is set to `true` and a valid camera is associated with
     * the map, the camera’s heading angle is used to rotate the plane of the
     * map around its center point. When this property is set to `false`, the
     * camera’s heading angle is ignored and the map is always oriented so
     * that true north is situated at the top of the map view
     */
    rotateEnabled: React.PropTypes.bool,

    /**
     * When this property is set to `true` and a valid camera is associated
     * with the map, the camera’s pitch angle is used to tilt the plane
     * of the map. When this property is set to `false`, the camera’s pitch
     * angle is ignored and the map is always displayed as if the user
     * is looking straight down onto it.
     */
    //pitchEnabled: React.PropTypes.bool,
    overlookEnabled: React.PropTypes.bool,

    /**
     * If `false` the user won't be able to change the map region being displayed.
     * Default value is `true`.
     */
    scrollEnabled: React.PropTypes.bool,

    trafficEnabled: React.PropTypes.bool,

    /**
     * The map type to be displayed.
     *
     * - standard: standard road map (default)
     * - satellite: satellite view
     * - hybrid: satellite view with roads and points of interest overlaid
     *
     * @platform ios
     */
    mapType: React.PropTypes.oneOf([
      'standard',
      'satellite',
    //  'hybrid',
    ]),

    /**
     * The region to be displayed by the map.
     *
     * The region is defined by the center coordinates and the span of
     * coordinates to display.
     */
    region: React.PropTypes.shape({
      latitude: React.PropTypes.number.isRequired,
      longitude: React.PropTypes.number.isRequired,
      latitudeDelta: React.PropTypes.number,
      longitudeDelta: React.PropTypes.number,
    }),
    initialRegion: React.PropTypes.shape({
      latitude: React.PropTypes.number.isRequired,
      longitude: React.PropTypes.number.isRequired,
      latitudeDelta: React.PropTypes.number,
      longitudeDelta: React.PropTypes.number,
    }),
    /**
     * Map annotations with title/subtitle.
     * @platform ios
     */
    annotations: React.PropTypes.arrayOf(React.PropTypes.shape({
      /**
       * The location of the annotation.
       */
      latitude: React.PropTypes.number.isRequired,
      longitude: React.PropTypes.number.isRequired,

      /**
       * Whether the pin drop should be animated or not
       */
      animateDrop: React.PropTypes.bool,

      /**
       * Whether the pin should be draggable or not
       */
      draggable: React.PropTypes.bool,

      /**
       * Event that fires when the annotation drag state changes.
       */
      onDragStateChange: React.PropTypes.func,

      /**
       * Event that fires when the annotation gets was tapped by the user
       * and the callout view was displayed.
       */
      onFocus: React.PropTypes.func,

      /**
       * Event that fires when another annotation or the mapview itself
       * was tapped and a previously shown annotation will be closed.
       */
      onBlur: React.PropTypes.func,

      /**
       * Annotation title/subtile.
       */
      title: React.PropTypes.string,
      subtitle: React.PropTypes.string,

      /**
       * Callout views.
       */
      leftCalloutView: React.PropTypes.element,
      rightCalloutView: React.PropTypes.element,

      /**
       * The pin color. This can be any valid color string, or you can use one
       * of the predefined PinColors constants. Applies to both standard pins
       * and custom pin images.
       *
       * Note that on iOS 8 and earlier, only the standard PinColor constants
       * are supported for regular pins. For custom pin images, any tintColor
       * value is supported on all iOS versions.
       */
      tintColor: React.PropTypes.number,

      /**
       * Custom pin image. This must be a static image resource inside the app.
       */
      image: Image.propTypes.source,

      /**
       * Custom pin view. If set, this replaces the pin or custom pin image.
       */
      view: React.PropTypes.element,

      /**
       * annotation id
       */
      id: React.PropTypes.string,

      /**
       * Deprecated. Use the left/right/detailsCalloutView props instead.
       */
      hasLeftCallout: deprecatedPropType(
        React.PropTypes.bool,
        'Use `leftCalloutView` instead.'
      ),
      hasRightCallout: deprecatedPropType(
        React.PropTypes.bool,
        'Use `rightCalloutView` instead.'
      ),
      onLeftCalloutPress: deprecatedPropType(
        React.PropTypes.func,
        'Use `leftCalloutView` instead.'
      ),
      onRightCalloutPress: deprecatedPropType(
        React.PropTypes.func,
        'Use `rightCalloutView` instead.'
      ),
    })),

    /**
     * Map overlays
     * @platform ios
     */
    overlays: React.PropTypes.arrayOf(React.PropTypes.shape({
      /**
       * Polyline coordinates
       */
      coordinates: React.PropTypes.arrayOf(React.PropTypes.shape({
        latitude: React.PropTypes.number.isRequired,
        longitude: React.PropTypes.number.isRequired
      })),

      /**
       * Line attributes
       */
      lineWidth: React.PropTypes.number,
      strokeColor: ColorPropType,
      fillColor: ColorPropType,

      /**
       * Overlay id
       */
      id: React.PropTypes.string
    })),

    /**
     * Maximum size of area that can be displayed.
     * @platform ios
     */
    maxDelta: React.PropTypes.number,

    /**
     * Minimum size of area that can be displayed.
     * @platform ios
     */
    minDelta: React.PropTypes.number,

    /**
     * Insets for the map's legal label, originally at bottom left of the map.
     * See `EdgeInsetsPropType.js` for more information.
     * @platform ios
     */
    legalLabelInsets: EdgeInsetsPropType,

    /**
     * Callback that is called continuously when the user is dragging the map.
     */
    onRegionChange: React.PropTypes.func,

    /**
     * Callback that is called once, when the user is done moving the map.
     */
    onRegionChangeComplete: React.PropTypes.func,

    /**
     * Deprecated. Use annotation onFocus and onBlur instead.
     */
    onAnnotationPress: React.PropTypes.func,

    /**
     * @platform android
     */
    active: React.PropTypes.bool,
  },

  componentDidMount: function() {
    const { region, initialRegion } = this.props;
    if (region) {
      this.refs.map.setNativeProps({ region });
    } else if (initialRegion ) {
      this.refs.map.setNativeProps({ region: initialRegion });
    }
  },


  render: function() {
    let children = [], {annotations, overlays, followUserLocation, userLocationViewParams, showsZoomControl} = this.props;
    annotations = annotations && annotations.map((annotation: Object) => {
      let {
        id,
        image,
        tintColor,
        view,
        leftCalloutView,
        rightCalloutView,
      } = annotation;

      if (!view && image && tintColor) {
        view = <Image
          style={{
            tintColor: processColor(tintColor),
          }}
          source={image}
        />;
        image = undefined;
      }
      if (view) {
        if (image) {
          console.warn('`image` and `view` both set on annotation. Image will be ignored.');
        }
        var viewIndex = children.length;
        children.push(React.cloneElement(view, {
          style: [styles.annotationView, view.props.style || {}]
        }));
      }
      if (leftCalloutView) {
        var leftCalloutViewIndex = children.length;
        children.push(React.cloneElement(leftCalloutView, {
          style: [styles.calloutView, leftCalloutView.props.style || {}]
        }));
      }
      if (rightCalloutView) {
        var rightCalloutViewIndex = children.length;
        children.push(React.cloneElement(rightCalloutView, {
          style: [styles.calloutView, rightCalloutView.props.style || {}]
        }));
      }

      let result = {
        ...annotation,
        tintColor: tintColor && processColor(tintColor),
        image,
        viewIndex,
        leftCalloutViewIndex,
        rightCalloutViewIndex,
        view: undefined,
        leftCalloutView: undefined,
        rightCalloutView: undefined,
      };
      result.id = id || encodeURIComponent(JSON.stringify(result));
      result.image = image && resolveAssetSource(image);
      return result;
    });
    overlays = overlays && overlays.map((overlay: Object) => {
      let {id, fillColor, strokeColor} = overlay;
      let result = {
        ...overlay,
        strokeColor: strokeColor && processColor(strokeColor),
        fillColor: fillColor && processColor(fillColor),
      };
      result.id = id || encodeURIComponent(JSON.stringify(result));
      return result;
    });

    const findByAnnotationId = (annotationId: string) => {
      if (!annotations) {
        return null;
      }
      for (let i = 0, l = annotations.length; i < l; i++) {
        if (annotations[i].id === annotationId) {
          return annotations[i];
        }
      }
      return null;
    };

    // TODO: these should be separate events, to reduce bridge traffic
    let onPress, onAnnotationDragStateChange, onAnnotationFocus, onAnnotationBlur;
    if (annotations) {
      onPress = (event: Event) => {
        if (event.nativeEvent.action === 'annotation-click') {
          // TODO: Remove deprecated onAnnotationPress API call later.
          this.props.onAnnotationPress && this.props.onAnnotationPress(event.nativeEvent.annotation);
	  this.props.onMarkerPress     && this.props.onMarkerPress(event.nativeEvent.annotation);
        } else if (event.nativeEvent.action === 'callout-click') {
          const annotation = findByAnnotationId(event.nativeEvent.annotationId);
          if (annotation) {
            // Pass the right function
            if (event.nativeEvent.side === 'left' && annotation.onLeftCalloutPress) {
              annotation.onLeftCalloutPress(event.nativeEvent);
            } else if (event.nativeEvent.side === 'right' && annotation.onRightCalloutPress) {
              annotation.onRightCalloutPress(event.nativeEvent);
            }
          }
        }
      };
      onAnnotationDragStateChange = (event: Event) => {
        const annotation = findByAnnotationId(event.nativeEvent.annotationId);
        if (annotation) {
          // Update location
          annotation.latitude = event.nativeEvent.latitude;
          annotation.longitude = event.nativeEvent.longitude;
          // Call callback
          annotation.onDragStateChange &&
            annotation.onDragStateChange(event.nativeEvent);
        }
      };
      onAnnotationFocus = (event: Event) => {
        const annotation = findByAnnotationId(event.nativeEvent.annotationId);
        if (annotation && annotation.onFocus) {
          annotation.onFocus(event.nativeEvent);
        }
      };
      onAnnotationBlur = (event: Event) => {
        const annotation = findByAnnotationId(event.nativeEvent.annotationId);
        if (annotation && annotation.onBlur) {
          annotation.onBlur(event.nativeEvent);
        }
      };
    }

    // TODO: these should be separate events, to reduce bridge traffic
    if (this.props.onRegionChange || this.props.onRegionChangeComplete) {
      var onChange = (event: Event) => {
	//console.log('------------------onRegionChange.continuous:'+event.nativeEvent.continuous)
        if (event.nativeEvent.continuous) {
          this.props.onRegionChange &&
            this.props.onRegionChange(event.nativeEvent.region);
        } else {
          this.props.onRegionChangeComplete &&
            this.props.onRegionChangeComplete(event.nativeEvent.region);
        }
      };
    }

    // followUserLocation defaults to true if showUserLocation is set
    if (followUserLocation === undefined) {
      followUserLocation = this.props.showUserLocation;
    }

    if (!!userLocationViewParams) {
        let {accuracyCircleFillColor, accuracyCircleStrokeColor, image} = userLocationViewParams;
        accuracyCircleFillColor = accuracyCircleFillColor && processColor(accuracyCircleFillColor);
        accuracyCircleStrokeColor = accuracyCircleStrokeColor && processColor(accuracyCircleStrokeColor);
        userLocationViewParams.accuracyCircleFillColor = accuracyCircleFillColor;
        userLocationViewParams.accuracyCircleStrokeColor = accuracyCircleStrokeColor;

        image = image && resolveAssetSource(image);
        userLocationViewParams.image = image;
    }

    if (showsZoomControl === undefined) {
        showsZoomControl = false;
    }
    let props = {
        ...this.props,
        region: null,
    }
    return (
      <RCTBaiduMap
          {...props}
          ref="map"
          annotations={annotations}
          children={children}
          followUserLocation={followUserLocation}
          showsZoomControl={showsZoomControl}
          overlays={overlays}
          onPress={onPress}
          onChange={onChange}
          onAnnotationDragStateChange={onAnnotationDragStateChange}
          onAnnotationFocus={onAnnotationFocus}
          onAnnotationBlur={onAnnotationBlur}
          userLocationViewParams={userLocationViewParams}
      />
    );
  },

  zoomToLocs(locs) {
      if (!Array.isArray(locs) && Object.prototype.toString.call(locs) === '[object Object]') {
          locs = [locs];
      }
      if (!Array.isArray(locs)) {
          return;
      }
      UIManager.dispatchViewManagerCommand(
          findNodeHandle(this.refs["map"]),
          UIManager.RCTBaiduMap.Commands.zoomToLocs,
          [locs]
      );
  },
});

const styles = StyleSheet.create({
  annotationView: {
    position: 'absolute',
    backgroundColor: 'transparent',
  },
  calloutView: {
    position: 'absolute',
    backgroundColor: 'white',
  },
});

/**
 * Standard iOS MapView pin color constants, to be used with the
 * `annotation.tintColor` property. On iOS 8 and earlier these are the
 * only supported values when using regular pins. On iOS 9 and later
 * you are not obliged to use these, but they are useful for matching
 * the standard iOS look and feel.
 */
let PinColors = RCTBaiduMapConstants && RCTBaiduMapConstants.PinColors;
BaiduMapView.PinColors = PinColors && {
  RED: PinColors.RED,
  GREEN: PinColors.GREEN,
  PURPLE: PinColors.PURPLE,
};

const RCTBaiduMap = requireNativeComponent('RCTBaiduMap', BaiduMapView, {
  nativeOnly: {
    onAnnotationDragStateChange: true,
    onAnnotationFocus: true,
    onAnnotationBlur: true,
    onChange: true,
    onPress: true
  }
});

module.exports = BaiduMapView;
