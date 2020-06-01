#import "RnMia.h"
#import <UIKit/UIKit.h>
#import "RCTUtils.h"

@implementation RnMia

RCT_EXPORT_MODULE()

- (dispatch_queue_t)methodQueue {
  return dispatch_get_main_queue();
}

# pragma mark - Mia Bridge API

/// Presents a checkout screen for given payment details
/// @param paymentID Payment ID retrieved from Easy
/// @param paymentURL Payment URL retrieved from Easy
/// @param redirectURL The URL used to redirect from Easy hosted web view
/// @param completion Callback invoked upon completion
///     - Returns empty parameters if successful.
///     - Returns an error dictionary object if payment failed.
/// @param cancellation Invoked if user cancelled payment process (typically by dismissing the WebView)
RCT_EXPORT_METHOD(checkoutWithPaymentID:(NSString *)paymentID
                  paymentURL:(NSString *)paymentURL
                  isEasyHostedWithRedirectURL:(NSString *)redirectURL
                  completion:(RCTResponseSenderBlock)completion
                  cancellation:(RCTResponseSenderBlock)cancellation) {

    void (^successHandler)(MiaCheckoutController *) = ^void (MiaCheckoutController *mia) {
        [[mia presentingViewController] dismissViewControllerAnimated:true completion:^{ completion(@[]); }];
    };

    void (^cancellationHandler)(MiaCheckoutController *) = ^void (MiaCheckoutController *mia) {
        [[mia presentingViewController] dismissViewControllerAnimated:true completion:^{ cancellation(@[]); }];
    };

    void (^failureHandler)(MiaCheckoutController *, NSError *) = ^void (MiaCheckoutController *mia, NSError *error) {

        NSMutableDictionary *userInfo = [error.userInfo mutableCopy];
        if (userInfo == nil) {
            userInfo = [[NSMutableDictionary alloc]initWithObjectsAndKeys:@"No userInfo", NSLocalizedFailureReasonErrorKey, nil];
        }
        [userInfo setValue:error.localizedDescription forKey:@"Error"];
        NSDictionary<NSString *, id> *rnError = RCTMakeError(error.localizedDescription, nil, userInfo);

        [[mia presentingViewController] dismissViewControllerAnimated:true completion:^{ completion(@[rnError]); }];
    };

    UIViewController* checkout =
    [[MiaSDK class]checkoutControllerForPaymentWithID:paymentID
                                           paymentURL:paymentURL
                          isEasyHostedWithRedirectURL:redirectURL
                                              success:successHandler
                                         cancellation:cancellationHandler
                                              failure:failureHandler];

    [[[[UIApplication sharedApplication]keyWindow]rootViewController]
     presentViewController:checkout animated:true completion:nil];
}

@end
