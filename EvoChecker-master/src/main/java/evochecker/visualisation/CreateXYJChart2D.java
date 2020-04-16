/**
 * 
 */
package evochecker.visualisation;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import javax.swing.JFrame;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import javafx.scene.chart.XYChart;


public class CreateXYJChart2D {

	int total = 0;

	
	public void ReadData(ITrace2D trace) {
		String row;
        BufferedReader csvReader;
               
        
		try {
			csvReader = new BufferedReader(new FileReader("data/FUN_NSGAII"));
			while ((row = csvReader.readLine()) != null) {
	            String[] tempData = row.split(" ");
	            String valueX = tempData[0];
	            String valueY = tempData[1];
	            Double valueXd = Double.valueOf(valueX);
	            Double valueYd = Double.valueOf(valueY);
	            
	            System.out.println(tempData[0] + " " + tempData[1] + " " + tempData[2]);
	            System.out.println("(x, y) = " + "(" + valueXd + ", " + valueYd + ")");
	            trace.addPoint(valueXd, valueYd);
	            
	            total ++;
	        }
	        csvReader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		graph.getData().add(series);

	}
	
	
	  
	  public static void main(String[]args){
//		//ChartCreator
//	    CreateXYJChart2D CreatorChart = new CreateXYJChart2D();
//		// Create a chart:  
//	    Chart2D chart = new Chart2D();
//	    // Create an ITrace: 
//	    ITrace2D trace = new Trace2DSimple(); 
//	    // Add the trace to the chart. This has to be done before adding points (deadlock prevention): 
//	    chart.addTrace(trace);
//	    // Add all points
//	    CreatorChart.ReadData(trace);
//	    
////	    
////	    Random random = new Random();
////	    for(int i=100;i>=0;i--){
////	      trace.addPoint(i,random.nextDouble()*10.0+i);
////	    }
////	    
//	    // Make it visible:
//	    // Create a frame.
//	    JFrame frame = new JFrame("Scatter Plot");
//	    // add the chart to the frame: 
//	    frame.getContentPane().add(chart);
//	    frame.setSize(640,480);
//	    // Enable the termination button [cross on the upper right edge]: 
//	    frame.addWindowListener(
//	        new WindowAdapter(){
//	          public void windowClosing(WindowEvent e){
//	              System.exit(0);
//	          }
//	        }
//	      );
//	    frame.setVisible(true);
	    
		//ChartCreator
		CreateXYJChart2D CreatorChart = new CreateXYJChart2D();
	   Chart2D test = new Chart2D();
	   JFrame frame = new JFrame("Chart2D- Debug");
	   frame.getContentPane().add(test);
	   frame.setSize(400,200);
	   frame.setVisible(true);
	   ITrace2D atrace = new Trace2DLtd(100);
	   test.addTrace(atrace);
	   CreatorChart.ReadData(atrace);

	    // Enable the termination button [cross on the upper right edge]: 
	    frame.addWindowListener(
	        new WindowAdapter(){
	          public void windowClosing(WindowEvent e){
	              System.exit(0);
	          }
	        }
	      );
	    frame.setVisible(true);
	   
	   }
}





