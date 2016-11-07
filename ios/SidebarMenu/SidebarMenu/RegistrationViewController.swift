//
//  RegistrationViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 10/8/16.
//  Copyright © 2016 AppCoda. All rights reserved.
// Intra in cont ViewController

import UIKit
import JVFloatLabeledTextField
import FBSDKLoginKit

@objc protocol RegistrationViewControllerDelegate
{
    func keyboardWillShowWithSize(size:CGSize, andDuration duration:TimeInterval)
    func keyboardWillHideWithSize(size:CGSize,andDuration duration:TimeInterval)
}

class RegistrationViewController: UIViewController, UITextFieldDelegate, FBSDKLoginButtonDelegate {
    
    @IBOutlet weak var resetBt: UIButton!
    @IBOutlet weak var signInBt: UIButton!
    @IBOutlet var registerButton: UIBarButtonItem!
    
    @IBOutlet var fbButton: FBSDKLoginButton!
     let placeholderColor = UIColor(red: 0, green: 122/255, blue: 255/255, alpha: 1)
 
     var keyboardDelegate:RegistrationViewControllerDelegate?
    
    let notificationCenter = NotificationCenter.default
    
    let signUpWindowPosition:CGPoint=CGPoint(x: 505, y: 285)
  
    @IBOutlet var emailText: JVFloatLabeledTextField!

    @IBOutlet var backBtn: UIBarButtonItem!
    
    @IBOutlet var passwordText: JVFloatLabeledTextField!
    
    var auth: String = " "
    
    @IBAction func resetButton(_ sender: AnyObject) {
        
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ResetPasswordViewController")
        
        self.present(viewController, animated: false, completion: nil)
    }
  
    @IBAction func loginButton(_ sender: AnyObject) {
        
        DispatchQueue.main.asyncAfter(deadline: .now() ) {
            self.loginNow()
        }
    //    startConnection()
        
        
        
        
        
        

        
    }
    
    
    
    
    
    @IBOutlet weak var scrollView: UIScrollView!
  
    @IBOutlet weak var contentView: UIView!
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        
        resetBt.layer.shadowOffset = CGSize(width: 5, height:5)
        resetBt.layer.shadowOpacity = 0.3
        resetBt.layer.shadowRadius = 2
        
        signInBt.layer.shadowOffset = CGSize(width: 5, height:5)
        signInBt.layer.shadowOpacity = 0.3
        signInBt.layer.shadowRadius = 2
        
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

        
        
       registerButton.target = self
        registerButton.action = #selector(RegistrationViewController.registerNow)
        
        emailText.attributedPlaceholder = NSAttributedString(string:"Email",
                                                               attributes:[NSForegroundColorAttributeName: placeholderColor])
        passwordText.attributedPlaceholder = NSAttributedString(string:"Parola",
                                                                attributes:[NSForegroundColorAttributeName: placeholderColor])
        
        let titleDict: NSDictionary = [NSForegroundColorAttributeName: UIColor.white]
        
        self.navigationController?.navigationBar.titleTextAttributes = titleDict as? [String : Any]
        
        emailText.delegate = self
        passwordText.delegate = self
        
        
        backBtn.target = self
        backBtn.action = #selector(RegistrationViewController.goBack)
        
        notificationCenter.addObserver(self, selector: #selector(keyboardWillShow), name: Notification.Name.UIKeyboardWillShow, object: nil)
        notificationCenter.addObserver(self, selector: #selector(keyboardWillHide), name: Notification.Name.UIKeyboardWillHide, object: nil)
        
        
        
        
        // Do any additional setup after loading the view.
    }
    
    
    func loginNow(){
        
        
        
        print("LogIN")
        let myUrl = "http://82.76.188.13:3000/signup/login/" + emailText.text! + "&" + passwordText.text!
        //        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        
        let request = NSMutableURLRequest(url: URL(string: myUrl)! as URL)
        request.httpMethod = "GET"
        
        //   request.addValue(myToken, forHTTPHeaderField: "Authorization")
        
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        let task = URLSession.shared.dataTask(with: request as URLRequest) {
            data, response, error in
            
            
            let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            
            print("RESPONSE:\(responseString)")
            
            
            do {
                let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
                
                if let parseJSON = myJSON {
                    
                    let collection = parseJSON as! Dictionary<String, AnyObject>
                    
                   
                    if let userID =  parseJSON["userID"] as? String{
                        print("USER:\(userID)")
                        UserDefaults.standard.set(userID, forKey: "userID")
                        
                    }
                    if let token = collection["token"]?["value"] as? String{
                        print("TOKEN:\(token)")
                        UserDefaults.standard.set(token, forKey: "token")
                    }
                    
                    if let myError = parseJSON["error"] as? String{
                        UserDefaults.standard.set(myError, forKey: "myError")
                    }
                    
                   
                    
                }
            } catch {
               
                print(error)
            }
            
            
        }
        task.resume()

        if(UserDefaults.standard.string(forKey: "userID") != nil){
            
            NotificationsViewController.title = "Notifica proprietarul"
            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RevealViewController")
            
            self.present(viewController, animated: false, completion: nil)
            
        }else{
            let alertController = UIAlertController(title: "Error:", message: UserDefaults.standard.string(forKey: "myError") , preferredStyle: .alert)
            
            // Create the actions
            let okAction = UIAlertAction(title: "OK", style: UIAlertActionStyle.default) {
                UIAlertAction in
                NSLog("OK Pressed")
            }
            let cancelAction = UIAlertAction(title: "Cancel", style: UIAlertActionStyle.cancel) {
                UIAlertAction in
                NSLog("Cancel Pressed")
            }
            alertController.addAction(okAction)
            alertController.addAction(cancelAction)
            
            // Present the controller
            self.present(alertController, animated: true, completion: nil)
        }

        
        
    }
    
   
    
    func registerNow(){
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "VerificationViewController")
        
        self.present(viewController, animated: false, completion: nil)

    }
    
    func resetPassword(){
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ResetPasswordViewController")
        
        self.present(viewController, animated: false, completion: nil)
    }
    
    
    func goBack(){
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "LoginViewController")
        
        self.present(viewController, animated: false, completion: nil)
        

        
    }
    
    func keyboardWillShow(notification: NSNotification)
    {
        let info:NSDictionary = notification.userInfo! as NSDictionary
        let keyboardFrame = info[UIKeyboardFrameEndUserInfoKey] as! NSValue
        let keyboardSize = keyboardFrame.cgRectValue.size
        
      //  _:CGFloat = keyboardSize.height
        
        let animationDurationValue = info[UIKeyboardAnimationDurationUserInfoKey] as! NSNumber
        
        let animationDuration : TimeInterval = animationDurationValue.doubleValue
        
        self.keyboardDelegate?.keyboardWillShowWithSize(size: keyboardSize, andDuration: animationDuration)
        
        // push up the signUpWindow
//        UIView.animateWithDuration(animationDuration, delay: 0.25, options: UIViewAnimationOptions.CurveEaseInOut, animations: {
//            self.signUpWindow.frame = CGRectMake(self.signUpWindowPosition.x, (self.signUpWindowPosition.y - keyboardHeight+140), self.signUpWindow.bounds.width, self.signUpWindow.bounds.height)
//            }, completion: nil)
    }
    
    func keyboardWillHide(notification: NSNotification)
    {
        let info:NSDictionary = notification.userInfo! as NSDictionary
        
        let keyboardFrame = info[UIKeyboardFrameEndUserInfoKey] as! NSValue
        let keyboardSize = keyboardFrame.cgRectValue.size
        
     //   var keyboardHeight:CGFloat = keyboardSize.height
        
        let animationDurationValue = info[UIKeyboardAnimationDurationUserInfoKey] as! NSNumber
        
        let animationDuration : TimeInterval = animationDurationValue.doubleValue
        
        self.keyboardDelegate?.keyboardWillHideWithSize(size: keyboardSize, andDuration: animationDuration)
        
        // pull signUpWindow back to its original position
//        UIView.animateWithDuration(animationDuration, delay: 0.25, options: UIViewAnimationOptions.CurveEaseInOut, animations: {
//            self.view.frame = CGRectMake(self.signUpWindowPosition.x, self.signUpWindowPosition.y, self.signUpWindow.bounds.width, self.signUpWindow.bounds.height)
//            }, completion: nil)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    
    func startConnection(){
        
        let parameters = ["first_name": "Jenu",
                          "last_name": "Eujenu",
                          "nickname": "nick",
                          "email": "jenu_magnificu@yahoo.com",
                          "driver_license": "B1",
                          "photo": "sec91@gmail.com",
                          "platform": "iOS",
                          "user_city": "Oradea"] as Dictionary<String, String>
        
        
        
        
        let myUrl = NSURL(string: "http://82.76.188.13:3000/signup/user");
        let session = URLSession.shared
        let request = NSMutableURLRequest(url:myUrl! as URL);
        
        request.httpMethod = "POST";// Compose a query string
        
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        request.httpBody = try! JSONSerialization.data(withJSONObject: parameters, options: [] )
        
        let task = session.dataTask(with: request as URLRequest) {
            data, response, error in
            
            if error != nil
            {
                print("error=\(error)")
                return
            }
            
            // You can print out response object
            print("response = \(response)")
            
            // Print out response body
            let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            print("responseString = \(responseString)")
            
            //Let's convert response sent from a server side script to a NSDictionary object:
            do {
                let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
                
                if let parseJSON = myJSON {
                    // Now we can access value of First Name by its key
                    if let user = parseJSON["userID"] as? String{
                        print("USERID:\(user)")
                        
                        UserDefaults.standard.set(user, forKey: "userID")
                        
                        
                    }
                    let token = parseJSON["token"] as? Dictionary<String, AnyObject>
                    if let values = token?["value"] as? String{
                        UserDefaults.standard.set(values, forKey: "token")
                        print("Values:\(values)")
                        //  self.myToken(token: values)
                        
                        
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


    
     func textFieldShouldReturn(_ textField: UITextField) -> Bool
    {
        switch textField
        {
        case emailText:
            emailText.resignFirstResponder()
            passwordText.becomeFirstResponder()
            break
        case passwordText:
            passwordText.resignFirstResponder()
            emailText.becomeFirstResponder()
            break
        default:
            textField.resignFirstResponder()
        }
        return true
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

    
    
    

   
}
