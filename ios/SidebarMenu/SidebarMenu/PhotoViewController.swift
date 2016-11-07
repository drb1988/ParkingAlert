//
//  PhotoViewController.swift
//  SidebarMenu
//
//  Created by Simon Ng on 2/2/15.
//  Copyright (c) 2015 AppCoda. All rights reserved.
//

import UIKit

class PhotoViewController: UIViewController {
    
    @IBOutlet var scrollView: UIScrollView!
    @IBOutlet weak var menuButton:UIBarButtonItem!
    static var notif: String = " "
    static var others: String = " "
    var reachability = SplashViewController()
    

    @IBAction func saveButton(_ sender: AnyObject) {
        
        
        
        print("lalalalalalala")
        
        if(self.carNumber.text != "" ){
            
            print("textview")
            
            
            
            print(carNumber.text)
            
            print(carName.text)
            
            print(model.text)
            
            print(production.text)
            
            print(producer.text)
            
            
            
            
            print("MYQRCode:\(UserDefaults.standard.object(forKey: "myQR"))")
            
            let modifiedCar = String(describing: self.carNumber.text?.characters.filter {$0 != " "})
            print("modifiedCar:\(modifiedCar)")
            
            let parameters = ["plates": self.carNumber.text! ,
                              "given_name": self.carName.text!,
                              "make": self.producer.text!,
                              "model": self.model.text!,
                              "year": self.production.text!,
                              "enable_notifications": PhotoViewController.notif ,
                              "enable_others": PhotoViewController.others,
                              "qr_code" : UserDefaults.standard.object(forKey: "myQR") as! String
             
           ] as Dictionary<String, String>
            
            
            let myUrl = "http://82.76.188.13:3000/users/addCar/" + UserDefaults.standard.string(forKey: "userID")!
           
            
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
            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "CarsTableViewController")
            
            self.present(viewController, animated: false, completion: nil)

            
            
            
        }
        else{
            let alert = UIAlertView(title: "Atentie", message: "Va rugam introduceti numarul masinii.", delegate: nil, cancelButtonTitle: "OK")
            alert.show()
        }
        

        
        

        
        
        
        
        
    }
   
    
    static var switchValue : Bool = true
    static var secondswitch: Bool = true
    @IBOutlet var mySwitch: UISwitch!
    @IBOutlet var production: UITextField!
    @IBOutlet var model: UITextField!
    @IBOutlet var producer: UITextField!
    @IBOutlet var carNumber: UITextField!
    @IBOutlet var carName: UITextField!
    var name: String = ""
    
    let lightBlue = UIColor(red: 0/255, green: 122/255, blue: 255/255, alpha: 1)
    
    @IBOutlet var mySecondSwitch: UISwitch!
    
    @IBAction func stateChanged(_ sender: AnyObject) {
        
        
        if mySwitch.isOn {
            
            
            PhotoViewController.switchValue = false
           switchText.textColor = UIColor.gray
            print("Switch is on")
            mySwitch.setOn(false, animated:true)
            PhotoViewController.notif = "false"
        } else {
            
            PhotoViewController.switchValue = true
            switchText.textColor = lightBlue
            mySwitch.setOn(true, animated:true)
            PhotoViewController.notif = "true"
        }
        
    }
    
    
    @IBAction func stateSecondChanged(_ sender: AnyObject) {
        
        
        
        if mySecondSwitch.isOn == false {
            PhotoViewController.secondswitch = false
            secondSwitchText.textColor = UIColor.gray
         
            mySecondSwitch.setOn(false, animated:true)
            PhotoViewController.others = "false"
        } else {
            
            
            let alert = UIAlertController(title: "Acum se mai poate", message: "adauga si altcineva la masina scanand QRcode-ul", preferredStyle: .alert)
            
            let okButton = UIAlertAction(title: "Ok", style: .default) { action -> Void in
                PhotoViewController.secondswitch = true
                self.secondSwitchText.textColor = self.lightBlue
                self.mySecondSwitch.setOn(true, animated:true)
                PhotoViewController.others = "true"
                
            }
            alert.addAction(okButton)
            
            self.present(alert, animated: true, completion: nil)

          
        }
        
        
        
        
    }
    
  
    @IBOutlet var switchText: UILabel!

    

    @IBOutlet var secondSwitchText: UITextField!
 
    
    override func viewDidAppear(_ animated: Bool) {

//        profileImage.layer.borderWidth = 1
//        profileImage.layer.masksToBounds = false
//        profileImage.layer.borderColor = UIColor.black.cgColor
//        profileImage.layer.cornerRadius = profileImage.frame.height/2
//        profileImage.clipsToBounds = true
//        
        
        let titleDict: NSDictionary = [NSForegroundColorAttributeName: UIColor.white]
        self.navigationController?.navigationBar.titleTextAttributes = titleDict as? [String : Any]

           
    }
    override func viewDidLoad() {
        super.viewDidLoad()

        menuButton.target = self
        
        menuButton.action = #selector(goBack)
        
       
        
//        
//        if revealViewController() != nil {
//            menuButton.target = revealViewController()
//            menuButton.action = #selector(SWRevealViewController.revealToggle(_:))
//            view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
//        }
        
        
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(PhotoViewController.dismissKeyboard))
        view.addGestureRecognizer(tap)
        
        
        
      
        
    }
    
    
    
   
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    func dismissKeyboard(){
        view.endEditing(true)

    }
    
    
    func goBack(){
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "CarsTableViewController")
        
        self.present(viewController, animated: false, completion: nil)
        
        
    }
    
}

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */


