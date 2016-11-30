//
//  RCTConvert+AMapKit.m
//  RCTAMap
//
//  Created by yiyang on 16/2/29.
//  Copyright © 2016年 creditease. All rights reserved.
//

#import "RCTImageSource.h"

#import "RCTConvert+BaiduMapKit.h"
#import "RCTConvert+CoreLocation.h"
#import "RCTBaiduMapAnnotation.h"
#import "RCTBaiduMapOverlay.h"

@implementation RCTConvert (BaiduMapKit)

+ (BMKCoordinateSpan)BMKCoordinateSpan:(id)json
{
    json = [self NSDictionary:json];
    return (BMKCoordinateSpan){
        [self CLLocationDegrees:json[@"latitudeDelta"]],
        [self CLLocationDegrees:json[@"longitudeDelta"]]
    };
}

+ (BMKCoordinateRegion)BMKCoordinateRegion:(id)json
{
    return (BMKCoordinateRegion) {
        [self CLLocationCoordinate2D:json],
        [self BMKCoordinateSpan:json]
    };
}

RCT_ENUM_CONVERTER(BMKMapType, (@{
                                 @"standard": @(BMKMapTypeStandard),
                                 @"satellite": @(BMKMapTypeSatellite),
                                 }), BMKMapTypeStandard, integerValue)

+ (RCTBaiduMapAnnotation *)RCTBaiduMapAnnotation:(id)json
{
    json = [self NSDictionary:json];
    RCTBaiduMapAnnotation *annotation = [RCTBaiduMapAnnotation new];
    annotation.coordinate = [self CLLocationCoordinate2D:json];
    annotation.draggable = [self BOOL:json[@"draggable"]];
    annotation.title = [self NSString:json[@"title"]];
    annotation.subtitle = [self NSString:json[@"subtitle"]];
    annotation.identifier = [self NSString:json[@"id"]];
    annotation.hasLeftCallout = [self BOOL:json[@"hasLeftCallout"]];
    annotation.hasRightCallout = [self BOOL:json[@"hasRightCallout"]];
    annotation.animateDrop = [self BOOL:json[@"animateDrop"]];
    annotation.tintColor = [self UIColor:json[@"tintColor"]];
    annotation.image = [self getImageInJson:json[@"image"]];
    annotation.viewIndex = [self NSInteger:json[@"viewIndex"] ? :@(NSNotFound)];
    annotation.leftCalloutViewIndex =
    [self NSInteger:json[@"leftCalloutViewIndex"] ?: @(NSNotFound)];
    annotation.rightCalloutViewIndex =
    [self NSInteger:json[@"rightCalloutViewIndex"] ?: @(NSNotFound)];
    annotation.detailCalloutViewIndex =
    [self NSInteger:json[@"detailCalloutViewIndex"] ?: @(NSNotFound)];
    return annotation;
}
+ (UIImage *) getImageInJson:(id)json
{
    NSString *fontName = [self NSString:json[@"font"]];
    if(fontName){
        NSString *glyph = [self NSString:json[@"glyph"]];
        NSInteger *fontSize = [self NSInteger:json[@"size"]];
        UIColor *color = [self UIColor:json[@"color"]];
        UIFont *font = [UIFont fontWithName:fontName size:(int)fontSize];
        NSAttributedString *attributedString = [[NSAttributedString alloc] initWithString:glyph attributes:@{NSFontAttributeName: font, NSForegroundColorAttributeName: color}];
        CGSize iconSize = [attributedString size];
        UIGraphicsBeginImageContextWithOptions(iconSize, NO, 0.0);
        [attributedString drawAtPoint:CGPointMake(0, 0)];
        return UIGraphicsGetImageFromCurrentImageContext();
    }else{
        return [self UIImage:json];
    }
}

RCT_ARRAY_CONVERTER(RCTBaiduMapAnnotation)

+ (RCTBaiduMapOverlay *)RCTBaiduMapOverlay:(id)json
{
    json = [self NSDictionary:json];
    
    NSArray<NSDictionary *> *locations = [self NSDictionaryArray:json[@"coordinates"]];
    CLLocationCoordinate2D coordinates[locations.count];
    NSUInteger index = 0;
    for (NSDictionary *location in locations) {
        coordinates[index++] = [self CLLocationCoordinate2D:location];
    }
    
    RCTBaiduMapOverlay *overlay = [RCTBaiduMapOverlay polylineWithCoordinates:coordinates count:locations.count];
    
    overlay.strokeColor = [self UIColor:json[@"strokeColor"]];
    overlay.identifier = [self NSString:json[@"id"]];
    overlay.lineWidth = [self CGFloat:json[@"lineWidth"] ?: @1];
    return overlay;

}

RCT_ARRAY_CONVERTER(RCTBaiduMapOverlay)

+ (BMKLocationViewDisplayParam *)RCTBaiduMapLocationViewDisplayParam:(id)json
{
    json = [self NSDictionary:json];
    BMKLocationViewDisplayParam *param = [BMKLocationViewDisplayParam new];
    param.locationViewOffsetX = [self float:json[@"offsetX"]];
    param.locationViewOffsetY = [self float:json[@"offsetY"]];
    param.isAccuracyCircleShow = [self BOOL:json[@"showAccuracyCircle"]];
    param.accuracyCircleFillColor = [self UIColor:json[@"accuracyCircleFillColor"]];
    param.accuracyCircleStrokeColor = [self UIColor:json[@"accuracyCircleStrokeColor"]];
    param.isRotateAngleValid = [self BOOL:json[@"rotateAngleValid"]];
//    param.locationViewImgName = [self NSString:json[@"image"]];
    RCTImageSource *imageSource = [self RCTImageSource:json[@"image"]];
    if (imageSource != nil) {
        NSString *imgName = imageSource.request.URL.lastPathComponent;
        if (imgName != nil) {
            imgName = [imgName stringByDeletingPathExtension];
            param.locationViewImgName = imgName;
        }
    }
    
    return param;
}

@end
