/*
* Copyright (C) 2011 Andrew Reid and the modelGUI Project <http://mgui.wikidot.com>
* 
* This file is part of modelGUI[exec] (mgui-exec).
* 
* modelGUI[exec] is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* modelGUI[exec] is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with modelGUI[exec]. If not, see <http://www.gnu.org/licenses/>.
*/

package mgui.interfaces.startup;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import mgui.interfaces.shapes.ShapeSet3DInt;

/*********************************************
 * Frame which allows the user to select an init file on start-up. Will only show if more than one
 * init file is available.
 * 
 * @author Andrew Reid
 * @version 1.0
 * @since 1.0
 *
 */
public class InitFrame extends JFrame implements ActionListener{

	JPanel combo_panel;
	JLabel combo_label = new JLabel("Selected Init File: ");
	JComboBox combo_box = new JComboBox();
	JTextArea text_area = new JTextArea();
	JScrollPane scroll_pane = new JScrollPane(text_area);
	JPanel button_panel;
	JButton ok_button = new JButton("OK");
	JButton quit_button = new JButton("Quit");
	JButton default_button = new JButton("Default");
	
	public String init_file;
	InitListener listener;
	
	public boolean is_destroyed;
	
	public static void main(String[] args){
		
		InitFrame frame = new InitFrame(null);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	public InitFrame(InitListener listener){
		this.listener = listener;
		
		init();
	}
	
	
	private void init(){
		
		this.setSize(400, 300);
		this.getContentPane().setSize(400, 300);
		this.getContentPane().setPreferredSize(new Dimension(500, 600));
		this.setTitle("modelGUI Startup: Select Init File");
		
		Image img = getImage();
		this.setIconImage(img);
		
		text_area.setEditable(false);
		
		if (!initCombo()){
			this.dispose();
			is_destroyed = true;
			return;
			}
		
		combo_box.setActionCommand("Combo Changed");
		combo_box.addActionListener(this);
		combo_box.setSelectedIndex(0);
		
		ok_button.setActionCommand("OK");
		ok_button.addActionListener(this);
		
		quit_button.setActionCommand("Quit");
		quit_button.addActionListener(this);
		
		default_button.setActionCommand("Default");
		default_button.addActionListener(this);
		
		button_panel = getButtonPanel();
		combo_panel = getComboPanel();
		
		this.setLayout(new BorderLayout());
		this.add(combo_panel, BorderLayout.NORTH);
		this.add(scroll_pane, BorderLayout.CENTER);
		this.add(button_panel, BorderLayout.SOUTH);
		
		centerFrame();
		
	}
	
	public Image getImage(){
		java.net.URL imgURL = InitFrame.class.getResource("/mgui/resources/icons/mgui_logo_30.png");
		try{
			if (imgURL != null){
				//return ImageIO.read(imgURL);
				ImageIcon icon = new ImageIcon(imgURL);
				return icon.getImage();
				}
				
			else
				System.out.println("Cannot find resource: /mgui/resources/icons/mgui_logo_30.png");
		} catch (Exception e){
			e.printStackTrace();
			}
		return null;
	}
	
	private void centerFrame(){
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Container panel = this.getContentPane();
		int left = (int)((float)(screen.width - panel.getWidth()) / 2f);
		int top = 100; // (int)((float)(screen.height - panel.getHeight()) / 2f);

		this.setLocation(left, top);
		
	}
	
	public void setInitListener(InitListener listener){
		this.listener = listener;
	}
	
	public static void showFrame(InitListener listener){
		
		InitFrame frame = new InitFrame(listener);
		if (frame.is_destroyed) return;
		frame.pack();
		frame.setVisible(true);
		
	}
	
	public void actionPerformed(ActionEvent e){
		
		if (e.getActionCommand().equals("Combo Changed")){
			updateText();
			return;
			}
		
		if (e.getActionCommand().equals("OK")){
			this.init_file = (String)combo_box.getSelectedItem();
			this.setVisible(false);
			this.dispose();
			if (listener != null) listener.doInit(init_file);
			return;
			}
		
		if (e.getActionCommand().equals("Default")){
			this.init_file = null;
			this.setVisible(false);
			this.dispose();
			if (listener != null) listener.doInit(null);
			return;
			}
		
		if (e.getActionCommand().equals("Quit")){
			this.dispose();
			System.exit(0);
			}
		
	}
	
	private void updateText(){
		String init_file = (String)combo_box.getSelectedItem();
		if (init_file == null) return;
		
		text_area.setText("");
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(new File("init" + File.separator + init_file)));
			String line = reader.readLine();
			
			while (line != null){
				text_area.append(line + "\n");
				line = reader.readLine();
				}
			
			reader.close();
			
		}catch (IOException e){
			e.printStackTrace();
			return;
			}
		
	}
	
	private JPanel getButtonPanel(){
		
		JPanel panel = new JPanel();
		
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 0;
		panel.add(ok_button, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = 0;
		panel.add(default_button, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 4;
		c.gridy = 0;
		panel.add(quit_button, c);
		
		return panel;
	}
	
	private JPanel getComboPanel(){
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(combo_label, BorderLayout.WEST);
		panel.add(combo_box, BorderLayout.CENTER);
		return panel;
		
	}
	
	private boolean initCombo(){
		
		File init_dir = new File("init");
		if (! init_dir.exists()){
			System.out.println("Fatal error: no init dir found at " + init_dir.getAbsolutePath());
			this.dispose();
			System.exit(0);
			}
		
		String[] files = init_dir.list(getInitFilter());
		
		if (files.length == 0){
			System.out.println("Fatal error: no init dir found.");
			this.dispose();
			System.exit(0);
			}
		
		if (files.length == 1){
			//only one init file, so use it
			if (listener != null) listener.doInit(files[0]);
			return false;
			}
		
		for (int i = 0; i < files.length; i++){
			combo_box.addItem(files[i]);
			}

		return true;
	}
	
	
	private FilenameFilter getInitFilter(){
		
		return new FilenameFilter(){
			
			public boolean accept(File file, String name){
				return name.endsWith(".init");
			}
			
		};
		
	}
	
}