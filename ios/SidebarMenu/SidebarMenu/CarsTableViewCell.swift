//
//  CarsTableViewCell.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 9/21/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit

class CarsTableViewCell: UITableViewCell {

    @IBOutlet var numberCar: UILabel!
    @IBOutlet var nameCar: UILabel!
    @IBOutlet var imageCar: UIImageView!
    

    
    //MARK: UI Updates
    
    

    override func awakeFromNib() {
        super.awakeFromNib()
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
