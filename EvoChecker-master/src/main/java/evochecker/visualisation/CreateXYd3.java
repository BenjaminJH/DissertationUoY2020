/**
 * 
 */
package evochecker.visualisation;

import com.github.gwtd3.api.D3;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.github.gwtd3.*;
/**
 * @author benjamin
 *
 */
public class CreateXYd3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		  final Label versionLabel = new Label("d3.js current version: " + D3.version());
		  RootPanel.get().add(versionLabel);
//		System.out.println(versionLabel);
	}

}
