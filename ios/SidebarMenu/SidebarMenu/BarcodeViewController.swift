



import UIKit
import AVFoundation
import CoreLocation


class BarcodeViewController: UIViewController, QRCodeReaderViewControllerDelegate, CLLocationManagerDelegate {
    lazy var reader = QRCodeReaderViewController(builder: QRCodeReaderViewControllerBuilder {
        $0.reader          = QRCodeReader(metadataObjectTypes: [AVMetadataObjectTypeQRCode])
        $0.showTorchButton = true
    })
    @IBOutlet var scanImageView: UIImageView!

    @IBOutlet var menuButton: UIBarButtonItem!
    
      var locationManager: CLLocationManager!
    static var carPlate: String = " "
    static var latitude: String = " "
    static var longitude: String = " "
    
    static var receiverCar: String = " "
    
    
   func scanAction() {
        if QRCodeReader.supportsMetadataObjectTypes() {
            reader.modalPresentationStyle = .formSheet
            reader.delegate               = self
            
            reader.completionBlock = { (result: QRCodeReaderResult?) in
                if let result = result {
                    print("Completion with result: \(result.value) of type \(result.metadataType)")
                    let myUrl = "http://82.76.188.13:3000/users/getUsersForCode/" + result.value
                    
                    let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
                    
                    print("myToken:\(myToken)")
                    print("myURL:\(myUrl)")
                    
                    let request = NSMutableURLRequest(url: URL(string: myUrl)! as URL)
                    
                    let session = URLSession.shared
                    request.httpMethod = "GET"
                    
                    //Note : Add the corresponding "Content-Type" and "Accept" header. In this example I had used the application/json.
                    request.addValue("application/json", forHTTPHeaderField: "Content-Type")
                    request.addValue("application/json", forHTTPHeaderField: "Accept")
                    request.addValue(myToken, forHTTPHeaderField: "authorization")
                    //    request.httpBody = try! JSONSerialization.data(withJSONObject: parameters, options: [] )
                    
                    let task = session.dataTask(with: request as URLRequest) { data, response, error in
                        
               
                        do{
                            let allContacts = try JSONSerialization.jsonObject(with: data! , options: JSONSerialization.ReadingOptions())
                            
                            
                            if let json = allContacts as? Array<AnyObject> {
                                if(json.count != 0){
                               //     print(json)
                                    for index in 0...json.count-1 {
                                        
                                        let contact : AnyObject? = json[index]
                                     //   print("Contact:\(contact)")
                                        
                                        let collection = contact! as! Dictionary<String, AnyObject>
                                    //    print("Collection:\(collection)")
                                        
                                        
                                        if let receiverID  = collection["userID"] as? String{
                                            print("ReceiverIDDDD:\(receiverID)")
                                            
                                        
                                        if let car = collection["car"]?["plates"] as? String{
                                           print("CarCar:\(car)")
                                            BarcodeViewController.receiverCar = car
                                            self.sendNotification(userID: receiverID, car: car)
                                           
                                            
                                            
                                        }
                                        }
                                
                                    }
                                }
                            }
                        }catch {
                            print(error)
                        }
                        
                    }
                     task.resume()
                    
                }

                
                
        
                
                
            }
    
            present(reader, animated: true, completion: nil)
        }
        else {
            let alert = UIAlertController(title: "Error", message: "Reader not supported by the current device", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: nil))
            
            present(alert, animated: true, completion: nil)
        }
    }
    
    

        
    func sendNotification(userID: String, car: String){
        
        let date = NSDate()
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        let dateString = dateFormatter.string(from: date as Date)
        print(dateString)

        
        let parameters = ["status": "3",
                          "is_active": "false",
                          "latitude": BarcodeViewController.latitude,
                          "longitude": BarcodeViewController.longitude,
                          "vehicle": car,
                          "sender_id": UserDefaults.standard.string(forKey: "userID")!,
                          "sender_nickname": "Cristi",
                          "receiver_id": userID,
                          "receiver_nickname": "Vasile"]  as Dictionary<String, String>
//
        let myUrl = "http://82.76.188.13:3000/notifications/notification"
        
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
                print("no data found: \(error)")
                return
            }
            
            
        }
        print("am trimis notificare")
        
        MapViewController.latitude = BarcodeViewController.latitude
        MapViewController.longitude = BarcodeViewController.longitude
        MapViewController.numberPlates = car
        MapViewController.createHour = dateString
        ProgressViewController.myType = true
        
        
   

        task.resume()
        
        

    }
    
    
    
    
    override func viewDidLoad() {
        
        scanAction()
//        let tapGestureRecognizer = UITapGestureRecognizer(target:self, action:#selector(BarcodeViewController.scanAction))
//        self.scanImageView.isUserInteractionEnabled = true
//        self.scanImageView.addGestureRecognizer(tapGestureRecognizer)
        
        
        
        self.menuButton.target = self
        self.menuButton.action = #selector(BarcodeViewController.goBack)
        
        
        
        self.locationManager = CLLocationManager()
        locationManager.requestAlwaysAuthorization()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.startUpdatingLocation()
    }
    

    func locationManager(_ manager: CLLocationManager!,   didUpdateLocations locations: [AnyObject]!) {
        
        
        let userLocation = locations.last
       
        
     
        print("CoreLocation")
        if(userLocation?.coordinate.latitude != nil){
        print(userLocation!.coordinate.latitude)
        BarcodeViewController.latitude = "\(userLocation!.coordinate.latitude)"
        }
        
        if(userLocation?.coordinate.longitude != nil){
            print(userLocation!.coordinate.longitude)
               BarcodeViewController.longitude = "\(userLocation!.coordinate.longitude)"
        }
        
        locationManager.stopUpdatingLocation()
        
    }
    
    func goBack(){
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RevealViewController")
        
        self.present(viewController, animated: false, completion: nil)
    }
    
    
    // MARK: - QRCodeReader Delegate Methods
    
    func reader(_ reader: QRCodeReaderViewController, didScanResult result: QRCodeReaderResult) {
        dismiss(animated: true) { [weak self] in
            let alert = UIAlertController(
                title: BarcodeViewController.receiverCar,
                message: "a fost notificat cu succes ! ",
                preferredStyle: .alert
            )
            
            
          
      
            alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
               self?.changeView()
                
            }))
            
            self?.present(alert, animated: true, completion: nil)
            
            
            
            
        }
    }
  
    func changeView(){
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MapViewController")
        
        self.present(viewController, animated: false, completion: nil)

    }
    func readerDidCancel(_ reader: QRCodeReaderViewController) {
        dismiss(animated: true, completion: nil)
       
    }
    
    private func createReader() -> QRCodeReaderViewController {
        let builder = QRCodeReaderViewControllerBuilder { builder in
            builder.reader          = QRCodeReader(metadataObjectTypes: [AVMetadataObjectTypeQRCode])
            builder.showTorchButton = true
        }
        
        return QRCodeReaderViewController(builder: builder)
    }
}
