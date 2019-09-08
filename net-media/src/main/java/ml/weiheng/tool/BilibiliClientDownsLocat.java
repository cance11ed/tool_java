package ml.weiheng.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BilibiliClientDownsLocat {

  static String downloadPath = "/home/h/file/mv/download";
  static String outPath = "/home/h/file/mv/tmp";
  static String bakPath = outPath + File.separator + "bak.json";
  static JSONArray changes = new JSONArray();

  public static void main(String[] args) {
    doMv();
  }

  /** 移动文件 */
  public static void doMv() {

    File downloadDirFile = new File(downloadPath);
    List<String> cmds = new ArrayList<>();

    Arrays.stream(Objects.requireNonNull(downloadDirFile.listFiles()))
        .forEach(
            animeDirFile ->
                Arrays.stream(Objects.requireNonNull(animeDirFile.listFiles()))
                    .forEach(
                        oneDirFile -> {
                          File entryFile = null;
                          File contentDirFile = null;
                          for (File i : oneDirFile.listFiles()) {
                            if (i.isDirectory()) {
                              contentDirFile = i;
                            } else if (i.getName().equalsIgnoreCase("entry.json")) {
                              entryFile = i;
                            }
                          }

                          JSONObject json = JSON.parseObject(readFileContent(entryFile));
                          String animeName = json.getString("title");
                          String jiName = json.getJSONObject("ep").getString("index_title");
                          String jiIdx = json.getJSONObject("ep").getString("index");

                          for (File i : contentDirFile.listFiles()) {
                            if ("index.json".equalsIgnoreCase(i.getName())) {
                              continue;
                            }
                            String newFilePath =
                                outPath
                                    + File.separator
                                    + animeName
                                    + File.separator
                                    + getIdx(jiIdx)
                                    + "_"
                                    + jiName;
                            new File(newFilePath).getParentFile().mkdirs();

                            if ("audio.m4s".equalsIgnoreCase(i.getName())
                                || "video.m4s".equalsIgnoreCase(i.getName())) {
                              newFilePath = newFilePath + ".mp4";
                              cmds.add(
                                  mergeAudioAndVideoCmd(
                                      Arrays.stream(contentDirFile.listFiles())
                                          .filter(x -> !"index.json".equalsIgnoreCase(x.getName()))
                                          .collect(Collectors.toList()),
                                      newFilePath));
                              addChange(i.getAbsolutePath(), newFilePath);
                              break;
                            } else {
                              newFilePath =
                                  newFilePath
                                      + "_"
                                      + (getFileSuffix(i.getName()).equalsIgnoreCase("blv")
                                          ? i.getName() + ".flv"
                                          : i.getName());
                              addChange(i.getAbsolutePath(), newFilePath);
                              i.renameTo(new File(newFilePath));
                              System.out.println(i.getAbsolutePath() + " -> " + newFilePath);
                            }
                          }
                        }));

    System.out.println("merge cmd: ");
    System.out.println("#!/bin/bash");
    cmds.forEach(System.out::println);
    write(new File(bakPath), changes.toString());
  }

  /** 从记录恢复文件移动 */
  @Test
  public void revert() {
    JSONArray arr = JSON.parseArray(readFileContent(new File(bakPath)));
    arr.forEach(
        i -> {
          JSONObject json = ((JSONObject) i);
          String n = json.getString("n");
          if (!"mp4".equalsIgnoreCase(getFileSuffix(n))) {
            System.out.println(json.getString("n") + " -> " + json.getString("o"));
            new File(json.getString("n")).renameTo(new File(json.getString("o")));
          }
        });
  }

  /**
   * 添加文件移动记录
   *
   * @param o
   * @param n
   */
  private static void addChange(String o, String n) {
    JSONObject json = new JSONObject();
    json.put("o", o);
    json.put("n", n);
    changes.add(json);
  }

  /**
   * 补全文件序号到三位
   *
   * @param idx
   * @return
   */
  private static String getIdx(String idx) {
    if (idx.length() == 1) {
      return "00" + idx;
    }
    if (idx.length() == 2) {
      return "0" + idx;
    }
    return idx;
  }

  /**
   * 生成合并音视频文件命令
   *
   * @param farr
   * @param newFilePath
   * @return
   */
  private static String mergeAudioAndVideoCmd(List<File> farr, String newFilePath) {
    return String.format(
        "ffmpeg -i %s -i %s -c:v copy -c:a aac  -strict experimental \"%s\"",
        farr.get(0).getAbsolutePath(), farr.get(1).getAbsolutePath(), newFilePath);
  }

  /**
   * 覆盖文件内容
   *
   * @param f
   * @param content
   */
  private static void write(File f, String content) {
    f.getParentFile().mkdirs();
    if (f.exists()) {
      f.delete();
    }
    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(f);
      fileOutputStream.write(content.getBytes("UTF-8"));
      fileOutputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 读取文件内容
   *
   * @param file
   * @return
   */
  private static String readFileContent(File file) {
    String encoding = "UTF-8";
    byte[] filecontent = new byte[(int) file.length()];
    try {
      FileInputStream in = new FileInputStream(file);
      in.read(filecontent);
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      return new String(filecontent, encoding);
    } catch (UnsupportedEncodingException e) {
      System.err.println("The OS does not support " + encoding);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 获取文件后缀
   *
   * @param fileName
   * @return
   */
  private static String getFileSuffix(String fileName) {
    return fileName.substring(fileName.lastIndexOf(".") + 1);
  }
}
