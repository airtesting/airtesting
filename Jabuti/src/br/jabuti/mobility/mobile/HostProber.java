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


package br.jabuti.mobility.mobile;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import mucode.MuServer;
import br.jabuti.mobility.mobile.agent.HostProberAgent;
import br.jabuti.probe.ProbedNode;


/** <p>This class implements a class to create 
 * using an agent to send the registered execution sequence. At 
 * itinialization it registers an object to be notified when the 
 * program exits. At that point, that object call 
 * {@link HostProber#dump}.
 */
public class HostProber {
	static HostProberAgent agent = null;
    static public final String delimiter = "**********************";
    static protected Hashtable threadsAndProbs = null;
    static private String hostDestino = null;
    static private String projectName = null;
	static private HostProberTimer timer = null;
	static private int nAgents;
	static private int delayDump;
	static private MuServer ms;
	static private String thisHost = null;
	static private boolean worked;
	static private long timestamp;


    static synchronized public void init(String host, 
    									String DelaProject, 
    									Integer timeout,
    									MuServer ms)
    {
    	init(host, DelaProject, timeout.intValue(), ms);
    }

	
    static synchronized public void init(String host, 
    									String DelaProject, 
    									int timeout,
    									MuServer aserver )
    {
		hostDestino = host;
		projectName = DelaProject;
		delayDump = timeout;
		// evita que qquer classe do pacote seja enviada ao destino.
		ms = new MuServer();
		ms.addUbiquitousPackage("br.jabuti.mobility");
		ms.addUbiquitousPackage("br.jabuti.mobility.mobile");
		ms.addUbiquitousPackage("br.jabuti.probe");
		try 
		{
			thisHost = InetAddress.getLocalHost().getHostName();
			if ( aserver == null )
				thisHost += ":client";
			else
				thisHost += ":"+aserver.getPort();
		}
		catch (UnknownHostException e)
		{
        	System.err.println("Cannot get local host name...");
			thisHost = "UNKNOWN";			
		}
		timestamp = (new Date()).getTime();
		threadsAndProbs = new Hashtable();
       	// register an object that will be notified if the program exits
       	Runtime.getRuntime().addShutdownHook(new HostProberHook());
    }

    
    static synchronized public void stop()
    {
    	dump();
    	if (-- nAgents == 0 )
    	{
    		timer.stopTimer();
    		timer = null;
			// send an agent to tell the server as agent has ended
	        worked = false;
			int cont = 10;
			do 
			{
				agent = new HostProberAgent(thisHost, timestamp,
											HostProberAgent.END_TRACES, 
											hostDestino, 
											projectName, 
											ms);
		        agent.start(); // send the agent to the server
		        try
		        {
		        	agent.join();
		        }
		        catch (InterruptedException e)
		        {;}
		     } while ( !worked && cont-- > 0);
		    // if it is not able to send after 10 tries, give it up
    	}
    }

    static synchronized public void start()
    {
    	nAgents++;
		if ( timer == null)
		{
			timer = new HostProberTimer(delayDump);
		//	timer.start();

		}
		else
		{
			timer.reset();
		}
		
		// send an agent to tell the server a new agent is been executed
        worked = false;
		int cont = 10;
		do 
		{
			agent = new HostProberAgent(thisHost, timestamp,
										HostProberAgent.
										START_TRACES, 
										hostDestino, 
										projectName, 
										ms);
	        agent.start(); // send the agent to the server
	        try
	        {
	        	agent.join();
	        }
	        catch (InterruptedException e)
	        {;}
	     } while ( !worked && cont-- > 0);
	    // if it is not able to send after 10 tries, give it up
	}

	static public String getHostDestino()
	{
		return hostDestino;
	}
	
	static public String getProjectName()
	{
		return projectName;
	}
	
	static public int getTimeout()
	{
		return delayDump;
	}

    /** This method registers the execution of a given node */
    static synchronized public void probe(Object o, 
    								String clazz, 
    								int metodo,
    								long nest, 
    								Object n) 
    {
		
		if (nAgents <= 0)
			return;
        Runnable tr = Thread.currentThread();
        String s = o == null ? "STATIC" : 
        						o.getClass().getName() + System.identityHashCode(o);
        ProbedNode pb = new ProbedNode(tr.toString(), s,
                clazz, metodo, "");
        ArrayList probedNodes;

        if (threadsAndProbs.containsKey(pb)) {
            probedNodes = (ArrayList) threadsAndProbs.get(pb);
        } else {
            probedNodes = new ArrayList();
            threadsAndProbs.put(pb, probedNodes);
        }
        probedNodes.add(nest+ ":" + n);
    }
	
	static synchronized public void probe(String clazz, int metodo, long nest, Object n) {
        probe(null, clazz, metodo, nest, n);
    }
    
    
    // is not synchronized, otherwise a deadlock happens
    static public void signalAgentSent()
    {
    	worked = true;
    }

    /** This method stores (for example, sending to a file) the 
     * registered execution up to that point */
    static synchronized public void dump() {
        if (nAgents <= 0 || threadsAndProbs.size() <= 0) 
        {
            return;
        }
        worked = false;
		agent = new HostProberAgent(thisHost, timestamp,
					threadsAndProbs, hostDestino, 
					projectName, ms);
        agent.start(); // send the agent to the server
        try
        {
        	agent.join();
        }
        catch (InterruptedException e)
        {;}
        if ( worked )
			threadsAndProbs = new Hashtable();
    }	

    static private long nestlevel = 0;
    
    static synchronized public long getNest()
    {
    	return nestlevel++; 
    }
}


class HostProberHook extends Thread {
    public void run() {
        HostProber.stop();
    }
}
