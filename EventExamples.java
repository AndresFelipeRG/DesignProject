/**
 * Copyright 2016 Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package examples;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;
import cc.kave.commons.model.events.ActivityEvent;
import cc.kave.commons.model.events.CommandEvent;
import cc.kave.commons.model.events.ErrorEvent;
import cc.kave.commons.model.events.EventTrigger;
import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.model.events.InfoEvent;
import cc.kave.commons.model.events.NavigationEvent;
import cc.kave.commons.model.events.SystemEvent;
import cc.kave.commons.model.events.completionevents.CompletionEvent;
import cc.kave.commons.model.events.testrunevents.TestRunEvent;
import cc.kave.commons.model.events.userprofiles.UserProfileEvent;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlEvent;
import cc.kave.commons.model.events.visualstudio.BuildEvent;
import cc.kave.commons.model.events.visualstudio.DebuggerEvent;
import cc.kave.commons.model.events.visualstudio.DocumentEvent;
import cc.kave.commons.model.events.visualstudio.EditEvent;
import cc.kave.commons.model.events.visualstudio.FindEvent;
import cc.kave.commons.model.events.visualstudio.IDEStateEvent;
import cc.kave.commons.model.events.visualstudio.InstallEvent;
import cc.kave.commons.model.events.visualstudio.SolutionEvent;
import cc.kave.commons.model.events.visualstudio.UpdateEvent;
import cc.kave.commons.model.events.visualstudio.WindowEvent;
import cc.kave.commons.utils.io.ReadingArchive;
import cc.kave.commons.utils.io.json.JsonUtils;

/**
 * This class contains several code examples that explain how to read enriched
 * event streams with the CARET platform. It cannot be run, the code snippets
 * serve as documentation.
 */
public class EventExamples {

	/**
	 * this variable should point to a folder that contains a bunch of .zip
	 * files that may be nested in subfolders. If you have downloaded the event
	 * dataset from our website, please unzip the archive and point to the
	 * containing folder here.
	 */
	private static final String DIR_USERDATA = "dir_user_data";

	/**
	 * 1: Find all users in the dataset.
	 * 
	 */
	public static void main() {
	  readPlainEvents();
	}
	public static List<String> findAllUsers() {
		// This step is straight forward, as events are grouped by user. Each
		// .zip file in the dataset corresponds to one user.

		List<String> zips = Lists.newLinkedList();
		for (File f : FileUtils.listFiles(new File(DIR_USERDATA), new String[] { "zip" }, true)) {
			zips.add(f.getAbsolutePath());
		}
		return zips;
	}

	/**
	 * 2: Reading events
	 */
	public static void readAllEvents() {
		// each .zip file corresponds to a user
		List<String> userZips = findAllUsers();

		for (String user : userZips) {
			// you can use our helper to open a file...
			ReadingArchive ra = new ReadingArchive(new File(user));
			// ...iterate over it...
			while (ra.hasNext()) {
				// ... and desrialize the IDE event.
				IIDEEvent e = ra.getNext(IIDEEvent.class);
				// afterwards, you can process it as a Java object
				process(e, user);
			}
			ra.close();
		}
	}

	/**
	 * 3: Reading the plain JSON representation
	 */
	public static void readPlainEvents() {
		// the example is basically the same as before, but...
		List<String> userZips = findAllUsers();

		for (String user : userZips) {
			ReadingArchive ra = new ReadingArchive(new File(user));
			while (ra.hasNext()) {
				// ... sometimes it is easier to just read the JSON...
				String json = ra.getNextPlain();
				// .. and call the deserializer yourself.
				IIDEEvent e = JsonUtils.fromJson(json, IIDEEvent.class);
				process(e, user);

				// Not all event bindings are very stable already, reading the
				// JSON helps debugging possible bugs in the bindings
			}
			ra.close();
		}
	}

	/**
	 * 4: Processing events
	 */
	private static void process(IIDEEvent event, String user) {
		// once you have access to the instantiated event you can dispatch the
		// type. As the events are not nested, we did not implement the visitor
		// pattern, but resorted to instanceof checks.
	  PrintWriter pw = null;
	    try {
        pw  = new PrintWriter(new FileOutputStream(
            new File("events.csv"), 
            true /* append = true */)); 
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
	    StringBuilder sb = new StringBuilder();
		if (event instanceof CommandEvent) {
			// if the correct type is identified, you can cast it...
			CommandEvent ce = (CommandEvent) event;
			// ...and access the special context for this kind of event
			sb.append(user);
			sb.append(",");
			sb.append(ce.IDESessionUUID);
			sb.append(",");
			sb.append("CommandEvent");
			sb.append(",");
			sb.append(ce.TriggeredAt);
			sb.append(",");
			sb.append(ce.Duration.getSeconds());
			sb.append("\n");
			
			pw.write(sb.toString());
			pw.close();
			//System.out.println(ce.CommandId);
		}
		if(event instanceof ActivityEvent) {
		   ActivityEvent ae = (ActivityEvent) event;
		   sb.append(user);
           sb.append(",");
           sb.append(ae.IDESessionUUID);
           sb.append(",");
           sb.append("ActivityEvent");
           sb.append(",");
           sb.append(ae.TriggeredAt);
           sb.append(",");
           sb.append(ae.Duration.getSeconds());
           sb.append("\n");
           
           pw.write(sb.toString());
           pw.close();
		   
		}
		if(event instanceof InstallEvent) {
		   InstallEvent ie = (InstallEvent) event;
		   sb.append(user);
           sb.append(",");
           sb.append(ie.IDESessionUUID);
           sb.append(",");
           sb.append("InstallEvent");
           sb.append(",");
           sb.append(ie.TriggeredAt);
           sb.append(",");
           sb.append(ie.Duration.getSeconds());
           sb.append("\n");
           
           pw.write(sb.toString());
           pw.close();
		}
		if(event instanceof CompletionEvent) {
		   CompletionEvent ce = (CompletionEvent) event;
		   sb.append(user);
           sb.append(",");
           sb.append(ce.IDESessionUUID);
           sb.append(",");
           sb.append("CompletionEvent");
           sb.append(",");
           sb.append(ce.TriggeredAt);
           sb.append(",");
           sb.append(ce.Duration.getSeconds());
           sb.append("\n");
           
           pw.write(sb.toString());
           pw.close();
		}
		if(event instanceof TestRunEvent) {
		   TestRunEvent tre = (TestRunEvent) event;
		   sb.append(user);
           sb.append(",");
           sb.append(tre.IDESessionUUID);
           sb.append(",");
           sb.append("TestRunEvent");
           sb.append(",");
           sb.append(tre.TriggeredAt);
           sb.append(",");
           sb.append(tre.Duration.getSeconds());
           sb.append("\n");
           
           pw.write(sb.toString());
           pw.close();
		}
		if(event instanceof UserProfileEvent) {
		  UserProfileEvent upe = (UserProfileEvent) event;
		  
		  sb.append(user);
          sb.append(",");
          sb.append(upe.IDESessionUUID);
          sb.append(",");
          sb.append("UserProfileEvent");
          sb.append(",");
          sb.append(upe.TriggeredAt);
          sb.append(",");
          sb.append(upe.Duration.getSeconds());
          sb.append("\n");
          
          pw.write(sb.toString());
          pw.close();
		}
		if(event instanceof VersionControlEvent) {
		  VersionControlEvent vce = (VersionControlEvent) event;
		  sb.append(user);
          sb.append(",");
          sb.append(vce.IDESessionUUID);
          sb.append(",");
          sb.append("VersionControlEvent");
          sb.append(",");
          sb.append(vce.TriggeredAt);
          sb.append(",");
          sb.append(vce.Duration.getSeconds());
          sb.append("\n");
		  
		}
		if(event instanceof WindowEvent) {
		  WindowEvent we = (WindowEvent) event;
		  sb.append(user);
          sb.append(",");
          sb.append(we.IDESessionUUID);
          sb.append(",");
          sb.append("WindowEvent");
          sb.append(",");
          sb.append(we.TriggeredAt);
          sb.append(",");
          sb.append(we.Duration.getSeconds());
          sb.append("\n");
		}
		if(event instanceof BuildEvent) {
		  BuildEvent be = (BuildEvent) event;
		  sb.append(user);
          sb.append(",");
          sb.append(be.IDESessionUUID);
          sb.append(",");
          sb.append("BuildEvent");
          sb.append(",");
          sb.append(be.TriggeredAt);
          sb.append(",");
          sb.append(be.Duration.getSeconds());
          sb.append("\n");
		}
		if(event instanceof DebuggerEvent) {
		  DebuggerEvent de = (DebuggerEvent) event;
		  sb.append(user);
          sb.append(",");
          sb.append(de.IDESessionUUID);
          sb.append(",");
          sb.append("DebuggerEvent");
          sb.append(",");
          sb.append(de.TriggeredAt);
          sb.append(",");
          sb.append(de.Duration.getSeconds());
          sb.append("\n");
		}
		if(event instanceof DocumentEvent) {
		  DocumentEvent de  = (DocumentEvent) event;
		  sb.append(user);
          sb.append(",");
          sb.append(de.IDESessionUUID);
          sb.append(",");
          sb.append("DocumentEvent");
          sb.append(",");
          sb.append(de.TriggeredAt);
          sb.append(",");
          sb.append(de.Duration.getSeconds());
          sb.append("\n");
		}
		if(event instanceof EditEvent) {
		  EditEvent ee = (EditEvent) event;
		  sb.append(user);
          sb.append(",");
          sb.append(ee.IDESessionUUID);
          sb.append(",");
          sb.append("EditEvent");
          sb.append(",");
          sb.append(ee.TriggeredAt);
          sb.append(",");
          sb.append(ee.Duration.getSeconds());
          sb.append("\n");
		}
		if(event instanceof FindEvent) {
		  FindEvent fe = (FindEvent) event;
		  sb.append(user);
          sb.append(",");
          sb.append(fe.IDESessionUUID);
          sb.append(",");
          sb.append("FindEvent");
          sb.append(",");
          sb.append(fe.TriggeredAt);
          sb.append(",");
          sb.append(fe.Duration.getSeconds());
          sb.append("\n");
		  
		}
		if(event instanceof IDEStateEvent) {
		  IDEStateEvent ide = (IDEStateEvent) event;
		  sb.append(user);
          sb.append(",");
          sb.append(ide.IDESessionUUID);
          sb.append(",");
          sb.append("IDEStateEvent");
          sb.append(",");
          sb.append(ide.TriggeredAt);
          sb.append(",");
          sb.append(ide.Duration.getSeconds());
          sb.append("\n");
		}
		if(event instanceof SolutionEvent) {
		  SolutionEvent se = (SolutionEvent) event;
		  sb.append(user);
          sb.append(",");
          sb.append(se.IDESessionUUID);
          sb.append(",");
          sb.append("SolutionEvent");
          sb.append(",");
          sb.append(se.TriggeredAt);
          sb.append(",");
          sb.append(se.Duration.getSeconds());
          sb.append("\n");
		}
		if(event instanceof UpdateEvent) {
		  UpdateEvent ue = (UpdateEvent) event;
		  sb.append(user);
          sb.append(",");
          sb.append(ue.IDESessionUUID);
          sb.append(",");
          sb.append("UpdateEvent");
          sb.append(",");
          sb.append(ue.TriggeredAt);
          sb.append(",");
          sb.append(ue.Duration.getSeconds());
          sb.append("\n");
		}
		
		if(event instanceof ErrorEvent) {
		    ErrorEvent ee = (ErrorEvent) event;
		     sb.append(user);
	          sb.append(",");
	          sb.append(ee.IDESessionUUID);
	          sb.append(",");
	          sb.append("ErrorEvent");
	          sb.append(",");
	          sb.append(ee.TriggeredAt);
	          sb.append(",");
	          sb.append(ee.Duration.getSeconds());
	          sb.append("\n");
		}
		if(event instanceof InfoEvent) {
		   
		    InfoEvent ie = (InfoEvent) event;
		     sb.append(user);
	          sb.append(",");
	          sb.append(ie.IDESessionUUID);
	          sb.append(",");
	          sb.append("InfoEvent");
	          sb.append(",");
	          sb.append(ie.TriggeredAt);
	          sb.append(",");
	          sb.append(ie.Duration.getSeconds());
	          sb.append("\n");
		}
		if(event instanceof NavigationEvent) {
            NavigationEvent ne = (NavigationEvent) event;
            sb.append(user);
            sb.append(",");
            sb.append(ne.IDESessionUUID);
            sb.append(",");
            sb.append("NavigationEvent");
            sb.append(",");
            sb.append(ne.TriggeredAt);
            sb.append(",");
            sb.append(ne.Duration.getSeconds());
            sb.append("\n");
        }
		if(event instanceof  SystemEvent) {
		    SystemEvent  se = (SystemEvent) event;
		     sb.append(user);
	          sb.append(",");
	          sb.append(se.IDESessionUUID);
	          sb.append(",");
	          sb.append("SystemEvent");
	          sb.append(",");
	          sb.append(se.TriggeredAt);
	          sb.append(",");
	          sb.append(se.Duration.getSeconds());
	          sb.append("\n");
		}
		
	     else {
			// there a many different event types to process, it is recommended
			// that you browse the package to see all types and consult the
			// website for the documentation of the semantics of each event...
	       sb.append(user);
           sb.append(",");
           sb.append("N/A");
           sb.append(",");
           sb.append("OTHER");
           sb.append(",");
           sb.append("N/A");
           sb.append(",");
           sb.append("N/A");
           sb.append("\n");
	     }
	}
}