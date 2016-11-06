//
//  ViewController.swift
//  WavTapClient
//
//  Created by Ako Tulu on 08/09/16.
//  Copyright © 2016 Koodinurk Ltd. All rights reserved.
//

import UIKit
import CocoaAsyncSocket
import SwiftyUserDefaults

extension DefaultsKeys {
    static let HostKey = DefaultsKey<String>("hostKey")
    static let PortKey = DefaultsKey<String>("portKey")
}

class ViewController: UIViewController
{
    var mAsyncSocket: GCDAsyncSocket!
    var mStreamPlayer: StreamPlayer!
    
    let TimeOut = 5.0;
    
    // MARK:
    // MARK: Outlets
    
    @IBOutlet weak var mHostTextField: UITextField!
    @IBOutlet weak var mPortTextField: UITextField!
    @IBOutlet weak var mConnectButton: UIButton!
    @IBOutlet weak var mDisconnectButton: UIButton!
    
    @IBOutlet var mTouchViews: [UIControl]!
    
    // MARK:
    // MARK: View mehtods
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        
        mAsyncSocket = GCDAsyncSocket(delegate: self, delegateQueue:DispatchQueue.main)
        
        mHostTextField.text = Defaults[.HostKey]
        mPortTextField.text = Defaults.hasKey(.PortKey) ? Defaults[.PortKey] : "32905"
    }

    override func didReceiveMemoryWarning()
    {
        super.didReceiveMemoryWarning()
    }
    
    // MARK:
    // MARK: Action methods
    
    @IBAction func connectClicked(_ sender: UIButton)
    {
        for view in mTouchViews {
            view.isEnabled = false
        }
        
        mConnectButton.isEnabled = false;
        
        if let host = mHostTextField.text, let portString = mPortTextField.text, let port = UInt16(portString) {
            do {
                try mAsyncSocket!.connect(toHost: host, onPort: port, withTimeout: TimeOut)
                mAsyncSocket.readData(toLength: UInt(MemoryLayout<AudioStreamBasicDescription>.size), withTimeout: -1, tag: 0)
            } catch {
                print("Failed to connect")
            }
        }
    }
    
    @IBAction func disconnectClicked(_ sender: UIButton)
    {
        mAsyncSocket.disconnect();
    }
}

extension ViewController: GCDAsyncSocketDelegate
{
    func socket(_ socket : GCDAsyncSocket, didConnectToHost host:String, port p:UInt16)
    {
        print("didConnectToHost")

        Defaults[.HostKey] = mHostTextField.text!
        Defaults[.PortKey] = mPortTextField.text!
        
        mConnectButton.isHidden = true;
        mDisconnectButton.isHidden = false;
    }
    
    func socketDidDisconnect(_ sock: GCDAsyncSocket, withError err: Error?)
    {
        print("socketDidDisconnect")
        
        for view in mTouchViews {
            view.isEnabled = true
        }
                
        if mStreamPlayer != nil {
            mStreamPlayer.stopStream()
            mStreamPlayer = nil;
        }
        
        mConnectButton.isEnabled = true;
        mConnectButton.isHidden = false;
        mDisconnectButton.isHidden = true;
    }
    
    func socket(_ sock: GCDAsyncSocket, didRead data: Data, withTag tag: Int)
    {
        if tag == 0 {

            var asbd:AudioStreamBasicDescription = AudioStreamBasicDescription();
            
            memcpy(&asbd, (data as NSData).bytes, MemoryLayout<AudioStreamBasicDescription>.size)
            
            mStreamPlayer = StreamPlayer.init(outputFormat: asbd);
            mStreamPlayer.startStream()
        }
        else
        {
            mStreamPlayer.play(data);
        }
        
        mAsyncSocket.readData(toLength: mStreamPlayer.packetSize, withTimeout: -1, tag: 1)
    }
}
