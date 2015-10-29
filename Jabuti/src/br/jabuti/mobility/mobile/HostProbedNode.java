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


/** <P>This class represents a node reached by the execution in a 
 given host. To characterize
 the node it is necessary to know <BR>
 <UL>
 <LI> the host where the code was executed
 <LI> the thread that executed the node
 <LI> the object executing the code
 <LI> the class and method of the node
 <LI> the node number
 </UL><BR>
 */

import br.jabuti.probe.ProbedNode;

public class HostProbedNode extends ProbedNode {
	
    /**
	 * Added to jdk1.5.0_04 compiler
	 */
	private static final long serialVersionUID = 1785140362905450583L;
	public String 	host;  // the host where the code executes
	
    public HostProbedNode(String h, 
                      String th, 
                      String ob, 
                      String cl, 
                      int mt, 
                      String n) 
   {
   		super(th, ob, cl, mt, n);
   		host = h;
    }
	
   public HostProbedNode(String h, ProbedNode pb) 
   {
   		super(pb.threadCode, 
   			  pb.objectCode,
   			  pb.clazz,
   			  pb.metodo,
   			  pb.node);
   		host = h;
    }

	public String getHost()
	{
		return host;
	}
		

    public boolean isSame(HostProbedNode x) {
        return host.equals(x.host) &&
        		super.equals(x);
    }
	
    public String toString() {
        return "<Host: " + host +
        		", Thread: " + threadCode + ", Object: " + objectCode
                + ", Class: " + clazz + ", Method: " + metodo + ", Node: "
                + node + ">";
    }
	
    public boolean equals(Object x) {
        if (!(x instanceof HostProbedNode)) {
            return false;
        }

        return super.equals(x);
    }
	
    public int hashCode() {
        return host.hashCode() + super.hashCode();
    }
		
}
