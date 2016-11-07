//
//  ProgressViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 9/30/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit

class ProgressViewController: UIViewController {
//
//    @IBOutlet var back: UIBarButtonItem!
//    
//     var time: Double = 0.1
//    static var dateString: Double = 0
//    static var responseHour: String = " "
//    @IBOutlet var circleProgressView: CircleProgressView!
//    var timer = Timer()
//    var timeLeft : Double = 10
//    static var maximum : Double = 0
//    
//    
//    static var  currentTime: Double = 0
//    
//    @IBOutlet var timeLabel: UILabel!
//    static var minutes : String = " "
//    override func viewDidLoad() {
//        super.viewDidLoad()
//        
//        circleProgressView.progress = ProgressViewController.maximum
//        
//        back.target = self
//        back.action = #selector(ProgressViewController.goBack)
//        
//        
//        let myDate = ProgressViewController.responseHour
//        let dateFormatter = DateFormatter()
//        dateFormatter.dateFormat = "HH:mm:ss"
//        let date = dateFormatter.date(from: myDate)!
//        dateFormatter.dateFormat = "HH"
//        let hour = dateFormatter.string(from: date)
//        dateFormatter.dateFormat = "mm"
//        let minute = dateFormatter.string(from: date)
//        dateFormatter.dateFormat = "ss"
//        let seconds = dateFormatter.string(from: date)
//        ProgressViewController.dateString = Double(hour)!*360 + Double(minute)!*60 + Double(seconds)!
//        
//        
//        //ora la care am primit notificarea
//        print("dateString:\(ProgressViewController.dateString)")
//        
//
//        
//        //ora curenta
//        let datte = Date()
//        let calendar = Calendar.current
//        
//        let hours = calendar.component(.hour, from: datte)
//        let minutes = calendar.component(.minute, from: datte)
//        let second = calendar.component(.second, from: datte)
//        print("hours = \(hours):\(minutes):\(second)")
//        
//        
//        
//        //ora curenta
//        let currentTime = second + minutes*60 + hours*360
//        print("currentTime:\(currentTime)")
//        
//        
//        //timeLeft = 60
//        
//       
//        
//        timeLeft = Double(ProgressViewController.dateString) + Double(ProgressViewController.minutes)!*60 - Double(currentTime)
//        if( Double(ProgressViewController.minutes)!*60>timeLeft){
//            self.timer = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(self.changeProgress), userInfo: nil, repeats: true)
//
//               }else{
//            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MapViewController")
//            
//            
//            self.present(viewController, animated: false, completion: nil)
//
//        }
//   
//    }
//    
//    func getTime(x: String, hour: String){
//        ProgressViewController.minutes = x
//        ProgressViewController.responseHour = hour
//        
//    }
//    
//    func goBack(){
//        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ReceivedNotifications")
//        
//        
//        self.present(viewController, animated: false, completion: nil)
//        
//
//    }
//    
//    func secondsTominutes (seconds : Int) -> ( Int) {
//        return ((seconds % 3600) / 60)    }
//    
//    
//    func secondstoSec (seconds : Int) -> ( Int) {
//        return ((seconds % 3600) % 60)    }
//    
//    func changeProgress(){
//        
//        
//        let max: Double = Double(ProgressViewController.minutes)!*60
//        print(max/60)
//        print("here")
//        if(timeLeft > 0){
//        timeLeft = timeLeft-1.0
//            let m = secondsTominutes(seconds: Int(timeLeft))
//            let s = secondstoSec(seconds: Int(timeLeft))
//         
//        timeLabel.text = String(m) + " : " + String(s)
//        circleProgressView.progress += 1.0/timeLeft
//        ProgressViewController.maximum = circleProgressView.progress
//
//       
//        if(timeLeft==0){
//            timer.invalidate()
//            timeLabel.text = "Gata"
//        }
//        
//        }
//        else{
//            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MapViewController")
//            
//            
//            self.present(viewController, animated: false, completion: nil)
//
//        }
////
////      let dates = Double(ProgressViewController.dateString) + Double(ProgressViewController.minutes)!*60
////
////        
////            
////      
////            
////            let time = (Double(ProgressViewController.minutes))!/60 - 1
////            
////            timeLabel.text = String(time) + " min"
////            
////            let progresscounter:Double = time
////            
////            circleProgressView.progress=progresscounter/0.5
//        }
//
//    
//
//    override func didReceiveMemoryWarning() {
//        super.didReceiveMemoryWarning()
//        // Dispose of any resources that can be recreated.
//    }
    

    
}
