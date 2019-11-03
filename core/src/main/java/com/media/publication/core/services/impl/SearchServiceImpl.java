package com.media.publication.core.services.impl;

import java.util.Map;

import javax.jcr.Session;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.media.pubication.core.services.SearchService;
import com.day.cq.search.Query;

@Component(service=SearchService.class)
public class SearchServiceImpl implements SearchService{
	
	@Reference
	QueryBuilder builder;

	@Override
	public SearchResult getResult(Map<String, String> predicateMap, Session session) {
		SearchResult searchResult = null;
        final Query query = builder.createQuery(PredicateGroup.create(predicateMap), session);
        if (null != query) {
            query.setStart(0);
            query.setHitsPerPage(0);
            searchResult = query.getResult();
        }
        return searchResult;
		
	}

}
