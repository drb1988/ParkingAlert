//
//  EmailValidationViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 10/10/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit
import JVFloatLabeledTextField

import FBSDKLoginKit

class EmailValidationViewController: UIViewController, FBSDKLoginButtonDelegate {
    
           let placeholderColor = UIColor(red: 0, green: 122/255, blue: 255/255, alpha: 1)

    @IBOutlet var fbloginButton: FBSDKLoginButton!
    
    @IBOutlet var emailTextField: JVFloatLabeledTextField!
    static var verificationID : String = "nah"
    
    static var token : String = "nah"
    
    static var name : String = "nah"
    var auth: String = ""
    
    static var surname : String = "nah"
    
     static var email : String = "nah"
    
    static var password : String = "nah"
    
    @IBOutlet var validationCodeTextField: JVFloatLabeledTextField!
    
    @IBOutlet var backBtn: UIBarButtonItem!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        
        
        fbloginButton.layer.shadowOffset = CGSize(width: 5, height:5)
        fbloginButton.layer.shadowOpacity = 0.3
        fbloginButton.layer.shadowRadius = 2
        
        
        validateBt.layer.shadowOffset = CGSize(width: 5, height:5)
        validateBt.layer.shadowOpacity = 0.3
        validateBt.layer.shadowRadius = 2

        
        revalidateBt.layer.shadowOffset = CGSize(width: 5, height:5)
        revalidateBt.layer.shadowOpacity = 0.3
        revalidateBt.layer.shadowRadius = 2

        
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(EmailValidationViewController.dismissKeyboard))
        
        
        view.addGestureRecognizer(tap)
        
        
        if (FBSDKAccessToken.current() != nil)
        {
            // User is already logged in, do work such as go to next view controller.
        }
        else
        {
            
            fbloginButton.readPermissions = ["public_profile", "email", "user_friends"]
            fbloginButton.delegate = self
        }

        backBtn.target = self
        
        backBtn.action = #selector(EmailValidationViewController.goBack)
        
        validationCodeTextField.attributedPlaceholder = NSAttributedString(string:"Cod validare",
                                                                 attributes:[NSForegroundColorAttributeName: placeholderColor])
        
        emailTextField.attributedPlaceholder = NSAttributedString(string:"Email",
                                                                  attributes:[NSForegroundColorAttributeName: placeholderColor])
        
        
        
    //    print(EmailValidationViewController.verificationID)
        
        let titleDict: NSDictionary = [NSForegroundColorAttributeName: UIColor.white]
        
        self.navigationController?.navigationBar.titleTextAttributes = titleDict as? [String : Any]
        
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    
    @IBOutlet weak var validateBt: UIButton!
    
    @IBOutlet weak var revalidateBt: UIButton!
    

    @IBAction func verifyEmail(_ sender: AnyObject) {
        
        
        print("verifyEmail")
        print(EmailValidationViewController.email)
        print(validationCodeTextField.text!)
        
        if(validationCodeTextField
            .text != " "){
        
        verifEmail(email: EmailValidationViewController.email, token: validationCodeTextField.text! )
        }
        else{
            callAlert()
        }
        
        
    }
    
    func callAlert(){
        let actionSheetController: UIAlertController = UIAlertController(title: "Va rugam introduceti codul trimis pe adresa dv de email", message: "", preferredStyle: .alert)
        
        
        let nextAction: UIAlertAction = UIAlertAction(title: "Ok", style: .default) { action -> Void in
            //Do your task             //NSUserDefaults.standardUserDefaults().removePersistentDomainForName(NSBundle.mainBundle().bundleIdentifier!)
            //                  NSUserDefaults.standardUserDefaults().synchronize()
        }
        actionSheetController.addAction(nextAction)
        self.present(actionSheetController, animated: true, completion: nil)
    }

    
    func goBack(){
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "VerificationViewController")
        
        self.present(viewController, animated: false, completion: nil)
        

        
    }
    
    
    func verifEmail(email: String, token: String){
        
        
        
        let parameters = ["email": email, "token": token] as Dictionary<String, String>
        
        
        print("EmailValidationViewController.verificationID:\(EmailValidationViewController.verificationID)")
        
        let myUrl = NSURL(string: "http://82.76.188.13:3000/signup/verifyEmail/" +  EmailValidationViewController.verificationID)
        
        print("VErification URL: \(myUrl)")
        
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
                        if(responseString == "Success"){
                            self.signUpUser()
                        }
            
            
            
            //     Let's convert response sent from a server side script to a NSDictionary object:
            //            do {
            //                let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
            //
            //                if let parseJSON = myJSON {
            //                    // Now we can access value of First Name by its key
            //                    if let verificationID = parseJSON["verificationID"] as? String{
            //
            //                     //   self.emailValidation.getEmailID(x: verificationID)
            //
            //                    }
            //
            //                    
            //                }
            //            } catch {
            //                print(error)
            //            }
        }
        task.resume()
        

        
        
        
        
    }
    
    @IBAction func resendMail(_ sender: AnyObject) {
        
        if(emailTextField.text != " "){
         sendMail(email: emailTextField.text!, token: validationCodeTextField.text! )
        }
    }
    
    func sendMail(email: String, token: String){
        
        
        let parameters = ["email": email, "token": token] as Dictionary<String, String>
        
        
        
        
        let myUrl = NSURL(string: "http://82.76.188.13:3000/signup/sendEmailVerification")
        
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
//            if(responseString == "Success"){
//                self.signUpUser()
//            }
            
            
            
            //     Let's convert response sent from a server side script to a NSDictionary object:
//            do {
//                let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
//                
//                if let parseJSON = myJSON {
//                    // Now we can access value of First Name by its key
//                    if let verificationID = parseJSON["verificationID"] as? String{
//                        
//                     //   self.emailValidation.getEmailID(x: verificationID)
//                        
//                    }
//                    
//                    
//                }
//            } catch {
//                print(error)
//            }
        }
        task.resume()
        
        
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "EmailValidationViewController")
        
        self.present(viewController, animated: false, completion: nil)
        
        
    }
    
    
    
    func signUpUser(){
        
        print("//sign up called")
        
        let parameters = ["first_name": EmailValidationViewController.name,
                          "last_name": EmailValidationViewController.surname,
                          "email": EmailValidationViewController.email,
                          "photo": "sec91@gmail.com",
                          "platform": "iOS",
                          "password":EmailValidationViewController.password] as Dictionary<String, String>
        
        
        
        
        let myUrl = NSURL(string: "http://82.76.188.13:3000/signup/user" )
        
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
            
            
            
            //     Let's convert response sent from a server side script to a NSDictionary object:
                        do {
                            let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
            
                            if let parseJSON = myJSON {
                                
                                let collection = parseJSON as! Dictionary<String, AnyObject>

                                // Now we can access value of First Name by its key
                                if let userID = parseJSON["userID"] as? String{
                                    print("USER:\(userID)")
                                UserDefaults.standard.set(userID, forKey: "userID")
            
                                }
                                if let token = collection["token"]?["value"] as? String{
                                     print("TOKEN:\(token)")
                                    UserDefaults.standard.set(token, forKey: "token")
                                }
            
            
                            }
                        } catch {
                            print(error)
                        }
            
            
        }
        task.resume()
        
        
       NotificationsViewController.title = "Adauga masina"
        
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RevealViewController")
        
        self.present(viewController, animated: false, completion: nil)
        
        
//        let storyboard = UIStoryboard(name: "Main", bundle: nil)
//        let vc = storyboard.instantiateViewController(withIdentifier: "SplashViewC") as UIViewController
//        UIApplication.shared.keyWindow?.rootViewController?.present(vc, animated: true, completion: nil)
        
    }
    
    
    func getEmailID(x: String){
        print(x)
         EmailValidationViewController.verificationID = x
    }
    

    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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


    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
