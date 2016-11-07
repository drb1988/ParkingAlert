//
//  EditDetailsCarViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 10/17/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit
import IQKeyboardManagerSwift

class EditDetailsCarViewController: UIViewController, UITextFieldDelegate{
    
    static var name: String = " "
    static var plate: String = " "
    static var producer: String = " "
    static  var model: String = " "
    static     var year: String = " "
    
    @IBOutlet var detailsStackView: UIStackView!
    @IBOutlet var detailsScrollView: UIScrollView!
    @IBOutlet var carNameText: UITextField!

    @IBOutlet var carPlateText: UITextField!
    @IBOutlet var carModelText: UITextField!
    
    
    @IBOutlet var carProducerText: UITextField!
    
    @IBOutlet var carYearText: UITextField!
    override func viewDidLoad() {
        super.viewDidLoad()
        
      IQKeyboardManager.sharedManager().enable = true

       carNameText.delegate = self
        carProducerText.delegate=self
        carPlateText.delegate = self
        carModelText.delegate = self
        carYearText.delegate = self
    }

    private func scrollToEnd(addedView: UITextField) {
        let contentViewHeight = detailsScrollView.contentSize.height + addedView.bounds.height + detailsStackView.spacing
        let offsetY = contentViewHeight - detailsScrollView.bounds.height
        if (offsetY > 0) {
            detailsScrollView.setContentOffset(CGPoint(x: detailsScrollView.contentOffset.x, y: offsetY), animated: true)
        }
    }
    
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool
    {
        switch textField
        {
        case carNameText:
            carNameText.resignFirstResponder()
     
            carPlateText.becomeFirstResponder()
            textFieldShouldBeginEditing(textField: carPlateText)
            
            break
        case carPlateText:
            carPlateText.resignFirstResponder()
            carModelText.becomeFirstResponder()
            textFieldShouldBeginEditing(textField: carModelText)
            break
        case carModelText:
            carModelText.resignFirstResponder()
            carProducerText.becomeFirstResponder()
            textFieldShouldBeginEditing(textField: carProducerText)
        case carProducerText:
            carProducerText.resignFirstResponder()
            carYearText.becomeFirstResponder()
            textFieldShouldBeginEditing(textField: carYearText)
        default:
            textField.resignFirstResponder()
        }
        return true
    }
    
    
    func textFieldShouldBeginEditing(textField: UITextField){
        let pointInTable:CGPoint = textField.superview!.convert(textField.frame.origin, to:detailsScrollView)
        var contentOffset:CGPoint = detailsScrollView.contentOffset
        contentOffset.y  = pointInTable.y
        if let accessoryView = textField.inputAccessoryView {
            contentOffset.y -= accessoryView.frame.size.height
        }
        detailsScrollView.contentOffset = contentOffset

    }


    
    @IBAction func doneAction(_ sender: AnyObject) {
        
        
                  print("bam3")
                print("SELF:\(self.carPlateText.text!)")
        
        
                print("Plate:\(EditDetailsCarViewController.plate)")
        
        
        
        
                let parameters = ["plates": self.carPlateText.text!,
                                  "given_name": self.carNameText.text!,
                                  "make": self.carProducerText.text!,
                                  "model": self.carModelText.text!,
                                  "year": self.carYearText.text!
                    ] as Dictionary<String, String>
        
        
                let myUrl = "http://82.76.188.13:3000/users/editCar/" + UserDefaults.standard.string(forKey: "userID")! + "&" + EditDetailsCarViewController.plate
        
        
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
        
                task.resume()
        
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "CarsTableViewController")
        
        self.present(viewController, animated: false, completion: nil)

        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        
        print("EditDetailsCarViewController.name:\(EditDetailsCarViewController.name)")
        carNameText.text = EditDetailsCarViewController.name
        carPlateText.text = EditDetailsCarViewController.plate
        carModelText.text = EditDetailsCarViewController.model
        carProducerText.text = EditDetailsCarViewController.producer
        carYearText.text = EditDetailsCarViewController.year
        
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
