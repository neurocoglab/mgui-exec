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

/*********************************************
 * Simple launcher for a modelGUI session. The single optional argument specifies the init file. If it is
 * not specified the default init file will be used.
 *
 * @author Andrew Reid
 * @version 1.0
 * @since 1.0
 */

public class MguiStart {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length > 0){
			InterfaceSession.start(args[0]);
		}else{
			InterfaceSession.start();
		}
		
	}

}