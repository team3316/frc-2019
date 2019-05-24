package com.team3316.config;

import com.team3316.config.util.Files;
import com.team3316.config.util.Network;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
  public static void main (String[] args) {
    final JFrame frame = new JFrame();
    frame.setTitle("D-Bug Config Utility");

    JPanel panel = new JPanel();
    frame.setBounds(0, 0, 200, 200);
    frame.setLocationRelativeTo(null);

    JButton getLogsButton = new JButton("Download Logs");
    JButton uploadConfigButton = new JButton("Upload Config");

    frame.add(panel);
    panel.add(getLogsButton);
    panel.add(uploadConfigButton);
    frame.setVisible(true);

    uploadConfigButton.addActionListener(arg0 -> {
      Files.writeConfig(true);
      Files.writeConfig(false);
      Network.transferConfig();
    });

    getLogsButton.addActionListener(arg0 -> Network.fetchLogs());
  }
}