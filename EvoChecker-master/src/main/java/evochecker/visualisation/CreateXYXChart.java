/**
 * 
 */
package evochecker.visualisation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.VectorGraphicsEncoder.VectorGraphicsFormat;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.internal.chartpart.ToolTips;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 * @author benjamin
 *
 */
public class CreateXYXChart {

//	List<Double> xData;
//	List<Double> yData;
	
    public void chartSaver() {
        double[] yData = new double[] { 2.0, 1.0, 0.0 };
        
        // Create Chart
        XYChart chart = new XYChart(500, 400);
        chart.setTitle("Sample Chart XChart");
        chart.setXAxisTitle("X");
        chart.setXAxisTitle("Y");
        XYSeries series = chart.addSeries("y(x)", null, yData);
        series.setMarker(SeriesMarkers.CIRCLE);
     
        try {
//	        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapFormat.PNG);
//	        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapFormat.JPG);
//	        BitmapEncoder.saveJPGWithQuality(chart, "./Sample_Chart_With_Quality.jpg", 0.95f);
//	        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapFormat.BMP);
//	        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapFormat.GIF);
//	     
//	        BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapFormat.PNG, 300);
//	        BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapFormat.JPG, 300);
//	        BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapFormat.GIF, 300);
	     
//	        VectorGraphicsEncoder.saveVectorGraphic(chart, "./Sample_Chart", VectorGraphicsFormat.EPS);
	        VectorGraphicsEncoder.saveVectorGraphic(chart, "./charts/XChart-Sample_Chart", VectorGraphicsFormat.PDF);
//	        VectorGraphicsEncoder.saveVectorGraphic(chart, "./Sample_Chart", VectorGraphicsFormat.SVG);
        } catch (FileNotFoundException e) { e.printStackTrace();
        } catch (IOException e) { e.printStackTrace(); }
    }
    
//    @Override
    public XYChart getChart() {
   
      // Create Chart
      XYChart chart = new XYChartBuilder().width(800).height(600).build();
      
      
      // Customize Chart
      chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
      chart.getStyler().setChartTitleVisible(false);
      chart.setTitle("Pareto Front NSGAII on DPM");
      chart.setXAxisTitle("Power Usage");
      chart.setYAxisTitle("Lost Messages");
      
      chart.getStyler().setLegendPosition(LegendPosition.InsideSW);
      chart.getStyler().setMarkerSize(16);
      
      chart.getToolTips();
      chart.getStyler().setToolTipsAlwaysVisible(true);
      
      
      // Series
      LinkedList<Double> xData = new LinkedList<Double>();
      LinkedList<Double> yData = new LinkedList<Double>();
      
      createDataset(0, 1, xData, yData);
      chart.addSeries("NSGAII_DPM", xData, yData);
      try {
		VectorGraphicsEncoder.saveVectorGraphic(chart, "./charts/XChart-NSGAII_DPM3", VectorGraphicsFormat.PDF);
		BitmapEncoder.saveBitmap(chart, "./charts/XChart-NSGAII_DPM3", BitmapFormat.PNG);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
//      String[] labels = new String[10];
//      labels[0] = "a";labels[1] = "b";labels[2] = "c";labels[3] = "d";labels[4] = "e";
//      labels[5] = "f";labels[6] = "g";labels[7] = "h";labels[8] = "i";labels[9] = "j";
//      XYSeries series = chart.addSeries("myname", xData, yData);
//      series.setToolTips(labels);
      
      return chart;
    }
    
    
    
	private void createDataset(int axisA, int axisB, LinkedList<Double> xData, LinkedList<Double> yData) {
		String row;
		BufferedReader csvReader;
       	int total = 0;
       	BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("data/FUN_NSGAII"));
	       	while (reader.readLine() != null) total++;
	       	reader.close();
		} catch (FileNotFoundException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
       	
//		xData = new double[total];
//      yData = new double[total];
		
        int counter = 0;
		try {
			csvReader = new BufferedReader(new FileReader("data/FUN_NSGAII"));
			while ((row = csvReader.readLine()) != null) {
	            String[] tempData = row.split(" ");
	            String valueX = tempData[axisA];
	            String valueY = tempData[axisB];
	            Double valueXd = Double.valueOf(valueX);
	            Double valueYd = Double.valueOf(valueY);
	            
//	            System.out.println(tempData[0] + " " + tempData[1] + " " + tempData[2]);
//	            System.out.println("(x, y) = " + "(" + valueX + ", " + valueY + ")");
	            xData.add(counter, valueXd);
	            yData.add(counter, valueYd);
	            counter++;
	        }
	        csvReader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    //Queue Length vs. Lost Messages
//	    series.add(0.013, 8.149);
	}
	
	
	
	
	public static void main(String[] args) {
		CreateXYXChart myChartObject = new CreateXYXChart();
//		myChartObject.createDataset(0, 1);
		
	    // Create Chart
//	    XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", myChartObject.xData, myChartObject.yData);
	 
	    // Show it
//	    new SwingWrapper(chart).displayChart();
	    
//	    myChartObject.chartSaver();
	    
	 // Show it
	    new SwingWrapper(myChartObject.getChart()).displayChart();
	}

}
