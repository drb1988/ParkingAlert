//
//  CustomTableViewCell.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 9/19/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit

class CustomTableViewCell: UITableViewCell {

    @IBOutlet var notifImageView: UIImageView!
    @IBOutlet var textmessageLabel: UILabel!
    @IBOutlet var nameLabel: UILabel!
  
    
    @IBOutlet var hourLabel: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
//       notifImageView.layer.cornerRadius = notifImageView.frame.height/2
//       notifImageView.clipsToBounds = true
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
