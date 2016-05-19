package cems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.Environment;

public class scrapingWeb {
	private static final String POST_PARAMS = "";
	public static void main(String[] args) {

		scrapingWeb test = new scrapingWeb();
		HashMap hm = test.response();
		 Set set = hm.entrySet();
		 Iterator i = set.iterator();
		 while(i.hasNext()) {
		 Entry me = (Map.Entry)i.next();
		 
		 System.out.print(me.getKey()+": ");
		 System.out.println(me.getValue());
		 }
//
//		hm.put("ctl00$ctl06$txtTenDangnhap", "1210210121");
//		hm.put("ctl00$ctl06$txtMatKhau", "21041994");
//		hm.put("ctl00$ctl06$cmbTypeOfUser", "1");
//		hm.put("ctl00$ctl06$buttonDangnhap", "");
		//test.login("1210210121", "21041994", "1", hm);
		test.login(hm);
//		try {
//			test.sendGET();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	/***
	 * Get content web page from request
	 * 
	 * @return content string
	 */
	public HashMap response() {
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		String html = null;
		HashMap dataRequest = new HashMap();
		
		try {
			URL url = new URL("http://cems-husc.hueuni.edu.vn/Page/Index.aspx");
			urlConnection = (HttpURLConnection) url.openConnection();
			// System.out.println(urlConnection.getHeaderField("Cookies"));
			urlConnection.setRequestMethod("GET");
		
			urlConnection.connect();
			
			InputStream inputStream = urlConnection.getInputStream();
			Map<String, List<String>> headers = urlConnection.getHeaderFields();
			Set set = headers.entrySet();
			Iterator i = set.iterator();
			 while(i.hasNext()) {
			 Entry me = (Map.Entry)i.next();
			 List<String> value = (List<String>) me.getValue();
			 dataRequest.put(me.getKey(), value.get(0));
			 }
			StringBuffer buffer = new StringBuffer();
			
			if (inputStream == null) {
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] dataString = getRequestProperty(line);
				if (dataString != null) {
					dataRequest.put(dataString[0], dataString[1].trim());
				}
				buffer.append(line + "\n");
			}

			if (buffer.length() == 0) {
				return null;
			}
			html = buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					System.out.println("Error closing stream");
				}
			}
		}
//		 System.out.println(html);
		return dataRequest;
	}
	/***
	 * Get parameter data for login
	 * 
	 * @param content
	 * @return array string parameter
	 */
	public String[] getRequestProperty(String line) {
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

	public String login(String user, String password, String typeOfUser,
			HashMap hm) {
		hm.put("ctl00$ctl06$txtTenDangnhap", user);
		hm.put("ctl00$ctl06$txtMatKhau", password);
		hm.put("ctl00$ctl06$cmbTypeOfUser", typeOfUser);
		hm.put("ctl00$ctl06$buttonDangnhap", "");
		Set set = hm.entrySet();
		Iterator i = set.iterator();

		
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		String html = null;
		try {
			URL url = new URL("http://cems-husc.hueuni.edu.vn/Page/Index.aspx");
			urlConnection = (HttpURLConnection) url.openConnection();
			while (i.hasNext()) {
				Entry me = (Map.Entry) i.next();
				if (me.getKey() != null) {
					System.out.print(me.getKey() + ": ");
					System.out.println(me.getValue());
					urlConnection.setRequestProperty(me.getKey().toString(), me
							.getValue().toString());
				}

			}
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
			System.out.println("Set cooike times 2"+urlConnection.getHeaderField("Set-Cookie"));
			
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			System.out.println("Param times 2");
			while ((line = reader.readLine()) != null) {
				if(getRequestProperty(line)!=null) {
					System.out.println(getRequestProperty(line)[0] + " --"+getRequestProperty(line)[1]);
				}
				
				buffer.append(line + "\n");
			}

			if (buffer.length() == 0) {
				System.out.println("No buffer");
				return null;
			}
			html = buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					System.out.println("Error closing stream");
				}
			}
		}
		return html;
	}

	public void login(HashMap hm) {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String post_param = "";
		post_param+="__VIEWSTATE="+hm.get("__VIEWSTATE");
		post_param+="&__VIEWSTATEGENERATOR"+hm.get("__VIEWSTATEGENERATOR");
		post_param +="&__EVENTVALIDATION="+hm.get("__EVENTVALIDATION");
		post_param+="&ctl00$ctl06$txtTenDangnhap="+hm.get("ctl00$ctl06$txtTenDangnhap");
//		post_param+="&ctl00$ctl06$txtMatKhau="+hm.get("ctl00$ctl06$txtMatKhau");
//		post_param+="&ctl00$ctl06$cmbTypeOfUser="+hm.get("ctl00$ctl06$cmbTypeOfUser");
//		post_param+="&ctl00$ctl06$buttonDangnhap="+hm.get("ctl00$ctl06$buttonDangnhap");
		System.out.println(post_param);
		try {
			URL url = new URL("http://cems-husc.hueuni.edu.vn/Page/Index.aspx");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:46.0) Gecko/20100101 Firefox/46.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			con.setRequestProperty("Set-Cookie", hm.get("Set-Cookie").toString());
//			String[] info = hm.get("Set-Cookie").toString().split(";");
//			String aspNetSessinID=info[0].replace("ASP.NET_SessionId=", "");
//			con.setRequestProperty("ASP.NET_SessionId", aspNetSessinID);
//			//String aspNetSessinID=info[0].replace("ASP.NET_SessionId=", "");
//			System.out.println("asp id: "+aspNetSessinID);
//			System.out.println(hm.get("Set-Cookie"));
			// For POST only - START
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(post_param.getBytes(StandardCharsets.UTF_8 ));
			os.flush();
			os.close();
			// For POST only - END
			int responseCode = con.getResponseCode();
			System.out.println(con.getHeaderField("Set-Cookie"));

			System.out.println("POST Response Code :: " + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine+"\n");
				}
				in.close();

				// print result
				System.out.println(response.toString());
			} else {
				
				System.out.println("POST request not worked");
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	 private static void sendGET() throws IOException {
	        URL obj = new URL("http://cems-husc.hueuni.edu.vn/Page/Index.aspx");
	        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	        con.setRequestMethod("GET");
	        con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:46.0) Gecko/20100101 Firefox/46.0");
	        int responseCode = con.getResponseCode();
	        System.out.println("GET Response Code :: " + responseCode);
	        if (responseCode == HttpURLConnection.HTTP_OK) { // success
	            BufferedReader in = new BufferedReader(new InputStreamReader(
	                    con.getInputStream()));
	            String inputLine;
	            StringBuffer response = new StringBuffer();
	 
	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();
	 
	            // print result
	            System.out.println(response.toString());
	        } else {
	            System.out.println("GET request not worked");
	        }
	 
	    }
}
