//
//  PersonalProfileViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 9/24/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit




class PersonalProfileViewController: UIViewController, UIImagePickerControllerDelegate,
UINavigationControllerDelegate {
    @IBOutlet var backButton: UIBarButtonItem!
    
    static var name: String = " "
    static var sname: String = " "
    static var email: String = " "
    static var phone: String = " "

    @IBOutlet var backImage: UIImageView!
    
    @IBOutlet var profileImage: UIImageView!
    
    @IBOutlet var email: UITextField!
    @IBOutlet var phone: UITextField!
    
    let kDefaultAnimationDuration = 2.0
    
    @IBAction func doneButton(_ sender: UIBarButtonItem) {
        
        
        updateUser()
        
        let viewTos = UILabel(frame:CGRect(x: 0, y: 0, width: 200, height: 100));
        viewTos.text = "Profilul dumneavoastra a fost salvat cu succes";
        viewTos.numberOfLines = 0;
        viewTos.backgroundColor = UIColor.black.withAlphaComponent(0.54)
        viewTos.textColor = UIColor.white
        viewTos.layer.cornerRadius = 5;
        viewTos.clipsToBounds = true;
        viewTos.textAlignment = .center;
        //        viewTos.sizeToFit()
        //        viewTos.center = CGPointMake(addCommentViewController.view.center.x, UIScreen.mainScreen().bounds.height -
        //        viewTos.frame.size.height - 20);
        // viewTos.center = addCommentViewController.view.center
        
        var point = self.view.center
        point.y -= 100
        viewTos.center = point
        viewTos.layer.zPosition = 999;
        self.view.addSubview(viewTos);
        
        UIView.animate(withDuration: 4.0, delay: 0.1, options: UIViewAnimationOptions.curveEaseOut, animations: {
            
            viewTos.alpha = 0.0
            
        })
        

    }
    @IBOutlet var surname: UITextField!
    @IBOutlet var name: UITextField!
    override func viewDidLoad() {
        
        
        
        super.viewDidLoad()
        
        let titleDict: NSDictionary = [NSForegroundColorAttributeName: UIColor.white]
        self.navigationController?.navigationBar.titleTextAttributes = titleDict as? [String : Any]
        
       
        
        
        
//        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(LoginViewController.dismissKeyboard))
//        view.addGestureRecognizer(tap)
        
        let gesture: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(PersonalProfileViewController.changePicture))
        profileImage.addGestureRecognizer(gesture)

        if revealViewController() != nil {
            backButton.target = revealViewController()
            backButton.action = #selector(SWRevealViewController.revealToggle(_:))
            view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
        }

//        
//        backButton.target = self
//        backButton.action = #selector(PersonalProfileViewController.goBack)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        
        getProfilePicture()
        
        
        name.text =  PersonalProfileViewController.name
        surname.text = PersonalProfileViewController.sname
        email.text = PersonalProfileViewController.email
        phone.text = PersonalProfileViewController.phone
        
        let blurEffect = UIBlurEffect(style: UIBlurEffectStyle.light)
        let blurEffectView = UIVisualEffectView(effect: blurEffect)
        blurEffectView.frame = backImage.bounds
        blurEffectView.autoresizingMask = [.flexibleWidth, .flexibleHeight] // for supporting device rotation
        backImage.addSubview(blurEffectView)
        
      //  profileImage.layer.borderWidth = 1
        profileImage.layer.masksToBounds = false
        profileImage.layer.borderColor = UIColor.black.cgColor
        profileImage.layer.cornerRadius = profileImage.frame.height/2
        profileImage.clipsToBounds = true
        

        
    }
    
 
    

    
         
    

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func uploadPhoto(info: [String : AnyObject]) {
        if let chosenImage = info[UIImagePickerControllerOriginalImage] as? UIImage {
            let imageData = UIImageJPEGRepresentation(chosenImage, 0.6)
            if imageData == nil {
                return
            }
        
            
     //   let parameters = ["file": imageData] as Dictionary<Any, Any>
        
        
        
        
        let myUrl = NSURL(string: "http://82.76.188.13:3000/users/setPicture/" +  UserDefaults.standard.string(forKey: "userID")!)
        
        let session = URLSession.shared
        let request = NSMutableURLRequest(url:myUrl! as URL);
        
        request.httpMethod = "POST";// Compose a query string
        
            
            let body = NSMutableData()
            
            let fname = "test.png"
            let mimetype = "image/png"
            
            let boundary = generateBoundaryString()
            
            //define the multipart request type
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue("application/json", forHTTPHeaderField: "Accept")
            
            let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
            request.addValue(myToken, forHTTPHeaderField: "authorization")

            request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
            //define the data post parameter
            
            body.append("--\(boundary)\r\n".data(using: String.Encoding.utf8)!)
            body.append("Content-Disposition:form-data; name=\"test\"\r\n\r\n".data(using: String.Encoding.utf8)!)
            body.append("hi\r\n".data(using: String.Encoding.utf8)!)
            
            
            
            body.append("--\(boundary)\r\n".data(using: String.Encoding.utf8)!)
            body.append("Content-Disposition:form-data; name=\"file\"; filename=\"\(fname)\"\r\n".data(using: String.Encoding.utf8)!)
            body.append("Content-Type: \(mimetype)\r\n\r\n".data(using: String.Encoding.utf8)!)
            body.append(imageData!)
            body.append("\r\n".data(using: String.Encoding.utf8)!)
            
            
            body.append("--\(boundary)--\r\n".data(using: String.Encoding.utf8)!)
            
            
            
            request.httpBody = body as Data
            
            
            let task = session.dataTask(with: request as URLRequest) {
                data, response, error in
                
                if error != nil
                {
                    print("error=\(error)")
                    return
                }
                
                // You can print out response object
                print("response = \(response)")
                
                // Print out response body
                let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
                print("responseString = \(responseString)")
                
                
                print(error)

            
        }
        
        task.resume()
        
        

        
        }
        
        
    }
    
    
  func getProfilePicture(){
    
    
    print("getUser")
    
    let myURL = "http://82.76.188.13:3000/users/getUser/" + UserDefaults.standard.string(forKey: "userID")!
    
    let url = NSURL(string: myURL)
    
    let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
    
    let request = NSMutableURLRequest(url:url as! URL);
    request.httpMethod = "GET"
    
    request.addValue(myToken, forHTTPHeaderField: "Authorization")
    
    request.addValue("application/json", forHTTPHeaderField: "Content-Type")
    let task = URLSession.shared.dataTask(with: request as URLRequest) {
        data, response, error in
        print("RESPONSE:\(response)")
        
        do {
            let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
            
            if let parseJSON = myJSON {
                // Now we can access value of First Name by its key
                if let profile_picture = parseJSON["profile_picture"] as? String{
                    
                    let imageUrlString = profile_picture
                    let imageUrl:NSURL = NSURL(string: imageUrlString)!
                    
                    // Start background thread so that image loading does not make app unresponsive
                  DispatchQueue.main.asyncAfter(deadline: .now() ) {
                        
                    let imageData:NSData = NSData(contentsOf: imageUrl as URL)!
                       
                    
                    let image = UIImage(data: imageData as Data)
                    self.profileImage.image = image
                    self.profileImage.contentMode = UIViewContentMode.scaleAspectFill

                    
                    }
                }
            }
            }catch {
            print(error)
        }
        
    }
    task.resume()
    

    
   
    
    
    }
    
    func generateBoundaryString() -> String
    {
        return "Boundary-\(NSUUID().uuidString)"
    }
    
    func changePicture(){
        
        print("image touch")
        actionSheet()
        
    }
    
    func actionSheet(){
        let alertController = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        
        let sendButton = UIAlertAction(title: "Camera", style: .default, handler: { (action) -> Void in
            
            self.openCamera()
          
            
        })
        
        let  deleteButton = UIAlertAction(title: "Galerie", style: .default, handler: { (action) -> Void in
            self.openLibrary()
        })
        
        let cancelButton = UIAlertAction(title: "Anuleaza", style: .cancel, handler: { (action) -> Void in
            print("Cancel button tapped")
        })
        
        
        alertController.addAction(sendButton)
        alertController.addAction(deleteButton)
        alertController.addAction(cancelButton)
        
        self.navigationController!.present(alertController, animated: true, completion: nil)
    }
    
    func openCamera(){
        if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.camera) {
            let imagePicker = UIImagePickerController()
            imagePicker.delegate = self
            imagePicker.sourceType = UIImagePickerControllerSourceType.camera;
            imagePicker.allowsEditing = false
            self.present(imagePicker, animated: true, completion: nil)
          
        }
    }
    
    
    func openLibrary(){
        if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.photoLibrary) {
            let imagePicker = UIImagePickerController()
            imagePicker.delegate = self
            imagePicker.sourceType = UIImagePickerControllerSourceType.photoLibrary;
            imagePicker.allowsEditing = true
            self.present(imagePicker, animated: true, completion: nil)
        }
    }
    
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        if let pickedImage = info[UIImagePickerControllerOriginalImage] as? UIImage {
            profileImage.contentMode = .scaleToFill
            profileImage.image = pickedImage
        }
        dismiss(animated: true, completion: nil)
        let imageData = UIImageJPEGRepresentation(profileImage.image!, 0.6)
        let compressedJPGImage = UIImage(data: imageData!)
        UIImageWriteToSavedPhotosAlbum(compressedJPGImage!, nil, nil, nil)
        
        uploadPhoto(info: info as [String : AnyObject])
    }
    
    
    func updateUser(){
        
        
     
        
        let parameters = ["email": email.text!,
                          "first_name": name.text!,
                          "last_name": surname.text!,
                          "platform": "iOS",
                          "phone_number": phone.text!] as Dictionary<String, String>
        
        
     
        
        let myUrl = NSURL(string: "http://82.76.188.13:3000/users/updateUser/" +  UserDefaults.standard.string(forKey: "userID")!)
        
        let session = URLSession.shared
        let request = NSMutableURLRequest(url:myUrl! as URL);
        
        request.httpMethod = "POST";// Compose a query string
        
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        request.addValue(myToken, forHTTPHeaderField: "authorization")
        request.httpBody = try! JSONSerialization.data(withJSONObject: parameters, options: [] )
        
        let task = session.dataTask(with: request as URLRequest) {
            data, response, error in
            
            if error != nil
            {
                print("error=\(error)")
                return
            }
            
            // You can print out response object
            print("response = \(response)")
            
            // Print out response body
            let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            print("responseString = \(responseString)")
        
            
            print(error)
          
        }
        task.resume()
        
        
        

        
    }
    
    func goBack(){
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RevealViewController")
        
        self.present(viewController, animated: false, completion: nil)
        

    }
}

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */


