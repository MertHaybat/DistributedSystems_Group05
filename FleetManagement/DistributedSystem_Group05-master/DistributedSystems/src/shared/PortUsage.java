
package shared;

import java.net.Socket;

public class PortUsage{
	
	public boolean isPortInUse(String host, int port) {
		boolean result = false;
		try {
		        (new Socket(host, port)).close();
		        result = true;
		        } catch (Exception e) {
		        //No exception needed
		        }
		return result;
		}
	}