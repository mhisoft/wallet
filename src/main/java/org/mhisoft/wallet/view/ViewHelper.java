/*
 *
 *  * Copyright (c) 2014- MHISoft LLC and/or its affiliates. All rights reserved.
 *  * Licensed to MHISoft LLC under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. MHISoft LLC licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 *
 */

package org.mhisoft.wallet.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.mhisoft.common.util.FileUtils;
import org.mhisoft.wallet.model.WalletSettings;
import org.mhisoft.wallet.service.ServiceRegistry;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.accessories.DefaultAccessoriesPanel;
import com.googlecode.vfsjfilechooser2.filepane.VFSFilePane;

/**
 * Description:
 *
 * @author Tony Xue
 * @since May, 2016
 */
public class ViewHelper {

	public static Font getDefaultFont() {
		return ServiceRegistry.instance.getWalletForm().tree.getFont();   //Arial, regular, font size 20.
	}

	public static void setFontSize(List<Component> componetsList, int newFontSize) {
		WalletSettings.getInstance().setFontSize(newFontSize);
		for (Component component : componetsList) {
			Font original = component.getFont();
			Font newFont = original.deriveFont(Float.valueOf(newFontSize));
			component.setFont(newFont);

		}
	}

	public static void setFontSizeForComponent(JComponent component, int newFontSize)  {
		Font original = component.getFont();
		Font newFont = original.deriveFont(Float.valueOf(newFontSize));
		component.setFont(newFont);
	}

	public static void setFontSize(Container c, int newFontSize) {
		List<Component> components = getAllComponents(c);
		setFileChooserFont (components.toArray( new Component[components.size()] ), newFontSize);
	}



	public static void setFontSize(Component[] componetsList, int newFontSize) {
		if (componetsList != null)
			setFontSize(Arrays.asList(componetsList), newFontSize);
	}

	//set JDialog
	public static void setUIManagerFontSize() {
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, WalletSettings.getInstance().getFontSize());
		UIManager.put("OptionPane.messageFont", font);
		UIManager.put("OptionPane.buttonFont", font);
	}


	//recursivly set fonts.
	protected  static void setFileChooserFont(Component[] comp, int newFontSize) {
		for (int x = 0; x < comp.length; x++) {
			if (comp[x] instanceof Container)
				setFileChooserFont(((Container) comp[x]).getComponents(), newFontSize);
			try {
				Font original = comp[x].getFont();
				Font newFont = original.deriveFont(Float.valueOf(newFontSize));

				comp[x].setFont(newFont);


			} catch (Exception e) {
			}//do nothing
		}
	}



	/**
	 * Resgier allthe components in the jFrame.
	 *
	 * @param c
	 * @return
	 */
	public static List<Component> getAllComponents(final Container c) {
		Component[] comps = c.getComponents();
		List<Component> compList = new ArrayList<Component>();
		for (Component comp : comps) {
			compList.add(comp);

			if (comp instanceof JMenu) {
				JMenu menu = (JMenu) comp;
				for (int i = 0; i < menu.getItemCount(); i++) {
					if (menu.getItem(i)!=null)
					compList.add(menu.getItem(i));
				}
			}
			else if (comp instanceof Container)
				compList.addAll(getAllComponents((Container) comp));

		}
		return compList;
	}



	/* use java swing JFileChooser. */
	public static String chooseFilev1(String... extensions) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		String defaultDir = WalletSettings.getInstance().getRecentOpenDir();
		chooser.setCurrentDirectory(new File(defaultDir));
		if (extensions==null || extensions.length==0)
			extensions = new String[] {"dat"} ;
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Wallet data files", extensions));
		chooser.setPreferredSize(new Dimension(WalletSettings.getInstance().getDimensionX()/2, WalletSettings.getInstance().getDimensionX()/3));
		//start with detailed view.
		//Action details = chooser.getActionMap().get("viewTypeDetails");
		//details.actionPerformed(null);

		//set font
		setFileChooserFont(chooser.getComponents(), WalletSettings.getInstance().getFontSize());

		int returnValue = chooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			String path = chooser.getSelectedFile().getAbsolutePath();
			WalletSettings.getInstance().setRecentOpenDir(FileUtils.gerFileDir(path));
			return path;
		}
		return null;
	}


	/**
	 * @param defaultDir
	 * @param selectionMode
	 * @param fileHidingEnabled     If true, hidden files are not shown in the file chooser.
	 * @param MultiSelectionEnabled
	 * @return
	 */
	public static File[] chooseFiles(final File defaultDir, VFSJFileChooser.SELECTION_MODE selectionMode,
			FileUtils.FilesTypeFilter filesTypeFilter,
			boolean fileHidingEnabled,
			boolean MultiSelectionEnabled
			,Dimension preferredSize
			, Font newFont
	) {
		// create a file chooser
		final MyVFSJFileChooser fileChooser = new MyVFSJFileChooser();

		// configure the file dialog
		fileChooser.setAccessory(new DefaultAccessoriesPanel(fileChooser));
		fileChooser.setFileHidingEnabled(fileHidingEnabled);
		fileChooser.setMultiSelectionEnabled(MultiSelectionEnabled);
		fileChooser.setFileSelectionMode(selectionMode);
		fileChooser.setPreferredSize(preferredSize);
		//font size
		//fileChooser.setFont(newFont);


		if (defaultDir != null)
			fileChooser.setCurrentDirectory(defaultDir);
		if (filesTypeFilter!=null)
			fileChooser.setFileFilter(filesTypeFilter);


		fileChooser.firePropertyChange("viewType", VFSFilePane.VIEWTYPE_LIST, VFSFilePane.VIEWTYPE_DETAILS);


		// show the file dialog
		VFSJFileChooser.RETURN_TYPE answer = fileChooser.showOpenDialog(ServiceRegistry.instance.getWalletForm().getFrame());


		// check if a file was selected
		if (answer == VFSJFileChooser.RETURN_TYPE.APPROVE) {
			File[] files;
			if (MultiSelectionEnabled)
				files = fileChooser.getSelectedFiles();
			else {
				files = new File[1];
				files[0] = fileChooser.getSelectedFile();
			}

//			// remove authentication credentials from the file path
//			final String safeName = VFSUtils.getFriendlyName(aFileObject.toString());
//
//			System.out.printf("%s %s", "You selected:", safeName);
			return files;
		}
		return null;
	}





	public static String chooseFile( VFSJFileChooser.SELECTION_MODE selectionMode, String... extensions) {

		File[] files = chooseFiles(WalletSettings.getInstance().getRecentOpenDirFile()
				, selectionMode//VFSJFileChooser.SELECTION_MODE.FILES_ONLY
				, new FileUtils.FilesTypeFilter(extensions)
				, true, false
				, new Dimension(WalletSettings.getInstance().getDimensionX()*3/5, WalletSettings.getInstance().getDimensionX()/3)
				, getDefaultFont()
		);
		if (files != null && files.length > 0) {

			String path = files[0].getAbsolutePath();
			WalletSettings.getInstance().setRecentOpenDir(FileUtils.gerFileDir(path));
			return path;
		}
		return null;



	}


}
