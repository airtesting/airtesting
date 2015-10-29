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


package br.jabuti.mobility.mobile.agent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

import mucode.MuServer;
import mucode.abstractions.MuAgent;
import br.jabuti.mobility.HostProberServer;
import br.jabuti.mobility.mobile.HostProber;

public class HostProberAgent extends MuAgent
{
	/**
	 * Added to jdk1.5.0_04 compiler
	 */
	private static final long serialVersionUID = 5213032578170757706L;
	String host, destino, DelaProject;
	long timeStamp;
	Hashtable hs;
	boolean migrated;
	private int op;
	static final public int TRACE_REPORT = 0,
							START_TRACES = 1,
							END_TRACES = 2;
	
	
  	public HostProberAgent(String host, long stamp,
  							Hashtable traces,
  	                       	String destino, String proj,
  	                       	MuServer ms) 
  	{
     	super(ms);
     	this.host = host;
     	this.destino = destino;
     	DelaProject = proj;
     	hs = traces;
     	migrated = false;
     	op = TRACE_REPORT;
     	timeStamp = stamp;
  	}

  	public HostProberAgent(String host, long stamp, int oper,
  	                       String destino, String proj,
  	                       MuServer ms) 
  	{
     	super(ms);
     	this.host = host;
     	this.destino = destino;
     	DelaProject = proj;
     	hs = null;
     	migrated = false;
     	op = oper;
     	timeStamp = stamp;
  	}


  	public HostProberAgent() { super(); }  

  	public void run() 
  	{
  		if ( ! migrated )
  		{
  			boolean ok = true;
			try 
			{
				migrated = true;
				go(destino);
			}
			catch (Exception e)
			{
				System.err.println(e.getClass().getName() + " reason: " +
					e.getMessage());
				e.printStackTrace();
				System.err.println("Could not send data to " + destino +
					". Check the reason...");
				System.err.println(this);
				ok = false;
			}	  			
			finally
			{
				if (ok) HostProber.signalAgentSent();
			}
  		}
  		else
  		{
  			try
  			{
  				HostProberServer hps = (HostProberServer) MuServer.getServer();
  				
  				// verifica qual o tipo de operacao o agente quer realizar
  				switch (op)
  				{
  					case START_TRACES: // inicio da execucao de um agente
  						hps.startTraces(host, timeStamp, DelaProject);
  						break;
  					case TRACE_REPORT: // chegada de uma porcao do trace de um agente
		  				hps.addTraces(host, timeStamp, DelaProject, hs);
  						break;
  					case END_TRACES: // final da execucao de todos os agentes em um server
  						hps.endTraces(host, timeStamp, DelaProject);
  						break;
  				}
  				
  			}
  			catch (FileNotFoundException e)
  			{
  				System.err.println(e.getClass().getName() + " " + e.getMessage());
  			}
  			catch (IOException e)
  			{
  				System.err.println(e.getClass().getName() + " " + e.getMessage());
  			}
  		}
  	}
  	
  	public String toString()
  	{
  		String s = "";
  		switch (op)
  		{
  			case START_TRACES:
  				s = "START_TRACES" ;
  				break;
  			case TRACE_REPORT:
  				s = "TRACE_REPORT" ;
  				break;
  			case END_TRACES:
  				s = "END_TRACES" ;
  				break;
  		}
  		return this.getClass().getName() + ":" + s + " " + host + 
  		 		" " + timeStamp + " " + destino + " " + DelaProject;
  	}
}
