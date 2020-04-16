/**
 * 
 */
package evochecker.visualisation;

import java.awt.BorderLayout;

//import de.erichseifert.gral.data.DataSeries;
//import de.erichseifert.gral.data.DataTable;
//import de.erichseifert.gral.graphics.Insets2D;
//import de.erichseifert.gral.plots.XYPlot;
//import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
//import de.erichseifert.gral.plots.lines.LineRenderer;
//import de.erichseifert.gral.ui.InteractivePanel;
//import javax.swing.JFrame;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.io.data.DataReader;
import de.erichseifert.gral.io.data.DataReaderFactory;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.AbstractLineRenderer2D;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetColorCommand;
/**
 * @author benjamin
 *
 */
public class CreateXYGral extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3964701124163812380L;

	public void execute(String algorithmString, String problemString) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);

//        DataReader reader = DataReaderFactory.getInstance().get("text/tab-separated-values");
//        DataTable myOwnData = reader.read(new FileInputStream(filename), Double.class, Double.class);
        String myFilename = "data/FUN_" + algorithmString;
        System.out.println(myFilename);
        
        String row;
        BufferedReader csvReader;
        
        DataTable data = new DataTable(Double.class, Double.class);
        
        
        int total = 0;
		try {
			csvReader = new BufferedReader(new FileReader(myFilename));
			while ((row = csvReader.readLine()) != null) {
	            String[] tempData = row.split(" ");
	            String valueX = tempData[0];
	            String valueY = tempData[1];
	            System.out.println("(x, y) = " + "(" + valueX + ", " + valueY + ")");
	            data.add(Double.valueOf(valueX), Double.valueOf(valueY));
	            total ++;
	        }
	        csvReader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        
        
        
        XYPlot plot = new XYPlot(data);
        getContentPane().add(new InteractivePanel(plot));
//        LineRenderer lines = new DefaultLineRenderer2D();
        PointRenderer points = new DefaultPointRenderer2D();
        
        
//        plot.setLineRenderers(data, lines);
        plot.setPointRenderers(data, points);
//        Color color = new Color(0.0f, 0.3f, 1.0f);
        Color color2 = new Color(1.0f, 0.0f, 0.0f);
//        plot.getPointRenderers(data).SetColor(color);
        points.setColor(color2);
//        lines.setColor(color);
        
        
        double insetsTop = 20.0,
        	       insetsLeft = 60.0,
        	       insetsBottom = 60.0,
        	       insetsRight = 40.0;
        	plot.setInsets(new Insets2D.Double(
        	    insetsTop, insetsLeft, insetsBottom, insetsRight));
        
        plot.getTitle().setText("Pareto front for " + algorithmString + " on " + problemString);
        
        Label myXLabel = new Label("X");
        Label myYLabel = new Label("Y");
        plot.getAxisRenderer(XYPlot.AXIS_X);
        AxisRenderer myX = plot.getAxisRenderer(XYPlot.AXIS_X);
        AxisRenderer myY = plot.getAxisRenderer(XYPlot.AXIS_Y);
        myX.setLabel(myXLabel);
        myY.setLabel(myYLabel);
        myX.setTickSpacing(0.20);
        myY.setTickSpacing(0.01);
//        myX.setIntersection(-Double.MAX_VALUE);
//        myY.setIntersection(-Double.MAX_VALUE);
        
//        getContentPane().add(new InteractivePanel(plot), BorderLayout.CENTER);
        
        
        
        this.setVisible(true);
        System.out.println("Total = " + total);
    }

	public static void main(String[] args) {
		//String problemName = problem.getName();
		//algorithmStr
		CreateXYGral frame = new CreateXYGral();
		frame.execute("NSGAII", "DPM");
//		frame.setVisible(true);
//		
//		DataTable data = new DataTable(Double.class, Double.class, Double.class);
//
//		final int POINT_COUNT = 1000;
//		java.util.Random rand = new java.util.Random();
//		for (int i = 0; i < POINT_COUNT; i++) {
//		    double x = rand.nextGaussian();
//		    double y1 = rand.nextGaussian() + x;
//		    double y2 = rand.nextGaussian() - x;
//		    data.add(x, y1, y2);
//		}
//	    DataSeries series1 = new DataSeries("Series 1", data, 0, 1);
//	    DataSeries series2 = new DataSeries("Series 2", data, 0, 2);
//	    XYPlot plot = new XYPlot(series1, series2);
//	    System.out.println("Flag 1");
//	    
//	    double insetsTop = 20.0, insetsLeft = 60.0, insetsBottom = 60.0, insetsRight = 40.0;
//	    plot.setInsets(new Insets2D.Double(insetsTop, insetsLeft, insetsBottom, insetsRight));
//	    
//	    LinePlot frame = new LinePlotTest();
//        frame.setVisible(true);
//	    
//	    
//	    plot.getTitle().setText("Nice scatter");
//	    getContentPane().add(new InteractivePanel(plot));
	}

}
