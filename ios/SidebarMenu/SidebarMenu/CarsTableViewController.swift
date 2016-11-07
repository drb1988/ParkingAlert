//
//  CarsTableViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 9/21/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit
import AVFoundation


class CarsTableViewController: UITableViewController, QRCodeReaderViewControllerDelegate {
    

    lazy var reader = QRCodeReaderViewController(builder: QRCodeReaderViewControllerBuilder {
        $0.reader          = QRCodeReader(metadataObjectTypes: [AVMetadataObjectTypeQRCode])
        $0.showTorchButton = true
    })

    var reachability = SplashViewController()
    static var enableNotifications : Bool = true
    static var allowOthers: Bool = true
    @IBOutlet var backButton: UIBarButtonItem!
   
    var cars = Cars()
    let SegueIdentifier = "DetailsViewController"

    @IBOutlet var addCar: UIBarButtonItem!
    
    @IBOutlet var carsTableView: UITableView!
    
     var images : [UIImage] = []
    
    
    
    func image(_ image: UIImage, didFinishSavingWithError error: NSError?, contextInfo: UnsafeRawPointer) {
        if let error = error {
            // we got back an error!
            let ac = UIAlertController(title: "Save error", message: error.localizedDescription, preferredStyle: .alert)
            ac.addAction(UIAlertAction(title: "OK", style: .default))
            present(ac, animated: true)
        } else {
            let ac = UIAlertController(title: "Saved!", message: "Your altered image has been saved to your photos.", preferredStyle: .alert)
            ac.addAction(UIAlertAction(title: "OK", style: .default))
            present(ac, animated: true)
        }
    }
    
    func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        let documentsDirectory = paths[0]
        return documentsDirectory
    }
    
    func generateQRCode(from string: String) -> UIImage? {
        let data = string.data(using: String.Encoding.ascii)
        
        if let filter = CIFilter(name: "CIQRCodeGenerator") {
            filter.setValue(data, forKey: "inputMessage")
            let transform = CGAffineTransform(scaleX: 3, y: 3)
            
            if let output = filter.outputImage?.applying(transform) {
                return UIImage(ciImage: output)
            }
        }
        
        return nil
    }
    
    
    
    func scanAction() {
        if QRCodeReader.supportsMetadataObjectTypes() {
            reader.modalPresentationStyle = .formSheet
            reader.delegate               = self
            
            reader.completionBlock = { (result: QRCodeReaderResult?) in
                if let result = result {
                    print("Completion with result: \(result.value) of type \(result.metadataType)")
                     UserDefaults.standard.set(result.value, forKey: "myQR")
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
    
    
    func reader(_ reader: QRCodeReaderViewController, didScanResult result: QRCodeReaderResult) {
        dismiss(animated: true) { [weak self] in
            let alert = UIAlertController(
                title: "Codul a fost",
                message: "scanat cu succes ! ",
                preferredStyle: .alert
            )
            
            
            
            
            alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                self?.changeView()
                
            }))
            
            self?.present(alert, animated: true, completion: nil)
            
            
            
            
        }
    }
    
    func changeView(){
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ScanQRViewController")
        
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


    

    override func viewWillAppear(_ animated: Bool) {
        self.getCar()
        self.carsTableView.reloadData()

    }
    
  
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
       // getCar()
        
        backButton.target = self
        backButton.action = #selector(CarsTableViewController.goBack)
        addCar.target = self

        addCar.action = #selector(CarsTableViewController.addCars)
        
//        
//        getStatusFromRemoteSource { (status) -> Void in
//            print("Status:\(status)")
//            self.carName = status
//            
//        }
//    
      self.carsTableView.reloadData()
       
        
        
        }
    

    
    
    
    
    func getCar(){
        
        
        
        if let userID = UserDefaults.standard.string(forKey: "userID") {
            print("MOrtii:\(userID)")
            
        }
        
        if let token = UserDefaults.standard.string(forKey: "token"){
            print("Matii:\(token)")
        }
        
        
        
        _ = ["content-type": "application/json",
                      "authorization": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNTdlNTBmNjkwMjllZGQyNGE4OTk4ZTBkIiwiZW1haWwiOiJqZW51In0.TK1mqgYXMOWCRGTLe62JbkQYzCnq4Q7uVol3K-2-438",
                      "cache-control": "no-cache",
                      "postman-token": "e5de12cc-7c04-f8e8-212f-512b71da0b19"]
        
        let myUrl = "http://82.76.188.13:3000/users/getCars/" + UserDefaults.standard.string(forKey: "userID")!
        print("myURL:\(myUrl)")
        
        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
       
        let url=NSURL(string: myUrl)
         print("myToken:\(myToken)")
        print("GetCarsmyURL:\(myUrl)")
        let request = NSMutableURLRequest(url:url as! URL);
        
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
            print("responseString = \(responseString)")
            
            // Set request HTTP method to GET. It could be POST as well
  
            do{
                let allContacts = try JSONSerialization.jsonObject(with: data! , options: JSONSerialization.ReadingOptions())
                
                
                if let json = allContacts as? Array<AnyObject> {
                    if(json.count != 0){
                        print(json)
                        for index in 0...json.count-1 {
                            
                            let contact : AnyObject? = json[index]
                            print("Contact:\(contact)")
                            
                            let collection = contact! as! Dictionary<String, AnyObject>
                            print("Collection:\(collection)")
                            
                            
                            if let name  = collection["given_name"] as? String{
                                self.cars.carName.append(name)
                            }
                            
                            if let plate = collection["plates"] as? String{
                                self.cars.carNumber.append(plate)
                                
                            }
                            
                            if let producer  = collection["make"] as? String{
                                self.cars.carProducer.append(producer)
                                
                            }
                            
                            if let model  = collection["model"] as? String{
                                self.cars.carModel.append(model)
                            }
                            
                            if let year  = collection["year"] as? String{
                                self.cars.carYear.append(year)
                            }
                            if let mySwitch = collection["enable_notifications"] as? Bool{
                                
                                CarsTableViewController.enableNotifications = mySwitch
                            }
                            
                            
                            if let mysecSwitch = collection["enable_others"] as? Bool{
                                
                                CarsTableViewController.allowOthers = mysecSwitch
                            }
                            
                            if let myQRcode = collection["qr_code"] as? String{
                                
                                let img = self.generateQRCode(from: myQRcode)
                                self.images.append(img!)
                                
                            }

                            DispatchQueue.main.asyncAfter(deadline: .now() ) {
                                self.carsTableView.reloadData()
                            }
                        }
                        
                    }
                    print(self.images)
                   // print(self.carName)
                   // print(self.carNumber)
                }
                
            }catch _ as NSError{
                
            }
        }
        task.resume()
        
        
        
        }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    
    
 
    
    func goBack(){
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RevealViewController")
        
        self.present(viewController, animated: false, completion: nil)
        

        
    }
    
    
    func addCars(){
        
        actionSheet()
        
               
        
       
        
    }
    


    
    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        if(self.cars.carName.isEmpty == false){

    
        return self.cars.carName.count
        }
        else {
            return 1
        }
      
        }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
 
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "car", for: indexPath) as! CarsTableViewCell
        
        
      if(self.cars.carName.isEmpty == false){
            
            cell.nameCar.text = cars.carName[indexPath.row]
            cell.numberCar.text = cars.carNumber[indexPath.row]
            cell.imageCar.image = images[indexPath.row]
            cell.accessoryType = .disclosureIndicator
        
        
                }
        else{
        
            cell.nameCar.text = "Nu ai nicio masina"
            cell.numberCar.text = "Trebuie sa ai cel putin o masina adaugata"
     //   actionSheet()

        }
    
        return cell
        
        
    }
 
    
    
    func actionSheet(){
        let alertController = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        
        let sendButton = UIAlertAction(title: "Am cod QR", style: .default, handler: { (action) -> Void in
          
            
            self.scanAction()
            ScanQRViewController.type = true
//            
//            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "BarcodeViewController")
//            
//            self.present(viewController, animated: false, completion: nil)
        })
        
        let  deleteButton = UIAlertAction(title: "Nu am cod QR", style: .default, handler: { (action) -> Void in
                      ScanQRViewController.type = false
                      self.changeView()
        })
        
        let cancelButton = UIAlertAction(title: "Anuleaza", style: .cancel, handler: { (action) -> Void in
            print("Cancel button tapped")
        })
        
        
        alertController.addAction(sendButton)
        alertController.addAction(deleteButton)
        alertController.addAction(cancelButton)
        
        self.navigationController!.present(alertController, animated: true, completion: nil)
    }

    
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    

    
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            self.carsTableView.deleteRows(at: [indexPath], with: .fade)
            self.cars.carName.remove(at: indexPath.row)
            self.carsTableView.reloadData()
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if  segue.identifier == SegueIdentifier,
          
            let blogIndex = tableView.indexPathForSelectedRow?.row
        {
            EditCarViewController.name = cars.carName[blogIndex]
            EditCarViewController.plate = cars.carNumber[blogIndex]
            EditCarViewController.producer = cars.carProducer[blogIndex]
            EditCarViewController.model = cars.carModel[blogIndex]
            EditCarViewController.year = cars.carYear[blogIndex]
            EditCarViewController.mySwitchValue = CarsTableViewController.enableNotifications
            EditCarViewController.mySecondSwitchValue = CarsTableViewController.allowOthers
            EditCarViewController.myImage = images[blogIndex]
            
            
        }
        
 
    }
    
    
    


}
