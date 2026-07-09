package com.taoufikcode.krosschat

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val os = "IOS"

}

actual fun getPlatform(): Platform = IOSPlatform()