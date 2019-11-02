package ml.weiheng.tool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

/** @author h */
public class ImgSizePartion {

  public static void main(String[] args) throws IOException {
    //    String imgPath = "/sdcard/Pictures/CoolMarket";
    //    String outWidthPath = "/sdcard/file/image/acg_background_pc";
    //    String outheightPath = "/sdcard/file/image/acg_background_phone";

    String imgPath = "/home/h/Downloads/CoolMarket";
    String outWidthPath = "/home/h/file/img/acg_background_pc";
    String outHeightPath = "/home/h/file/img/acg_background_phone";

    partion(imgPath, outWidthPath, outHeightPath);
  }

  private static void partion(String p, String wout, String hout) throws IOException {
    File parent = new File(p);
    BufferedImage sourceImg;
    int successCnt = 0;
    int failedCnt = 0;
    for (File file : Objects.requireNonNull(parent.listFiles())) {
      sourceImg = ImageIO.read(new FileInputStream(file));
      String originPath = file.getAbsolutePath();
      if (sourceImg == null) {
        failedCnt++;
        log("一个失败! 文件:%s无法读取为图像文件", originPath);
        continue;
      }

      int w = sourceImg.getWidth();
      int h = sourceImg.getHeight();
      String uPath = w >= h ? wout : hout;
      String fin = uPath + File.separator + file.getName();

      if (file.renameTo(new File(fin))) {
        successCnt++;
        log("文件已移动: %s -> %s", originPath, fin);
      } else {
        failedCnt++;
        log("一个失败! 文件:%s无法移动到", originPath, fin);
      }
    }
    log("分拣完成~~, 共%s个文件, 成功:%s, 失败:%s.", successCnt + failedCnt, successCnt, failedCnt);
  }

  private static void log(String msg, Object... params) {
    if (Objects.isNull(params)) {
      System.out.println(msg);
    } else {
      System.out.println(String.format(msg, params));
    }
  }
}
