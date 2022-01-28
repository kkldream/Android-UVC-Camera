# Android-UVC-Camera
 
### Project1

使用[Liuguihong/AndroidUVCCamera](https://github.com/Liuguihong/AndroidUVCCamera)此Github上的UVC庫，簡單Demo了UVC的調用+TextureView的預覽。

### Project2

使用[jiangdongguo/AndroidUSBCamera](https://github.com/jiangdongguo/AndroidUSBCamera)此Github上的UVC庫，功能同Project1，另多加了拍照和分段錄影的功能，分段錄影類似行車紀錄器，假設每小時儲存1次，同時預覽保持顯示，且使用兩組`BufferedOutputStream`來對串流資料做交互擷取，所以不會出現漏秒問題。

### Project3

使用[jiangdongguo/AndroidUSBCamera](https://github.com/jiangdongguo/AndroidUSBCamera)此Github上的UVC庫，使用Project2進行修改，並將函式庫裡的`UVCCameraHelper`類別取消靜態宣告，實現多組USB Camera同時預覽，但目前無法同時錄影、拍照。

### 參考

[saki4510t/UVCCamera](https://github.com/saki4510t/UVCCamera) ([這個庫的中文介紹](https://blog.csdn.net/fengshiguang2012/article/details/79569280))
