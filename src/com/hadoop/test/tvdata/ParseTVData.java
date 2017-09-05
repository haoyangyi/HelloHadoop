package com.hadoop.test.tvdata;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @function 解析数据
 * @author 小讲
 * 
 */
public class ParseTVData {
	/**
	 * @function 使用 Jsoup 工具，解析输入数据，
	 * @param text
	 * @return list
	 */
	public static List<String> transData(String text) {
		List<String> list = new ArrayList<String>();
		Document doc;
		String rec = "";
		try {
			doc = Jsoup.parse(text);// jsoup解析数据
			Elements content = doc.getElementsByTag("WIC");
			String num = content.get(0).attr("cardNum");// 记录编号
			if (num == null || num.equals("")) {
				num = " ";
			}

			String stbNum = content.get(0).attr("stbNum");// 机顶盒号
			if (stbNum.equals("")) {
				return list;
			}

			String date = content.get(0).attr("date");// 日期

			Elements els = doc.getElementsByTag("A");
			if (els.isEmpty()) {
				return list;
			}

			for (Element el : els) {
				String e = el.attr("e");// 结束时间

				String s = el.attr("s");// 开始时间

				String sn = el.attr("sn");// 频道名称

				rec = stbNum + "@" + date + "@" + sn + "@" + s + "@" + e;
				list.add(rec);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return list;
		}
		return list;
	}
}
