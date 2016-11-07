//
//  ProgressViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 10/24/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit


class ProgressViewController: UIViewController {
    
    @IBOutlet var back: UIBarButtonItem!
    var ifextended: Bool = false
    static var crash : Bool = false
    
    
    @IBOutlet weak var btnArrive: UIButton!
    @IBOutlet weak var profileImage: UIImageView!
    var time: Double = 0.1
    static var dateString: Double = 0
    static var responseHour: String = " "
    @IBOutlet var circleProgressView: CircleProgressView!
    var timer = Timer()
    var timerExtended = Timer()
    var timeLeft : Double = 10
    static var maximum : Double = 0
    static var plates: String = " "
    static var myType : Bool = false
    static var notifID : String = " " 
    var myReview = ReviewViewController()

    var counter: Int = 0
    static var myTime: Double = 0.0
    @IBAction func doneButton(_ sender: AnyObject) {
    
   //      hasExtended()
        
        
    }
    @IBOutlet weak var detailsText: UITextView!
    
    static var  currentTime: Double = 0
    
    @IBOutlet var timeLabel: UILabel!
    static var minutes : String = " "
    
    
    
    override func viewDidAppear(_ animated: Bool) {
        
        
        let str = MapViewController.receiverPic
        let imageUrlString = str
        let imageUrl:NSURL = NSURL(string: imageUrlString)!
        
        
        let imageData:NSData = NSData(contentsOf: imageUrl as URL)!
        
        
        let profile = UIImage(data: imageData as Data)
        
        profileImage.image = profile

        
        
        btnArrive.layer.cornerRadius = 5
        
        timer.fire()
        
    }
    
    
    
    override func viewDidLoad() {
        
        super.viewDidLoad()
        
        if(ProgressViewController.myType == false){
            detailsText.isHidden = true
            profileImage.isHidden = true
            btnArrive.isHidden = true
            btnArrive.setTitle("Extinde timpul cu 5 minute", for: UIControlState.normal)
             btnArrive.addTarget(self, action: #selector(extended), for: UIControlEvents.touchUpInside)
            
        }
        else{
            detailsText.isHidden = false
            btnArrive.setTitle("A venit la masina", for: UIControlState.normal)
            profileImage.isHidden = false
            btnArrive.addTarget(self, action: #selector(arrived), for: UIControlEvents.touchUpInside)
        }
        print("din receiver")
        
        
        
        detailsText.text = ProgressViewController.plates + " vine in " + ProgressViewController.minutes + " minute" + "\n" + "Raspuns la " + ProgressViewController.responseHour
        
        circleProgressView.progress = ProgressViewController.maximum
        
        back.target = self
        back.action = #selector(ProgressViewController.goBack)
        profileImage.layer.cornerRadius = profileImage.frame.height/2
        
        
        let myDate = ProgressViewController.responseHour
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "HH:mm:ss"
        let date = dateFormatter.date(from: myDate)!
        dateFormatter.dateFormat = "HH"
        let hour = dateFormatter.string(from: date)
        dateFormatter.dateFormat = "mm"
        let minute = dateFormatter.string(from: date)
        dateFormatter.dateFormat = "ss"
        let seconds = dateFormatter.string(from: date)
        ProgressViewController.dateString = Double(hour)!*360 + Double(minute)!*60 + Double(seconds)!
        
        
        //ora la care am primit notificarea
        print("dateString:\(ProgressViewController.dateString)")
        
        
        
        //ora curenta
        let datte = Date()
        let calendar = Calendar.current
        
        let hours = calendar.component(.hour, from: datte)
        let minutes = calendar.component(.minute, from: datte)
        let second = calendar.component(.second, from: datte)
        print("hours = \(hours):\(minutes):\(second)")
        
        
        
        //ora curenta
        let currentTime = second + minutes*60 + hours*360
        print("currentTime:\(currentTime)")
        
        
        //timeLeft = 60
        
        
        
        self.timeLeft = Double(ProgressViewController.dateString) + Double(ProgressViewController.minutes)!*60 - Double(currentTime)
             print(self.timeLeft)
        
        ProgressViewController.myTime = timeLeft
           //  print("ham:\(Double(ProgressViewController.minutes)!)")
//        if( Double(ProgressViewController.minutes)!*60>self.timeLeft){
        //Double(ProgressViewController.minutes)!*60
        if(Double(ProgressViewController.dateString) + 3 * 60 > Double(currentTime)){
          //  print("ham:\(Double(ProgressViewController.minutes)!)")
            self.timer = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(self.changeProgress), userInfo: nil, repeats: true)
            
        }else{
            print("bam")
            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ReviewViewController")
            
            
            self.present(viewController, animated: true, completion: nil)
        
        }
    }
    
    
    func hasExtended(){
        
        
        
     
        
        
        print("intra in hasextended")
        
        self.ifextended = true
        
        
        
    
        
        self.timeLeft = timeLeft + 5*60
        ProgressViewController.myTime = timeLeft
        
        circleProgressView.progress = 0.0
        timer.invalidate()
       
        
        self.timerExtended =  Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(self.changeProgressExtended), userInfo: nil, repeats: true)
        
       

    }
    
    func extended(){
        self.ifextended = true
        
        
       
        print("intra in extended")
        
        self.timeLeft +=  5*60
        ProgressViewController.myTime = self.timeLeft
     
        timer.invalidate()
        changeProgress()
        
        
        
        let parameters = ["extension_time": "5"] as Dictionary<String, String>
        
        
        
        
        let myUrl = NSURL(string: "http://82.76.188.13:3000/notifications/receiverExtended/" + ProgressViewController.notifID)
        
        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        let session = URLSession.shared
        let request = NSMutableURLRequest(url:myUrl! as URL);
        
        request.httpMethod = "POST";// Compose a query string
        
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        request.addValue(myToken, forHTTPHeaderField: "authorization")
        
        request.httpBody = try! JSONSerialization.data(withJSONObject: parameters, options: [] )
        
        let task = session.dataTask(with: request as URLRequest) {
            data, response, error in
            
            if error != nil
            {
                print("error=\(error)")
                return
            }
            
            print("response = \(response)")
            
        }
        task.resume()
        
    btnArrive.isHidden = true
        
        
    }
    
    func arrived(){
        timerExtended.invalidate()
        myReview.sendReview(response: "true", time: "0")
        stopTimer()
        
        print("person has arrived")
        
//        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "SummaryViewController")
//        
//        
//        self.present(viewController, animated: false, completion: nil)
//
        
                let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "SummaryViewController")
        
        
                self.present(viewController, animated: false, completion: nil)
        
        
    }
    
    func getTime(x: String, hour: String, plates: String){
        ProgressViewController.minutes = x
        ProgressViewController.plates = plates
        ProgressViewController.responseHour = hour
        
        print("ProgressViewController.responseHour\(ProgressViewController.responseHour)")
        
    }
    
    func goBack(){
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ReceivedNotifications")
        
        
        self.present(viewController, animated: false, completion: nil)
        
        
    }
    
    func secondsTominutes (seconds : Int) -> ( Int) {
        return ((seconds % 3600) / 60)    }
    
    
    func secondstoSec (seconds : Int) -> ( Int) {
        return ((seconds % 3600) % 60)    }
    
    func changeProgress(){
        
        if(ProgressViewController.crash == true){
            
            
            timer.invalidate()
            hasExtended()
            
            
        }
        
        if(self.ifextended == false){
        
        if(timeLeft == 60){
            
              btnArrive.isHidden = false
        }
        }
        else{
            btnArrive.isHidden = true
        }
        
        if(ProgressViewController.myType == false){
                btnArrive.isHidden = true
        }else{
                btnArrive.isHidden = false
        }
        
        print("timeleft:\(timeLeft)")
        
        if(timeLeft > 0){
           
            timeLeft = timeLeft-1.0
          
            counter+=1
            let m = secondsTominutes(seconds: Int(timeLeft))
            let s = secondstoSec(seconds: Int(timeLeft))
            if(s >= 10){
                
                timeLabel.text = "0"+String(m) + " : " + String(s)
            }
            else{
                timeLabel.text = "0"+String(m) + " : " + "0"+String(s)
            }
            circleProgressView.progress += 1.0/ProgressViewController.myTime
            ProgressViewController.maximum = circleProgressView.progress
            
            
            if(timeLeft==0){
                timer.invalidate()
               
                timeLabel.text = "Gata"
                print("send bad review")
                
                
                if(ProgressViewController.myType == false){
                let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "SummaryViewController")
                
                
                self.present(viewController, animated: false, completion: nil)
                
                }
                else{
                    let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ReviewViewController")
                    
                    
                    self.present(viewController, animated: false, completion: nil)
                }
            }
            
        }
        else{
            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ReviewViewController")
            
            
            self.present(viewController, animated: false, completion: nil)
            
        }
        
        //
        //      let dates = Double(ProgressViewController.dateString) + Double(ProgressViewController.minutes)!*60

        //            let time = (Double(ProgressViewController.minutes))!/60 - 1
        //
        //            timeLabel.text = String(time) + " min"
        //
        //            let progresscounter:Double = time
        //
        //            circleProgressView.progress=progresscounter/0.5
    }
    
    
    func changeProgressExtended(){
        
        print("timeleftExtended:\(timeLeft)")
        
        if(timeLeft > 0){
            
            timeLeft = timeLeft-1.0
            
            counter+=1
            let m = secondsTominutes(seconds: Int(timeLeft))
            let s = secondstoSec(seconds: Int(timeLeft))
            if(s >= 10){
                
                timeLabel.text = "0"+String(m) + " : " + String(s)
            }
            else{
                timeLabel.text = "0"+String(m) + " : " + "0"+String(s)
            }
            circleProgressView.progress += 1.0/ProgressViewController.myTime
        }
        
    }
    
    
    func stopTimer()
    {
        
        print("Timer invalidate !!!")
       timer.invalidate()
      
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    
}
