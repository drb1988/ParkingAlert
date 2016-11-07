//
//  MapViewController.swift
//  SidebarMenu
//
//  Created by Simon Ng on 2/2/15.
//  Copyright (c) 2015 AppCoda. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation
import AddressBookUI



class MapViewController: UIViewController, CLLocationManagerDelegate, MKMapViewDelegate {
    @IBOutlet weak var timeLabel: UILabel!
    static var invalid : Bool = false
    
    var reachability = SplashViewController()

    @IBOutlet var mapView: MKMapView!
    
    @IBOutlet weak var messageLabel: UILabel!
    var review = ReviewViewController()

    @IBOutlet weak var myProgress: UIProgressView!
    
    @IBOutlet var adressText: UITextView!
    
    var locationManager = CLLocationManager()
    static var latitude: String = "47.080750423527398"
    static var longitude: String = "21.924092089777741"
    static var createHour: String = " "
    static var receiverPic: String = "http://82.76.188.13:3000/file-1476958406884.jpg"
    static var numberPlates: String = "BH12ABC"
    static var myType: Bool = true
    static var dateString: Double = 0.0
   
    var mylatitude: Double = 0
    var mylongitude: Double = 0
    @IBOutlet var backButton: UIBarButtonItem!
    
    
    var maptimer = Timer()
  
    var counter: Int = 0;
    
    var total: Int = 60;

   
    override func viewDidLoad() {
        
        
               
        let pulseAnimation = CABasicAnimation(keyPath: "opacity")
        pulseAnimation.duration = 5
        pulseAnimation.fromValue = 0
        pulseAnimation.toValue = 1
        pulseAnimation.timingFunction = CAMediaTimingFunction(name: kCAMediaTimingFunctionEaseInEaseOut)
        pulseAnimation.autoreverses = true
        pulseAnimation.repeatCount = FLT_MAX
        self.messageLabel.layer.add(pulseAnimation, forKey: "animateOpacity")
        
        
      
        let myDate = MapViewController.createHour
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        let date = dateFormatter.date(from: myDate)!
        dateFormatter.dateFormat = "HH"
        let hour = dateFormatter.string(from: date)
        dateFormatter.dateFormat = "mm"
        let minute = dateFormatter.string(from: date)
        dateFormatter.dateFormat = "ss"
        let seconds = dateFormatter.string(from: date)
        MapViewController.dateString = Double(hour)!*360 + Double(minute)!*60 + Double(seconds)!
        
        
        
        
        
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
        
        
        backButton.target = self
        backButton.action = #selector(MapViewController.goBack)
        
  
            myProgress.isHidden = false
            myProgress.progress = 0.0
            maptimer = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector:#selector(MapViewController.timerFunction), userInfo: nil, repeats: true);
            
            myProgress.setProgress(0.00, animated: false)
        
        if(MapViewController.dateString + 60.0 < Double(currentTime))
        {
            maptimer.invalidate()
            review.sendReview(response: "false", time: "0")
            
        }
        


     

    }
    
    
  
    
    func stopTimer()
    {
        
        print("Timer invalidate !!!")
        maptimer.invalidate()
        
    }
    
    
    
    func timerFunction() {
        
        counter += 1;
        print("counter:\(counter)")
        
        timeLabel.text = NSString(format:"%d", 60-counter) as String;
        
        myProgress.setProgress(Float(counter)/Float(total), animated: false);
        
     
        if(MapViewController.invalid == true){
            maptimer.invalidate()
        }
        
        
        if(counter == total) {
            
            maptimer.invalidate()
            print("a expirat timpul de 60 secunde")
            
            review.sendReview(response: "false", time: "0")
            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RevealViewController")
            
            self.present(viewController, animated: false, completion: nil)

            
        }
        
    }

    
        
    
    override func viewDidAppear(_ animated: Bool) {
        
        print("awimbawue")
        
  
        let myUrl = "http://82.76.188.13:3000/users/getNotification/" + UserDefaults.standard.string(forKey: "userID")!
        print("myURL:\(myUrl)")
        
        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        
        let url=NSURL(string: myUrl)
        print("myToken:\(myToken)")
        print("GetNotifmyURL:\(myUrl)")
        let request = NSMutableURLRequest(url:url as! URL);
        request.httpMethod = "GET"
        
        request.addValue(myToken, forHTTPHeaderField: "authorization")
        
        
        
        let task = URLSession.shared.dataTask(with: request as URLRequest) {
            data, response, error in
            
            guard data != nil else {
                self.reachability.setupReachability(hostName: "google.com", useClosures: true)
                self.reachability.startNotifier()
                return
            }
            
            
            //let allContactsData=NSData(contentsOf:url as! URL)
            
            let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            print("responseStringNotif = \(responseString)")
            
            // Set request HTTP method to GET. It could be POST as well
            do{
                let allContacts = try JSONSerialization.jsonObject(with: data! , options: JSONSerialization.ReadingOptions())
                
     
                let collection = allContacts as! Dictionary<String, AnyObject>
                
                            
                            if let receiver  = collection["receiver_picture"] as? String{
                                MapViewController.receiverPic = receiver
                                print(receiver)
                                
                            }
                            
                            if let notification = collection["_id"] as? String{
                                ReviewViewController.notifID = notification
                                print("_IDS:\(notification)")
                            }
                            if let number = collection["vehicle"] as? String{
                                print(number)
                                MapViewController.numberPlates = number
                            }
                            
                            //  let collection = contact! as! Dictionary<String, AnyObject>
                            //  print("Collection:\(collection)")
                            
                            
                            
                            
                    
                
            }catch _ as NSError{
            }
            
        }
        task.resume()
        
        
        
        self.locationManager.delegate = self
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        self.locationManager.requestAlwaysAuthorization()
        self.mapView.delegate=self
        self.locationManager.startUpdatingLocation()
        
        
        
   
    }
    
    func receiverPicture(){
        
        let myUrl = "http://82.76.188.13:3000/users/getNotifications/" + UserDefaults.standard.string(forKey: "userID")!
        print("myURL:\(myUrl)")
        
        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        
        let url=NSURL(string: myUrl)
        print("myToken:\(myToken)")
        print("GetNotifmyURL:\(myUrl)")
        let request = NSMutableURLRequest(url:url as! URL);
        request.httpMethod = "GET"
        
        request.addValue(myToken, forHTTPHeaderField: "authorization")
        
        
        
        let task = URLSession.shared.dataTask(with: request as URLRequest) {
            data, response, error in
            
            guard data != nil else {
                self.reachability.setupReachability(hostName: "google.com", useClosures: true)
                self.reachability.startNotifier()
                return
            }
            
            
            //let allContactsData=NSData(contentsOf:url as! URL)
            
            let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            print("responseStringNotif = \(responseString)")
            
            // Set request HTTP method to GET. It could be POST as well
            
            do{
                let allContacts = try JSONSerialization.jsonObject(with: data! , options: JSONSerialization.ReadingOptions())
                
                
                if let json = allContacts as? Array<AnyObject> {
                    if(json.count != 0){
                        print(json)
                        for index in 0...json.count-1 {
                            
                            let contact : AnyObject? = json[index]
                            print("Contact:\(contact)")
                            
                            if let receiver  = contact?["receiver_picture"] as? String{
                                MapViewController.receiverPic = receiver
                                print(receiver)
                                
                            }
                            
                            if let number = contact?["vehicle"] as? String{
                                print(number)
                                MapViewController.numberPlates = number
                            }
                            
//                            let collection = contact! as! Dictionary<String, AnyObject>
//                            print("Collection:\(collection)")
                           
                            
                          
                            
                           
                        }
                    }
                }
             }catch _ as NSError{
            }
            
        }
        task.resume()
        
        
        
    }
    
    func getTime() -> String{
     
        let cal =  Calendar(identifier: .gregorian)
        
        let myDate = MapViewController.createHour
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        let date = dateFormatter.date(from: myDate)!
        dateFormatter.dateFormat = "yyyy"
        let year = dateFormatter.string(from: date)
        dateFormatter.dateFormat = "MM"
        let month = dateFormatter.string(from: date)
        dateFormatter.dateFormat = "dd"
        let day = dateFormatter.string(from: date)
        dateFormatter.dateFormat = "HH"
        let hour = dateFormatter.string(from: date)
        dateFormatter.dateFormat = "mm"
        let minute = dateFormatter.string(from: date)
        
        
        
        print(year, month, day, hour, minute)
        
        
        let date1 = cal.date(from: DateComponents(year: Int(year), month:  Int(month), day: Int(day), hour: Int(hour), minute: Int(minute)))!
        
        
        
        let timeOffset = timeAgoSince(date1)
        print(timeOffset)
        
        return timeOffset

        
    }
    
    
    func goBack(){
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ReceivedNotifications")
        
        self.present(viewController, animated: true, completion: nil)
        
    }
    


    

    

    
    //this function moves the map
    
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        
        
      //  let userLocation = locations.last
        
       
        
        
        let latDelta:CLLocationDegrees = 0.01
        let lonDelta:CLLocationDegrees = 0.01
        
        let span:MKCoordinateSpan = MKCoordinateSpanMake(latDelta, lonDelta)
        
        let location: CLLocationCoordinate2D = CLLocationCoordinate2DMake(MapViewController.latitude.toDouble(), MapViewController.longitude.toDouble())
        
        let region:MKCoordinateRegion = MKCoordinateRegionMake(location, span)
        let myLocation = CLLocation(latitude:MapViewController.latitude.toDouble() , longitude:MapViewController.longitude.toDouble())
        
        self.mapView.setRegion(region, animated: true)
        
        let geocoder = CLGeocoder()
        
        geocoder.reverseGeocodeLocation(myLocation, completionHandler: { (placemarks, error) -> Void in
            
            // Place details
            var placeMark: CLPlacemark!
            placeMark = placemarks?[0]
            
            // Address dictionary
            print(placeMark.addressDictionary, terminator: "")
            
            // Location name
            if let locationName = placeMark.addressDictionary!["Name"] as? NSString {
             //   print(locationName, terminator: "")
            
            
            // Street address
            if let street = placeMark.addressDictionary!["Thoroughfare"] as? NSString {
                print(street as String)
            
            
            // City
            if let city = placeMark.addressDictionary!["City"] as? NSString {
              //  print(street as String)
            
            
            // Zip code
            if let zip = placeMark.addressDictionary!["ZIP"] as? NSString {
               // print(zip, terminator: "")
            
        
            // Country
            if (placeMark.addressDictionary!["Country"] as? NSString) != nil {
              //  print(country, terminator: "")
                
                let str = String((locationName as String) + " " + (city as String) + " " + (zip as String))
                self.adressText.text = "Locatia: " + "\n" + str!
                
            }
                }
                }
                }
            }
        })
        
        
        let annotation: MKPointAnnotation = MKPointAnnotation()

  
        annotation.coordinate = location
        
        
        
        annotation.title = "Notificat " + MapViewController.numberPlates
        annotation.subtitle =  getTime()
        
        mapView.addAnnotation(annotation)
        
        self.locationManager.stopUpdatingLocation()

        
        
        
    }
    
    
    
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView?
    {
        if !(annotation is MKPointAnnotation) {
            return nil
        }
        
        let annotationIdentifier = "AnnotationIdentifier"
        var annotationView = mapView.dequeueReusableAnnotationView(withIdentifier: annotationIdentifier)
        
        if annotationView == nil {
            annotationView = MKAnnotationView(annotation: annotation, reuseIdentifier: annotationIdentifier)
            annotationView!.canShowCallout = true
        }
        else {
            annotationView!.annotation = annotation
        }
        
        annotationView?.image = UIImage(named: "rsz_pin.png")
        let myImage = UIImageView(frame: CGRect(x: 0, y: 0, width: 45, height: 45))
        
        
        let str = MapViewController.receiverPic
        let imageUrlString = str
        let imageUrl:NSURL = NSURL(string: imageUrlString)!
        
       // print("MapViewController.receiverPic:\(MapViewController.receiverPic)")
        let imageData:NSData = NSData(contentsOf: imageUrl as URL)!
        
        
        let profileImage = UIImage(data: imageData as Data)
        
        let backColor = UIColor(red: 83.0/255.0, green: 135.0/255.0, blue: 203.0/255.0, alpha: 1.0)
        myImage.layer.borderColor = backColor.cgColor
        
        myImage.layer.masksToBounds = true
        myImage.layer.cornerRadius = myImage.frame.height / 2
       // myImage.layer.borderWidth = 7
        let imageCircle = profileImage?.resize(CGSize(width: 20, height: 20))?.circled(forRadius: 20)
        myImage.image = imageCircle
        myImage.contentMode = UIViewContentMode.scaleAspectFill
        annotationView!.leftCalloutAccessoryView = myImage
        annotationView!.leftCalloutAccessoryView?.backgroundColor = backColor
        
        
        return annotationView
    }
    
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}


    extension String {
        func toDouble() -> Double {
            if let unwrappedNum = Double(self) {
                return unwrappedNum
            } else {
                // Handle a bad number
                print("Error converting \"" + self + "\" to Double")
                return 0.0
            }
        }
}


extension Date {
    /// Returns the amount of years from another date
    func years(from date: Date) -> Int {
        return Calendar.current.dateComponents([.year], from: date, to: self).year ?? 0
    }
    /// Returns the amount of months from another date
    func months(from date: Date) -> Int {
        return Calendar.current.dateComponents([.month], from: date, to: self).month ?? 0
    }
    /// Returns the amount of weeks from another date
    func weeks(from date: Date) -> Int {
        return Calendar.current.dateComponents([.weekOfYear], from: date, to: self).weekOfYear ?? 0
    }
    /// Returns the amount of days from another date
    func days(from date: Date) -> Int {
        return Calendar.current.dateComponents([.day], from: date, to: self).day ?? 0
    }
    /// Returns the amount of hours from another date
    func hours(from date: Date) -> Int {
        return Calendar.current.dateComponents([.hour], from: date, to: self).hour ?? 0
    }
    /// Returns the amount of minutes from another date
    func minutes(from date: Date) -> Int {
        return Calendar.current.dateComponents([.minute], from: date, to: self).minute ?? 0
    }
    /// Returns the amount of seconds from another date
    func seconds(from date: Date) -> Int {
        return Calendar.current.dateComponents([.second], from: date, to: self).second ?? 0
    }
    /// Returns the a custom time interval description from another date
    func offset(from date: Date) -> String {
        if years(from: date)   > 0 { return "\(years(from: date))y"   }
        if months(from: date)  > 0 { return "\(months(from: date))M"  }
        if weeks(from: date)   > 0 { return "\(weeks(from: date))w"   }
        if days(from: date)    > 0 { return "\(days(from: date))d"    }
        if hours(from: date)   > 0 { return "\(hours(from: date))h"   }
        if minutes(from: date) > 0 { return "\(minutes(from: date))m" }
        if seconds(from: date) > 0 { return "\(seconds(from: date))s" }
        return ""
    }
}




public func timeAgoSince(_ date: Date) -> String {
    
    let calendar = Calendar.current
    let now = Date()
    let unitFlags: NSCalendar.Unit = [.second, .minute, .hour, .day, .weekOfYear, .month, .year]
    let components = (calendar as NSCalendar).components(unitFlags, from: date, to: now, options: [])
    
    if let year = components.year, year >= 2 {
        return "\(year) ani"
    }
    
    if let year = components.year, year >= 1 {
        return "Anul trecut"
    }
    
    if let month = components.month, month >= 2 {
        return "\(month) luni"
    }
    
    if let month = components.month, month >= 1 {
        return "Luna trecuta"
    }
    
    if let week = components.weekOfYear, week >= 2 {
        return "acum \(week)saptamani"
    }
    
    if let week = components.weekOfYear, week >= 1 {
        return "Saptamana trecuta"
    }
    
    if let day = components.day, day >= 2 {
        return "acum \(day) zile"
    }
    
    if let day = components.day, day >= 1 {
        return "Ieri"
    }
    
    if let hour = components.hour, hour >= 2 {
        return "acum \(hour) ore"
    }
    
    if let hour = components.hour, hour >= 1 {
        return "Ora trecuta"
    }
    
    if let minute = components.minute, minute >= 2 {
        return "acum \(minute) minute"
    }
    
    if let minute = components.minute, minute >= 1 {
        return "un minut"
    }
    
    if let second = components.second, second >= 3 {
        return "acum \(second) secunde"
    }
    
    return "Chiar acum"
    
}

extension UIImage {
    func resize(_ size: CGSize) -> UIImage? {
        let rect = CGRect(origin: .zero, size: size)
        return redraw(in: rect)
    }
    
    func redraw(in rect: CGRect) -> UIImage? {
        UIGraphicsBeginImageContextWithOptions(rect.size, false, UIScreen.main.scale)
        
        guard let context = UIGraphicsGetCurrentContext(), let cgImage = cgImage else { return nil }
        
        context.draw(cgImage, in: rect)
        
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image
    }
    
    func circled(forRadius radius: CGFloat) -> UIImage? {
        let rediusSize = CGSize(width: radius, height: radius)
        let rect = CGRect(origin: .zero, size: size)
        
        UIGraphicsBeginImageContextWithOptions(size, false, UIScreen.main.scale)
        
        guard let context = UIGraphicsGetCurrentContext() else { return nil }
        
        let bezierPath = UIBezierPath(roundedRect: rect, byRoundingCorners: [.allCorners], cornerRadii: rediusSize)
        context.addPath(bezierPath.cgPath)
        context.clip()
        
        draw(in: rect)
        context.drawPath(using: .fillStroke)
        
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return image
    }


}

