//
//  AnswerViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 10/3/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit

class AnswerViewController: UIViewController {

    var review = ReviewViewController()
    var progress = ProgressViewController()
    static var plates : String = " "
    static var notifID: String = " "
    @IBOutlet var label1: UILabel!
    
    var gradient = CAGradientLayer()
    
   
    var reachability = SplashViewController()
   
    @IBOutlet var label2: UILabel!
 
  
       
    
   
     
    
    override func viewDidLoad() {
        
        super.viewDidLoad()
        actionSheet()
        
        readNotification()
        
        
   
        label1.text = "In cat timp poti veni la masina?"
        label1.textColor = UIColor.white
        label2.text = "BH 12 ABC creaza o problema."
        label2.textColor = UIColor.white
       // myStackView.backgroundColor =  UIColor(patternImage: UIImage(named: "gradient.jpg")!)
        
        
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    
    func readNotification(){
        
        
        
        let myUrl = "http://82.76.188.13:3000/notifications/receiverRead/" + AnswerViewController.notifID
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
        }
        task.resume()
        
        
    }

    
    
    
    func sendAnswer(x : String, id: String){
        
        print("merge response:\(x)")
        
        let parameters = ["latitude": "24.0992",
                            "longitude": "44.8384",
                            "estimated": x] as Dictionary<String, String>
        print("sendAnswer\(id)")
        let myUrl = "http://82.76.188.13:3000/notifications/receiverAnswered/" + id
        
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
            print("RSP:\(response)")
            guard data != nil
           
            
            else {
                print("no data found: \(error)")
                return
            }
            
            
        }
        
        print("am trimis notificare")
        task.resume()
        
 
        let datte = Date()
        let calendar = Calendar.current
        
        let hours = calendar.component(.hour, from: datte)
        let minutes = calendar.component(.minute, from: datte)
        let seconds = calendar.component(.second, from: datte)
        let dateString = String(hours) + ":" + String(minutes) + ":" + String(seconds)
        
        ProgressViewController.minutes = x
        ProgressViewController.responseHour = dateString
        ProgressViewController.plates =  "BH79FBU"
        
        ProgressViewController.myType = false
        
        
        if(x != "100"){
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ProgressViewController")
        
        
        self.present(viewController, animated: false, completion: nil)

        }
        else{
            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RevealViewController")
            
            
            self.present(viewController, animated: false, completion: nil)
        }
        
    }
    
    func goBack(){
        
       
    }
    
    
    func actionSheet(){
        let alertController = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        
        _ = UIAlertAction(title: "Am cod QR", style: .default, handler: { (action) -> Void in
            
            
            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "BarcodeViewController")
            
            self.present(viewController, animated: false, completion: nil)
        })
        
        let  three = UIAlertAction(title: "3 minute", style: .default, handler: { (action) -> Void in
             self.sendAnswer(x: "3", id: AnswerViewController.notifID)        })
        
        let five = UIAlertAction(title: "5 minute", style: .default, handler: { (action) -> Void in
             self.sendAnswer(x: "5", id: AnswerViewController.notifID)
        })
        
        let seven = UIAlertAction(title: "7 minute", style: .default, handler: { (action) -> Void in
            
             self.sendAnswer(x: "7", id: AnswerViewController.notifID)        })
        
        let noway =  UIAlertAction(title: "Nu pot veni la masina", style: .default, handler: { (action) -> Void in
            self.sendAnswer(x: "100", id: AnswerViewController.notifID)
        })

        let layer = CAGradientLayer()
        
        
        layer.colors = [UIColor.red.cgColor, UIColor.yellow.cgColor]

        let gradientColors: [CGColor] = [UIColor.green.cgColor, UIColor.yellow.cgColor, UIColor.orange.cgColor, UIColor.red.cgColor]
        
        let gradientLayer: CAGradientLayer = CAGradientLayer()
        gradientLayer.colors = gradientColors
        gradientLayer.frame = alertController.view.bounds
        
      
    //    alertController.view.layer.insertSublayer(gradientLayer, at: 1)
        
 
        
        alertController.addAction(three)
        alertController.addAction(five)
        alertController.addAction(seven)
        alertController.addAction(noway)
        
        self.navigationController!.present(alertController, animated: true, completion: nil)
    }

    
    
    func getID(x: String){
        
        print("notifID")
        print(x)
        AnswerViewController.notifID = x
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
