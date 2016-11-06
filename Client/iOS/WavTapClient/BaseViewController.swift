//
//  BaseViewController.swift
//  WavTapClient
//
//  Created by Ako Tulu on 08/09/16.
//  Copyright © 2016 Koodinurk Ltd. All rights reserved.
//

import UIKit

class BaseViewController: UIViewController
{
    override func viewDidLoad()
    {
        super.viewDidLoad()
        
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(BaseViewController.resignFirstResponders))
        
        tapGesture.cancelsTouchesInView = false
        
        self.view.addGestureRecognizer(tapGesture)
    }
            
    func resignFirstResponders() {
        // Abstract method
    }
}
