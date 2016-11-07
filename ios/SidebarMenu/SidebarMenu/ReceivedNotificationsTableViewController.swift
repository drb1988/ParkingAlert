//
//  ReceivedNotificationsTableViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 9/19/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit



//struct notificationArray:varObject {
// 
//    var name: String
//    var sender_id: String
//    var receiver_id: String
//    var answered_at : String
//    var estimated: String
//
//    
//    init (name: String, sender_id: String, receiver_id: String, answered_at: String, estimated:String) {
//        self.name = name
//        self.sender_id = sender_id
//        self.receiver_id = receiver_id
//        self.answered_at = answered_at
//        self.estimated = estimated
//      
//     }
//}


class ReceivedNotificationsTableViewController: UITableViewController { /*, UISearchBarDelegate, UISearchResultsUpdating{*/

    @IBOutlet var backButton: UIBarButtonItem!
    @IBOutlet var notificationTableView: UITableView!
    
     var reachability = SplashViewController()
     var progress = ProgressViewController()
     var answer = AnswerViewController()
     var name = [String]()
     var sender_id = [String]()
     var receiver_id = [String]()
     var answered = [String]()
     var estimated = [String]()
     var read_at = [String]()
     var id = [String]()
     var created = [String] ()
     var fullDate = [String]()
     var receiver_picture = [UIImage]()
     var sender_picture = [UIImage]()
     var carNumber = [String]()

    
    var latitude = [String]()
    var longitude = [String]()
    
    
   // var candies = [Candy]()
    
   // var array = [notificationArray]()
 //   var filteredCandies = [Candy]()
    let searchController = UISearchController(searchResultsController: nil)
    
    
    
    

    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        self.notificationTableView.delegate = self
        self.notificationTableView.dataSource = self
        self.notificationTableView.allowsSelection = true

        getNotifications()
        
        
        
        print("FCMToken:\(UserDefaults.standard.value(forKey: "tokenFCM"))")
        
//        let navigationBarAppearace = UINavigationBar.appearance()
//        
//     
//        navigationBarAppearace.barTintColor = UIColor.blueColor()
//        
//        // change navigation item title color
//        navigationBarAppearace.titleTextAttributes = [NSForegroundColorAttributeName:UIColor.whiteColor()]

        
        // Setup the Search Controller
     //   searchController.searchResultsUpdater = self
     //   searchController.searchBar.delegate = self
        definesPresentationContext = true
       // searchController.dimsBackgroundDuringPresentation = false
        
        // Setup the Scope Bar
        //searchController.searchBar.scopeButtonTitles = ["All", "Chocolate", "Hard", "Other"]
        
        tableView.tableHeaderView = searchController.searchBar
        
                backButton.target = self
                backButton.action = #selector(ReceivedNotificationsTableViewController.backAction)
        
//        candies = [
//            Candy(category:"M-ai blocat", name:"Marcel Merge")
//           ]
   
    }
    
    func getNotifications(){
        
        
        let myUrl = "http://82.76.188.13:3000/users/getNotifications/" + UserDefaults.standard.string(forKey: "userID")!
       // print("myURL:\(myUrl)")
        
        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        
        let url=NSURL(string: myUrl)
     //   print("myToken:\(myToken)")
      //  print("GetNotifmyURL:\(myUrl)")
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
            

            
            do{
                let allContacts = try JSONSerialization.jsonObject(with: data! , options: JSONSerialization.ReadingOptions())
                
                
                if let json = allContacts as? Array<AnyObject> {
                    if(json.count != 0){
                  
                        for index in 0...json.count-1 {
                            
                            let contact : AnyObject? = json[index]
                     
                            
                            let collection = contact! as! Dictionary<String, AnyObject>
                        
                            
                            
                            if let sendername  = collection["sender_nickname"] as? String{
                                self.name.append(sendername)
                                
                            }
                            if let id = collection["_id"] as? String{
                                self.id.append(id)
                            }
                            
                            if let senderID = collection["sender_id"] as? String{
                                self.sender_id.append(senderID)
                               
                                }
                            
                            if let receiverID = collection["receiver_id"] as? String{
                               self.receiver_id.append(receiverID)
                             
                                
                              
                                }
                            
//                            aici nu ma scap de optionals
                            if let coordinates = collection["location"]?["coordinates"] as? Array<AnyObject>{
                              
                                 let x  = coordinates[0] as? String
                                
                                if x != nil{
                                 
                                    self.latitude.append(x!)
                                    }
                                
                                let y = coordinates[1] as? String
                                
                                if y != nil{
                                  
                                    self.longitude.append(y!)
                                }

                            }
                            
                            
                            if let carNr = collection["vehicle"] as? String{
                                self.carNumber.append(carNr)
                            }
//
                            //aici e varianta simpla daca imi face DRB ruta :)
                            
//                            if let latitude = collection["latitude"] as? String{
//                                self.latitude.append(latitude)
//                            }
//                            
//                            if let latitude = collection["latitude"] as? String{
//                                self.longitude.append(latitude)
//                            }
//                            
                            if let create = collection["create_date"] as? String{
                             
                                self.fullDate.append(create)
                                let myDate = create
                                let dateFormatter = DateFormatter()
                                dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
                                let date = dateFormatter.date(from: myDate)!
                                dateFormatter.dateFormat = "HH:mm"
                                let dateString = dateFormatter.string(from: date)
                       
                                self.created.append(dateString)
                                
                            }
                            
//                            if let receiver_picture = contact?["receiver_picture"] as? String{
//                                
//                                
//                                let str = receiver_picture
//                                let imageUrlString = str
//                                let imageUrl:NSURL = NSURL(string: imageUrlString)!
//                                
//                               
//                                let imageData:NSData = NSData(contentsOf: imageUrl as URL)!
//                                
//                                
//                                let profileImage = UIImage(data: imageData as Data)
//                                self.receiver_picture.append(profileImage!)
//                            }
//                            
//                            if let senderpic = contact?["sender_picture"] as? String{
//                                
//                                
//                                let str = senderpic
//                                let imageUrlString = str
//                                let imageUrl:NSURL = NSURL(string: imageUrlString)!
//                                
//                                
//                                let imageData:NSData = NSData(contentsOf: imageUrl as URL)!
//                                
//                                
//                                let profileImage = UIImage(data: imageData as Data)
//                             
//                                self.sender_picture.append(profileImage!)
//                            }
                            
                            if let answered  = collection["answer"]?["answered_at"] as? String{
                                let myDate = answered
                                let dateFormatter = DateFormatter()
                                dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
                                let date = dateFormatter.date(from: myDate)!
                                dateFormatter.dateFormat = "HH:mm:ss"
                                let dateString = dateFormatter.string(from: date)
                
                                self.answered.append(dateString)
                                
                                }
                            else{
                                self.answered.append("null")
                            }

                            if let estimated  = collection["answer"]?["estimated"] as? String{
                                
                               self.estimated.append(estimated)
                                    
                                
                                }
                            else{
                                self.estimated.append("null")
                            }
                        
                            if let read  = collection["answer"]?["read_at"] as? String{
                                let strTime = read
                                let formatter = DateFormatter()
                                formatter.dateFormat = "HH:mm"
                                let hour = String(describing: formatter.date(from: strTime))
                                self.read_at.append(hour)
                                
                                
                            }
                            else{
                                self.read_at.append("null")
                            }
                            

                    
                            DispatchQueue.main.asyncAfter(deadline: .now() ) {
                                self.notificationTableView.reloadData()
                            }                    }
                        
                    }
                                }
                
               
    print(self.sender_picture.count)
                
            }catch _ as NSError{
                
            }
        }
        task.resume()
        

        
    }
    

    
    


    
    override func viewWillAppear(_ animated: Bool) {
               super.viewWillAppear(animated)
        
        
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    
 

    

     func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
//        if searchController.isActive && searchController.searchBar.text != "" {
//            return filteredCandies.count
//        }
        return sender_id.count 
    }
    
    
    override func tableView(_ tableView: UITableView,
                            cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let size: CGFloat = 26
       
        let width = max(size, 0.7 * size ) // perfect circle is smallest allowed
        let badge = UILabel(frame: CGRect( x: 0, y: 0, width: width, height: size))
        badge.text = "\(2)"
        badge.layer.cornerRadius = size / 2
        badge.layer.masksToBounds = true
        badge.textAlignment = .center
        badge.textColor = UIColor.white
        badge.backgroundColor = UIColor.red
     
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath as IndexPath) as! CustomTableViewCell
//        let candy: Candy
//        if searchController.isActive && searchController.searchBar.text != "" {
//            print("diferit")
//            candy = filteredCandies[indexPath.row]
//        } else {
//            candy = candies[indexPath.row]
//        }
        
        
        //var items = notificationArray()
        
//        let imageUrlString = receiver_picture[indexPath.row]
//        let imageUrl:NSURL = NSURL(string: imageUrlString)!
//        
//      
//        let imageData:NSData = NSData(contentsOf: imageUrl as URL)!
//        
//        
//        let profileImage = UIImage(data: imageData as Data)
        
        
        
//        cell.notifImageView.layer.cornerRadius = cell.notifImageView.frame.height/2
//        cell.notifImageView.clipsToBounds = true
      

       
        cell.notifImageView.image = UIImage(named: "profile.png")
        
        
//   
//        if sender_picture.indices.contains(indexPath.row) == true{
//            cell.notifImageView.image = sender_picture[indexPath.row]
//        }
//        else{
//            cell.notifImageView.image = UIImage(named: "profile.png")
//        }
//        
        
        
        if(sender_id[indexPath.row] == UserDefaults.standard.string(forKey: "userID")){
            if(estimated[indexPath.row] == "null"){
                
                
    
              //  print("1")
        cell.nameLabel.text = "Ai trimis notificare"
    //    cell.notifImageView.image = sender_picture[indexPath.row]
        cell.textmessageLabel.text = "M-ai blocat"
       
     //   cell.accessoryType = .disclosureIndicator
        cell.hourLabel.text = created[indexPath.row]
        
        //cell.accessoryView = badge
            }
            else{
        
               // print("2")
                    
               // cell.nameLabel.text = carNumber[indexPath.row] + " a raspuns"
                cell.nameLabel.text = "Ai primit raspuns"
                cell.textmessageLabel.text = "Vin in " + estimated[indexPath.row] + " minute"
                
         
                
              //  cell.accessoryType = .disclosureIndicator
                cell.hourLabel.text = created[indexPath.row]
           }
        }
        else{
            if(read_at[indexPath.row] == "null"){
               // print("3")
         //   cell.nameLabel.text = carNumber[indexPath.row] + " te-a notificat"
                cell.nameLabel.text = "Ai primit notificare"
            cell.textmessageLabel.text = "M-ai blocat"
         
            //cell.accessoryType = .disclosureIndicator
            cell.hourLabel.text = created[indexPath.row]
            }
            else{
               // print("4")
                cell.nameLabel.text = "Ai trimis raspuns"
                cell.textmessageLabel.text = "Vin in " + estimated[indexPath.row] + " minute"
            
               // cell.accessoryType = .disclosureIndicator
                cell.hourLabel.text = answered[indexPath.row]
                
                
            }
        }

        return cell
    }
    
    func readNotification(x: String){
        
        
        
        let myUrl = "http://82.76.188.13:3000/notifications/receiverRead/" + x
        let myToken = "Bearer " + UserDefaults.standard.string(forKey: "token")!
        
        let request = NSMutableURLRequest(url: URL(string: myUrl)! as URL)
        request.httpMethod = "GET"
        
        request.addValue(myToken, forHTTPHeaderField: "Authorization")
        
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        let task = URLSession.shared.dataTask(with: request as URLRequest) {
            data, response, error in
            print("RESPONSE:\(response)")
        }
        task.resume()
        
        
    }

    
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print(indexPath)
    }


    func backAction()
    {
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "RevealViewController")
        
        self.present(viewController, animated: false, completion: nil)
        // filteredTableData.removeAll(keepCapacity: false)
        
        
        
    }
    
    
    
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            self.sender_id.remove(at: indexPath.row)
            tableView.deleteRows(at: [indexPath as IndexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view.
        }

    }
}




//        let cell = tableView.cellForRow(at: indexPath as IndexPath)
//        print("nu ma vrea")
//        let label = cell?.viewWithTag(1) as! UILabel
//        
//       if(label.text == "Ai trimis notificare")
//        {
//            
//            MapViewController.latitude = latitude[indexPath.row]
//            MapViewController.longitude = longitude[indexPath.row]
//            
//            MapViewController.createHour = fullDate[indexPath.row]
//            print("Notif created:\(fullDate[indexPath.row])")
//            
//            
//            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MapViewController")
//            
//            self.present(viewController, animated: false, completion: nil)
//            
//        }
//        if(label.text == "Ai primit notificare")
//        {
//           // print()
//            
//            
//           answer.getID(x: id[indexPath.row])
//            
//          
//            
//                let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "AnswerViewController")
//            
//            self.present(viewController, animated: false, completion: nil)
//        }
//        if(label.text == "Ai primit raspuns")
//        {
//            
//            readNotification(x: id[indexPath.row])
//            
//            ReviewViewController.notifID = id[indexPath.row]
//            
//            MapViewController.latitude = latitude[indexPath.row]
//            MapViewController.longitude = longitude[indexPath.row]
//            
//            MapViewController.createHour = fullDate[indexPath.row]
//
//            progress.getTime(x: estimated[indexPath.row], hour: answered[indexPath.row], plates: carNumber[indexPath.row] )
//            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ProgressViewController")
//            self.present(viewController, animated: false, completion: nil)
//        }
//        if(label.text == "Ai trimis raspuns")
//        {
//            let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MapViewController")
//            
//            self.present(viewController, animated: false, completion: nil)
//        }

    
    
    
    
//    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
//        if  segue.identifier == "",
//            let destination = segue.destination as? EditCarViewController,
//            let blogIndex = tableView.indexPathForSelectedRow?.row
//        {
////            destination.name = carName[blogIndex]
////            destination.plate = carNumber[blogIndex]
////            destination.producer = carProducer[blogIndex]
////            destination.model = carModel[blogIndex]
////            destination.year = carYear[blogIndex]
//            
//            
//        }
//    }

    
    
    
    
//    
//    func filterContentForSearchText(searchText: String) {
//        filteredCandies = candies.filter({( candy : Candy) -> Bool in
//            //let categoryMatch = (scope == "All") || (candy.category == scope)
//            if(candy.name.contains(searchText.lowercased()) != false){
//            return candy.name.contains(searchText)
//            }
//            return candy.category.contains(searchText)
//        })
//        tableView.reloadData()
//    }
//    
    
//    
//    func updateSearchResults(for searchController: UISearchController) {
//   // let searchBar = searchController.searchBar
//       filterContentForSearchText(searchText: searchController.searchBar.text!)
//        
//    }
    
    
    
//    func updateSearchResultsForSearchController(searchController: UISearchController) {
//        
//        let searchBar = searchController.searchBar
//        //  let scope = searchBar.scopeButtonTitles![searchBar.selectedScopeButtonIndex]
//        
//    }

    
//    func searchBar(_ searchBar: UISearchBar, selectedScopeButtonIndexDidChange selectedScope: Int) {
//        filterContentForSearchText(searchText: searchBar.text!)
//    }
//    
    
//    func updateSearchResultsForSearchController(searchController: UISearchController) {
//        let searchBar = searchController.searchBar
//        let scope = searchBar.scopeButtonTitles![searchBar.selectedScopeButtonIndex]
//        filterContentForSearchText(searchController.searchBar.text!, scope: scope)
//    }
//
//
//    func searchBar(searchBar: UISearchBar, selectedScopeButtonIndexDidChange selectedScope: Int) {
//        filterContentForSearchText(searchBar.text!, scope: searchBar.scopeButtonTitles![selectedScope])
//    }
    
    


    /*
    // Override to support rearranging the table view.
    override func tableView(tableView: UITableView, moveRowAtIndexPath fromIndexPath: NSIndexPath, toIndexPath: NSIndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(tableView: UITableView, canMoveRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */


