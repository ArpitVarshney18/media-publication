package com.media.publication.core.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.day.cq.dam.api.Asset;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.media.pubication.core.services.SearchService;
import com.media.publication.core.pojo.AssetGalleryData;
import com.media.publication.core.utility.Utility;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AssetGalleryModel {

	@Inject
	@Default(values = "Asset Gallery")
	private String title;

	@Inject
	@Default(values = "/content/dam/we-retail/en/activities/hiking")
	private String assetPath;

	@OSGiService
	private SearchService service;

	@SlingObject
	private ResourceResolver resourceResolver;

	private List<AssetGalleryData> assetDataList;

	private static final String DEFUALT_THUMBNAIL_PATH = "/content/dam/media-publication/not_available.jpeg/jcr:content/renditions/cq5dam.thumbnail.319.319.png";
	private static final String DETAIL_PAGE_PATH = "/content/media-publication/en/asset-gallery-detail.html";

	@PostConstruct
	protected void init() throws RepositoryException {
		Session session = resourceResolver.adaptTo(Session.class);
		SearchResult result = service.getResult(getSearchPredicateMap(), session);
		populateDataFromResult(result);
	}

	private void populateDataFromResult(SearchResult result) throws RepositoryException {
		assetDataList = new ArrayList<>();
		if (null != result && result.getTotalMatches() > 0) {
			for (final Hit hit : result.getHits()) {
				final Resource resource = resourceResolver.getResource(hit.getPath());
				if (null != resource) {
					final Asset asset = resource.adaptTo(Asset.class);
					if (null != asset) {
						AssetGalleryData data = new AssetGalleryData();
						data.setFileName(StringUtils.substringBefore(asset.getName(), "."));
						data.setThumnailPath(getThumbNailPath(asset));
						data.setDetailPagePath(Utility.addParameter(DETAIL_PAGE_PATH, "assetName",asset.getName()));
						assetDataList.add(data);
					}
				}
			}
		}
	}

	private String getThumbNailPath(Asset asset) {
		if (null != asset.getRendition("cq5dam.thumbnail.319.319.png")) {
			return asset.getRendition("cq5dam.thumbnail.319.319.png").getPath();
		}
		return DEFUALT_THUMBNAIL_PATH;
	}

	private Map<String, String> getSearchPredicateMap() {
		final Map<String, String> predicateMap = new LinkedHashMap<>();
		predicateMap.put("path", assetPath);
		predicateMap.put("type", "dam:Asset");
		predicateMap.put("path.flat", "true");
		return predicateMap;
	}

	public String getTitle() {
		return title;
	}

	public String getAssetPath() {
		return assetPath;
	}

	public List<AssetGalleryData> getAssetDataList() {
		return assetDataList;
	}

}
