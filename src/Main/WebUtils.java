package Main;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebUtils {
	public static String loadSite(String siteName) throws Exception {
		URL url = new URL(siteName);
		HttpURLConnection  con = (HttpURLConnection) url.openConnection();
		con.setReadTimeout(5000);
		con.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
		con.addRequestProperty("User-Agent", "Mozilla");
		con.addRequestProperty("Referer", "google.com");

		boolean redirect = false;

		do {
			int status = con.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP
						|| status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
			} else {
				redirect = false;
				break;
			}

			String newUrl = con.getHeaderField("Location");
			String cookies = con.getHeaderField("Set-Cookie");

			con = (HttpURLConnection) new URL(newUrl).openConnection();
			con.setRequestProperty("Cookie", cookies);
			con.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			con.addRequestProperty("User-Agent", "Mozilla");
			con.addRequestProperty("Referer", "google.com");

			System.out.println("Redirect to URL : " + newUrl);
		} while (redirect);


		Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
		Matcher m = p.matcher(con.getContentType());
		String charset = m.matches() ? m.group(1) : "ISO-8859-1";
		Reader r = new InputStreamReader(con.getInputStream(), charset);
		StringBuilder buf = new StringBuilder();
		while (true) {
			int ch = r.read();
			if (ch < 0)
				break;
			buf.append((char) ch);
		}
		return buf.toString();
	}


	public static ArrayList<String> getAllLinks(String data, String url) {
		ArrayList<String> linkList = new ArrayList<String>();

		Pattern p1 = Pattern.compile("<a\\s+href=\"(.+?)\"", Pattern.MULTILINE);
		Matcher m1 = p1.matcher(data); 
		while(m1.find()) {
			if (m1.group(1).indexOf("#") != -1) {
				continue;
			} else if (m1.group(1).indexOf("http://") == -1 && m1.group(1).indexOf("https://") == -1) {
				String clearUrl = url.substring(0, url.lastIndexOf('/'));
				if (m1.group(1).charAt(0) == '/' && clearUrl.charAt(clearUrl.length() - 1) != '/') {
					linkList.add(clearUrl + m1.group(1));
				} else {
					if (clearUrl.endsWith("/") && m1.group(1).charAt(0) != '/') {
						linkList.add(clearUrl + m1.group(1));
					} else {
						linkList.add(clearUrl + '/' + m1.group(1));
					}
				}
			} else {
				linkList.add(m1.group(1));
			}
		}

		Pattern p2 = Pattern.compile("<img\\s+src=\"(.+?)\"", Pattern.MULTILINE);
		Matcher m2 = p2.matcher(data); 
		while(m2.find()) {
			if (m2.group(1).indexOf("#") != -1) {
				continue;
			} else if (m2.group(1).indexOf("http://") == -1 && m2.group(1).indexOf("https://") == -1) {
				String clearUrl = url.substring(0, url.lastIndexOf('/'));
				if (m2.group(1).charAt(0) == '/' && clearUrl.charAt(clearUrl.length() - 1) != '/') {
					linkList.add(clearUrl + m2.group(1));
				} else {
					if (clearUrl.endsWith("/") && m2.group(1).charAt(0) != '/') {
						linkList.add(clearUrl + m2.group(1));
					} else {
						linkList.add(clearUrl + '/' + m2.group(1));
					}
				}
			} else {
				linkList.add(m2.group(1));
			}
		}

		return linkList;
	}

	public static String generateString(Random rng, String characters, int length)
	{
		char[] text = new char[length];
		for (int i = 0; i < length; i++)
		{
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}
		return new String(text);
	}
}
