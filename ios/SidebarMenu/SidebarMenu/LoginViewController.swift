//
//  LoginViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 9/21/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import Foundation
import UIKit

import FBSDKLoginKit
import FBSDKShareKit




class LoginViewController: UIViewController, FBSDKLoginButtonDelegate{
    /*!
     @abstract Sent to the delegate when the button was used to login.
     @param loginButton the sender
     @param result The results of the login
     @param error The error (if any) from the login
     */
//    public func loginButton(_ loginButton: FBSDKLoginButton!, didCompleteWith result: FBSDKLoginManagerLoginResult!, error: Error!) {
//        FBSDKGraphRequest.init(graphPath: "me", parameters: ["fields":"first_name, last_name, picture.type(large)"]).start { (connection, result, error) -> Void in
//            
//         //   let strFirstName: String = ((result as? AnyObject)!.objectForKey("first_name") as? String)!
//      //      let myData = result as Dictionary
//          //  let strLastName: String = ((result as? AnyObject)!.object(forKey: "last_name") as? String)!
//          
//            
//                 let strPictureURL: String = (result.objectForKey("picture")?.objectForKey("data")?.objectForKey("url") as? String)!
//              print(strPictureURL)
//            //
//            //            self.lblName.text = "Welcome, \(strFirstName) \(strLastName)"
//            //            self.ivUserProfileImage.image = UIImage(data: NSData(contentsOfURL: NSURL(string: strPictureURL)!)!)
//        }
//
//    }

    
    
    
    @IBOutlet weak var fbButton: FBSDKLoginButton!
   
    
    let defaults = UserDefaults.standard
    var auth: String = " "

    @IBAction func loginButton(_ sender: AnyObject) {
        
        
       
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RegistrationViewController")
        
        self.present(viewController, animated: false, completion: nil)
        
        
       
    }

    @IBAction func signupButton(_ sender: AnyObject) {
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "VerificationViewController")
        
        self.present(viewController, animated: false, completion: nil)
 
    
    }
    

    
    @IBOutlet weak var loginBt: UIButton!
    
    @IBOutlet weak var signUpBt: UIButton!
    
    

    
   // @IBOutlet weak var loginView : FBSDKLoginButton!
       override func viewWillAppear(_ animated: Bool) {
        
     
        
        let titleDict: NSDictionary = [NSForegroundColorAttributeName: UIColor.white]
        self.navigationController?.navigationBar.titleTextAttributes = titleDict as? [String : Any]
        
        

       
    }
    

    
    

//    
//    func loginButtonDidLogOut(_ loginButton: FBSDKLoginButton!)
//    {
//        let loginManager: FBSDKLoginManager = FBSDKLoginManager()
//        loginManager.logOut()
//        
//      //  ivUserProfileImage.image = nil
//      //  lblName.text = ""
//    
//    }
//    //    MARK: Other Methods
//    
//    func configureFacebook()
//    {
//        fbButton.readPermissions = ["public_profile", "email", "user_friends"];
//        fbButton.delegate = self
//    }
    
    override func viewDidLoad() {
    
       // configureFacebook()
        
        loginBt.layer.shadowOffset = CGSize(width: 5, height:5)
        loginBt.layer.shadowOpacity = 0.3
        loginBt.layer.shadowRadius = 2
        
        signUpBt.layer.shadowOffset = CGSize(width: 5, height:5)
        signUpBt.layer.shadowOpacity = 0.3
        signUpBt.layer.shadowRadius = 2
        
        
        fbButton.layer.shadowOffset = CGSize(width: 5, height:5)
        fbButton.layer.shadowOpacity = 0.3
        fbButton.layer.shadowRadius = 2
        
        if (FBSDKAccessToken.current() != nil)
        {
            // User is already logged in, do work such as go to next view controller.
        }
        else
        {
           
            fbButton.readPermissions = ["public_profile", "email", "user_friends"]
            fbButton.delegate = self
        }
        
    }


    
    func myToken(token: String)->String{
        auth = token
        print("Auth:\(auth)")
        
        return token
    }
    
    
    func loginButton(_ loginButton: FBSDKLoginButton!, didCompleteWith result: FBSDKLoginManagerLoginResult!, error: Error!) {
        print("User Logged In")
        returnUserData()
        
        if ((error) != nil)
        {
            // Process error
        }
        else if result.isCancelled {
            // Handle cancellations
        }
        else {
            // If you ask for multiple permissions at once, you
            // should check if specific permissions missing
            if result.grantedPermissions.contains("email")
            {
                // Do work
            }
        }
    }
    
    
    
    func loginButtonDidLogOut(_ loginButton: FBSDKLoginButton!) {
        print("User Logged Out")
    }
    
    
    
    func returnUserData()
    {
        FBSDKGraphRequest(graphPath: "me", parameters: ["fields": "id, name, first_name, last_name, email"]).start(completionHandler: { (connection, result, error) -> Void in
            
            if ((error) != nil)
            {
                // Process error
                print("Error: \(error)")
            }
            else
            {
                print("fetched user: \(result)")
                if let json = result as? Dictionary<String, AnyObject> {
                    
                    
                    if let first_name = json["first_name"] as? String{
                        
                           print(first_name)
                    
                    
                        if let last_name = json["last_name"] as? String{
                                  print(last_name)
                    
                            if let email = json["email"] as? String{
                                 print(email)
                    
                    
                                if let facebookID = json["id"] as? String{
                                print(facebookID)
                                    
                                    
                                    let parameters = ["first_name": first_name,
                                                      "last_name": last_name,
                                                      "email": email,
                                                      "facebookID": facebookID,
                                                      "platform": "iOS"

                                      ] as Dictionary<String, String>
                                    
                                    
                                    let myUrl = "http://82.76.188.13:3000/signup/facebookLogin"
                                    
                                //    let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
                                    
                                 //   print("myToken:\(myToken)")
                                    print("myURL:\(myUrl)")
                                    
                                    let request = NSMutableURLRequest(url: URL(string: myUrl)! as URL)
                                    
                                    let session = URLSession.shared
                                    request.httpMethod = "POST"
                                    
                                    //Note : Add the corresponding "Content-Type" and "Accept" header. In this example I had used the application/json.
                                    request.addValue("application/json", forHTTPHeaderField: "Content-Type")
                                    request.addValue("application/json", forHTTPHeaderField: "Accept")
                                  //  request.addValue(myToken, forHTTPHeaderField: "authorization")
                                    request.httpBody = try! JSONSerialization.data(withJSONObject: parameters, options: [] )
                                    
                                    let task = session.dataTask(with: request as URLRequest) { data, response, error in
                                        print(response)
                                        let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
                                        print("responseString = \(responseString)")
                                        do {
                                            let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
                                            
                                            if let parseJSON = myJSON {
                                                
                                                let collection = parseJSON as! Dictionary<String, AnyObject>
                                                
                                                // Now we can access value of First Name by its key
                                                if let userID = parseJSON["userID"] as? String{
                                                    print("USER_Facebook:\(userID)")
                                                    UserDefaults.standard.set(userID, forKey: "userID")
                                                    
                                                
                                                if let token = collection["token"]?["value"] as? String{
                                                    print("TOKEN:\(token)")
                                                    UserDefaults.standard.set(token, forKey: "token")
                                                    
                                                    
                                                    self.checkCars()
                                                }
                                                
                                                }
                                                
                                            }
                                        } catch {
                                            print(error)
                                        }
                                        
                                        
                                    }
                                    
                                    task.resume()
                                    
                                
                                    let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RevealViewController")
                                    
                                    self.present(viewController, animated: false, completion: nil)
                                    
                                }

                            }
                        }
                    }
                    
                        
                    
                }
                
//                let userName: String = (result.objectForKey("first_name") as? String)!
//                print("User Name is: \(userName)")
                
//           
//                if let userEmail = (result as AnyObject).value("email") as? NSString {
//                    print("User Email is: \(userEmail)")
//                }
                }

            
        })
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

    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }

}
