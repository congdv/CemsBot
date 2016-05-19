package cems;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRedirectExample {
	public static void main(String[] args) {

	    try {

		String url = "http://cems-husc.hueuni.edu.vn/Page/Index.aspx";

		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setReadTimeout(5000);
		conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
		conn.addRequestProperty("User-Agent", "Mozilla");

		System.out.println("Request URL ... " + url);

		boolean redirect = false;
		HashMap dataRequest = new HashMap();
		// normally, 3xx is redirect
		int status = conn.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
 
            while ((inputLine = in.readLine()) != null) {
            	String[] dataString = getRequestProperty(inputLine);
				if (dataString != null) {
					dataRequest.put(dataString[0], dataString[1].trim());
				}
            }
            in.close();
			redirect = true;
		}

		System.out.println("Response Code ... " + status);
		String cookies = conn.getHeaderField("Set-Cookie");
		conn.disconnect();
		String post_param = "";
		post_param+="__VIEWSTATE="+dataRequest.get("__VIEWSTATE");
		post_param+="&__VIEWSTATEGENERATOR"+dataRequest.get("__VIEWSTATEGENERATOR");
		post_param +="&__EVENTVALIDATION="+dataRequest.get("__EVENTVALIDATION");
		post_param+="&ctl00$ctl06$txtTenDangnhap=1210210121";
		post_param+="&ctl00$ctl06$txtMatKhau=21041994";
		post_param+="&ctl00$ctl06$cmbTypeOfUser=1";
		post_param+="&ctl00$ctl06$buttonDangnhap=";
		System.out.println(post_param);
		if (redirect) {
			conn = (HttpURLConnection) obj.openConnection();
			// get the cookie if need, for login
			
			System.out.println(cookies);
			// open the new connnection again
			conn.setRequestProperty("Cookie", cookies);
			conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			conn.addRequestProperty("User-Agent", "Mozilla");
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			os.write(post_param.getBytes(StandardCharsets.UTF_8 ));
			os.flush();
			os.close();						
			status = conn.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
				String inputLine;
				StringBuffer html = new StringBuffer();
				
				while ((inputLine = in.readLine()) != null) {
					html.append(inputLine+"\n");
				}
				in.close();
				System.out.println(html);
			}
		}

		BufferedReader in = new BufferedReader(
	                              new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer html = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			html.append(inputLine);
		}
		in.close();

		System.out.println("URL Content... \n" + html.toString());
		System.out.println("Done");

	    } catch (Exception e) {
		e.printStackTrace();
	    }

	  }
	public static String[] getRequestProperty(String line) {
		String pattern = "^<input(.*)id=\"(.*)\"(.*)value=\"(.*)\"";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(line);
		if (m.find()) {
			String[] dataString = new String[2];
			dataString[0] = m.group(2);
			dataString[1] = m.group(4);
			return dataString;
		}

		return null;
	}

	}

