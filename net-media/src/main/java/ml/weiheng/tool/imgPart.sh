#!/bin/bash
adb pull /sdcard/Pictures/CoolMarket ~/Downloads/
java ImgSizePartion.java
echo '推送到设备'
adb push  ~/Downloads/width/* /sdcard/file/image/acg_background_pc/
adb push  ~/Downloads/height/* /sdcard/file/image/acg_background_phone/
echo '移动生效'
mv ~/Downloads/width/* ~/file/img/acg_background_pc/
mv ~/Downloads/height/* ~/file/img/acg_background_phone/
echo '操作完成'