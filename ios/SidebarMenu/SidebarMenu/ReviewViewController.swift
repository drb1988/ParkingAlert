//
//  ReviewViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 10/6/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit

class ReviewViewController: UIViewController {

//let btn1: UIButton = UIButton(frame: CGRect(x: 50, y: 400, width: 100, height: 50))
//let btn2: UIButton = UIButton(frame: CGRect(x: 100, y: 400, width: 100, height: 50))
    
    
    @IBOutlet weak var btn1: UIButton!
    
    @IBOutlet weak var btn2: UIButton!
    
    static var notifID: String = " "
    override func viewDidLoad() {
        super.viewDidLoad()

      //  view.backgroundColor = UIColor()

        
       btn1.layer.cornerRadius = 45
        btn1.setTitle("NU", for: .normal)
        btn1.addTarget(self, action: #selector(button1Action), for: .touchUpInside)
       
      
        
        
       btn2.layer.cornerRadius = 45
        btn2.setTitle("DA", for: .normal)
        btn2.addTarget(self, action: #selector(button2Action), for: .touchUpInside)
        
      
        
        
        
        
        
      
    }

    
    func button1Action(){
        
        sendReview(response: "false", time: "0")
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RevealViewController")
        
        self.present(viewController, animated: false, completion: nil)
    }
    
    
    func button2Action(){
        
        sendReview(response: "true", time: "0")
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RevealViewController")
        
        self.present(viewController, animated: false, completion: nil)
        
    }
    
    func sendReview(response: String, time: String){
        
        var parameters = [:] as Dictionary<String, String>
        
        if(response == "0"){
            
             parameters = [
            "is_ontime": time] as Dictionary<String, String>
            
        }
        
        if(time == "0"){
             parameters = [
            "feedback": response] as Dictionary<String, String>
            if(response == "true"){
                
                SummaryViewController.feedback = "A venit la masina"
                
            }else{
                
                SummaryViewController.feedback = "Timpul a expirat"
                SummaryViewController.answer = "gen"
                
            }
        }
//        let parameters = ["feedback": response,
//                          "is_ontime": time] as Dictionary<String, String>
       
        let myUrl = "http://82.76.188.13:3000/notifications/sendReview/" + ReviewViewController.notifID
        
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
        print("am trimis review")
        task.resume()
        
              
        
   
        
        
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
