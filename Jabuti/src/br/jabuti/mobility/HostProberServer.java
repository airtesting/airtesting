/*  Copyright 2003  Auri Marcelo Rizzo Vicenzi, Marcio Eduardo Delamaro, 			    Jose Carlos Maldonado

    This file is part of Jabuti.

    Jabuti is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as 
    published by the Free Software Foundation, either version 3 of the      
    License, or (at your option) any later version.

    Jabuti is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Jabuti.  If not, see <http://www.gnu.org/licenses/>.
*/


package br.jabuti.mobility;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ListIterator;

import mucode.MuConstants;
import mucode.MuServer;
import br.jabuti.mobility.mobile.HostProber;
import br.jabuti.probe.ProbedNode;


public class HostProberServer extends MuServer
{
	Hashtable activeAgents = null;
	Hashtable projectList = null;

	public HostProberServer()
	{
		projectList = new Hashtable();
		activeAgents = new Hashtable();
		addUbiquitousPackage("br.jabuti.mobility");
		addUbiquitousPackage("br.jabuti.mobility.mobile");
		addUbiquitousPackage("br.jabuti.probe");
	}

	synchronized public String getProject(String name)
	{
		return (String) projectList.get(name.toLowerCase());
	}
	
	synchronized public boolean addProject(String name, String file)
	{
		if ( projectList.containsKey(name.toLowerCase()) )
			return false;
		projectList.put(name.toLowerCase(), file);
		return true;
	}
	
	synchronized public void removeProject(String name)
	{
		projectList.remove(name.toLowerCase());
	}
	
	
	synchronized public String[] getProjectList()
	{
		return (String[]) projectList.keySet().toArray(new String[0]);
	}
	
	synchronized public String[] getFileList()
	{
		String[] key = getProjectList();
		String[] entry = new String[key.length];
		for (int i = 0; i < entry.length; i++)
		{
			entry[i] = (String) projectList.get(key[i]);
		}
		return entry;
	}
	
	synchronized public void startTraces(String host, long stamp, String proj)
	{
		DD("Received START_TRACES from " + host + "-" + stamp + "-" + proj);
		if ( getProject(proj) == null )
			return;
		String key = host+stamp+proj;
		if ( activeAgents.containsKey(key) )
			return;
		DD("This is the first START_TRACES for that agent");
		activeAgents.put(key, new Hashtable());
	}
	
	
	synchronized public void endTraces(String host, long stamp, String proj)
			throws FileNotFoundException, IOException
	{
		DD("Received END_TRACES from " + host + "-" + stamp + "-" + proj);
		if ( getProject(proj) == null )
			return;
		String key = host+stamp+proj;
		if ( ! activeAgents.containsKey(key) )
		{
			DD("Server does not contain registers for such an agent " );
			return;
		}
		Hashtable hs = (Hashtable) activeAgents.get(key);
		DD("Number of paths stored: " + hs.size());
		dump(host, proj, hs);
		activeAgents.remove(key);
	}
	
	synchronized public void addTraces(	String host, long stamp, 
										String proj, Hashtable traces)
	{
		DD("Received TRACE_REPORT from " + host + "-" + stamp + "-" + proj);
		if ( getProject(proj) == null )
			return;
		DD("Hashtable size: " + traces.size());

		String key = host+stamp+proj;
		if ( ! activeAgents.containsKey(key) )
		{
			DD("Server does not contain registers for such an agent " );
			return;
		}
		
		Hashtable hs = (Hashtable) activeAgents.get(key);
		DD("Number of paths stored: " + hs.size());

        Enumeration en = traces.keys();
        while (en.hasMoreElements()) {
            ProbedNode tr = (ProbedNode) en.nextElement();
            ArrayList ar = (ArrayList) hs.get(tr);
			if (ar == null)
			{
				ar = new ArrayList();
				hs.put(tr, ar);
			}
			ar.addAll((ArrayList) traces.get(tr));
        }
		
	}


	synchronized public void dump(String host, String proj, Hashtable traces)
			throws FileNotFoundException, IOException
	{
		DD("Request from " + "host" + " to dump " + proj);
		String filename = getProject(proj);
		if (filename == null)
		{
			throw new FileNotFoundException("Project file not registered in the server");
		}
		if (filename.toUpperCase().endsWith(".JBT") )
		{
			filename = filename.substring(0, filename.length()-4);
		}
		filename += ".trc";
		File fileProber = new File(filename);
		if ( fileProber.exists() && ! fileProber.canRead() )
		{
			throw new FileNotFoundException("Invalid file name");
		}
		
		DD("Dumping on file " + fileProber);
       	RandomAccessFile raf = new RandomAccessFile(fileProber, "rw");

        raf.seek(raf.length());
        FileOutputStream fos = new FileOutputStream(raf.getFD());

        PrintStream fp = new PrintStream(fos);

        Enumeration en = traces.keys();

        while (en.hasMoreElements()) {
            ProbedNode tr = (ProbedNode) en.nextElement();

            dumpNodes(fp, host, tr, (ArrayList) traces.get(tr));
        }
        // write a delimiter 
        fp.println(HostProber.delimiter);
		
		fp.close();
	}

    void dumpNodes(PrintStream fp, String host, 
     					ProbedNode pbdNode, ArrayList probedNodes) 
    {
        try {
           	fp.println(HostTraceReader.class.toString());
           	fp.println(); // linha em branco representa o nome do caso de teste
        	fp.println(host);
            fp.println(pbdNode.threadCode);
            fp.println(pbdNode.objectCode);
            fp.println(pbdNode.clazz);
            fp.println(pbdNode.metodo);
            ListIterator li = probedNodes.listIterator();
            while (li.hasNext()) {
                Object o = li.next();
                fp.println(o);
            }
            fp.println("-1");
        } catch (Exception e) {}
    }
    
    
/** <p>This method reads the arguments to start the <c>MuServer</c> that will
*	handle the incoming teste cases executed in other hosts. The arguments 
* 	can be: </p>
*	<UL> <LI> -projectfile <filename> : the name of a file that contain the name
*	on the project to be handled by this server. The file has multiple entries,
* 	each of then formed by 2 lines of the file. The first line has the name of
*	the project, and the seconde, the corresponding trace file
*	<LI> -port xxx : the port to attach the server
* 	<LI> -debug: turns the debuging messages on
*/
    static public void main(String[] args) throws Exception
    {
    	String port = "1988";
    	String projFiles = null;
		HostProberServer ms = new HostProberServer();   	
    	for (int i = 0; i < args.length; i += 2)
    	{
    		if (args[i].toLowerCase().equals("-port") )
    		{
    			port = args[i+1];
    		}
    		else
    		if (args[i].toLowerCase().equals("-projectfile") )
    		{
    			projFiles = args[i+1];
    		}
    		else
    		if (args[i].toLowerCase().equals("-debug") )
    		{
				ms.setProperty(MuConstants.DEBUGkey, "true");
				i--;
    		}
    		else
    		{
    			throw new IllegalArgumentException("Invalid argument");
    		}
    	}
    	if (projFiles == null)
    	{
    			throw new IllegalArgumentException("Project file name expected");
    	}
		ms.setProperty(MuConstants.PORTkey, port);
		System.out.println("Server will be started at port " + ms.getPort() + " debug: " +
				ms.isDebugOn());
		System.out.println("Projects that will be handled:");

    	File f = new File(projFiles);
		BufferedReader br = new BufferedReader(new FileReader(f)); 
		for (String s = br.readLine(); s != null; s = br.readLine())
		{
			if (s.trim().length() <= 0)
				continue;
			String x = br.readLine();
			if ( x == null)
				break;
			ms.addProject(s.trim(), x.trim());
			System.out.println("\tProject: " + s.trim() + " file: " + x.trim());
		}
		ms.addUbiquitousPackage("br.jabuti.mobility.*");
		ms.boot();
    }
    
    
    private void DD(String x)
    {
    	if ( Boolean.valueOf(properties.getProperty(DEBUGkey)).booleanValue())
    		System.out.println(x);
    }
}
