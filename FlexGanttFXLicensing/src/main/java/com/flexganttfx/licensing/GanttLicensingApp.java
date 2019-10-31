/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.licensing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class GanttLicensingApp extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField licensee = new JTextField("", 30);
	private JTextField vendor = new JTextField("DLSC", 30);
    private JCheckBox runtime = new JCheckBox("Runtime");
	private JTextField counter = new JTextField("1", 2);
	private JComboBox<Version> version = new JComboBox<>(Version.values());
	private JComboBox<Product> product = new JComboBox<>(Product.values());
	private JTextArea key = new JTextArea(5, 30);
	private JButton generate = new JButton("Generate");

	public GanttLicensingApp() {
		super("Gantt Chart: Create License");

		key.setEditable(false);
		key.setLineWrap(true);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		((JComponent) getContentPane()).setBorder(new EmptyBorder(20, 20, 20,
				20));
		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);

		JLabel label = new JLabel("Licensee:");
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(label, gbc);
		label = new JLabel("Vendor:");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(label, gbc);
		label = new JLabel("Counter:");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(label, gbc);
		label = new JLabel("Version:");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(label, gbc);
		label = new JLabel("Product:");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(label, gbc);
		label = new JLabel("License Key:");
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx = 0;
		gbc.gridy = 6;
		add(label, gbc);

		// the controls
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(licensee, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(vendor, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		gbc.gridy = 2;
		add(runtime, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		gbc.gridy = 3;
		add(counter, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		gbc.gridy = 4;
		add(version, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		gbc.gridy = 5;
		add(product, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		gbc.gridy = 56;
		add(new JScrollPane(key), gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 2;
		gbc.gridy = 7;
		add(generate, gbc);

		pack();

		generate.addActionListener(e -> {
			Version ver = (Version) version.getSelectedItem();
			try {
				LicensingHelper.initPrivateKey(ver);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			Product pro = (Product) product.getSelectedItem();

			try {
				key.setText(LicensingHelper.createSingleLicense(
						licensee.getText(), vendor.getText(),
						runtime.isSelected(), ver.getText(), pro.toString(),
						Integer.parseInt(counter.getText())));
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (GeneralSecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}

	public static void main(String[] args) {
		GanttLicensingApp dialog = new GanttLicensingApp();
		dialog.setVisible(true);
	}
}
