package com.eqsys.util;

import java.net.MalformedURLException;
import java.net.URL;

public class ParseUtil {

	private static final String TRANMODE_CONS = "连续波形传输";
	private static final String TRANMODE_TRIWN_STRING = "触发传输不传波形";
	private static final String TRANMODE_TRIW_STRING = "触发传输传波形";

	public static String parseTransMode(short mode) {

		switch (mode) {
		case 2:
			return TRANMODE_TRIW_STRING;
		case 3:
			return TRANMODE_TRIWN_STRING;
		default:
			return TRANMODE_CONS;
		}

	}

	public static short parseTransMode(String mode) {

		if (TRANMODE_CONS.equals(mode)) {
			return 1;
		} else if (TRANMODE_TRIW_STRING.equals(mode)) {
			return 2;
		} else if (TRANMODE_TRIWN_STRING.equals(mode)) {
			return 3;
		} else {
			return 0;
		}
	}

	/**
	 * 为了解决打包成jar包后找不到fxml文件的问题
	 * 
	 * @param path
	 *            fxml的绝对路径(这里的绝对路径是相对于工程的)
	 * @return
	 */
	public static URL getFXMLURL(String path) {

		URL url = null;
		url = ParseUtil.class.getResource(path);
		try {
			// 打包成jar包后不能通过getResource得到类路径
			// 所以,jar包中的文件用url表示方法: jar:url!{entity}
			// 如 : jar:file:/c:/a/b.jar!/com/test/a.txt
			if (url == null) {
				URL jarUrl = ParseUtil.class.getProtectionDomain()
						.getCodeSource().getLocation();
				url = new URL("jar:" + jarUrl + "!" + path);
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		return url;
	}

}
