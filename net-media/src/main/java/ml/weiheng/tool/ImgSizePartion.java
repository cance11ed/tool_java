package ml.weiheng.tool;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class ImgSizePartion {

  private String imgPath = "/home/h/Downloads/CoolMarket1";
  private String outWidthPath = "/home/h/Downloads/width";
  private String outheightPath = "/home/h/Downloads/height";

  @Test
  public void partion() throws IOException {
    File parent = new File(imgPath);
    BufferedImage sourceImg;
    int cnt = 0;

    for (File file : Objects.requireNonNull(parent.listFiles())) {
      sourceImg = ImageIO.read(new FileInputStream(file));
      if (sourceImg == null) {
        continue;
      }

      int w = sourceImg.getWidth();
      int h = sourceImg.getHeight();
      String name = file.getName();
      String uPath;
      if (w >= h) {
        uPath = outWidthPath;
      } else {
        uPath = outheightPath;
      }
      String fin = uPath + File.separator + name;
      System.out.println(fin);
      cnt++;
      file.renameTo(new File(fin));
    }

    System.out.println("cnt");
    System.out.println(cnt);
  }

}
