/**
 * 
 */
package evochecker.visualisation;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class CreateXYJFreeChart extends JFrame {

	int width = 640; //1440
	int height = 480;//1080
	String titleString;
	String algorithmString;
	String problemString;
	String wAxisLabel = null;
	String xAxisLabel = null;
	String yAxisLabel = null;
	JFreeChart chart = null;
	List<Integer> maxList;
	
	public CreateXYJFreeChart(String algorithmString, String problemString, List<Integer> maximiseList) {
		super("XY Scatter chart"); //Though we'll want a scatter plot
		this.algorithmString = algorithmString;
		this.problemString = problemString;
		maxList = maximiseList;
		JPanel chartPanel = createChartPanel();
		add(chartPanel, BorderLayout.CENTER);
		
		setSize(width, height);
		//setSize(1440, 1080);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
	
	
	/*
	 * In this method you comment in or out which axis you want below so you have two Axis uncommented for the relavent problem
	 * The problem is automatic so if you're running FX, DPM's axis wont interfere
	 * Once chosen the function produces a relavent dataset and chart
	 * As of right now this is hard-coded to three optimisation objectives however adaptation to more is easily plausible though
	 * outside the scope of the current goals.
	 */
	private JPanel createChartPanel() {

		if(this.problemString.equals("DPM")) {//DPM Optimisation axis in order
//			wAxisLabel = "Power Usage";
		    xAxisLabel = "Lost Messages";
		    yAxisLabel = "Queue Length";
		} else if(this.problemString.equals("FX")){//FX Optimisation axis in order
		    wAxisLabel = "Success";
//		    xAxisLabel = "Cost";
		    yAxisLabel = "Time";
		} else {
			throw new IllegalArgumentException("Undefined Problem");
		}
	    if(wAxisLabel == null & xAxisLabel != null & yAxisLabel != null) {
	    	maxList.remove(0);
	    	XYDataset dataset = createDataset(1, 2);
	    	chart = ChartFactory.createScatterPlot(titleString, xAxisLabel, yAxisLabel, dataset);
	    } else if (xAxisLabel == null & wAxisLabel != null & yAxisLabel != null) {
	    	maxList.remove(1);
	    	XYDataset dataset = createDataset(0, 2);
	    	chart = ChartFactory.createScatterPlot(titleString, wAxisLabel, yAxisLabel, dataset);
	    } else if (yAxisLabel == null & wAxisLabel != null & xAxisLabel != null) {
	    	maxList.remove(2);
	    	XYDataset dataset = createDataset(0, 1);
	    	chart = ChartFactory.createScatterPlot(titleString, wAxisLabel, xAxisLabel, dataset);
	    } else {
	    	throw new IllegalArgumentException("Must have exactly 2 axis at a time.");
	    }

	    saveMyChart(chart);
	    return new ChartPanel(chart);
	}
	
	/*
	 * this is a simple method to save the produces chart as a png depending on most recent run of specified algorithm and problem.
	 */
	private void saveMyChart(JFreeChart mychart) { //Saves your chart to a specified folder as a file 
		File imageFile = new File("charts/XYScatter_" + this.algorithmString+"_"+this.problemString + ".png");
		int savedImageWidth = 640;
		int savedImageHeight = 480;

		try {
		    ChartUtils.saveChartAsPNG(imageFile, mychart, savedImageWidth, savedImageHeight);
		} catch (IOException ex) {
		    System.err.println(ex);
		}
		
	}
	
	/*
	 * This method below Creates a dataset based on the values chosen as the axis in createChartPanel()
	 * We pass in the relavent values for the axis chosen, if maximise (like success) it converts it to a positive value
	 */
	private XYDataset createDataset(int axisA, int axisB) {
		String row;
        BufferedReader csvReader;
        int total = 0;
		
		titleString = "Pareto front scatter for " + this.algorithmString + " on " + this.problemString;
		XYSeriesCollection dataset = new XYSeriesCollection();
	    XYSeries series = new XYSeries(this.algorithmString+"_"+this.problemString);
		String myFilename = "data/FUN_" + this.algorithmString;
//        System.out.println(myFilename);
		
		try {
			csvReader = new BufferedReader(new FileReader(myFilename));
			while ((row = csvReader.readLine()) != null) {
	            String[] tempData = row.split(" ");
	            String valueX = tempData[axisA];
	            String valueY = tempData[axisB];
	            Double valueXd = Double.valueOf(valueX);
	            Double valueYd = Double.valueOf(valueY);
	            if(maxList.get(0) == 1) {
	            	valueXd = -valueXd;
	            }
	            if(maxList.get(1) == 1) {
	            	valueYd = -valueYd;
	            }
	            
//	            System.out.println(tempData[0] + " " + tempData[1] + " " + tempData[2]);
//	            System.out.println("(x, y) = " + "(" + valueX + ", " + valueY + ")");
	            series.add(valueXd, valueYd);
	            total ++;
	        }
	        csvReader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    //Queue Length vs. Lost Messages
//	    series.add(0.013, 8.149);
	    dataset.addSeries(series);
	 
	    return dataset;
	}
	
	
	public static void main(String[] args) {
		ArrayList<Integer> myList = new ArrayList<Integer>();
		myList.add(0);
		myList.add(0);
		myList.add(0);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new CreateXYJFreeChart("NSGAII", "DPM", myList).setVisible(true);
			}
		});
	}

}
