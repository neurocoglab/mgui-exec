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

package mgui.interfaces.exec;

import mgui.interfaces.InterfaceSession;
import mgui.interfaces.startup.InitFrame;
import mgui.interfaces.startup.InitListener;

/*******************************************************
 * Simple launcher for a modelGUI session which allows the user to specify an init file via the {@link InitFrame}. 
 *  
 * @author Andrew Reid
 * @version 1.0
 * @since 1.0
 *
 */
public class MguiExec implements InitListener {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		InitFrame.showFrame(new MguiExec());
		
	}
	
	public void doInit(String init_file){
		
		if (init_file != null){
			InterfaceSession.start(init_file);
		}else{
			InterfaceSession.start();
			}
		
		
	}

}