//
//  SplashViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 10/4/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit

import Foundation
import SystemConfiguration



class SplashViewController: UIViewController {
    var window: UIWindow?
    var defaults  = UserDefaults.standard
    var overlayView = UIView()
    
    class var shared: SplashViewController {
        struct Static {
            static let instance: SplashViewController = SplashViewController()
        }
        return Static.instance
    }
    
    
var reachability: Reachability?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        setupReachability(hostName: nil, useClosures: true)
        startNotifier()
        
        let dispatchTime = DispatchTime.now()
        DispatchQueue.main.asyncAfter(deadline: dispatchTime) {
            self.stopNotifier()
            self.setupReachability(hostName: "google.com", useClosures: true)
            self.startNotifier()
            
        }
        
//        if Reachability.isInternetAvailable() == true {
        
        
//        } else {
//            print("Internet connection FAILED")
//            let alert = UIAlertView(title: "No Internet Connection", message: "Make sure your device is connected to the internet.", delegate: nil, cancelButtonTitle: "OK")
//            alert.show()
//        }

        

           
            
        
  

    }
    
    func popOverlay(){
        print("popOverlay")
        
        
        let myView = UIView()
        myView.frame = self.view.frame
        myView.backgroundColor = UIColor.groupTableViewBackground   //.withAlphaComponent(0.6)
        let imageView = UIImageView(frame: CGRect(x: 10, y:55, width: 300, height: 400))
        imageView.image = UIImage(named: "nointernet.png")

        
        let retry = UIButton(frame: CGRect(x: 90, y:480, width: 60, height: 20))
        retry.setTitle("Retry", for: UIControlState.normal)
        retry.backgroundColor = UIColor.gray
        retry.layer.cornerRadius = 3
        retry.setTitleColor(UIColor.white, for: UIControlState.normal)
        retry.addTarget(self, action: "retryAction", for: UIControlEvents.touchUpInside)
        retry.center.x = myView.center.x // for horizontal
        //retry.center.y = myView.center.y // for vertical
        
        
        myView.addSubview(imageView)
        myView.addSubview(retry)
        window?.addSubview(myView)
        
    }
    
    
 
    
    public func showOverlay() {
        if  let appDelegate = UIApplication.shared.delegate as? AppDelegate,
            let window = appDelegate.window {
            overlayView.frame = self.view.frame
            overlayView.center = CGPoint(x: window.frame.width / 2.0, y: window.frame.height / 2.0)
            overlayView.backgroundColor = UIColor.lightGray
            overlayView.clipsToBounds = true
            
            let label = UILabel(frame: CGRect(x: 70, y:45, width: 60, height: 20))
            label.textColor = UIColor.white
            label.text = "No internet connection"
            
            let retry = UIButton(frame: CGRect(x: 70, y:70, width: 60, height: 20))
            retry.setTitle("Retry", for: UIControlState.normal)
            retry.tintColor = UIColor.white
            retry.addTarget(self, action: #selector(SplashViewController.retryAction), for: UIControlEvents.touchUpInside)
            
            
            overlayView.addSubview(label)
            overlayView.addSubview(retry)
            window.addSubview(overlayView)
            
            
        }
    }
    
    
    func retryAction(){
        
        self.stopNotifier()
        self.setupReachability(hostName: "google.com", useClosures: true)
        self.startNotifier()
        
    }
    public func hideOverlayView() {
       
        overlayView.removeFromSuperview()
    }
   
    
    
    func alertView(View: UIAlertView!, clickedButtonAtIndex buttonIndex: Int){
        
        switch buttonIndex{
            
        case 1:
           Refresh()
            break
            
        default: break
            //Some code here..
            
        }
    }
    
    func Refresh() {
        // Do some reloading of data and update the table view's data source
        // Fetch more objects from a web service, for example...
       UIApplication.shared.keyWindow?.rootViewController = storyboard!.instantiateViewController(withIdentifier: "SplashViewController")
      
    }

    
    func checkCars(){
        if let userID = UserDefaults.standard.string(forKey: "userID") {
            print("MOrtii:\(userID)")
            
        }
        
        if let token = UserDefaults.standard.string(forKey: "token"){
            print("Matii:\(token)")
        }
        
        
        
        
        _ = ["content-type": "application/json",
             "authorization": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlNTBmNjkwMjllZGQyNGE4OTk4ZTBkIiwiZW1haWwiOiJqZW51In0.TK1mqgYXMOWCRGTLe62JbkQYzCnq4Q7uVol3K-2-438",
             "cache-control": "no-cache",
             "postman-token": "e5de12cc-7c04-f8e8-212f-512b71da0b19"]
        
        let myUrl = "http://82.76.188.13:3000/users/getCars/" + UserDefaults.standard.string(forKey: "userID")!
        print("myURL:\(myUrl)")
        
        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        
        let url=NSURL(string: myUrl)
        print("myToken:\(myToken)")
        print("GetCarsmyURL:\(myUrl)")
        let request = NSMutableURLRequest(url:url as! URL);
        
        request.addValue(myToken, forHTTPHeaderField: "authorization")
        
        
        let task = URLSession.shared.dataTask(with: request as URLRequest) {
            data, response, error in
            
            
            
            
            //let allContactsData=NSData(contentsOf:url as! URL)
            
            let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            print("responseString = \(responseString)")
            
            // Set request HTTP method to GET. It could be POST as well
            
            do{
                let allContacts = try JSONSerialization.jsonObject(with: data! , options: JSONSerialization.ReadingOptions())
                
                
                if let json = allContacts as? Array<AnyObject> {
                    if(json.count != 0){
                       NotificationsViewController.title = "Notifica proprietarul"
                        
                    }else{
                        
                          NotificationsViewController.title = "Adauga masina"
                    }
                }
            }catch _ as NSError{
                
            }
        }
        task.resume()
        
        
        
        
        
        
    }
    


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    
    func setupReachability(hostName: String?, useClosures: Bool) {
      
        
       
        
        let reachability = hostName == nil ? Reachability() : Reachability(hostname: hostName!)
        self.reachability = reachability
        
        if useClosures {
            reachability?.whenReachable = { reachability in
                DispatchQueue.main.async {
                    self.hideOverlayView()
                   
                        if (self.defaults.object(forKey: "userID") != nil) {
                            
                            
                            
                            
                            self.checkCars()
                            
                            self.window = UIWindow(frame: UIScreen.main.bounds)
                            let storyBoard = UIStoryboard(name: "Main", bundle: nil)
                            let viewController = storyBoard.instantiateViewController(withIdentifier: "RevealViewController") as UIViewController
                            self.window?.rootViewController = viewController
                            self.window?.makeKeyAndVisible()
                        }
                        else{
                            
                            self.window = UIWindow(frame: UIScreen.main.bounds)
                            let storyBoard = UIStoryboard(name: "Main", bundle: nil)
                            let viewController = storyBoard.instantiateViewController(withIdentifier: "LoginViewController") as UIViewController
                            self.window?.rootViewController = viewController
                            self.window?.makeKeyAndVisible()
                            
                        }
                    }

                  
                
            }
            reachability?.whenUnreachable = { reachability in
                DispatchQueue.main.async {
//                    let alert = UIAlertView(title: "No Internet Connection", message: "Make sure your device is connected to the internet.", delegate: nil, cancelButtonTitle: "OK")
//                     alert.show()
          self.popOverlay()
                }
            }
        } else {
            NotificationCenter.default.addObserver(self, selector: #selector(SplashViewController.reachabilityChanged(_:)), name: ReachabilityChangedNotification, object: reachability)
        }
    }
    
    func startNotifier() {
        print("--- start notifier")
        do {
            try reachability?.startNotifier()
        } catch {
            
            return
        }
    }
    
    func stopNotifier() {
        print("--- stop notifier")
        reachability?.stopNotifier()
        NotificationCenter.default.removeObserver(self, name: ReachabilityChangedNotification, object: nil)
        reachability = nil
    }
    
    
    func reachabilityChanged(_ note: Notification) {
        let reachability = note.object as! Reachability
        
        if reachability.isReachable {
          
        } else {
          
        }
    }
    
    deinit {
        stopNotifier()
    }

    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
