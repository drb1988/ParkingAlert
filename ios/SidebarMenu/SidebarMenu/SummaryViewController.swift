//
//  SummaryViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 10/28/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit

class SummaryViewController: UIViewController {

    @IBOutlet weak var feedbackLabel: UILabel!
    @IBOutlet weak var answerAtLabel: UILabel!
    @IBOutlet weak var answerLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var carLabel: UILabel!
    
    
    
    
    static var carText: String = "BH33DDD"
    static var myDate: String = " "
    static var answer: String = " "
    static var answerAt: String = " "
    static var feedback: String = " "
    
    @IBAction func okButton(_ sender: AnyObject) {
        
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RevealViewController")
        
        
        self.present(viewController, animated: false, completion: nil)
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        carLabel.text = SummaryViewController.carText
        dateLabel.text = SummaryViewController.myDate
        answerLabel.text = SummaryViewController.answer
        answerAtLabel.text = SummaryViewController.answerAt
        feedbackLabel.text = SummaryViewController.feedback
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
