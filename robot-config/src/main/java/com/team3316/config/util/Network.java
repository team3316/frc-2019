package com.team3316.config.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

public class Network {
  public static void transferConfig () {
    JSch jsch = new JSch();
    Session session;

    String host = "roborio-3316-frc.local";
    String user = "admin";
    String pass = "";
    int port = 22;

    String filePathA = Files.getPathForFile("configFileA.ser");
    String filePathB = Files.getPathForFile("configFileB.ser");

    String uploadPathA = "/home/lvuser/config/configFileA.ser";
    String uploadPathB = "/home/lvuser/config/configFileB.ser";

    try {
      session = jsch.getSession(user, host, port);
      session.setConfig("StrictHostKeyChecking", "no");
      session.setPassword(pass);
      session.connect();

      Channel channel = session.openChannel("sftp");
      channel.connect();
      ChannelSftp sftpChannel = (ChannelSftp) channel;

      byte[] buffer = new byte[4096];
      BufferedInputStream bis;
      BufferedOutputStream bos;
      int readCount;

      // com.team3316.config.Config A transferring
      bis = new BufferedInputStream(new FileInputStream(filePathA));
      bos = new BufferedOutputStream(sftpChannel.put(uploadPathA));

      while ((readCount = bis.read(buffer)) > 0) {
        bos.write(buffer, 0, readCount);
      }
      bis.close();
      bos.close();

      // com.team3316.config.Config B transferring
      bis = new BufferedInputStream(new FileInputStream(filePathB));
      bos = new BufferedOutputStream(sftpChannel.put(uploadPathB));

      while ((readCount = bis.read(buffer)) > 0) {
        bos.write(buffer, 0, readCount);
      }

      bis.close();
      bos.close();
      System.out.println("File uploaded");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void fetchLogs () {
    JSch jsch = new JSch();
    Session session = null;

    String host = "roborio-3316-frc.local";
    String user = "admin";
    String pass = "";
    int port = 22;

    String sourcePath = "/home/lvuser/logs/";
    String destPath = Files.getPathForFile("logs/");

    try {
      session = jsch.getSession(user, host, port);
      session.setConfig("StrictHostKeyChecking", "no");
      session.setPassword(pass);
      session.connect();

      Channel channel = session.openChannel("sftp");
      channel.connect();
      ChannelSftp sftpChannel = (ChannelSftp) channel;

      byte[] buffer = new byte[4096];
      BufferedInputStream bis;
      BufferedOutputStream bos;
      int readCount;

      Vector<?> logs = sftpChannel.ls(sourcePath);

      sftpChannel.cd(sourcePath);

      for (int i = 0; i < logs.size(); i++) {
        String fullString = logs.get(i).toString();
        String fileString = fullString.substring(fullString.lastIndexOf(" ") + 1);
        if (fullString.contains("logFile") && !fullString.contains("lck")) {

          /*
           * File existingLog = new File(destPath + fileString); if (existingLog.exists())
           * { continue; }
           */

          bis = new BufferedInputStream(sftpChannel.get(fileString));
          bos = new BufferedOutputStream(new FileOutputStream(new File(destPath + fileString)));

          while ((readCount = bis.read(buffer)) > 0) {
            bos.write(buffer, 0, readCount);
          }

          System.out.println("Transferred " + fileString);
          bis.close();
          bos.close();
        } else if (fullString.contains("lck")) {
          sftpChannel.rm(fileString);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
