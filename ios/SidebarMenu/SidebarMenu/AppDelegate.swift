//
//  AppDelegate.swift
//  TestingFirebase
//
//  Created by Claudia Mateas on 9/23/16.
//  Copyright © 2016 Claudia Mateas. All rights reserved.
//

import UIKit
import CoreData
import Firebase
import FirebaseInstanceID
import FirebaseMessaging
import UserNotificationsUI
import UserNotifications
import CoreLocation
import Fabric
import Crashlytics
import IQKeyboardManagerSwift
import FBSDKLoginKit
import AVFoundation
import SystemConfiguration
import Foundation


//public class Reachability {
//    
//    class func isInternetAvailable() -> Bool
//    {
//        var zeroAddress = sockaddr_in()
//        zeroAddress.sin_len = UInt8(MemoryLayout.size(ofValue: zeroAddress))
//        zeroAddress.sin_family = sa_family_t(AF_INET)
//        
//        let defaultRouteReachability = withUnsafePointer(to: &zeroAddress) {
//            $0.withMemoryRebound(to: sockaddr.self, capacity: 1) {zeroSockAddress in
//                SCNetworkReachabilityCreateWithAddress(nil, zeroSockAddress)
//            }
//        }
//        
//        var flags = SCNetworkReachabilityFlags()
//        if !SCNetworkReachabilityGetFlags(defaultRouteReachability!, &flags) {
//            return false
//        }
//        let isReachable = (flags.rawValue & UInt32(kSCNetworkFlagsReachable)) != 0
//        let needsConnection = (flags.rawValue & UInt32(kSCNetworkFlagsConnectionRequired)) != 0
//        return (isReachable && !needsConnection)
//    }
//}
@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    
    var answer = AnswerViewController()
    var mapview = MapViewController()
    var review = ReviewViewController()
    
    var window: UIWindow?
   var locationManager = CLLocationManager()
    
    let myNotification = Notification.Name(rawValue:"MyNotification")
    let progress = ProgressViewController()
    
    let notification = NotificationsViewController()
    let defauls = UserDefaults.standard
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        IQKeyboardManager.sharedManager().enable = true

        
//        if(Reachability.isInternetAvailable() == true){
        
        
      
        
           Fabric.with([Crashlytics.self])
        
        
           locationManager.requestWhenInUseAuthorization()
        
        // [START register_for_notifications]
        if #available(iOS 10.0, *) {
            let authOptions : UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {_,_ in })
            
            // For iOS 10 display notification (sent via APNS)
            UNUserNotificationCenter.current().delegate = self
            // For iOS 10 data message (sent via FCM)
           // FIRMessaging.messaging().remoteMessageDelegate = self
            application.registerForRemoteNotifications()
            
        } else {
            
            let settings = UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            application.registerUserNotificationSettings(settings)
            application.registerForRemoteNotifications()
        }
        
        
        
        
        
        
        // [END register_for_notifications]
        
        
        FIRApp.configure()
        
        
       
        let nc = NotificationCenter.default
        nc.addObserver(forName:myNotification, object:nil, queue:nil, using:catchNotification)
        
        
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(self.tokenRefreshNotification),
                                               name: NSNotification.Name.firInstanceIDTokenRefresh,
                                               object: nil)
        
        
       //  Override point for customization after application launch.
        
        
        
//        else{
//            
//            
//            let alert = UIAlertView(title: "No Internet Connection", message: "Make sure your device is connected to the internet.", delegate: nil, cancelButtonTitle: "OK")
//            alert.delegate = self
//            alert.show()
//            
//            
//            
//        }
  
        
        
      //     return FBSDKApplicationDelegate.sharedInstance().application(application, didFinishLaunchingWithOptions: launchOptions)
    return true
    
    }
    
    func alertView(View: UIAlertView!, clickedButtonAtIndex buttonIndex: Int){
        
        switch buttonIndex{
            
        case 1:
           break
        default: break
            //Some code here..
            
        }
    }
    

    func catchNotification(notification:Notification) -> Void {
        print("Catch notification")
        
        guard let userInfo = notification.userInfo,
            let message  = userInfo["message"] as? String,
            let date     = userInfo["date"]    as? Date else {
                print("No userInfo found in notification")
                return
        }
        let mynotif = UILocalNotification()
        
        mynotif.soundName = UILocalNotificationDefaultSoundName
        UIApplication.shared.scheduleLocalNotification(mynotif)
    }

    
//     func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [NSObject : AnyObject], fetchCompletionHandler completionHandler: (UIBackgroundFetchResult) -> Void) {
    
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> ()) {
    
        if (application.applicationState == .inactive) {
            print("inactive")
            completionHandler(.newData)
            
            updateFromSyncData(userInfo: userInfo as NSDictionary)
            
        }
        
        if (application.applicationState == .active) {
            
            completionHandler(.newData)
            print("userInfooooo\(userInfo)")
            
            
           
            updateFromSyncData(userInfo: userInfo as NSDictionary)
            
        }
        
        if (application.applicationState == .background) {
            
            print("background")

            
            completionHandler(.newData)
            
            updateFromSyncData(userInfo: userInfo as NSDictionary)
            
        }
        
    }
    
    
    
    
//    @available(iOS 10.0, *)
//    func userNotificationCenter(_ center: UNUserNotificationCenter,  willPresent notification: UNNotification, withCompletionHandler   completionHandler: @escaping (_ options:   UNNotificationPresentationOptions) -> Void) {
//        print("Handle push from foreground")
//       
//        // custom code to handle push while app is in the foreground
//        print("\(notification.request.content.userInfo)")
//    }
    
    @available(iOS 10.0, *)
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        print("Handle push from background or closed")
        // if you set a member variable in didReceiveRemoteNotification, you  will know if this is from closed or background
         updateFromSyncData(userInfo: response.notification.request.content.userInfo as NSDictionary)
        print("\(response.notification.request.content.userInfo)")
    }
    

    
    
    func updateFromSyncData(userInfo: NSDictionary) -> Void {
        
        print("dooh")
        
        print(userInfo["car_id"] as? String)
        
        print(userInfo["longitude"] as? String)
        print(userInfo["latitude"] as? String)
        print(userInfo["answered_at"] as? String)
        
        if let aps = userInfo["aps"] as? NSDictionary {
            if let mydict = aps["alert"] as? NSDictionary{
            print("body: \(mydict["body"] as? String)")
                if let myType = userInfo["notification_type"] as? String{
                    
                    print("MYTYPE:\(myType)")
               
                switch myType {
                    
                    
                case "sender" :
                    
                    // am primit notificare
                   
                        ReviewViewController.notifID = (userInfo["notification_id"] as? String)!
                        print("ReviewViewController.notifID :\(ReviewViewController.notifID )")
                        let systemSoundID: SystemSoundID = 1016
                        AudioServicesPlaySystemSound (systemSoundID)
                        AnswerViewController.notifID = (userInfo["notification_id"] as? String)!
                        ProgressViewController.notifID = (userInfo["notification_id"] as? String)!
                        AnswerViewController.plates = (userInfo["car_id"] as? String)!
                        self.window = UIWindow(frame: UIScreen.main.bounds)
                        let storyBoard = UIStoryboard(name: "Main", bundle: nil)
                        let viewController = storyBoard.instantiateViewController(withIdentifier: "AnswerViewController") as UIViewController
                        self.window?.rootViewController = viewController
                        self.window?.makeKeyAndVisible()

                    break
               
                case "review":
                    
                    
                    let notifID = userInfo["notification_id"] as? String
                    
                    let myUrl = "http://82.76.188.13:3000/notifications/getNotification/" + notifID!
                    print("myURL:\(myUrl)")
                    
                    let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
                    
                    let url=NSURL(string: myUrl)
                    print("myToken:\(myToken)")
                    print("GetNotifmyURL:\(myUrl)")
                    let request = NSMutableURLRequest(url:url as! URL);
                    request.httpMethod = "GET"
                    
                    request.addValue(myToken, forHTTPHeaderField: "authorization")
                    
                    
                    
                    let task = URLSession.shared.dataTask(with: request as URLRequest){
                        data, response, error in
         
                        
                        let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
                         print("responseStringNotif = \(responseString)")
                        
                        do{
                            let allContacts = try JSONSerialization.jsonObject(with: data! , options: JSONSerialization.ReadingOptions())

                                        let collection = allContacts as! Dictionary<String, AnyObject>
                                        print("Collection:\(collection)")
                                        
                                        if let feedback = collection["review"]?["feedback"] as? String{
                                            print(feedback)
                                        SummaryViewController.feedback = feedback
                                        }
                                        
                                        if let created = collection["create_date"] as? String{
                                            SummaryViewController.myDate = created
                                             print(created)
                                        }
                                        
                                        if let answered = collection["answer"]?["answered_at"] as? String{
                                            SummaryViewController.answerAt = answered
                                             print(answered)
                                        }
                                        
                                        if let vehicle = collection["vehicle"] as? String{
                                            SummaryViewController.carText = vehicle
                                             print(vehicle)
                                        }
                                        SummaryViewController.answer = "i'm coming"
                        }catch {
                            print(error)
                        }
                    }
                    task.resume()
                    
                    self.window = UIWindow(frame: UIScreen.main.bounds)
                    let storyBoard = UIStoryboard(name: "Main", bundle: nil)
                    let viewController = storyBoard.instantiateViewController(withIdentifier: "SummaryViewController") as UIViewController
                    self.window?.rootViewController = viewController
                    self.window?.makeKeyAndVisible()
                    
                    
                    
                
                    break
                case "receiver" :
                    
                    // am trimis notificare si am primit raspuns
                    print("MYINFO:\(userInfo)")
                    let systemSoundID: SystemSoundID = 1016
                    AudioServicesPlaySystemSound (systemSoundID)
                    
                    MapViewController.invalid = true
                    ReviewViewController.notifID = (userInfo["notification_id"] as? String)!
                    let myDate = userInfo["answered_at"] as? String

                    let dateFormatter = DateFormatter()
                    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
                    let date = dateFormatter.date(from: myDate!)!
                    dateFormatter.dateFormat = "HH:mm:ss"
                    let dateString = dateFormatter.string(from: date)

                    
                    MapViewController.latitude = (userInfo["latitude"] as? String)!
                    MapViewController.longitude = (userInfo["longitude"] as? String)!
                    
                    MapViewController.createHour = (userInfo["answered_at"] as? String)!
                    
//                    print("ESTIMATED_TIME: \((userInfo["estimated_time"] as? String)!)")
                    
                    if let estimated = userInfo["estimated_time"] as? String {
                        if(estimated != "100"){
                         ProgressViewController.minutes = estimated
                        }
                        else{
                            
                            review.sendReview(response: "false", time: "0")
                            self.window = UIWindow(frame: UIScreen.main.bounds)
                            let storyBoard = UIStoryboard(name: "Main", bundle: nil)
                            let viewController = storyBoard.instantiateViewController(withIdentifier: "SummaryViewController") as UIViewController
                            self.window?.rootViewController = viewController
                            self.window?.makeKeyAndVisible()
                            
                            
                        }
                        print("ESTIMATED_TIME: \(estimated)")
                            
                            
                        
                    }
                   
                    ProgressViewController.responseHour = dateString
                    ProgressViewController.plates = (userInfo["car_id"] as? String)!

                    ProgressViewController.myType = true
                    
                    
                    SummaryViewController.carText = (userInfo["car_id"] as? String)!
                    SummaryViewController.myDate = (userInfo["answered_at"] as? String)!
                    SummaryViewController.answerAt = (userInfo["answered_at"] as? String)!
                    
                    if( userInfo["estimated_time"]as? String != "100"){
                    self.window = UIWindow(frame: UIScreen.main.bounds)
                    let storyBoard = UIStoryboard(name: "Main", bundle: nil)
                    let viewController = storyBoard.instantiateViewController(withIdentifier: "ProgressViewController") as UIViewController
                    self.window?.rootViewController = viewController
                    self.window?.makeKeyAndVisible()
                    }else{
                        self.window = UIWindow(frame: UIScreen.main.bounds)
                        let storyBoard = UIStoryboard(name: "Main", bundle: nil)
                        let viewController = storyBoard.instantiateViewController(withIdentifier: "SummaryViewController") as UIViewController
                        self.window?.rootViewController = viewController
                        self.window?.makeKeyAndVisible()
                    }
                    break
                    
                case "extended" :
                    print("Extendeeeedddd notfication type")
                    ProgressViewController.crash = true
                    ProgressViewController.myType = true
                    
                    break
                case "read":
                    print("readdddddd")
                    break
                default:
                    print("Nahh")
                }
                }
        
            // Show payload in SecondViewController etc.
            }
                
            
        }
        
    }
    // [END receive_message]
    
    // [START refresh_token]
    func tokenRefreshNotification(notification: NSNotification) {
        if let refreshedToken = FIRInstanceID.instanceID().token() {
            print("InstanceID token: \(refreshedToken)")
     self.defauls.set(refreshedToken, forKey: "tokenFCM")
            connectToFcm()

        }
        
        // Connect to FCM since connection may have failed when attempted before having a token.
        connectToFcm()
    }
    // [END refresh_token]
    
    // [START connect_to_fcm]
    func connectToFcm() {
        FIRMessaging.messaging().connect { (error) in
            if (error != nil) {
                print("Unable to connect with FCM. \(error)")
            } else {
                print("Connected to FCM.")
            }
        }
    }
    // [END connect_to_fcm]
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        connectToFcm()
       
     
    }
    
    // [START disconnect_from_fcm]
    func applicationDidEnterBackground(_ application: UIApplication) {
        FIRMessaging.messaging().disconnect()
        print("Disconnected from FCM.")
    }
    // [END disconnect_from_fcm]
    
    
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
    
        print("lalaalala")
        //Tricky line
        FIRInstanceID.instanceID().setAPNSToken(deviceToken as Data, type: FIRInstanceIDAPNSTokenType.unknown)
        print("Device Token:", deviceToken)
        

        

    }
    
    
    func application(application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("EEEEEEError : Fail to register to APNS : \(error)")
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }
    
    func application(application: UIApplication, openURL url: URL, sourceApplication: String?, annotation: AnyObject) -> Bool
    {
        return FBSDKApplicationDelegate.sharedInstance().application(application, open: url as URL!, sourceApplication: sourceApplication, annotation: annotation)
    }
        
    
    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        // Saves changes in the application's managed object context before the application terminates.
     //   self.saveContext()
        
        
        
        let loginManager: FBSDKLoginManager = FBSDKLoginManager()
        loginManager.logOut()
    }
    
    // MARK: - Core Data stack
    
    @available(iOS 10.0, *)
    lazy var persistentContainer: NSPersistentContainer = {
        /*
         The persistent container for the application. This implementation
         creates and returns a container, having loaded the store for the
         application to it. This property is optional since there are legitimate
         error conditions that could cause the creation of the store to fail.
         */
        let container = NSPersistentContainer(name: "SidebarMenu")
        container.loadPersistentStores(completionHandler: { (storeDescription, error) in
            if let error = error as NSError? {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                
                /*
                 Typical reasons for an error here include:
                 * The parent directory does not exist, cannot be created, or disallows writing.
                 * The persistent store is not accessible, due to permissions or data protection when the device is locked.
                 * The device is out of space.
                 * The store could not be migrated to the current model version.
                 Check the error message to determine what the actual problem was.
                 */
                //fatalError("Unresolved error \(error), \(error.userInfo)")
            }
        })
        return container
    }()
    
    // MARK: - Core Data Saving support
    
//    func saveContext () {
//        if #available(iOS 10.0, *) {
//            let context = persistentContainer.viewContext
//        } else {
//            // Fallback on earlier versions
//        }
//        if connect.hasChanges {
//            do {
//                try connect.save()
//            } catch {
//                // Replace this implementation with code to handle the error appropriately.
//                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
//                _ = error as NSError
//                //  fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
//            }
//        }
//    }
    
    
    public func application(_ app: UIApplication, open url: URL, options: [UIApplicationOpenURLOptionsKey : Any] = [:]) -> Bool {
        
        return FBSDKApplicationDelegate.sharedInstance().application(
            app,
            open: url as URL!,
            sourceApplication: options[UIApplicationOpenURLOptionsKey.sourceApplication] as! String,
            annotation: options[UIApplicationOpenURLOptionsKey.annotation]
        )
    }
    
    public func application(_ application: UIApplication, open url: URL, sourceApplication: String?, annotation: Any) -> Bool {
        return FBSDKApplicationDelegate.sharedInstance().application(
            application,
            open: url as URL!,
            sourceApplication: sourceApplication,
            annotation: annotation)
    }
    
    
    
    
    
}


//@available(iOS 10, *)
//extension AppDelegate : UNUserNotificationCenterDelegate {
//    
//    // Receive displayed notifications for iOS 10 devices.
//    private func userNotificationCenter(center: UNUserNotificationCenter,
//                                        willPresentNotification notification: UNNotification,
//                                        withCompletionHandler completionHandler: (UNNotificationPresentationOptions) -> Void) {
//        let userInfo = notification.request.content.userInfo
//        // Print message ID.
//        print("Message ID: \(userInfo["gcm.message_id"]!)")
//        
//        // Print full message.
//        print("%@", userInfo)
//     
//    }
//}
//
//extension AppDelegate : FIRMessagingDelegate {
//    // Receive data message on iOS 10 devices.
//    func applicationReceivedRemoteMessage(_ remoteMessage: FIRMessagingRemoteMessage) {
//        print("%@", remoteMessage.appData)
//    }
//}




