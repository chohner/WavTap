//
//  ViewController.swift
//  WavTapClient
//
//  Created by Ako Tulu on 08/09/16.
//  Copyright © 2016 Koodinurk Ltd. All rights reserved.
//

import UIKit
import AVFoundation
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
    
    @IBOutlet var mTouchViews: [UIControl]!
    
    // MARK:
    // MARK: View mehtods
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        
        mAsyncSocket = GCDAsyncSocket(delegate: self, delegateQueue:dispatch_get_main_queue())
        
        mHostTextField.text = Defaults[.HostKey]
        mPortTextField.text = Defaults.hasKey(.PortKey) ? Defaults[.PortKey] : "32905"
        
        do {
            try AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryPlayback)
            try AVAudioSession.sharedInstance().setActive(true)
        } catch {
            print(error)
        }
    }

    override func didReceiveMemoryWarning()
    {
        super.didReceiveMemoryWarning()
    }
    
    // MARK:
    // MARK: Action methods
    
    @IBAction func connectClicked(sender: UIButton)
    {
        for view in mTouchViews {
            view.enabled = false
        }
        
        mConnectButton.setTitle("Connecting", forState: UIControlState.Normal)
        mConnectButton.enabled = false;
        
        if let host = mHostTextField.text, portString = mPortTextField.text, port = UInt16(portString) {
            do {
                try mAsyncSocket!.connectToHost(host, onPort: port, withTimeout: TimeOut)
                mAsyncSocket.readDataToLength(UInt(sizeof(AudioStreamBasicDescription)), withTimeout: -1, tag: 0)
            } catch {
                print("Failed to connect")
            }
        }
    }
}

extension ViewController: GCDAsyncSocketDelegate
{
    func socket(socket : GCDAsyncSocket, didConnectToHost host:String, port p:UInt16)
    {
        print("didConnectToHost")

        Defaults[.HostKey] = mHostTextField.text!
        Defaults[.PortKey] = mPortTextField.text!
        
        mConnectButton.setTitle("Disconnect", forState: UIControlState.Normal)
        mConnectButton.enabled = true;
    }
    
    func socketDidDisconnect(sock: GCDAsyncSocket!, withError err: NSError!)
    {
        print("socketDidDisconnect")
        
        for view in mTouchViews {
            view.enabled = true
        }
                
        if mStreamPlayer != nil {
            mStreamPlayer.stopStream()
            mStreamPlayer = nil;
        }
        
        mConnectButton.setTitle("Connect", forState: UIControlState.Normal)
        mConnectButton.enabled = true;
    }
    
    func socket(sock: GCDAsyncSocket!, didReadData data: NSData!, withTag tag: Int)
    {
        if tag == 0 {

            var asbd:AudioStreamBasicDescription = AudioStreamBasicDescription();
            data.getBytes(&asbd, length: sizeof(AudioStreamBasicDescription))
            
            mStreamPlayer = StreamPlayer.init(outputFormat: asbd);
            mStreamPlayer.startStream()
        }
        else
        {
            mStreamPlayer.play(data);
        }
        
        mAsyncSocket.readDataToLength(mStreamPlayer.packetSize, withTimeout: -1, tag: 1)
    }
}
