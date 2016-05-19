package cems;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RequestCems {
	private List<String> cookies;
	private HttpURLConnection conn;
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	public static void main(String[] args) throws Exception {
		String url = "http://cems-husc.hueuni.edu.vn/Page/Index.aspx";
		
		RequestCems http = new RequestCems();
		
		CookieHandler.setDefault(new CookieManager());
		
		String page = http.getPageContent(url);
		String postParams = http.getFormParams(page, null, null);
		http.sendPost(url, postParams);

		String moduleList = "http://cems-husc.hueuni.edu.vn/Student/ModuleList.aspx";
		String timeOfExam = "http://cems-husc.hueuni.edu.vn/Student/TimeOfExam.aspx";
		String result = http.getPageContent(moduleList);
		http.getModuleList(result);
		
	}
	private void getModuleList(String html) {
		Document doc = Jsoup.parse(html);
		Element table = doc.getElementById("ctl00_contentPlaceMain_panelTrongKHDT");
		Elements table_row = table.getElementsByTag("tr");
		//System.out.println(table_row.get(7));
		for(Element row: table_row) {
			Elements table_data = row.getElementsByTag("td");
			for(Element data:table_data) {
				System.out.println(data.text()+"\n");
			}
		}
	}
	private void sendPost(String url,String postParams) throws Exception {
		URL obj = new URL(url);
		conn = (HttpURLConnection) obj.openConnection();
		
		//Act like a browser
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if(cookies != null) {
			for(String cookie: this.cookies) {
				conn.addRequestProperty("Cookie", cookie);
				System.out.println(cookie);
			}
		}
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Referer", "http://cems-husc.hueuni.edu.vn/Page/Index.aspx");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	
		conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
		conn.setDoOutput(true);
		conn.setDoInput(true);

		// Send post request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();

		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = 
	             new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine+"\n");
		}
		in.close();
		// System.out.println(response.toString());
	}
	public String getFormParams(String html,String username,String password) {
		String result;
		result="__EVENTVALIDATION=%2FwEWBwLbqrW8DgKo3LPkCALOlfL9CwKNiIeUCwKMiIeUCwKPiIeUCwKVn81BM4nlGlseN0wi%2BmOXKNwYlf%2BMP3A%3D&__VIEWSTATE=%2FwEPDwUINDMyMjc1NTcPZBYCZg9kFgICAw9kFgICAQ9kFgYCAQ9kFgICAQ8WAh4LXyFJdGVtQ291bnQCChYUZg9kFgJmDxUEBj9hPTkwMocCVGjDtG5nIGJhzIFvIHYvdiDEkcSDbmcga3nMgSB0aGkgY8OizIFwIGNoxrDMgW5nIGNoacyJIG5nb2HMo2kgbmfGsMyDIFRpw6rMgW5nIEFuaCBBMiwgQjEgdmHMgCBjYcyBYyBuZ29hzKNpIG5nxrDMgyBUacOqzIFuZyBUcnVuZywgSGHMgG4sIFBoYcyBcCBCMSBkYcyAbmggY2hvIHNpbmggdmnDqm4gxJBIJkPEkCBow6rMoyBjaGnMgW5oIHF1eSB0YcyjaSB0csawxqHMgG5nIMSQYcyjaSBob8yjYyBOZ29hzKNpIG5nxrDMgyAtIMSQYcyjaSBob8yjYyBIdcOqzIEUNS8xNy8yMDE2IDQ6MTI6MTMgUE0AZAIBD2QWAmYPFQQGP2E9OTAxjgFUaMO0bmcgYsOhbyB24buBIHZp4buHYyB0aHUgaOG7jWMgcGjDrSBo4buNYyBr4buzIElJIG7Eg20gaOG7jWMgMjAxNSAtIDIwMTYgIMSR4buRaSB24bubaSBzaW5oIHZpw6puIGtob8OhIDM5IGPDsm4gZMawIGjhu41jIHBow60gaOG7jWMga%2BG7syBJFDUvMTcvMjAxNiA4OjM5OjEyIEFNAGQCAg9kFgJmDxUEBj9hPTkwMHxUSMOUTkcgQsOBTyBM4buKQ0ggVEhJIEvhur5UIFRIw5pDIEjhu4xDIFBI4bqmTiBI4buMQyBL4buyIDIsIE7Egk0gSOG7jEMgMjAxNS0yMDE2IEPDgUMgTOG7mlAgSOG7jEMgUEjhuqZOIEtIT8yBQSAzOCBWQcyAIDM5FDUvMTYvMjAxNiAyOjQxOjI3IFBNAGQCAw9kFgJmDxUEBj9hPTg5OXBUaMO0bmcgYsOhbyB24buBIHZp4buHYyB0aGF5IMSR4buVaSB0aOG7nWkgZ2lhbiB0dXnhu4NuIGThu6VuZyBj4bunYSBUcnVuZyB0w6JtIGR1IGzhu4tjaCBQaG9uZyBOaGEgLSBL4bq7IELDoG5nFDUvMTYvMjAxNiA4OjAzOjM5IEFNnAFOaMawIHRow7RuZyBiw6FvIMSRw6MgxJHGsGEsIG5heSB2w6wgbeG7mXQgc%2BG7kSBsw70gZG8gbsOqbiBiYW4gdOG7lSBjaOG7qWMgdGhheSDEkeG7lWkgdGjhu51pIGdpYW4gdHV54buDbiBk4bulbmcgYuG6r3QgxJHhuqd1IHThu6sgOWgwMCBuZ8OgeSAxNy8wNS8yMDE2LiBkAgQPZBYCZg8VBAY%2FYT04OTjbAVRow7RuZyBiw6FvIFRyxrDhu51uZyDEkEggS2hvYSBo4buNYyBIdeG6vzogdHJp4buDbiBraGFpIGPDtG5nIHTDoWMgYuG6p3UgY%2BG7rSDEkeG6oWkgYmnhu4N1IFF14buRYyBo4buZaSBLaMOzYSBYSVYgdsOgIEjEkE5EIGPDoWMgY%2BG6pXAgbmhp4buHbSBr4buzIDIwMTYtMjAyMSB04bqhaSDEkWnhu4NtIGLhuqd1IGPhu60gVHLGsOG7nW5nIMSQ4bqhaSBo4buNYyBLaG9hIGjhu41jLhQ1LzE0LzIwMTYgOTozMjo1MiBQTYMBU2luaCB2acOqbiBjw6FjIEtob2EsIELhu5kgbcO0biB0cuG7sWMgdGh14buZYyBj4bqnbiBu4bqvbSByw7UgdGjhu51pIGdpYW4gdsOgIGPDoWMgcXV5IMSR4buLbmgga2hpIHRoYW0gZ2lhIGPDtG5nIHTDoWMgYuG6p3UgY%2BG7rS5kAgUPZBYCZg8VBAY%2FYT04OTdUVGjDtG5nIGLDoW8gdi92IFThu5UgY2jhu6ljIHjDqXQgdsOgIGPDtG5nIG5o4bqtbiB04buRdCBuZ2hp4buHcCDEkeG7o3QgMiwgbsSDbSAyMDE2FDUvMTMvMjAxNiA5OjQxOjAyIEFNAGQCBg9kFgJmDxUEBj9hPTg5No4BVGjDtG5nIGLDoW8gduG7gSB2aeG7h2MgdGh1IGjhu41jIHBow60gaOG7jWMga%2BG7syBJSSBuxINtIGjhu41jIDIwMTUgLSAyMDE2ICDEkeG7kWkgduG7m2kgc2luaCB2acOqbiBraG%2FDoSAzOSBjw7JuIGTGsCBo4buNYyBwaMOtIGjhu41jIGvhu7MgSRQ1LzEyLzIwMTYgMjoxNDoxNSBQTQBkAgcPZBYCZg8VBAY%2FYT04OTVhVGjDtG5nIGLDoW8gduG7gSB2aeG7h2MgdHV54buDbiBk4bulbmcgbmjDom4gc%2BG7sSBj4bunYSBUcnVuZyB0w6JtIER1IGzhu4tjaCBQaG9uZyBOaGEgS%2BG6uyBCw6BuZxQ1LzExLzIwMTYgNDoxNTo0MSBQTdQBQ8OhYyBzaW5oIHZpw6puIMSRw6MgxJHEg25nIGvDvSB0aGFtIGdpYSB0dXnhu4NuIGThu6VuZyB2w6Agc2luaCB2acOqbiBraMOhYyBjw7Mgbmh1IGPhuqd1IMSR4bq%2FbiB0aGFtIGdpYSB0dXnhu4NuIGThu6VuZzoNClRo4budaSBnaWFuOiA4aDAwIG5nw6B5IDE3LzA1LzIwMTYNCsSQ4buLYSDEkWnhu4NtOiBQaMOybmcgSOG7mWkgdGjhuqNvIEtIMi0gRMOjeSBuaMOgIEtkAggPZBYCZg8VBAY%2FYT04OTQwQ2jGsMahbmcgdHLDrG5oIGjhu41jIGLhu5VuZyBQYW5hc29uaWMgbsSDbSAyMDE2FDUvMTEvMjAxNiA3OjE0OjIwIEFNAGQCCQ9kFgJmDxUEBj9hPTg5MzNEYW5oIHPDoWNoIHNpbmggdmnDqm4gdGhhbSBnaWEgdGnhur9wIHjDumMgY%2BG7rSB0cmkUNS8xMC8yMDE2IDM6MTg6NDkgUE1NxJDhu4thIMSRaeG7g206IEjhu5lpIHRyxrDhu51uZyDEkEggSHXhur8NClRo4budaSBnaWFuOiA4aDAwIG5nw6B5IDExLzA1LzIwMTZkAgMPDxYCHgdWaXNpYmxlaGRkAgUPFgIfAAIKFhRmD2QWAmYPFQMGP2E9ODkyigFUaMO0bmcgYsOhbyB24buBIHZp4buHYyDEkcSDbmcga8O9IHRoYW0gZ2lhIHBo4buPbmcgduG6pW4gdsOgIHR1eeG7g24gZOG7pW5nIGxhbyDEkeG7mW5nIHThuqFpIFRydW5nIHTDom0gZHUgbOG7i2NoIFBob25nIE5oYSAtIEvhursgQsOgbmcTNS85LzIwMTYgMzo0NDoxNiBQTWQCAQ9kFgJmDxUDBj9hPTg5MWBEYW5oIHPDoWNoIHNpbmggdmnDqm4gY8OhYyBLaG9hIHRoYW0gZ2lhIEjhu5lpIHRo4bqjbyBwaMOhdCDEkeG7mW5nIEdp4bqjaSB0aMaw4bufbmcgSG9uZGEgWS1FLVMTNS81LzIwMTYgNDoxMDoyOSBQTWQCAg9kFgJmDxUDBj9hPTg5MGFLaOG6o28gc8OhdCB24buBIG5odSBj4bqndSBo4buNYyB04bqtcCBr4bu5IG7Eg25nIG3hu4FtIGPhu6dhIHNpbmggdmnDqm4gdHLGsOG7nW5nIMSQSCBLaG9hIGjhu41jEzUvNS8yMDE2IDM6MDk6NDYgUE1kAgMPZBYCZg8VAwY%2FYT04ODmVAVF1eeG6v3QgxJHhu4tuaCB24buBIHZp4buHYyB4w7NhIHTDqm4gc2luaCB2acOqbiBraOG7j2kgZGFuaCBzw6FjaCBuaOG6rW4gaOG7jWMgYuG7lW5nIGtodXnhur9uIGtow61jaCBo4buNYyB04bqtcCBo4buNYyBr4buzIElJIG7Eg20gaOG7jWMgMjAxNS0yMDE2EzUvNS8yMDE2IDI6MTU6MjcgUE1kAgQPZBYCZg8VAwY%2FYT04ODidAVF1eeG6v3QgxJHhu4tuaCB24buBIHZp4buHYyBi4buVIHN1bmcgZGFuaCBzw6FjaCBuaOG6rW4gaOG7jWMgYuG7lW5nIGtodXnhur9uIGtow61jaCBo4buNYyB04bqtcCB2w6AgdHLhu6MgY%2BG6pXAgeMOjIGjhu5lpIGjhu41jIGvhu7MgSUkgbsSDbSBo4buNYyAyMDE1LTIwMTYTNS81LzIwMTYgMjowOTozNCBQTWQCBQ9kFgJmDxUDBj9hPTg4N4IBVGjDtG5nIGLDoW8gduG7gSB2aeG7h2MgY%2BG6pXAgaOG7jWMgYuG7lW5nIGtodXnhur9uIGtow61jaCBo4buNYyB04bqtcCB2w6AgdHLhu6MgY%2BG6pXAgWEggY2hvIHNpbmggdmnDqm4gSEtJSSBuxINtIGjhu41jIDIwMTUtMjAxNhQ1LzUvMjAxNiAxMDowOTo1MiBBTWQCBg9kFgJmDxUDBj9hPTg4NoYBVGjDtG5nIGJhzIFvIGto4bqjbyBzw6F0IMO9IGtp4bq%2FbiBzaW5oIHZpw6puIHbhu4EgaG%2FhuqF0IMSR4buZbmcgZ2nhuqNuZyBk4bqheSBj4bunYSBnaeG6o25nIHZpw6puIGhvzKNjIGt5zIAgMiwgbsSDbSBob8yjYyAyMDE1LTIwMTYTNS80LzIwMTYgNDowMjoxNiBQTWQCBw9kFgJmDxUDBj9hPTg4M1tUaMO0bmcgYsOhbyB24buBIHZp4buHYyB0aGF5IMSR4buVaSBs4buLY2ggdHV54buDbiBk4bulbmcgQ8O0bmcgdHkgQmFjdGVyaSBuZ8OgeSAwNi81LzIwMTYuFDUvNC8yMDE2IDEwOjI3OjU1IEFNZAIID2QWAmYPFQMGP2E9ODgytAFUaMO0bmcgYmHMgW8gdi92IMSRacOqzIB1IGNoacyJbmggbGnMo2NoIHRoaSBrw6rMgXQgdGh1zIFjIGhvzKNjIHBow6LMgG4sIGhvzKNjIGt5zIAgMiBuxINtIGhvzKNjIDIwMTUtMjAxNiwgY2HMgWMgbMahzIFwIHNpbmggdmnDqm4gdGh1w7TMo2MgdHLGsMahzIBuZyDEkGHMo2kgaG%2FMo2MgTmdvYcyjaSBuZ8awzIMTNS80LzIwMTYgODowMDowNiBBTWQCCQ9kFgJmDxUDBj9hPTg4MVhUaMO0bmcgYmHMgW8gdi92IHTDtMyJIGNoxrDMgWMgaG%2FMo2Mga3nMgCAzIGNoxrDGoW5nIHRyacyAbmggR0RRUEFOIG7Eg20gaG%2FMo2MgMjAxNS0yMDE2FTQvMjcvMjAxNiAxMDo1MTozMyBBTWRk8kLESOZk5jqeZmlqo%2FWvdUbgh5k%3D&__VIEWSTATEGENERATOR=97F8D7A2&ctl00%24ctl06%24buttonDangnhap=&ctl00%24ctl06%24cmbTypeOfUser=1&ctl00%24ctl06%24txtMatKhau=21041994&ctl00%24ctl06%24txtTenDangnhap=1210210121";
		System.out.println(result.length());
		return result.toString();
	}
	private String getPageContent(String url) throws Exception {
		URL obj = new URL(url);
		conn = (HttpURLConnection) obj.openConnection();
		
		//Default is Get
		conn.setRequestMethod("GET");
		conn.setUseCaches(false);
		
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accpet", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if(cookies != null) {
			for(String cookie: this.cookies) {
				conn.addRequestProperty("Cookie", cookie);
			}
		}
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " +url);
		System.out.println("Response Code : "+ responseCode);
		BufferedReader in = 
	            new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine+"\n");
		}
		in.close();

		// Get the response cookies
		setCookies(conn.getHeaderFields().get("Set-Cookie"));

		return response.toString();
	}
	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	  }
}
