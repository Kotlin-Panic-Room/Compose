package core

import com.sun.jna.Memory
import com.sun.jna.platform.win32.GDI32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinGDI
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

object Capture {
    private val user32: User32 = User32.INSTANCE
    private val gdi32: GDI32 = GDI32.INSTANCE
    const val SRCCOPY = 0xCC0020
    fun getRect(hWnd: WinDef.HWND): WinDef.RECT {
        val rect = WinDef.RECT()
        user32.GetWindowRect(hWnd, rect)
        rect.left = maxOf(0, rect.left)
        rect.top = maxOf(0, rect.top)
        rect.right = maxOf(0, rect.right)
        rect.bottom = maxOf(0, rect.bottom)
        return rect
    }

    fun getScreenShot(handle: WinDef.HWND, tlX: Int = 0, tlY: Int = 0, width: Int = 0, height: Int = 0): BufferedImage {
        var width = width
        var height = height


        if (width == 0 || height == 0) {
            // Get target window size
            val rect = WinDef.RECT()
            user32.GetClientRect(handle, rect)
            width = rect.right
            height = rect.bottom
        }

        // Start screenshot
        val hdcWindow = user32.GetDC(handle)
        val hdcMemDC = gdi32.CreateCompatibleDC(hdcWindow)
        val hBitmap = gdi32.CreateCompatibleBitmap(hdcWindow, width, height)
        val hOld = gdi32.SelectObject(hdcMemDC, hBitmap)
        gdi32.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, tlX, tlY, SRCCOPY)
        gdi32.SelectObject(hdcMemDC, hOld)
        gdi32.DeleteDC(hdcMemDC)

        val bmpInfo = WinGDI.BITMAPINFO()
        bmpInfo.bmiHeader.biSize = 40
        bmpInfo.bmiHeader.biWidth = width
        bmpInfo.bmiHeader.biHeight = -height  // negative to correct the upside down
        bmpInfo.bmiHeader.biPlanes = 1
        bmpInfo.bmiHeader.biBitCount = 32
        bmpInfo.bmiHeader.biCompression = WinGDI.BI_RGB

        val bufferSize = width * height * 4  // For BGRA format
        val buffer = Memory(bufferSize.toLong())
        gdi32.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmpInfo, WinGDI.DIB_RGB_COLORS)

        val image = BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
        val pixels = (image.raster.dataBuffer as DataBufferByte).data
        System.arraycopy(buffer, 0, pixels, 0, bufferSize)

        // Clean up
        gdi32.DeleteObject(hBitmap)
        user32.ReleaseDC(handle, hdcWindow)

        return image
    }
}
