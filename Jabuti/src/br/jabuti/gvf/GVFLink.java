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


package br.jabuti.gvf;


/**
 * This class defines a link (an arc or branch) between two
 * Nodes in a graph within the Graph Visualization Framework.
 * It can be used directly or subclassed to include application
 * specific behavior.
 * 
 * @author Plinio Vilela
 * @version 1.0 Aug 2001
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.Vector;

import com.graphbuilder.curve.ControlPath;
import com.graphbuilder.curve.CubicBSpline;
import com.graphbuilder.curve.GroupIterator;
import com.graphbuilder.curve.Point;
import com.graphbuilder.curve.ShapeMultiPath;


public class GVFLink implements GVFDisplayable {

    private GVFNode ini;
    private GVFNode fin;

    boolean selected = false;

    Color selectedColor = Color.red;
    Color unselectedColor = Color.black;
    Color color;

    private Line2D line;

    private String label = new String("");

    private int x, y;
    
    private Vector points;

    public GVFLink(GVFNode n1, GVFNode n2) {
        ini = n1;
        fin = n2;
        points = new Vector();
        color = unselectedColor;
        line = new Line2D.Float((float) ini.getX(), (float) ini.getY(), (float) fin.getX(), (float) fin.getY());
    }

    public GVFNode getSourceNode() {
        return ini;
    }

    public GVFNode getDestinationNode() {
        return fin;
    }

    public void setLabel(String l) {
        label = new String(l);
    }

    public void moveTo(int x, int y) {}

    public void translate(int deltaX, int deltaY) {}

    public boolean itsMe(int x, int y) {
        updateLocation();
        return (((Line2D.Float) line).contains((double) x, (double) y));
    }

    public boolean itsMe(Rectangle r) {
        updateLocation();
        return (r.contains(((Line2D.Float) line).getBounds()));
    }

    public void draw(Graphics g) {
    	if ( points.size() <= 2)
    		return;
    	
        g.setColor( getColor() );
        
        ControlPath cp = new ControlPath();
        for (int i = 2; i < points.size(); i += 2)
        {
        	Integer t = (Integer) points.get(i);
        	int x0 = t.intValue();
        	t = (Integer) points.get(i+1);
        	int y0 = t.intValue();
            MyPoint pt = new MyPoint(x0+ADJUST_X, y0+ADJUST_Y);
            cp.addPoint(pt);
        }
        GroupIterator gi = new GroupIterator("0:n-1", cp.numPoints());
        // NaturalCubicSpline bs = new NaturalCubicSpline(cp, gi);
         CubicBSpline bs = new CubicBSpline(cp, gi);
        bs.setInterpolateEndpoints(true);
//        bs.setClosed(false);
        ShapeMultiPath mp = new ShapeMultiPath();
        bs.appendTo(mp);
        drawShape(g, mp);

        int i = points.size();
        Integer t = (Integer) points.get(i-2);
    	int x0 = t.intValue();
    	t = (Integer) points.get(i-1);
    	int y0 = t.intValue();
    	t = (Integer) points.get(0);
    	int x = t.intValue();
    	t = (Integer) points.get(1);
    	int y = t.intValue();
    	drawDirectedLine(g, x0+ADJUST_X, y0+ADJUST_Y, x+ADJUST_X, y+ADJUST_Y);
    }

    public void selected(boolean s) {
        selected = s;
        if (s) {
            color = selectedColor;
        } else {
            color = unselectedColor;
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean contains(int x, int y) {
        updateLocation();
        return line.contains((double) x, (double) y);
    }

    public String getLabel() {
        return label;
    }
    
    public Color getColor() {
    	if ( CFGFrame.showPrimary() )
        	return color;
        else
        	return CFGFrame.getBkColor();
    }

    private void updateLocation() {
        ((Line2D.Float) line).setLine((float) ini.getX(), (float) ini.getY(), (float) fin.getX(), (float) fin.getY());
    }

    public static final double theta = Math.toRadians(15); // arrowhead sharpness
    public static final int size = 10; // arrowhead length

    protected void drawDirectedLine(Graphics g, int x1, int y1, int x2, int y2) {
        int x3;
        int y3;
        int x4;
        int y4;
        double angle;
		
        // Drawing the line
        Graphics2D g2 = (Graphics2D) g;
        
        Stroke normalStroke = g2.getStroke();

        g2.setStroke(new BasicStroke(1.5F));
        
        g2.drawLine(x1, y1, x2, y2);
	
        // calculate points for arrowhead
        angle = Math.atan2(y2 - y1, x2 - x1) + Math.PI;
	
        x3 = (int) (x2 + Math.cos(angle - theta) * size);
        y3 = (int) (y2 + Math.sin(angle - theta) * size);
	
        x4 = (int) (x2 + Math.cos(angle + theta) * size);
        y4 = (int) (y2 + Math.sin(angle + theta) * size);
	
        // draw arrowhead
        g2.drawLine(x2, y2, x3, y3);
        g2.drawLine(x2, y2, x4, y4);
        g2.drawLine(x3, y3, x4, y4);
        
        g2.setStroke(normalStroke);        
    }

    protected void drawShape(Graphics g, Shape sh) {
		Graphics2D g2 = (Graphics2D) g;
        
        Stroke normalStroke = g2.getStroke();

        g2.setStroke(new BasicStroke(1.5F));
                
        g2.draw(sh);
        
        g2.setStroke(normalStroke);        
    }

    
    public void addPoint(int x, int y)
    {
    	points.add(new Integer(x));
    	points.add(new Integer(y));
    }
}

class MyPoint implements Point
{
double[] xy;

	public MyPoint(int a, int b)
	{
		this((double) a, (double) b);
	}
	
	
	public MyPoint(double d, double e) {
		xy = new double[2];
		xy[0] = d;
		xy[1] = e;
	}


	public void setLocation(double[] arg0) {
		xy[0] = arg0[0];
		xy[1] = arg0[1];
	}


	public double[] getLocation() {
		return xy;
	}
}

