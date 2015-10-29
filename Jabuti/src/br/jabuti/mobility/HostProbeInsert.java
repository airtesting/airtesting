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


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Unknown;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

import br.jabuti.lookup.Program;
import br.jabuti.mobility.abstractions.MuAgent;
import br.jabuti.probe.DefaultProbeInsert;
import br.jabuti.verifier.InvalidInstructionException;
import br.jabuti.verifier.InvalidStackArgument;


/** This class is designed to insert probes on each 
 * node of certain classes in a given program
 */ 
public class HostProbeInsert extends DefaultProbeInsert {
	static public final String MUCODE_AGENT = "mucode.abstractions.MuAgent",
							   MY_AGENT_UTF8 = "br/jabuti/mobility/abstractions/MuAgent";
	static public final String 
	     JABUTI_HOST_INSTR_ATTRIBUTE = "JaBUTi Host Instrumented";

private Collection classList; // list of class to class instrument
/** The constructor.
     * @param p - The {@link br.jabuti.lookup.Program structure} that represents
     * the program to be instrumented
     * @param c - The list of classes to be instrumented. Each element
     * is a string with the complete name of the class
     * @param d - List of subclasses of MuAgent
     * @param server - indicates whether the instrumentation is done in the server
     * or in the client
     */
    public HostProbeInsert(Program p, 
    						Collection c, 
    						Collection d) 
    {
        super(p, c);
        classList = d;
    }



    public String getProbeClass() {
        return "br.jabuti.mobility.mobile.HostProber";
    }


    public Map instrument(int typeOfCFG)
            throws InvalidInstructionException,
            InvalidStackArgument {
        HashSet inst = new HashSet();

		Map mp = super.instrument(typeOfCFG);

        if (classList != null) {
            inst.addAll(classList);
        }
        
        Hashtable hs = new Hashtable();


		Iterator it = mp.keySet().iterator();		
		// para cada classe:
        while (it.hasNext()) 
        {
        	String className = (String) it.next();
        	JavaClass jv = (JavaClass) mp.get(className);
        	        	
        	// verifica se esta classe jah foi instrumentada por
        	// esse instrumentador
	        Attribute[] atr = jv.getAttributes();
	        boolean achou = false;
	        for (int j = 0;  j < atr.length && ! achou; j++)
	        {
	        	if ( atr[j] instanceof Unknown )
	        	{
	        		Unknown uatr = (Unknown) atr[j];
	        		byte[] b = uatr.getBytes();
	        		achou = Arrays.equals(b, 
	        		              JABUTI_HOST_INSTR_ATTRIBUTE.getBytes());
	        	}
	        }
	        
        	// se estah em inst eh pra instrumentar a classe
            if ( (! achou) && inst.contains(className)) 
            {
            	// chama a rotina que faz a instrumentacao
                jv = doClassInstrument( jv );
                jv = doMethodInstrument(jv);
                inst.remove(className);
        		hs.put(className, jv);
            } else { // coloca o codigo sem instrumentar
                hs.put(className, jv);
            }
        }
        return hs;
    }


    protected JavaClass doClassInstrument(JavaClass java_class)
            throws InvalidInstructionException,
            InvalidStackArgument {

        ClassGen cg = new ClassGen(java_class);	


		// procura a entrada da classe mucode.abstractions.MuAgent e 
		// substitui por mobility.abstractions.MuAgent
        ConstantPoolGen cp = cg.getConstantPool();
        int oldIndex = cp.lookupClass(MUCODE_AGENT);
        
        if (oldIndex >= 0)
        {
	        int newIndex = cp.addUtf8(MY_AGENT_UTF8);
	        ConstantClass oldConst = (ConstantClass) cp.getConstant(oldIndex);
	        
	        oldConst.setNameIndex(newIndex);
	        cp.setConstant(oldIndex, oldConst);
	        cg.setConstantPool(cp);
	    }	    
        int newIndex = cp.addUtf8(JABUTI_HOST_INSTR_ATTRIBUTE);
        Attribute atr = new Unknown(newIndex, 
        	JABUTI_HOST_INSTR_ATTRIBUTE.length(),
        	JABUTI_HOST_INSTR_ATTRIBUTE.getBytes(),
        	cp.getConstantPool() );
        cg.setConstantPool(cp);
        cg.addAttribute(atr);
        return (cg.getJavaClass());
	}


/* tentativamente substituido pelo metodo abaixo

    protected JavaClass doMethodInstrument(JavaClass java_class)
            throws InvalidInstructionException,
            InvalidStackArgument {

        ClassGen cg = new ClassGen(java_class);	
        ConstantPoolGen cp = cg.getConstantPool();

        Method[] methods = cg.getMethods();

	    for (int i = 0; i < methods.length; i++) {
			try
			{	    	
	                // only instrument the run() method
	            if (! methods[i].getName().equals("run") ||
	                ! methods[i].getSignature().equals("()V") ) {
	                    continue;
	            }
	            MethodGen mg = new MethodGen(methods[i], 
	                        cg.getClassName(),
	                        cp);
	            // InstructionHandle[] ih = mg.getInstructionList().getInstructionHandles();
	            ASMInstrumenter gi = new ASMInstrumenter(mg, cg, cp);
	  			
	
	            CFG gfc = new CFG(mg);
	
	//            gfc.releaseInstructionGraph(); // free some memory
				CFGNode entry = (CFGNode) gfc.getEntry();
				
	            InstructionHandle ih = entry.getStart();
	            
	            // inserir a instrumentacao para o metodo run
	            gi.insertBefore(ih, "aload_0  getfield " + "br.jabuti.mobility.abstractions.MuAgent " +
	   								" hostDestino \"Ljava/lang/String;\" "+ 
	   								"aload_0 getfield " + "br.jabuti.mobility.abstractions.MuAgent " +
	   								" projectName \"Ljava/lang/String;\" " +
	   								"aload_0 getfield " + "br.jabuti.mobility.abstractions.MuAgent " +
	   								" delay \"I\" " +
	   								"invokestatic br.jabuti.mobility.mobile.HostProber init " +
	   								        "\"(Ljava/lang/String;" + 
	   								        "Ljava/lang/String;" +
	   								        "I)V\"" +
	   								"invokestatic br.jabuti.mobility.mobile.HostProber start " +
	   								        "\"()V\""
	 							);            
				
	            methods[i] = mg.getMethod();
        	}
            catch (ParseException e) 
            { 
                System.err.println("Parser error " + e.getMessage());
            }
		}
        cg.setMethods(methods);
        return (cg.getJavaClass());
	}

*/
    protected JavaClass doMethodInstrument(JavaClass java_class)
    {
    	/* o trecho abaixo é inserido no inicio do metodo run no
    	 * agente sendo testado ele corresponde a:
    	 if ( hostDestino == null )
    	 {
    	 	hostDestino = HostProber.getHostDestino();
    	 	projectName = HostProber.getProjectName();
    	 	delay = HostProber.getTimeout();
    	 }
    	 else
    	 {	
    	 	HostProber.init(hostDestino, projectName, delay, getServer());
    	 }
    	 */
    	 	
    	String before  = 
    		" aload_0 " +
  			" getfield br.jabuti.mobility.abstractions.MuAgent hostDestino " +
  					"\"Ljava/lang/String;\" " + 
  			" ifnonnull label1 " +
    		" aload_0 " +
 			" invokestatic br.jabuti.mobility.mobile.HostProber " +
 			               "getHostDestino \"()Ljava/lang/String;\"" +
   			" putfield br.jabuti.mobility.abstractions.MuAgent hostDestino " +
  					"\"Ljava/lang/String;\" " + 
    		" aload_0 " +
   			" invokestatic br.jabuti.mobility.mobile.HostProber " +
   			               "getProjectName \"()Ljava/lang/String;\"" +
   			" putfield br.jabuti.mobility.abstractions.MuAgent projectName " +
  					"\"Ljava/lang/String;\" " + 
    		" aload_0 " +
   			" invokestatic br.jabuti.mobility.mobile.HostProber " +
   			               "getTimeout \"()I\"" +
   			" putfield br.jabuti.mobility.abstractions.MuAgent delay " +
  					"\"I\" " +
	   		"invokestatic br.jabuti.mobility.mobile.HostProber start \"()V\"" +
	   		" goto label2 " +
  			" label1: " +
    		" aload_0 " +
  			" getfield br.jabuti.mobility.abstractions.MuAgent hostDestino " +
  					"\"Ljava/lang/String;\" " + 
  			" aload_0 " +
  			" getfield br.jabuti.mobility.abstractions.MuAgent projectName " +
  					"\"Ljava/lang/String;\" " + 
  			" aload_0 " + 
  			" getfield br.jabuti.mobility.abstractions.MuAgent delay  \"I\" " + 
  			" aload_0 " +
  			"invokestatic mucode.MuServer getServer " +
  							"\"(Ljava/lang/Thread;)Lmucode/MuServer;\"" + 
	   		"invokestatic br.jabuti.mobility.mobile.HostProber init " +
					        "\"(Ljava/lang/String;" + 
					        "Ljava/lang/String;ILmucode/MuServer;)V\"" +
	   		"invokestatic br.jabuti.mobility.mobile.HostProber start \"()V\"" +
	   		" label2: nop ";
	   	String after = 
	   		"invokestatic br.jabuti.mobility.mobile.HostProber stop \"()V\"";


    	java_class = wrapMethod(java_class, "run", "()V", before, after);
    	return java_class;
    }


    protected JavaClass xdoMethodInstrument(JavaClass java_class)
            throws InvalidInstructionException,
            InvalidStackArgument {

        ClassGen cg = new ClassGen(java_class);	
        ConstantPoolGen cp = cg.getConstantPool();

        Method[] methods = cg.getMethods();

	    for (int i = 0; i < methods.length; i++) 
	    {
	            // only instrument the run() method
	        if (! methods[i].getName().equals("run") ||
	            ! methods[i].getSignature().equals("()V") ) {
	                continue;
	        }
	
	        MethodGen mg = new MethodGen(methods[i], 
	                    cg.getClassName(),
	                    cp);
	        mg.setName(MuAgent.runName);
				
	        methods[i] = mg.getMethod();
		}
        cg.setMethods(methods);
        return (cg.getJavaClass());
	}

}
