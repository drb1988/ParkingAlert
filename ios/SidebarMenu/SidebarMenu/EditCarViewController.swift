//
//  EditCarViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 9/22/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit

class EditCarViewController: UIViewController{

    
    static var enabled = "1"
   
   
    @IBOutlet var carPlate: UILabel!
    @IBOutlet var carName: UITextField!

    @IBOutlet var profileImageView: UIImageView!
    
    
   
    @IBOutlet var vehicleLabel: UILabel!
    @IBOutlet var backButton: UIBarButtonItem!
    
  
    
    let lightBlue = UIColor(red: 0/255, green: 122/255, blue: 255/255, alpha: 1)
    
    
    @IBOutlet var mySwitch: UISwitch!
    
    @IBOutlet var secSwitch: UISwitch!
    @IBOutlet var secSwitchText: UILabel!
    @IBOutlet var switchText: UILabel!
    
    var reachability = SplashViewController()
 
    
    @IBAction func stateChanged(_ sender: UISwitch) {
        
        
        
        
        if mySwitch.isOn {
            
         
            enableNotifications()
            switchText.textColor = lightBlue
            mySwitch.setOn(true, animated:true)

            
           
        } else {
            switchText.textColor = UIColor.gray
            print("Switch is on")
            EditCarViewController.enabled = "0"
            mySwitch.setOn(false, animated:true)
            disableNotifications()
                    }
    }
    

    
    @IBAction func secStateChanged(_ sender: UISwitch) {
   
        if secSwitch.isOn == true{
            
            
            let alert = UIAlertController(title: "Atentie", message: "Acum se mai poate adauga si altcineva la masina scanand QRcode-ul", preferredStyle: .alert)
            
            let okButton = UIAlertAction(title: "Ok", style: .default) { action -> Void in
                
            }
            alert.addAction(okButton)
            
            self.present(alert, animated: true, completion: nil)
            
            self.secSwitchText.textColor = self.lightBlue
            //  self.secSwitch.setOn(true, animated:true)
            self.allowOthers(boolean: true)

            

    
        } else {
            secSwitchText.textColor = UIColor.gray
            EditCarViewController.enabled = "0"
         //   secSwitch.setOn(false, animated:true)
            allowOthers(boolean: false)
        }
        
        
        
      
    }
    
    
    
    
    func doneButton(){
        
        //          print("bam3")
        //        print("SELF:\(self.carPlate.text!)")
        //
        //        _ = name
        //        print("Plate:\(name)")
        //
        //
        //
        //
        //        let parameters = ["plates": self.carPlate.text!,
        //                          "given_name": self.carName.text!,
        //                          "make": self.carProducer.text!,
        //                          "model": self.carModel.text!,
        //                          "year": self.carYear.text!,
        //                         "enable_notifications": EditCarViewController.enabled
        //            ] as Dictionary<String, String>
        //
        //
        //        let myUrl = "http://82.76.188.13:3000/users/editCar/" + UserDefaults.standard.string(forKey: "userID")! + "&" + self.carPlate.text!
        //
        //
        //        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        //
        //        print("myToken:\(myToken)")
        //        print("myURL:\(myUrl)")
        //
        //        let request = NSMutableURLRequest(url: URL(string: myUrl)! as URL)
        //
        //        let session = URLSession.shared
        //        request.httpMethod = "POST"
        //
        //        //Note : Add the corresponding "Content-Type" and "Accept" header. In this example I had used the application/json.
        //        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        //        request.addValue("application/json", forHTTPHeaderField: "Accept")
        //        request.addValue(myToken, forHTTPHeaderField: "authorization")
        //        request.httpBody = try! JSONSerialization.data(withJSONObject: parameters, options: [] )
        //
        //        let task = session.dataTask(with: request as URLRequest) { data, response, error in
        //            guard data != nil else {
        //                print("no data found: \(error)")
        //                return
        //            }
        //            
        //            
        //        }
        //        
        //        task.resume()

    }
    
    func disableNotifications(){
        
        let myUrl = "http://82.76.188.13:3000/users/disableNotifications/" + UserDefaults.standard.string(forKey: "userID")! + "&" + EditCarViewController.plate
        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        
        let request = NSMutableURLRequest(url: URL(string: myUrl)! as URL)
        request.httpMethod = "GET"
        
        request.addValue(myToken, forHTTPHeaderField: "Authorization")
        
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        let task = URLSession.shared.dataTask(with: request as URLRequest) {
            data, response, error in
            guard data != nil else {
                self.reachability.setupReachability(hostName: "google.com", useClosures: true)
                self.reachability.startNotifier()
                return
            }
            
            let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            print("RESPONSE:\(responseString)")
            
        }
        task.resume()

        
    }
    
    
    
    func enableNotifications(){
        
        
        let myUrl = "http://82.76.188.13:3000/users/enableNotifications/" + UserDefaults.standard.string(forKey: "userID")! + "&" + EditCarViewController.plate

        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        
        let request = NSMutableURLRequest(url: URL(string: myUrl)! as URL)
        request.httpMethod = "GET"
        
        request.addValue(myToken, forHTTPHeaderField: "Authorization")
        
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        let task = URLSession.shared.dataTask(with: request as URLRequest) {
            data, response, error in
            
            
            let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            print("RESPONSE:\(responseString)")
            
        }
        task.resume()
        
        
        

        
        
        
    }
    
    
    
    func allowOthers(boolean: Bool){
        var myBool = "true"
        
        
        let myIP = "http://82.76.188.13:3000/users/allowOthers/" + UserDefaults.standard.string(forKey: "userID")!
        if(boolean == true){
            myBool = "true"
        }else{
            myBool = "false"
        }
        
        
        // imi trebuie userID? carPlate si switchValue(boolean)
        let myUrl = myIP + "&" + EditCarViewController.plate + "&" + myBool

        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        
        let request = NSMutableURLRequest(url: URL(string: myUrl)! as URL)
        request.httpMethod = "GET"
        
        request.addValue(myToken, forHTTPHeaderField: "Authorization")
        
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        let task = URLSession.shared.dataTask(with: request as URLRequest) {
            data, response, error in
            
            
            let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            print("RESPONSE:\(responseString)")
            
//            do {
//                let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
//                
//                if let parseJSON = myJSON {
//                    // Now we can access value of First Name by its key
//                    if let result = parseJSON["carCode"] as? String{
//                        
//                        print(result)
//                        UserDefaults.standard.set(result, forKey: "myQR")
//                        let img = self.generateQRCode(from: UserDefaults.standard.object(forKey: "myQR") as! String)
//                        self.qrImageView.image = img
//                        
//                        
//                        
//                        UIImageWriteToSavedPhotosAlbum(img!, self, #selector(self.image(_:didFinishSavingWithError:contextInfo:)), nil)
//                        
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

    

    
   static var name: String = " "
   static var plate: String = " "
static var producer: String = " "
   static  var model: String = " "
static     var year: String = " "
  static   var mySwitchValue: Bool = true
static  var mySecondSwitchValue = true
  static  var myImage: UIImage!
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        ScanQRViewController.carNr = EditCarViewController.plate
        
        let gesture = UITapGestureRecognizer(target: self, action: #selector(EditCarViewController.showQR))
       profileImageView.isUserInteractionEnabled = true
        
        self.profileImageView.addGestureRecognizer(gesture)
        
        
       print("bam2")
      backButton.target = self
      backButton.action = #selector(EditCarViewController.goBack)
        
 
        
        
        
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(EditCarViewController.dismissKeyboard))
        view.addGestureRecognizer(tap)
        
        

    }
    
    
    func showQR(){
        
        print("imageTouch")
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ShowQRViewController")
        
        self.present(viewController, animated: false, completion: nil)
        

        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        
        
        EditDetailsCarViewController.name = EditCarViewController.name
        EditDetailsCarViewController.plate = EditCarViewController.plate
        EditDetailsCarViewController.producer = EditCarViewController.producer
        EditDetailsCarViewController.model = EditCarViewController.model
        EditDetailsCarViewController.year = EditCarViewController.year
        
          print("bam1")
        carName.text = EditCarViewController.name
        profileImageView.image = EditCarViewController.myImage
        
       EditQRCodeViewController.staticQR = EditCarViewController.myImage
        
        
        vehicleLabel.text = EditCarViewController.model + " " + EditCarViewController.producer + " " + EditCarViewController.year
        

       
        carPlate.text = EditCarViewController.plate
        
//        carProducer.text = producer
//        carProducer.textColor = lightBlue
//        carModel.text = model
//        carModel.textColor = lightBlue
//        carYear.text = year
//        carYear.textColor = lightBlue
        
        if(EditCarViewController.mySwitchValue == true){
            mySwitch.setOn(true, animated:true)
        }
        else{
            mySwitch.setOn(false, animated:true)
        }
    
    
    
    if(EditCarViewController.mySecondSwitchValue == true){
           secSwitch.setOn(true, animated:true)
        
    }
    else{
    secSwitch.setOn(false, animated:true)
    }
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

    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
//    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
//        if  segue.identifier == "EditDetailsCarViewController",
//            let destination = segue.destination as? EditDetailsCarViewController
//           
//        {
//          
//        }
//    }
//    

    
 

}
