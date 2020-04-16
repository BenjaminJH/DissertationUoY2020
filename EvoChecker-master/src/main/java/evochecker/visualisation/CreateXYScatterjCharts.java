///**
// * 
// */
package evochecker.visualisation;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//
//
//import java.awt.Color;
//import java.awt.Paint;
//import java.awt.Shape;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//
//import org.krysalis.jcharts.encoders.*;
//import org.krysalis.jcharts.encoders.ServletEncoderHelper;
//
//import javax.servlet.ServletOutputStream;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.krysalis.jcharts.axisChart.AxisChart;
//import org.krysalis.jcharts.chartData.AxisChartDataSet;
//import org.krysalis.jcharts.chartData.ChartDataException;
//import org.krysalis.jcharts.chartData.DataSeries;
//import org.krysalis.jcharts.encoders.PNGEncoder;
//import org.krysalis.jcharts.imageMap.ImageMap;
//import org.krysalis.jcharts.properties.AxisProperties;
//import org.krysalis.jcharts.properties.ChartProperties;
//import org.krysalis.jcharts.properties.LegendProperties;
//import org.krysalis.jcharts.properties.PointChartProperties;
//import org.krysalis.jcharts.properties.PropertyException;
//import org.krysalis.jcharts.test.TestDataGenerator;
//import org.krysalis.jcharts.types.ChartType;
//
//import evochecker.auxiliary.Utility;

/**
 * @author benjamin
 *
 */
public class CreateXYScatterjCharts {
	
//	private Socket[] socket = null;
//	private PrintWriter[] out;
//	private BufferedReader[] in;
//
//	public static final String PNG_MIME_TYPE = "myChart/png";
	
	public static void main(String[] args) {
		
		
		// TODO Auto-generated method stub
//		String[] xAxisLabels= { "1998", "1999", "2000", "2001", "2002", "2003", "2004" };
//		String xAxisTitle= "Years";
//		String yAxisTitle= "Problems";
//		String title= "Micro$oft at Work";
//		DataSeries dataSeries = new DataSeries( xAxisLabels, xAxisTitle, yAxisTitle, title );
//
//		double[][] data= new double[][]{ { 250, 45, -36, 66, 145, 80, 55 }, { 150, 15, 6, 62, -54, 10, 84 } }; String[] legendLabels= { "Bugs", "Security Holes" };
//		Paint[] paints= TestDataGenerator.getRandomPaints( 2 );
//
//		Shape[] shapes= { PointChartProperties.SHAPE_DIAMOND, PointChartProperties.SHAPE_TRIANGLE };
//		boolean[] fillPointFlags= { true, true };
//		Paint[] outlinePaints= { Color.black, Color.black };
//		PointChartProperties pointChartProperties= new PointChartProperties( shapes, fillPointFlags, outlinePaints );
//
//		AxisChartDataSet axisChartDataSet = null;
//		while (axisChartDataSet == null){
//			try {
//				axisChartDataSet = new AxisChartDataSet( data, legendLabels, paints, ChartType.POINT, pointChartProperties );
//			} catch (ChartDataException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		dataSeries.addIAxisPlotDataSet( axisChartDataSet  );
//
//		ChartProperties chartProperties= new ChartProperties();
//		AxisProperties axisProperties= new AxisProperties();
//		LegendProperties legendProperties= new LegendProperties();
//
//		AxisChart axisChart= new AxisChart( dataSeries, chartProperties, axisProperties, legendProperties, 500, 300 );
//		
//		File file = new File(PNG_MIME_TYPE);
//
//		
////		ImageMap imageMap= axisChart.getImageMap();
////		HttpServletRequest.setAttribute( ChartServlet.IMAGE_MAP, imageMap );
//////		HttpServletResponse
//		
//		
////		String params[] = new String[4];
////		params[0] = Utility.getProperty("JVM");
////		params[1] = "-jar";
////		params[2] = "res/executor.jar";
////		int portNum = 8880;
////		boolean isAlive = false;
////		System.out.println("Starting PRISM server " + portNum);
////		params[3] = String.valueOf(portNum);
////		String sPortNum = String.valueOf(portNum);
////		makeConnection(portNum, 1, params);
//		
////		ServletResponse myobject;
////		ServletOutputStream Stream = myobject.getOutputStream();
////		PNGEncoder.encode( axisChart, Stream ); 
//		
////		ServletOutputStream stream = new ServletOutputStream(file);
//		// http://127.0.0.1:8080/jChartsServletExamples/index.html
////		System.setProperty("java.awt.headless","true");
////		OutputStream out = new FileOutputStream("myChart.png");
//		
////		
////		
////		httpServletResponse.setContentType( PNG_MIME_TYPE );
////		PNGEncoder.encode( chart, httpServletResponse.getOutputStream() );
	}
	
}
