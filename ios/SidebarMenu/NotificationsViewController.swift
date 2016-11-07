//
//  NotificationsViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 9/14/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//


import UIKit
import BBBadgeBarButtonItem


class NotificationsViewController: UIViewController{
    
    var reachability = SplashViewController()
    static var title : String = " "
    
    @IBOutlet weak var menuButton: UIBarButtonItem!
    

    
    var window: UIWindow?
    
    @IBOutlet weak var buttonOwner: UIButton!
    var screenWidth: CGFloat = 0
    var screenHeight:CGFloat = 0
    
    
    @IBAction func doSomething(_ sender: AnyObject) {
        
         if(NotificationsViewController.title == "Notifica proprietarul"){
            
            showNotifications()
            
        }
         else{
            
            goCar()
        }
        
        
    }
    
    
    override func viewDidAppear(_ animated: Bool) {
        
       

        getUser()

    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        

        
        print("to?")
        
        print(UserDefaults.standard.string(forKey: "tokenFCM"))
        
//        if(UserDefaults.standard.string(forKey: "tokenFCM") != " "){
//            
            addSecurity()
//            
//        }
        
     
               let customButton = UIButton()
        customButton.setImage(UIImage(named: "bell.png"), for: UIControlState.normal)
        
        
        let frame = CGRect(x: 0, y: 0, width: 20, height: 20)
        customButton.frame = frame
        customButton.addTarget(self, action: #selector(NotificationsViewController.showBellNotifications), for: .touchUpInside)
        let barButton = BBBadgeBarButtonItem(customUIButton: customButton)
        
        buttonOwner.setTitle(NotificationsViewController.title, for: UIControlState.normal)
        buttonOwner.setImage(UIImage(named: "masini_inregistrate2.png"), for: UIControlState.normal)
        //    barButton?.badgeValue = "1"
        buttonOwner.imageEdgeInsets =  UIEdgeInsets(top: 10, left: 10, bottom: 3, right:200)
        buttonOwner.titleEdgeInsets =  UIEdgeInsets(top: 3, left: 0, bottom: 3, right: 10)
        buttonOwner.layer.shadowOffset = CGSize(width: 5, height:5)
        buttonOwner.layer.shadowOpacity = 0.3
        buttonOwner.layer.shadowRadius = 2
    
        barButton?.badgePadding = 1
        barButton?.badgeOriginX = 2
        
     
        if let wd = self.window {
            var vc = wd.rootViewController
            if(vc is UINavigationController){
                vc = (vc as! UINavigationController).visibleViewController
            }
            
            if(vc is NotificationsViewController){
            
                
             

            
            }
        }
        
        
        
     self.navigationItem.rightBarButtonItem = barButton
        self.navigationItem.rightBarButtonItem?.target = self
        self.navigationItem.rightBarButtonItem?.action = #selector(NotificationsViewController.showNotifications)

        
        let screenSize: CGRect = UIScreen.main.bounds
        screenWidth = screenSize.width
        screenHeight = screenSize.height
       
       // buttonOwner.backgroundColor = UIColor.redColor()
        buttonOwner.frame = CGRect(x: 0,y: 90,width: 500, height: 70)
        
       

              buttonOwner.setTitleColor(UIColor.white, for: UIControlState.normal)
        buttonOwner.layer.cornerRadius = 10
        
   
        
        
        if revealViewController() != nil {
            menuButton.target = revealViewController()
            menuButton.action = #selector(SWRevealViewController.revealToggle(_:))
            view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
        }
    }
    
    
    

    
    func goCar(){
        
        
        print("add car function")
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "CarsTableViewController")
        
        self.present(viewController, animated: false, completion: nil)

        
        
    }
    
       func addSecurity(){
        
        print("addSecurity called")
        let parameters = ["device_token": UserDefaults.standard.value(forKey: "tokenFCM") as! String,
                          "password": "nah",
                          "reg_ip": "82.76.188.13"
            ] as Dictionary<String, String>
        
        
        let myUrl = "http://82.76.188.13:3000/users/addSecurity/" + UserDefaults.standard.string(forKey: "userID")!
        
        
        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        
        print("myToken:\(myToken)")
        print("myURL:\(myUrl)")
        
        let request = NSMutableURLRequest(url: URL(string: myUrl)! as URL)
        
        let session = URLSession.shared
        request.httpMethod = "POST"
        
        //Note : Add the corresponding "Content-Type" and "Accept" header. In this example I had used the application/json.
        
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        request.addValue(myToken, forHTTPHeaderField: "authorization")
        request.httpBody = try! JSONSerialization.data(withJSONObject: parameters, options: [] )
        
        let task = session.dataTask(with: request as URLRequest) { data, response, error in
            guard data != nil else {
                
                
                return
            }
            
            
        }
        
        task.resume()
        
        
        
        
        
    }
    
    
    func showBellNotifications(){
        print("alandala")
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ReceivedNotifications")
        
        self.present(viewController, animated: false, completion: nil)
        
    }

    
    func showNotifications(){
        print("alandala")

        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "BarcodeViewController")
        
        self.present(viewController, animated: false, completion: nil)
        
    }
    
    func getUser(){
        
        print("getUser")
        
        let myURL = "http://82.76.188.13:3000/users/getUser/" + UserDefaults.standard.string(forKey: "userID")!
        
        let url = NSURL(string: myURL)
        
        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        
        let request = NSMutableURLRequest(url:url as! URL);
        request.httpMethod = "GET"
        
        request.addValue(myToken, forHTTPHeaderField: "Authorization")
        
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        let task = URLSession.shared.dataTask(with: request as URLRequest) {
            data, response, error in
            guard data != nil else {
             
                return
            }
            
            do {
                let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
                
                if let parseJSON = myJSON {
                    // Now we can access value of First Name by its key
                    if let fname = parseJSON["first_name"] as? String{
                        if let sname = parseJSON["last_name"] as? String{
                            
                            print(fname)
                            PersonalProfileViewController.name = fname
                            PersonalProfileViewController.sname = sname

                            if let email = parseJSON["email"] as? String{
                                
                                print(email)
                               PersonalProfileViewController.email = email
                                
                                if let phone = parseJSON["phone_number"] as? String{
                                     PersonalProfileViewController.phone = phone
                               
                                }
                                
                            }}
                        
                    }
                }
            }catch {
                print(error)
            }
            
        }
        task.resume()
        
        
        
    }
    
    
    
    func activeNotification(){
        
        print("Am notificare activa? ")
        
        let myUrl = "http://82.76.188.13:3000/notifications/getNotification/" + UserDefaults.standard.string(forKey: "userID")!
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
            
 
            
            do{
                let allContacts = try JSONSerialization.jsonObject(with: data! , options: JSONSerialization.ReadingOptions())
                
                
                if let json = allContacts as? Array<AnyObject> {
                    if(json.count != 0){
                        print("Am notificare activa? \(json)")
                        for index in 0...json.count-1 {
                            
                            let contact : AnyObject? = json[index]
                            print("Contact:\(contact)")
                            
                            let collection = contact! as! Dictionary<String, AnyObject>
                            print("Collection:\(collection)")
                        }
                    }
                }
            }catch {
                print(error)
            }
        }
        task.resume()
        
        
    
    }

      
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
 
    func getNotifications(badge: String){
        
        

        
    }
    
    
    
 


    
    
}
