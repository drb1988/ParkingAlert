//
//  EditQRCodeViewController.swift
//  SidebarMenu
//
//  Created by Claudia Mateas on 10/14/16.
//  Copyright © 2016 AppCoda. All rights reserved.
//

import UIKit
import AVFoundation

class EditQRCodeViewController: UIViewController, QRCodeReaderViewControllerDelegate {

    var scanqr = ScanQRViewController()
    lazy var reader = QRCodeReaderViewController(builder: QRCodeReaderViewControllerBuilder {
        $0.reader          = QRCodeReader(metadataObjectTypes: [AVMetadataObjectTypeQRCode])
        $0.showTorchButton = true
    })

    
    @IBOutlet var qrimageView: UIImageView!
    @IBOutlet var changeButton: UIButton!
    @IBOutlet var helpView: UIView!
    @IBOutlet var shareButton: UIBarButtonItem!
    @IBOutlet var backButton: UIBarButtonItem!
    static var staticQR: UIImage!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        shareButton.target = self
        shareButton.action = #selector(EditQRCodeViewController.shareAction)
        
       
        
        let titleDict: NSDictionary = [NSForegroundColorAttributeName: UIColor.white]
        self.navigationController?.navigationBar.titleTextAttributes = titleDict as? [String : Any]
    
        self.qrimageView.image = EditQRCodeViewController.staticQR
      
        changeButton.addTarget(self, action: #selector(changeQR), for: .touchUpInside)
                   }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func shareAction(){
        
        let layer = UIApplication.shared.keyWindow!.layer
        let scale = UIScreen.main.scale
        UIGraphicsBeginImageContextWithOptions(layer.frame.size, false, scale);
        
        layer.render(in: UIGraphicsGetCurrentContext()!)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        // UIImageWriteToSavedPhotosAlbum(image!, nil, nil, nil)
        let activityViewController = UIActivityViewController(activityItems: [image!], applicationActivities: nil)
        if UIDevice.current.userInterfaceIdiom == .pad {
            activityViewController.popoverPresentationController?.sourceView = self.view
        }
        self.present(activityViewController, animated: true, completion: nil)
        
        
        
        
    }
    func image(_ image: UIImage, didFinishSavingWithError error: NSError?, contextInfo: UnsafeRawPointer) {
        if let error = error {
            // we got back an error!
            let ac = UIAlertController(title: "Save error", message: error.localizedDescription, preferredStyle: .alert)
            ac.addAction(UIAlertAction(title: "OK", style: .default))
            present(ac, animated: true)
        } else {
            let ac = UIAlertController(title: "Saved!", message: "Your altered image has been saved to your photos.", preferredStyle: .alert)
            ac.addAction(UIAlertAction(title: "OK", style: .default))
            present(ac, animated: true)
        }
    }
    
    func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        let documentsDirectory = paths[0]
        return documentsDirectory
    }
    
    func generateQRCode(from string: String) -> UIImage? {
        let data = string.data(using: String.Encoding.ascii)
        
        if let filter = CIFilter(name: "CIQRCodeGenerator") {
            filter.setValue(data, forKey: "inputMessage")
            let transform = CGAffineTransform(scaleX: 3, y: 3)
            
            if let output = filter.outputImage?.applying(transform) {
                return UIImage(ciImage: output)
            }
        }
        
        return nil
    }
    
    func changeQR(){
        actionSheet()
    }
    
    
    func scanAction() {
        if QRCodeReader.supportsMetadataObjectTypes() {
            reader.modalPresentationStyle = .formSheet
            reader.delegate               = self
            
            reader.completionBlock = { (result: QRCodeReaderResult?) in
                if let result = result {
                    print("Completion with result: \(result.value) of type \(result.metadataType)")
                    UserDefaults.standard.set(result.value, forKey: "myQR")
                    self.scanqr.changeQR(myQR: result.value)
                }
            }
            
            present(reader, animated: true, completion: nil)
        }
        else {
            let alert = UIAlertController(title: "Error", message: "Reader not supported by the current device", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: nil))
            
            present(alert, animated: true, completion: nil)
        }
    }
    
    
    func reader(_ reader: QRCodeReaderViewController, didScanResult result: QRCodeReaderResult) {
        dismiss(animated: true) { [weak self] in
            let alert = UIAlertController(
                title: "Codul a fost",
                message: "scanat cu succes ! ",
                preferredStyle: .alert
            )
            
            
            
            
            alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                self?.changeView()
                
            }))
            
            self?.present(alert, animated: true, completion: nil)
            
            
            
            
        }
    }
    
    func changeView(){
        let viewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ScanQRViewController")
        
        self.present(viewController, animated: false, completion: nil)
        
   
        
        
    }
    func readerDidCancel(_ reader: QRCodeReaderViewController) {
        dismiss(animated: true, completion: nil)
        
    }
    
    private func createReader() -> QRCodeReaderViewController {
        let builder = QRCodeReaderViewControllerBuilder { builder in
            builder.reader          = QRCodeReader(metadataObjectTypes: [AVMetadataObjectTypeQRCode])
            builder.showTorchButton = true
        }
        
        return QRCodeReaderViewController(builder: builder)
    }
    

    
    func actionSheet(){
        let alertController = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        
        let sendButton = UIAlertAction(title: "Am cod QR", style: .default, handler: { (action) -> Void in
            
            
            self.scanAction()
            ScanQRViewController.type = true
            ScanQRViewController.global = true
    
        })
        
        let  deleteButton = UIAlertAction(title: "Nu am cod QR", style: .default, handler: { (action) -> Void in
            ScanQRViewController.type = false
            ScanQRViewController.global = true
            ScanQRViewController.forChange = true
            
            self.changeView()
       
            
                   })
        
        let cancelButton = UIAlertAction(title: "Anuleaza", style: .cancel, handler: { (action) -> Void in
            print("Cancel button tapped")
        })
        
        
        alertController.addAction(sendButton)
        alertController.addAction(deleteButton)
        alertController.addAction(cancelButton)
        
        self.navigationController!.present(alertController, animated: true, completion: nil)
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
