//
//  VerificationViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 10/10/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//


//Vino in Friendly ViewController

import UIKit
import JVFloatLabeledTextField

class VerificationViewController: UIViewController, UITextFieldDelegate {
    
    
    static var myName: String = " "
    static var mySurname: String = " "
    static var myEmail: String = " "
    static var myPass: String = " "
    static var myPhone: String =  " "
    
    
    
    let placeholderColor = UIColor(red: 0, green: 122/255, blue: 255/255, alpha: 1)

    @IBOutlet var backBtn: UIBarButtonItem!
    
    @IBOutlet var nameTextField: JVFloatLabeledTextField!
    
    
    var emailValidation = EmailValidationViewController()
    
    @IBOutlet var surnameTextField: JVFloatLabeledTextField!
    
    
    @IBOutlet var emailTextField: JVFloatLabeledTextField!
    
    @IBOutlet var verifEmailTextField: JVFloatLabeledTextField!
    
    @IBOutlet var logInButton: UIBarButtonItem!
    
    @IBOutlet var passwordTextField: JVFloatLabeledTextField!
    
    @IBOutlet var phoneTextField: JVFloatLabeledTextField!
    
    @IBOutlet weak var signUpBt: UIButton!
    @IBAction func signupButton(_ sender: AnyObject) {
        print(validateEmail(candidate: (emailTextField.text)!))
        
    if((emailTextField.text?.contains("@"))! && nameTextField.text != ""&&surnameTextField.text != "" && verifEmailTextField.text != "" && passwordTextField.text != ""){
        if(emailTextField.text == verifEmailTextField.text){
        
        sendMail(email: emailTextField.text!)
        EmailValidationViewController.name = nameTextField.text!
        VerificationViewController.myName = nameTextField.text!
        EmailValidationViewController.surname = surnameTextField.text!
        VerificationViewController.mySurname = surnameTextField.text!

        EmailValidationViewController.email = emailTextField.text!
        VerificationViewController.myEmail = emailTextField.text!
        EmailValidationViewController.password = passwordTextField.text!
        VerificationViewController.myPass =  passwordTextField.text!
            
        
            
        }else{
            emailTextField.textColor = UIColor.red
            verifEmailTextField.textColor = UIColor.red            
        }

    }
    else{
        
        callAlert()
        
        }
        
        
        
        
        
    }
    
    
    override func viewDidAppear(_ animated: Bool) {
        
        
        signUpBt.layer.shadowOffset = CGSize(width: 5, height:5)
        signUpBt.layer.shadowOpacity = 0.3
        signUpBt.layer.shadowRadius = 2

        
        if(VerificationViewController.myName != " " && VerificationViewController.mySurname != " " && VerificationViewController.myEmail != " " && VerificationViewController.myPass != " "){
        
            nameTextField.text = VerificationViewController.myName
            surnameTextField.text = VerificationViewController.mySurname
            emailTextField.text = VerificationViewController.myEmail
            verifEmailTextField.text = VerificationViewController.myEmail
            passwordTextField.text = VerificationViewController.myPass
        }
    }
    
    func callAlert(){
        let actionSheetController: UIAlertController = UIAlertController(title: "Va rugam completati toate campurile", message: "", preferredStyle: .alert)
        
        
        let nextAction: UIAlertAction = UIAlertAction(title: "Ok", style: .default) { action -> Void in
            //Do your task             //NSUserDefaults.standardUserDefaults().removePersistentDomainForName(NSBundle.mainBundle().bundleIdentifier!)
            //                  NSUserDefaults.standardUserDefaults().synchronize()
        }
        actionSheetController.addAction(nextAction)
        self.present(actionSheetController, animated: true, completion: nil)
    }

    func sendMail(email: String){
      
        
        let parameters = ["email": email] as Dictionary<String, String>
        
        
        
        
        let myUrl = NSURL(string: "http://82.76.188.13:3000/signup/sendEmailVerification"
        )

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
                    // Now we can access value of First Name by its key
                    if let verificationID = parseJSON["verificationID"] as? String{
                        
                        print("MYVERIFICATION:\(verificationID)")
                        
                        EmailValidationViewController.verificationID = verificationID
                   
                    }
                    
                    
                }
            } catch {
                print(error)
            }
        }
        task.resume()
        
        
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "EmailValidationViewController")
        
        self.present(viewController, animated: false, completion: nil)


    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        nameTextField.delegate = self
        surnameTextField.delegate = self
        emailTextField.delegate = self
        verifEmailTextField.delegate = self
        passwordTextField.delegate = self
        
        
        
        nameTextField.attributedPlaceholder = NSAttributedString(string:"Nume",
                                                             attributes:[NSForegroundColorAttributeName: placeholderColor])
        surnameTextField.attributedPlaceholder = NSAttributedString(string:"Prenume",
                                                                 attributes:[NSForegroundColorAttributeName: placeholderColor])
        emailTextField.attributedPlaceholder = NSAttributedString(string:"Email (iti vom trimite un cod de verificare)",
                                                                 attributes:[NSForegroundColorAttributeName: placeholderColor])
        verifEmailTextField.attributedPlaceholder = NSAttributedString(string:"Verifica email",
                                                                 attributes:[NSForegroundColorAttributeName: placeholderColor])
        passwordTextField.attributedPlaceholder = NSAttributedString(string:"Parola",
                                                                 attributes:[NSForegroundColorAttributeName: placeholderColor])

        let titleDict: NSDictionary = [NSForegroundColorAttributeName: UIColor.white]
        
        self.navigationController?.navigationBar.titleTextAttributes = titleDict as? [String : Any]
        
        backBtn.target = self
        backBtn.action = #selector(VerificationViewController.goBack)
     
        
        logInButton.target = self
        logInButton.action = #selector(VerificationViewController.logIn)
        
        
        
    }
    
    func logIn(){
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RegistrationViewController")
        
        self.present(viewController, animated: false, completion: nil)
    }
    
    func goBack(){
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "LoginViewController")
        
        self.present(viewController, animated: false, completion: nil)
        

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func validateEmail(candidate: String) -> Bool {
        let emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}"
        return NSPredicate(format: "SELF MATCHES %@", emailRegex).evaluate(with: candidate)
    }
    
    
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool
    {
        switch textField
        {
        case nameTextField:
            nameTextField.resignFirstResponder()
            surnameTextField.becomeFirstResponder()
            break
        case surnameTextField:
            surnameTextField.resignFirstResponder()
            emailTextField.becomeFirstResponder()
            break
        case emailTextField:
            emailTextField.resignFirstResponder()
            verifEmailTextField.becomeFirstResponder()
        case verifEmailTextField:
            verifEmailTextField.resignFirstResponder()
            passwordTextField.becomeFirstResponder()
        default:
            textField.resignFirstResponder()
        }
        return true
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
