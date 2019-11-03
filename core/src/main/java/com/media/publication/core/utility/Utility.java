package com.media.publication.core.utility;

public class Utility {

	private Utility() {
	}

	public static String appendURL(String sub1, String sub2) {
		if (null != sub1 && null != sub2) {
			return sub1 + "/" + sub2;
		}
		return null;
	}

	public static String addParameter(String url, String key, String value) {
		if (url != null && key != null) {
			url = removeTrailingSlash(url);

			if (url.contains("?")) {
				if (!url.endsWith("?") && !url.endsWith("&")) { // already has other params
					url += "&";
				}
			} else {
				url += "?";
			}
			url = appendParameter(url, key, value);
		}
		return url;
	}

	private static String appendParameter(String url, String key, String value) {
		url += key + "=" + value;
		return url;
	}

	private static String removeTrailingSlash(String url) {
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}

}
