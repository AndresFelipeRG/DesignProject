/**
 * Copyright 2016 University of Zurich
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.util.Set;
import cc.kave.commons.model.events.ActivityEvent;
import cc.kave.commons.model.events.CommandEvent;
import cc.kave.commons.model.events.ErrorEvent;
import cc.kave.commons.model.events.IDEEvent;
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
import cc.kave.commons.model.ssts.ISST;
import cc.kave.commons.utils.io.IReadingArchive;
import cc.kave.commons.utils.io.ReadingArchive;
import cc.kave.commons.utils.io.json.JsonUtils;

/**
 * Simple example that shows how the interaction dataset can be opened, all
 * users identified, and all contained events deserialized.
 */
public class GettingStarted {

	private String eventsDir;
	static int commandEvents;
	static int activityEvents;
	static int testse;
	static int versione;
	static int windowe;
	static int builde;
	static int completionevents;
	static int systemevents;
	static int navigationevents;
	static int other;
	static int installevent;
	static int documente;
	static int debuggere;
	static int userprofile;
	static int ideee;
	static int editevent;
	static int finde;
	static int updatevent;
	static int solutionevent;
	static int errore;
	static int infoe;

	public GettingStarted(String eventsDir) {
		this.eventsDir = eventsDir;
	}

	public void run() {

		System.out.printf("looking (recursively) for events in folder %s\n", new File(eventsDir).getAbsolutePath());

		/*
		 * Each .zip that is contained in the eventsDir represents all events that we
		 * have collected for a specific user, the folder represents the first day when
		 * the user uploaded data.
		 */
		Set<String> userZips = IoHelper.findAllZips(eventsDir);
        System.out.println(userZips.size());
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
        sb.append("user");
        sb.append(",");
        sb.append("IDESessionUUID");
        sb.append(",");
        sb.append("Event");
        sb.append(",");
        sb.append("TriggeredAt");
        sb.append(",");
        
        sb.append("Duration");
        sb.append("\n");
        
        pw.write(sb.toString());
        pw.close();
		for (String userZip : userZips) {
		    if(userZip.compareTo("2016-09-26/100.zip") != 0) {
			System.out.printf("\n#### processing user zip: %s #####\n", userZip);
			processUserZip(userZip);}
		}
		System.out.println("Command events: "+ commandEvents);
		System.out.println("activity events: "+ activityEvents);
		System.out.println("test events: "+ testse);
		System.out.println("version cotnrol events: "+ versione);
		System.out.println("window events: "+ windowe);
		System.out.println("build events: "+ builde);
		System.out.println("completion events: "+ completionevents);
		System.out.println("system events: "+ systemevents);
		System.out.println("navigation events: "+ navigationevents);
		System.out.println("other events: "+ other);
		System.out.println("install events: "+ installevent);
		System.out.println("document events: "+ documente);
		System.out.println("debugger events: "+ debuggere);
		System.out.println("userprofile events: "+ userprofile);
		System.out.println("ideee events: "+ ideee);
		System.out.println("edit events: "+ editevent);
		System.out.println("find events: "+ finde);
		System.out.println("update events: "+ updatevent);
		System.out.println("solution events: "+ solutionevent);
		System.out.println("error events: "+ errore);
		System.out.println("info events: "+ infoe);
	  

	}

	private void processUserZip(String userZip) {
		int numProcessedEvents = 0;
		// open the .zip file ...
		
		try (IReadingArchive ra = new ReadingArchive(new File(eventsDir, userZip))) {
			// ... and iterate over content.
			// the iteration will stop after 200 events to speed things up.
			while (ra.hasNext() && (numProcessedEvents++ < 5000000)) {
				/*
				 * within the userZip, each stored event is contained as a single file that
				 * contains the Json representation of a subclass of IDEEvent.
				 */
				//IDEEvent e = ra.getNext(IDEEvent.class);
				String json = ra.getNextPlain();
                // .. and call the deserializer yourself.
                IIDEEvent e = JsonUtils.fromJson(json, IIDEEvent.class);
				// the events can then be processed individually
				//processEvent(e);
				
				process(e, userZip);
			}
		}
	}

	/*
	 * if you review the type hierarchy of IDEEvent, you will realize that several
	 * subclasses exist that provide access to context information that is specific
	 * to the event type.
	 * 
	 * To access the context, you should check for the runtime type of the event and
	 * cast it accordingly.
	 * 
	 * As soon as I have some more time, I will implement the visitor pattern to get
	 * rid of the casting. For now, this is recommended way to access the contents.
	 */
	private void processEvent(IDEEvent e) {

		if (e instanceof CommandEvent) {
			process((CommandEvent) e);
		} else if (e instanceof CompletionEvent) {
			process((CompletionEvent) e);
		} else {
			/*
			 * CommandEvent and Completion event are just two examples, please explore the
			 * type hierarchy of IDEEvent to find other types and review their API to
			 * understand what kind of context data is available.
			 * 
			 * We include this "fall back" case, to show which basic information is always
			 * available.
			 */
			processBasic(e);
		}

	}
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
         commandEvents+=1;
       
          CommandEvent ce = (CommandEvent) event;
          // ...and access the special context for this kind of event
       
          String name = new File(user).getName();
          String u_name = name.substring(0,name.lastIndexOf(".zip"));
          
          sb.append(u_name);
          sb.append(",");
          sb.append(ce.IDESessionUUID);
          sb.append(",");
          sb.append("CommandEvent");
          sb.append(",");
          sb.append(ce.TriggeredAt);
          sb.append(",");
          
          sb.append("N/A");
          sb.append("\n");
          
          pw.write(sb.toString());
          pw.close();
          return;
          //System.out.println(ce.CommandId);
      }
      if(event instanceof ActivityEvent) {
         ActivityEvent ae = (ActivityEvent) event;
         String name = new File(user).getName();
         String u_name = name.substring(0,name.lastIndexOf(".zip"));
         activityEvents+=1;
         sb.append(u_name);
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
         return;
         
      }
      if(event instanceof InstallEvent) {
         InstallEvent ie = (InstallEvent) event;
         String name = new File(user).getName();
         String u_name = name.substring(0,name.lastIndexOf(".zip"));
         installevent+=1;
         sb.append(u_name);
         sb.append(",");
         sb.append(ie.IDESessionUUID);
         sb.append(",");
         sb.append("InstallEvent");
         sb.append(",");
         sb.append(ie.TriggeredAt);
         sb.append(",");
         sb.append("N/A");
         sb.append("\n");
         
         pw.write(sb.toString());
         pw.close();
         return;
      }
      if(event instanceof CompletionEvent) {
         CompletionEvent ce = (CompletionEvent) event;
         String name = new File(user).getName();
         String u_name = name.substring(0,name.lastIndexOf(".zip"));
         completionevents+=1;
         sb.append(u_name);
         sb.append(",");
         sb.append(ce.IDESessionUUID);
         sb.append(",");
         sb.append("CompletionEvent");
         sb.append(",");
         sb.append(ce.TriggeredAt);
         sb.append(",");
         sb.append("N/A");
         sb.append("\n");
         
         pw.write(sb.toString());
         pw.close();
         return;
      }
      if(event instanceof TestRunEvent) {
         TestRunEvent tre = (TestRunEvent) event;
         String name = new File(user).getName();
         String u_name = name.substring(0,name.lastIndexOf(".zip"));
         testse+=1;
         sb.append(u_name);
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
         return;
      }
      if(event instanceof UserProfileEvent) {
        UserProfileEvent upe = (UserProfileEvent) event;
        
        String name = new File(user).getName();
        String u_name = name.substring(0,name.lastIndexOf(".zip"));
        userprofile+=1;
        sb.append(u_name);
        sb.append(",");
        sb.append(upe.IDESessionUUID);
        sb.append(",");
        sb.append("UserProfileEvent");
        sb.append(",");
        sb.append(upe.TriggeredAt);
        sb.append(",");
        sb.append("N/A");
        sb.append("\n");
        
        pw.write(sb.toString());
        pw.close();
        return;
      }
      if(event instanceof VersionControlEvent) {
        VersionControlEvent vce = (VersionControlEvent) event;
        String name = new File(user).getName();
        String u_name = name.substring(0,name.lastIndexOf(".zip"));
        versione+=1;
        sb.append(u_name);
        sb.append(",");
        sb.append(vce.IDESessionUUID);
        sb.append(",");
        sb.append("VersionControlEvent");
        sb.append(",");
        sb.append(vce.TriggeredAt);
        sb.append(",");
        sb.append("N/A");
        sb.append("\n");

        pw.write(sb.toString());
        pw.close();
        return;
        
      }
      if(event instanceof WindowEvent) {
        WindowEvent we = (WindowEvent) event;
        String name = new File(user).getName();
        String u_name = name.substring(0,name.lastIndexOf(".zip"));
        windowe+=1;
        sb.append(u_name);
        sb.append(",");
        sb.append(we.IDESessionUUID);
        sb.append(",");
        sb.append("WindowEvent");
        sb.append(",");
        sb.append(we.TriggeredAt);
        sb.append(",");
        sb.append("N/A");
        sb.append("\n");

        pw.write(sb.toString());
        pw.close();
        return;
      }
      if(event instanceof BuildEvent) {
        BuildEvent be = (BuildEvent) event;
        String name = new File(user).getName();
        String u_name = name.substring(0,name.lastIndexOf(".zip"));
        builde+=1;
        sb.append(u_name);
        sb.append(",");
        sb.append(be.IDESessionUUID);
        sb.append(",");
        sb.append("BuildEvent");
        sb.append(",");
        sb.append(be.TriggeredAt);
        sb.append(",");
        sb.append(be.Duration.getSeconds());
        sb.append("\n");

        pw.write(sb.toString());
        pw.close();
        return;
      }
      if(event instanceof DebuggerEvent) {
        DebuggerEvent de = (DebuggerEvent) event;
        String name = new File(user).getName();
        String u_name = name.substring(0,name.lastIndexOf(".zip"));
        debuggere+=1;
        sb.append(u_name);
        sb.append(",");
        sb.append(de.IDESessionUUID);
        sb.append(",");
        sb.append("DebuggerEvent");
        sb.append(",");
        sb.append(de.TriggeredAt);
        sb.append(",");
        sb.append("N/A");
        sb.append("\n");

        pw.write(sb.toString());
        pw.close();
        return;
      }
      if(event instanceof DocumentEvent) {
        DocumentEvent de  = (DocumentEvent) event;
        String name = new File(user).getName();
        String u_name = name.substring(0,name.lastIndexOf(".zip"));
        documente+=1;
        sb.append(u_name);
        sb.append(",");
        sb.append(de.IDESessionUUID);
        sb.append(",");
        sb.append("DocumentEvent");
        sb.append(",");
        sb.append(de.TriggeredAt);
        sb.append(",");
        sb.append("N/A");
        sb.append("\n");

        pw.write(sb.toString());
        pw.close();
        return;
      }
      if(event instanceof EditEvent) {
        EditEvent ee = (EditEvent) event;
        String name = new File(user).getName();
        String u_name = name.substring(0,name.lastIndexOf(".zip"));
        editevent+=1;
        sb.append(u_name);
        sb.append(",");
        sb.append(ee.IDESessionUUID);
        sb.append(",");
        sb.append("EditEvent");
        sb.append(",");
        sb.append(ee.TriggeredAt);
        sb.append(",");
        sb.append(ee.Duration.getSeconds());
        sb.append("\n");

        pw.write(sb.toString());
        pw.close();
        return;
      }
      if(event instanceof FindEvent) {
        FindEvent fe = (FindEvent) event;
        String name = new File(user).getName();
        String u_name = name.substring(0,name.lastIndexOf(".zip"));
        finde+=1;
        sb.append(u_name);
        sb.append(",");
        sb.append(fe.IDESessionUUID);
        sb.append(",");
        sb.append("FindEvent");
        sb.append(",");
        sb.append(fe.TriggeredAt);
        sb.append(",");
        sb.append("N/A");
        sb.append("\n");

        pw.write(sb.toString());
        pw.close();
        return;
      }
      if(event instanceof IDEStateEvent) {
        IDEStateEvent ide = (IDEStateEvent) event;
        String name = new File(user).getName();
        String u_name = name.substring(0,name.lastIndexOf(".zip"));
        ideee+=1;
        sb.append(u_name);
        sb.append(",");
        sb.append(ide.IDESessionUUID);
        sb.append(",");
        sb.append("IDEStateEvent");
        sb.append(",");
        sb.append(ide.TriggeredAt);
        sb.append(",");
        sb.append("N/A");
        sb.append("\n");

        pw.write(sb.toString());
        pw.close();
        return;
      }
      if(event instanceof SolutionEvent) {
        SolutionEvent se = (SolutionEvent) event;
        String name = new File(user).getName();
        String u_name = name.substring(0,name.lastIndexOf(".zip"));
        solutionevent +=1;
        sb.append(u_name);
        sb.append(",");
        sb.append(se.IDESessionUUID);
        sb.append(",");
        sb.append("SolutionEvent");
        sb.append(",");
        sb.append(se.TriggeredAt);
        sb.append(",");
        sb.append("N/A");
        sb.append("\n");

        pw.write(sb.toString());
        pw.close();
        return;
      }
      if(event instanceof UpdateEvent) {
        UpdateEvent ue = (UpdateEvent) event;
        String name = new File(user).getName();
        String u_name = name.substring(0,name.lastIndexOf(".zip"));
        updatevent+=1;
        sb.append(u_name);
        sb.append(",");
        sb.append(ue.IDESessionUUID);
        sb.append(",");
        sb.append("UpdateEvent");
        sb.append(",");
        sb.append(ue.TriggeredAt);
        sb.append(",");
        sb.append("N/A");
        sb.append("\n");

        pw.write(sb.toString());
        pw.close();
        return;
      }
      
      if(event instanceof ErrorEvent) {
          ErrorEvent ee = (ErrorEvent) event;
          String name = new File(user).getName();
          String u_name = name.substring(0,name.lastIndexOf(".zip"));
          errore+=1;
          sb.append(u_name);
            sb.append(",");
            sb.append(ee.IDESessionUUID);
            sb.append(",");
            sb.append("ErrorEvent");
            sb.append(",");
            sb.append(ee.TriggeredAt);
            sb.append(",");
            sb.append("N/A");
            sb.append("\n");

            pw.write(sb.toString());
            pw.close();
            return;
      }
      if(event instanceof InfoEvent) {
         
          InfoEvent ie = (InfoEvent) event;
          String name = new File(user).getName();
          String u_name = name.substring(0,name.lastIndexOf(".zip"));
          infoe+=1;
          sb.append(u_name);
            sb.append(",");
            sb.append(ie.IDESessionUUID);
            sb.append(",");
            sb.append("InfoEvent");
            sb.append(",");
            sb.append(ie.TriggeredAt);
            sb.append(",");
            sb.append("N/A");
            sb.append("\n");

            pw.write(sb.toString());
            pw.close();
            return;
      }
      if(event instanceof NavigationEvent) {
          NavigationEvent ne = (NavigationEvent) event;
          String name = new File(user).getName();
          String u_name = name.substring(0,name.lastIndexOf(".zip"));
          navigationevents+=1;
          sb.append(u_name);
          sb.append(",");
          sb.append(ne.IDESessionUUID);
          sb.append(",");
          sb.append("NavigationEvent");
          sb.append(",");
          sb.append(ne.TriggeredAt);
          sb.append(",");
          sb.append("N/A");
          sb.append("\n");

          pw.write(sb.toString());
          pw.close();
          return;
      }
      if(event instanceof  SystemEvent) {
          SystemEvent  se = (SystemEvent) event;
          String name = new File(user).getName();
          String u_name = name.substring(0,name.lastIndexOf(".zip"));
          systemevents+=1;
          sb.append(u_name);
            sb.append(",");
            sb.append(se.IDESessionUUID);
            sb.append(",");
            sb.append("SystemEvent");
            sb.append(",");
            sb.append(se.TriggeredAt);
            sb.append(",");
            sb.append("N/A");
            sb.append("\n");

            pw.write(sb.toString());
            pw.close();
            return;
      }
      
       else {
          // there a many different event types to process, it is recommended
          // that you browse the package to see all types and consult the
          // website for the documentation of the semantics of each event...
         String name = new File(user).getName();
         String u_name = name.substring(0,name.lastIndexOf(".zip"));
         other+=1;
         sb.append(u_name);
         sb.append(",");
         sb.append("N/A");
         sb.append(",");
         sb.append(event.getClass().getSimpleName());
         sb.append(",");
         sb.append(event.getTriggeredAt());
         sb.append(",");
         sb.append("N/A");
         sb.append("\n");

         pw.write(sb.toString());
         pw.close();
       }}
	private void process(CommandEvent ce) {
		System.out.printf("found a CommandEvent (id: %s)\n", ce.getCommandId());
	}

	private void process(CompletionEvent e) {
		ISST snapshotOfEnclosingType = e.context.getSST();
		String enclosingTypeName = snapshotOfEnclosingType.getEnclosingType().getFullName();

		System.out.printf("found a CompletionEvent (was triggered in: %s)\n", enclosingTypeName);
	}

	private void processBasic(IDEEvent e) {
		String eventType = e.getClass().getSimpleName();
		ZonedDateTime triggerTime = e.getTriggeredAt();

		System.out.printf("found an %s that has been triggered at: %s)\n", eventType, triggerTime);
	}
}
