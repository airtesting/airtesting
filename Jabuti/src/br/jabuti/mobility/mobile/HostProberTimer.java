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


public class HostProberTimer extends Thread
{
	private int sleepTime;
	private volatile boolean fim, resetFlag;
	
	public HostProberTimer(int t)
	{
		sleepTime = t;
		fim = false;
		resetFlag = true;
	}
	
	
	public void run()
	{
		while (! fim )
		{
			synchronized (this)
			{
				if (resetFlag)
				{
					resetFlag = false;
				}
				else
				{
					HostProber.dump();
				}
			}
			try
			{
				sleep(sleepTime);
			}
			catch (InterruptedException e)
			{
				reset();
			}
		}
	}
	
	synchronized public void stopTimer()
	{
		fim = true;
		reset();
	}

	synchronized public void reset()
	{
		resetFlag = true;
	}
	
	public int getTimeout()
	{
		return sleepTime;
	}
}
