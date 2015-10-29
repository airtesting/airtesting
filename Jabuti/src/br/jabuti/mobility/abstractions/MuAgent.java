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




package br.jabuti.mobility.abstractions;


import mucode.MuServer;
import br.jabuti.mobility.mobile.HostProber;

/** 
*	This is a re-implementation of <code>mucode.abstractions.MuAgent</code>.
*	It replaces that class for code instrumentation proposes. The comments
*	in this doc. reproduces the documentatio of that class
*/
public abstract class MuAgent extends mucode.abstractions.MuAgent 
{
  protected String hostDestino, projectName;
  protected int delay;
  static final public String runName = "jabutiRun";

  	public MuAgent(MuServer server) 
  	{ 
  		super(server);
		hostDestino = new String(HostProber.getHostDestino());
		projectName = new String(HostProber.getProjectName());
		delay = HostProber.getTimeout();
  	}

  	public MuAgent() 
  	{ 
  		super();
  	} 


	public void init(String host, String proj)
	{
		hostDestino = host;
		projectName = proj;
	}
 
 /* 	public void run()
  	{
  		System.out.println("Rodando agente " + hostDestino + projectName);
  		try
  		{
  			HostProber.init(hostDestino, projectName, delay);
  			HostProber.start();
  			jabutiRun();
  		}
  		finally
  		{
  			HostProber.stop();
  		}
  	}
  	
  	abstract public void jabutiRun();
*/
}

