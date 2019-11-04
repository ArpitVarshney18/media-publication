package com.media.publication.core.models;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.dam.api.Asset;
import com.media.pubication.core.services.SearchService;
import com.media.publication.core.pojo.AssetGalleryDetailPageData;
import com.media.publication.core.utility.IAMUtil;
import com.media.publication.core.utility.Utility;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AssetGalleryDetailPage {

	private static final String NOT_SPECIFIED = "Not Specified";

	@ValueMapValue
	@Default(values = "Asset Gallery Detail Page")
	private String title;

	@OSGiService
	private SearchService service;

	@SlingObject
	private ResourceResolver resourceResolver;

	@Self
	private SlingHttpServletRequest request;

	@ValueMapValue
	@Default(values = "/content/dam/we-retail/en/activities/hiking")
	private String assetPath;

	private AssetGalleryDetailPageData data;
	private String assetName;

	@PostConstruct
	protected void init() {
		data = new AssetGalleryDetailPageData();
		assetName = request.getParameter("assetName");
		String resourcePath = Utility.appendURL(assetPath, assetName);
		if (null != resourcePath) {
			final Resource resource = resourceResolver.getResource(resourcePath);
			if (null != resource) {
				final Asset asset = resource.adaptTo(Asset.class);
				if (null != asset) {
					data.setAssetType(getAssetType(asset));
					data.setAssetImage(IAMUtil.getThumbNailPath(asset, "cq5dam.web.1280.1280.jpeg"));
					data.setAssetDownloadLink(StringUtils.defaultIfBlank(asset.getPath(), NOT_SPECIFIED));
					data.setAssetOwnerName(getValue(asset, "photoshop:AuthorsPosition"));
					data.setAssetOwnerEmail(getValue(asset, "Iptc4xmpCore:CiEmailWork"));
				}
			}
		}
	}

	private String getValue(Asset asset, String property) {
		if (null != asset.getMetadata(property)) {
			return asset.getMetadata(property).toString();
		}
		return NOT_SPECIFIED;
	}

	private String getAssetType(final Asset asset) {
		if (null != asset.getMetadata("dc:format")) {
			String format = asset.getMetadata("dc:format").toString();
			if (format.contains("video"))
				return "Video";
			else if (format.contains("pdf"))
				return "PDF";
			else if (format.contains("image"))
				return "Image";
			else
				return NOT_SPECIFIED;
		}
		return NOT_SPECIFIED;

	}

	public String getTitle() {
		return title;
	}

	public String getAssetName() {
		return assetName;
	}

	public String getAssetPath() {
		return assetPath;
	}

	public AssetGalleryDetailPageData getData() {
		return data;
	}

}
