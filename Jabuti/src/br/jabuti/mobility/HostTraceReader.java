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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import br.jabuti.mobility.mobile.HostProbedNode;
import br.jabuti.mobility.mobile.HostProber;
import br.jabuti.probe.DefaultProber;
import br.jabuti.probe.DefaultTraceReader;
import br.jabuti.probe.ProbedNode;
import br.jabuti.util.Debug;


/** This class reads a file stored by the {@link HostProber} class.
 */

public class HostTraceReader extends DefaultTraceReader {
	
    /**
	 * Added to jdk1.5.0_04 compiler
	 */
	private static final long serialVersionUID = 8350874638388323945L;
	public HostTraceReader(File f) throws IOException, FileNotFoundException {
    	super(f);
    }
	
    protected HostTraceReader() {
        super();
    }
	
/*    protected int readPaths() {
        paths = null;
        if (br == null) {
            return 0;
        }
        paths = new Hashtable();
        int k = 0;
		Object v[] = null;

        do {
        	try 
        	{
		       	String tipo = br.readLine();
		       	if (tipo.equals(DefaultProber.delimiter)) {
       	           break;
    		   	}
        		tcName = br.readLine(); // le nome do caso de teste
		       	if ( tipo.equals(DefaultTraceReader.class.toString() ) )
		       	{	
//        			v = super.readOnePath();
        			v[0] = new HostProbedNode("localhost", (ProbedNode) v[0]);
                }
                else
		       	if ( tipo.equals(this.getClass().toString() ) )
		       	{	
        			v = readOnePath();
                }
               	if ( v != null) paths.put((ProbedNode) v[0], (String[][])v[1]);
            } catch (Exception e) { 
              Debug.D("FINAL TRACE: (" + k + ") " + e + ""); 
                paths = null;
                return 0;
            }
        } while (v != null );
		
        Enumeration en = paths.keys();

        while (en.hasMoreElements()) {
            ProbedNode pdn = (ProbedNode) en.nextElement();
//            ArrayList arl = (ArrayList) paths.get(pdn);
            ArrayList v2 = new ArrayList();

//            getSinglePath(v2, arl); 
            paths.put(pdn, (String[][])v2.toArray(new String[v2.size()][]));
        }
        return paths.size();
    }
*/	
	
    protected Object[] readOnePath() throws Exception
    {
       	String host = br.readLine();
       	String thrd = br.readLine();
        String obj = br.readLine();
        String claz = br.readLine();
        String meth = br.readLine();

        ProbedNode pdn = new HostProbedNode( host,
                	    thrd, obj, claz,
                        Integer.parseInt(meth), "");

        ArrayList arl = new ArrayList();
		
        String nodeNumber = br.readLine();

        while (!nodeNumber.equals("-1")) {
                   arl.add(nodeNumber);
                   nodeNumber = br.readLine();
        }
        Object[] v = new Object[]  {pdn, arl};
        return v;
    }
	
	/** This method takes a ProbedNode and a path ina String[] form and 
	* returns the string correspondent to that path
	*/
	static String separator = new String("@");
    static public String xsaveToString(HostProbedNode head, String [] path)
    {
    	String s = HostTraceReader.class + separator;
    	s += head.host + separator;
    	s += head.threadCode + separator;
    	s += head.objectCode + separator;
    	s += head.clazz + separator;
    	s += head.metodo + separator;
    	
    	for (int i = 0; i < path.length; i++)
    	{
    	    System.out.println("Tamanho do path: " + i + " " + path.length);
    	    System.out.println("Tamanho do string: " + s.length() + " " + path[i]);
    		s += path[i] + separator;
    	}
    	s += -1 + separator;
    	
    	return s;
    }
	
	/** This method takes an string and returns a Vector where the firsts
	* element is a ProbedNode object and the second a String[] object with 
	* a path for that ProbedNode
	*/
	static public Vector xloadFromString(String x)
	{
		String s = new String(x);
		int k = s.indexOf(separator,0);
		s = s.substring(k+1);
		
		k = s.indexOf(separator);
		String host = s.substring(0, k);
		s = s.substring(k+1);		
		
		k = s.indexOf(separator);
		String thread = s.substring(0, k);
		s = s.substring(k+1);

		k = s.indexOf(separator);
		String obj = s.substring(0, k);
		s = s.substring(k+1);

		k = s.indexOf(separator);
		String clazz = s.substring(0, k);
		s = s.substring(k+1);

		k = s.indexOf(separator);
		String metodo = s.substring(0, k);
		s = s.substring(k+1);
		
		k = s.indexOf(separator);
		String no = s.substring(0, k);
		s = s.substring(k+1);
		
        ProbedNode pdn = new HostProbedNode( host,
                	    thread, obj, clazz,
                        Integer.parseInt(metodo), "");
		Vector v = new Vector();
		while ( ! no.equals("-1") )
		{
			v.add(no);
			k = s.indexOf(separator, 0);
			no = s.substring(0, k);
			s = s.substring(k+1);
		}
		
		Vector r = new Vector();
		r.add(pdn);
		r.add(v.toArray(new String[0]));
		return r;
	}
	
    protected ProbedNode getNodeFromRegistro(String registro) {
    	return new HostProbedNode("localhost", super.getNodeFromRegistro(registro));
    }
    
	
}
