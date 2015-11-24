package test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DocTest {

	public static void main(String[] args) throws Exception {
		String url = "http://search.51job.com/list/030000%252C00,000000,0000,00,1,99,%2B,0,1.html?lang=c&stype=2&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&lonlat=0%2C0&radius=-1&ord_field=0&list_type=0&confirmdate=9&fromType=18&dibiaoid=0";
		String url2 = "http://search.51job.com/list/030000%252C00,%2B,%2B,%2B,1,%2B,%2B,%2B,%2B.html?lang=c&fromType=18";
		int timeout = 10000;
		String userAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)";
		Document document = Jsoup.connect(url).timeout(timeout).userAgent(userAgent).followRedirects(true).post();
		System.out.println("doc: " + document.outerHtml());
	}
	
}
