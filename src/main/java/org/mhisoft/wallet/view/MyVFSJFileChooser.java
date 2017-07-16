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

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JDialog;

import org.apache.commons.vfs2.FileObject;
import org.mhisoft.wallet.model.WalletSettings;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.filechooser.AbstractVFSFileSystemView;

/**
 * Description:   customize the font size for 4k monitors.
 *
 * @author Tony Xue
 * @since Jul, 2017
 */
public class MyVFSJFileChooser extends VFSJFileChooser {

//	Font f;
//
//	@Override
//	public Font getFont() {
//		return f;
//	}
//
//	@Override
//	public void setFont(Font f) {
//		this.f = f;
//		super.setFont(f);
//	}

	public MyVFSJFileChooser() {
	}

	public MyVFSJFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
	}

	public MyVFSJFileChooser(File currentDirectory) {
		super(currentDirectory);
	}

	public MyVFSJFileChooser(FileObject currentDirectory) {
		super(currentDirectory);
	}

	public MyVFSJFileChooser(AbstractVFSFileSystemView fsv) {
		super(fsv);
	}

	public MyVFSJFileChooser(FileObject currentDirectory, AbstractVFSFileSystemView fsv) {
		super(currentDirectory, fsv);
	}

	public MyVFSJFileChooser(String currentDirectoryPath, AbstractVFSFileSystemView fsv) {
		super(currentDirectoryPath, fsv);
	}

	@Override
	protected JDialog createDialog(Component parent) throws HeadlessException {
		JDialog d = super.createDialog(parent);

		ViewHelper.setFileChooserFont(d.getComponents(), WalletSettings.getInstance().getFontSize());

		return d;


	}
}
