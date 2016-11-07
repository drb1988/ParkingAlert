//
//  ResetPasswordViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 10/11/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit
import JVFloatLabeledTextField

class ResetPasswordViewController: UIViewController {
    
    
    @IBOutlet var backBtn: UIBarButtonItem!
    
    
    let placeholderColor = UIColor(red: 0, green: 122/255, blue: 255/255, alpha: 1)

    @IBOutlet var emailTextField: JVFloatLabeledTextField!
    
    
    
    @IBAction func resetPass(_ sender: AnyObject) {
        
        print("am apasat resetPass")
        
        Preset()
        
        
    }
    
    
    func Preset(){
        
        print("reset PASSWORD")
        let myUrl = "http://82.76.188.13:3000/signup/resetPassword/" + emailTextField.text!
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
                    // Now we can access value of First Name by its key
                    if let result = parseJSON["result"] as? String{
                        
                     print(result)
                        if(result == "Mail Sent"){
                            
                            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RegistrationViewController")
                            
                            self.present(viewController, animated: false, completion: nil)

                            
                        }
                    }
                    
                    
                }
            } catch {
                print(error)
            }

            
        }
        task.resume()

        
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        
        backBtn.target = self
        
        backBtn.action = #selector(ResetPasswordViewController.goBack)

       
        emailTextField.attributedPlaceholder = NSAttributedString(string:"Email",
                                                                 attributes:[NSForegroundColorAttributeName: placeholderColor])
        // Do any additional setup after loading the view.
    }

    
    func goBack(){
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RegistrationViewController")
        
        self.present(viewController, animated: false, completion: nil)

    }
    
    
       override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
