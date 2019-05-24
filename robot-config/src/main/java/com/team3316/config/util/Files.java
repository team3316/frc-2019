package com.team3316.config.util;

import com.team3316.config.Config;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Files {
  public static String getPathForFile (String filename) {
    String userHome = System.getProperty("user.home");
    Path p = Paths.get(userHome, "dbug-config", filename);
    return p.toAbsolutePath().toString();
  }

  public static void writeConfig (boolean isRobotA) {
    try {
      // com.team3316.config.Config A writing
      String filepath = getPathForFile(isRobotA ? "configFileA.ser" : "configFileB.ser");
      FileOutputStream out = new FileOutputStream(filepath);
      ObjectOutputStream output = new ObjectOutputStream(out);

      output.writeObject(isRobotA ? Config.constantsA : Config.constantsB);
      output.writeObject(isRobotA ? Config.variablesA : Config.variablesB);

      output.close();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
