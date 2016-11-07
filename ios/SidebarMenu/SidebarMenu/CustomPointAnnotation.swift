//
//  CustomAnnotation.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 9/29/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import Foundation
import UIKit
import MapKit

class CustomPointAnnotation: NSObject, MKAnnotation {
    public var coordinate: CLLocationCoordinate2D

    
    
    var title: String!
    var imageView: UIImageView!
    
    
    init(coordinate: CLLocationCoordinate2D) {
        self.coordinate = coordinate
    }
}
