package com.media.publication.core.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.dam.api.Asset;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.media.pubication.core.services.SearchService;
import com.media.publication.core.pojo.AssetGalleryData;
import com.media.publication.core.utility.IAMUtil;
import com.media.publication.core.utility.Utility;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AssetGalleryModel {

	@ValueMapValue
	@Default(values = "Asset Gallery")
	private String title;

	@OSGiService
	private SearchService service;

	@SlingObject
	private ResourceResolver resourceResolver;

	@ScriptVariable
	private Page currentPage;

	private List<AssetGalleryData> assetDataList;

	private static final String DETAIL_PAGE_PATH = "/content/media-publication/en/asset-gallery-detail.html?wcmmode=disabled";

	@PostConstruct
	protected void init() throws RepositoryException {
		final String path = populateAssetPath();
		Session session = resourceResolver.adaptTo(Session.class);
		SearchResult result = service.getResult(getSearchPredicateMap(path), session);
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
						data.setThumnailPath(IAMUtil.getThumbNailPath(asset, "cq5dam.thumbnail.319.319.png"));
						data.setDetailPagePath(Utility.addParameter(DETAIL_PAGE_PATH, "assetName", asset.getName()));
						assetDataList.add(data);
					}
				}
			}
		}
	}

	private Map<String, String> getSearchPredicateMap(final String assetPath) {
		final Map<String, String> predicateMap = new LinkedHashMap<>();
		predicateMap.put("path", assetPath);
		predicateMap.put("type", "dam:Asset");
		predicateMap.put("path.flat", "true");
		return predicateMap;
	}

	private String populateAssetPath() {
		ContentPolicyManager policyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
		if (policyManager != null) {
			ContentPolicy contentPolicy = policyManager.getPolicy(currentPage.getContentResource());
			if (contentPolicy != null) {
				return (String) contentPolicy.getProperties().get("assetGalleryDamPath");
			}
		}
		return null;
	}

	public String getTitle() {
		return title;
	}
	
	public List<AssetGalleryData> getAssetDataList() {
		return assetDataList;
	}

}
