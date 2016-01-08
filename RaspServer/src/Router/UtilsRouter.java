package Router;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UtilsRouter {
	public static String executeCommand(String command) {
		StringBuffer output = new StringBuffer();
		StringBuffer outputerror = new StringBuffer();
		Process p;
		
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
//			System.out.println(output);
			
			String line2 = "";			
			while ((line2 = error.readLine())!= null) {
				outputerror.append(line2 + "\n");
			}
			System.out.println(outputerror);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}
}
