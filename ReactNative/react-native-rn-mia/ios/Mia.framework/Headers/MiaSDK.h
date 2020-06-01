//
//  MiaSDK.h
//  Mia
//
//  Created by Luke on 17/02/2020.
//  Copyright © 2020 Nets AS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MiaCheckoutController.h"

NS_ASSUME_NONNULL_BEGIN

@interface MiaSDK : NSObject

/// Returns a view controller that hosts checkout web view.
/// Pass a `redirectURL` iff Easy-hosted web view is presented.
///
/// Merchant-hosted payment does not require a `redirectURL`.
/// Pass `nil` `redirectURL` for merchant-hosted payment.
///
/// @param paymentID Payment ID
/// @param paymentURL Payment URL
/// @param redirectURL The URL used to redirect from Easy hosted web view
/// @param success Success
/// @param cancellation Cancellation
/// @param failure Failure
///
+ (MiaCheckoutController *)checkoutControllerForPaymentWithID:(NSString *)paymentID
                                                   paymentURL:(NSString *)paymentURL
                                  isEasyHostedWithRedirectURL:(NSString * _Nullable)redirectURL
                                                      success:(void(^)(MiaCheckoutController *))success
                                                 cancellation:(void(^)(MiaCheckoutController *))cancellation
                                                      failure:(void(^)(MiaCheckoutController *, NSError *))failure;


@end

NS_ASSUME_NONNULL_END
