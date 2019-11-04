package com.media.publication.core.utility;

import com.day.cq.dam.api.Asset;

public class IAMUtil {
 private IAMUtil() {}
 
 private static final String DEFAULT_THUMBNAIL_PATH = "/content/dam/media-publication/not_available.jpeg/jcr:content/renditions/cq5dam.thumbnail.319.319.png";
 
	public static String getThumbNailPath(Asset asset,String size) {
		if (null != asset.getRendition(size)) {
			return asset.getRendition(size).getPath();
		}
		return DEFAULT_THUMBNAIL_PATH;
	}
	
}
