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

package mgui.build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import mgui.command.CommandInterpreter;
import mgui.io.util.IoFunctions;

/******************************************
 * Builds an executable file based upon parameters including:
 * 
 * <ul>
 * -libdir: library directory
 * -classpathfile: file containing classpath entries
 * -initfile: file to init environment
 * -maxheapsize: max heap size for JVM (default 1000m)
 * -maxstacksize: max stack size for JVM (default 50m)
 * 
 * </ul>
 * 
 * @author Andrew Reid
 *
 */

public class Build {

	/**************
	 * Arguments:
	 * -libdir Directory for dependencies
	 * -classpathfile File containing classpath specifications
	 * -initfile Init file to use (must be in the mgui/resources/init folder)
	 * 
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		
		BuildSpecs specs = new BuildSpecs(args);
		String sep = File.separator;		

		try{
			if (specs.output_file == null){
				System.out.println("Must specify an output file..");
				System.exit(0);
				}
			File output = new File(specs.output_file);
			if ((output.exists() && !output.delete()) || !output.createNewFile()){
				System.out.println("Cannot create output file '" + output.getAbsolutePath() + "'..");
				System.exit(0);
				}
			
			if (specs.classpath_file == null){
				System.out.println("Must specify a classpath file..");
				System.exit(0);
				}
			File class_in = new File(specs.classpath_file);
			if (!class_in.exists()){
				System.out.println("Invalid classpath file '" + class_in.getAbsolutePath() + "'..");
				System.exit(0);
				}
		
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			writer.write("java -Xmx" + specs.heap_size + " -oss" + specs.stack_size + " -classpath \"." +
					File.pathSeparator + changeSeparators("jar/*"));
			
			BufferedReader reader = new BufferedReader(new FileReader(class_in));
			String line = reader.readLine();
			
			while (line != null){
				writer.write(File.pathSeparator + getReference(line, specs));
				line = reader.readLine();
				}
			
			writer.write("\" mgui.interfaces.exec.MguiStart" );
			if (specs.init_file != null) writer.write(" mgui" + sep + "resources" + sep + "init" + sep +
												      specs.init_file);
			writer.write("\n");
			//writer.write("PAUSE\n");
			
			writer.close();
			reader.close();
			System.out.println("Exec file written to '" + output.getAbsolutePath() + "'.");
			
		}catch (Exception e){
			e.printStackTrace();
		}
	
	}
	
	/*****************************************
	 * Constructs and returns a command line from the specified build file
	 * 
	 * @param build_file
	 * @return
	 * @throws IOException
	 */
	public static String getCommandLine(String build_file) throws IOException{
		return getCommandLine(build_file, null);
	}
	
	/*****************************************
	 * Constructs and returns a command line from the specified build file
	 * 
	 * @param build_file	The build file
	 * @param init_file		The init file; can be null
	 * @return
	 * @throws IOException
	 */
	public static String getCommandLine(String build_file, String init_file) throws IOException{
		
		File dir = IoFunctions.getCurrentDir();
		File file = new File(build_file);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		reader.close();
		
		line = line.substring(line.indexOf("mgui.build.Build") + 17);
		BuildSpecs specs = new BuildSpecs(line.split(" "));
		String sep = File.separator;
		
		String command = "java -Xmx" + specs.heap_size + " -oss" + specs.stack_size + " -classpath \"." +
						 File.pathSeparator + changeSeparators("jar/*");
		
		if (specs.classpath_file == null){
			System.out.println("Must specify a classpath file..");
			System.exit(0);
			}
		
		File class_in = new File(specs.classpath_file);
		if (!class_in.exists()){
			System.out.println("Invalid classpath file '" + class_in.getAbsolutePath() + "'..");
			System.exit(0);
			}
		
		reader = new BufferedReader(new FileReader(class_in));
		line = reader.readLine();
		
		while (line != null){
			command = command + File.pathSeparator + getReference(line, specs);
			line = reader.readLine();
			}
		
		command = command + "\" mgui.interfaces.exec.MguiStart";

		if (init_file == null) 
			init_file = specs.init_file;
		
		if (init_file != null) 
			command = command + " mgui" + sep + "resources" + sep + "init" + sep +
											      		specs.init_file;
		
		return command;
		
	}
	
	static String getReference(String line, BuildSpecs specs){
		
		line = line.replace("{libdir}", specs.lib_dir);
		
		return changeSeparators(line);
	}
	
	static void changeSeparators(String[] args){
		for (int i = 0; i < args.length; i++)
			args[i] = changeSeparators(args[i]);
	}
	
	static String changeSeparators(String s){
		
		String sep = "\\";
		if (sep.equals(File.separator))
			sep = "/";
		
		return s.replace(sep, File.separator);
		
	}

	static class BuildSpecs {
		
		//public String root_dir = System.getProperty("user.dir");
		public String lib_dir = null;
		public String classpath_file;
		public String init_file = null; //"mgui.model.init";
		public String output_file;
		public String heap_size = "1000m";
		public String stack_size = "50m";
		
		public BuildSpecs(String[] args){
			
			changeSeparators(args);
			
			for (int i = 0; i < args.length; i++){
				
				if (args[i].equals("-libdir")){
					lib_dir = args[i + 1];
					i++;
					}
				
				else if (args[i].equals("-classpathfile")){
					classpath_file = args[i + 1];
					i++;
					}
				
				else if (args[i].equals("-initfile")){
					init_file = args[i + 1];
					i++;
					}
				
				else if (args[i].equals("-outputfile")){
					output_file = args[i + 1];
					i++;
					}
				
				else if (args[i].equals("-maxheapsize")){
					heap_size = args[i + 1];
					i++;
					}
				
				else if (args[i].equals("-maxstacksize")){
					stack_size = args[i + 1];
					i++;
					}
				
				
				}
		}
		
		
		
	}
	
}