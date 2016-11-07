//
//  ScanQRViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 10/13/16.
//  Copyright © 2016 AppCoda. All rights reserved.


import UIKit
import CoreData
import AssetsLibrary


class ScanQRViewController: UIViewController {
    
    static var type : Bool = true
    static var global : Bool = false
   
    @IBOutlet weak var myIndicator: UIActivityIndicatorView!
    static var carNr : String = " "
    @IBOutlet var nextStepButton: UIButton!
    @IBOutlet var qrImageView: UIImageView!
    
    static var forChange: Bool = false
    
    
    override func viewDidAppear(_ animated: Bool) {
        myIndicator.startAnimating()
        nextStepButton.layer.cornerRadius =  5
      //  nextStepButton.isHidden = true
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        if(ScanQRViewController.global == true){
            nextStepButton.setTitle("OK", for: UIControlState.normal)
           //  UIImageWriteToSavedPhotosAlbum(self.qrImageView.image!, self, #selector(ScanQRViewController.image(_:didFinishSavingWithError:contextInfo:)), nil)
            
        }else{
            nextStepButton.setTitle("Treci la pasul urmator", for: UIControlState.normal)
          // UIImageWriteToSavedPhotosAlbum(self.qrImageView.image!, self, #selector(ScanQRViewController.image(_:didFinishSavingWithError:contextInfo:)), nil)
        }

        nextStepButton.layer.cornerRadius = 4
        if(ScanQRViewController.type == true){
            
      let img = generateQRCode(from: UserDefaults.standard.object(forKey: "myQR") as! String)
            qrImageView.image = img
            self.myIndicator.stopAnimating()
            self.myIndicator.isHidden = true
            self.nextStepButton.isHidden = false
         if let data = UIImagePNGRepresentation(img!) {
                    let filename = getDocumentsDirectory().appendingPathComponent("qrcode.png")
                    try? data.write(to: filename)
                }
            
          
            
       
            
        }
        
        else{
            
            print("generate QRCode")
            let myUrl = "http://82.76.188.13:3000/users/generateCarCode/" + UserDefaults.standard.string(forKey: "userID")!
              let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
            
            let request = NSMutableURLRequest(url: URL(string: myUrl)! as URL)
            request.httpMethod = "GET"
            
            request.addValue(myToken, forHTTPHeaderField: "Authorization")
            
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            let task = URLSession.shared.dataTask(with: request as URLRequest) {
                data, response, error in
                
                
                let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
                print("RESPONSE:\(responseString)")
                
                do {
                    let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
                    
                    if let parseJSON = myJSON {
                        // Now we can access value of First Name by its key
                        if let result = parseJSON["carCode"] as? String{
                            
                            print(result)
                            UserDefaults.standard.set(result, forKey: "myQR")
                            self.myIndicator.stopAnimating()
                           
                            let img = self.generateQRCode(from: UserDefaults.standard.object(forKey: "myQR") as! String)
                           //  self.changeQR(myQR: UserDefaults.standard.object(forKey: "myQR") as! String)
                            self.qrImageView.image = img
                            let dispatchTime = DispatchTime.now()
                            DispatchQueue.main.asyncAfter(deadline: dispatchTime) {
                                
                           UIImageWriteToSavedPhotosAlbum(self.qrImageView.image!, self, #selector(ScanQRViewController.image(_:didFinishSavingWithError:contextInfo:)), nil)
                            }
                            self.myIndicator.isHidden = true
                            self.nextStepButton.isHidden = false
                            
                            
                            if( ScanQRViewController.forChange == true){
                                self.qrGenerated()
                            }
                            
                            
                            
                            
                        }
                        
                        
                    }
                } catch {
                    print(error)
                }
                
                
            }
            task.resume()
            
         


            
        }
    }
    
    
    
    func qrGenerated(){
        
       changeQR(myQR: UserDefaults.standard.string(forKey: "myQR")!)
        
        
    }
    
    func changeQR(myQR: String){
        
        
        print("scanQR")
        let parameters = ["qr_code": myQR] as Dictionary<String, String>
        
        
     
        
        let myUrl = NSURL(string: "http://82.76.188.13:3000/users/chanceQR/" + (UserDefaults.standard.object(forKey: "userID") as! String) + "&" + ScanQRViewController.carNr)
        
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
        
        
        
        

        
    }
    
    func image(_ image: UIImage, didFinishSavingWithError error: NSError?, contextInfo: UnsafeRawPointer) {
        if let error = error {
            // we got back an error!
            let ac = UIAlertController(title: "Save error", message: error.localizedDescription, preferredStyle: .alert)
            ac.addAction(UIAlertAction(title: "OK", style: .default))
           
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
    
  //    if let img = createQRFromString("Hello world program created by someone") {
//        let somImage = UIImage(CIImage: img, scale: 1.0, orientation: UIImageOrientation.Down)
//    }

    @IBAction func nextButtonAction(_ sender: AnyObject) {
        
        if(nextStepButton.titleLabel?.text == "OK"){
            
            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "CarsTableViewController")
            
            self.present(viewController, animated: false, completion: nil)

            
            
               }
        else{
            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "PhotoViewController")
            
            self.present(viewController, animated: false, completion: nil)
            

        }
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
