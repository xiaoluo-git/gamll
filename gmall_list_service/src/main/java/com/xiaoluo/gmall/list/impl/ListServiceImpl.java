package com.xiaoluo.gmall.list.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xiaoluo.gmall.bean.SkuLsAttrValue;
import com.xiaoluo.gmall.bean.SkuLsInfo;
import com.xiaoluo.gmall.bean.SkuLsParams;
import com.xiaoluo.gmall.bean.SkuLsResult;
import com.xiaoluo.gmall.service.ListService;
import com.xiaoluo.gmall.service.util.RedisUtil;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListServiceImpl implements ListService{
	public static final String INDEX = "gmall_sku_info";

	public static final String TYPE = "sku";

	@Autowired
	private JestClient jestClient;

	@Autowired
	private RedisUtil redisUtil;

//	public static void main(String[] args) {
//
//		SkuLsParams skuLsParams = new SkuLsParams("苹果","61",new String[]{"154","157"},1,1);
//
//		new ListServiceImpl().searchSkuLsInfo(skuLsParams);
//	}

	@Override
	public void saveSkuLsInfo(SkuLsInfo skuLsInfo) {
		Index index = new Index.Builder(skuLsInfo).index(INDEX).type(TYPE).build();
		try {
			jestClient.execute(index);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public SkuLsResult searchSkuLsInfo(SkuLsParams skuLsParams) {
		skuLsParams.setPageSize(1);
		String dslStr = makeDslStr(skuLsParams);
		Search search = new Search.Builder(dslStr).addIndex(INDEX).addType(TYPE).build();
		SkuLsResult skuLsResult = new SkuLsResult();
		try {
			SearchResult searchResult = jestClient.execute(search);
			List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
			ArrayList<SkuLsInfo> skuLsInfos = new ArrayList<>();
			for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
				SkuLsInfo skuLsInfo = hit.source;
				String skuName = hit.highlight.get("skuName").get(0);
				skuLsInfo.setSkuName(skuName);
				skuLsInfos.add(skuLsInfo);
			}
			//sku集合
			skuLsResult.setSkuLsInfoList(skuLsInfos);
			//总条数
			skuLsResult.setTotal(searchResult.getTotal());
			//总页数
			skuLsResult.setTotalPages((long)Math.ceil(searchResult.getTotal() * 1.0 /skuLsParams.getPageSize()));
			//平台属性值
			ArrayList<String> attrValueIdList = new ArrayList<>();
			List<TermsAggregation.Entry> entryList = searchResult.getAggregations().getTermsAggregation("group_valueId").getBuckets();
			for (TermsAggregation.Entry entry : entryList) {
				attrValueIdList.add(entry.getKey());
			}
			skuLsResult.setAttrValueIdList(attrValueIdList);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return skuLsResult;
	}

	@Override
	public void incrHotScore(String skuId) {
		Jedis jedis = redisUtil.getJedis();
		Long count = jedis.incr("sku:" + skuId + ":count");
		if(count % 10 == 0){
			updateHotScore(skuId,count);
		}
	}

	private void updateHotScore(String skuId, Long count) {
		String updateJson="{\n" +
				"   \"doc\":{\n" +
				"     \"hotScore\":"+count+"\n" +
				"   }\n" +
				"}";

		Update update = new Update.Builder(updateJson).index(INDEX).type(TYPE).id(skuId).build();
		try {
			jestClient.execute(update);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String makeDslStr(SkuLsParams skuLsParams) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		if(!StringUtils.isEmpty(skuLsParams.getKeyword())){

			boolQueryBuilder.must(new MatchQueryBuilder("skuName", skuLsParams.getKeyword()));
			searchSourceBuilder.highlight(new HighlightBuilder().field("skuName").preTags("<span style='color:#F00'>").postTags("</span>"));
		}else{
			if(!StringUtils.isEmpty(skuLsParams.getCatalog3Id())){

				boolQueryBuilder.filter(new TermQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id()));
			}
		}

		if(skuLsParams.getValueId() != null && skuLsParams.getValueId().length >0){

			for (String skuValueId : skuLsParams.getValueId()) {
				boolQueryBuilder.filter(new TermQueryBuilder("skuAttrValueList.valueId",skuValueId));
			}
		}
		searchSourceBuilder.query(boolQueryBuilder);



		searchSourceBuilder.from((skuLsParams.getPageNo()-1) * skuLsParams.getPageSize());
		searchSourceBuilder.size(skuLsParams.getPageSize());

		searchSourceBuilder.sort("hotScore", SortOrder.DESC);

		searchSourceBuilder.aggregation(new TermsBuilder("group_valueId").field("skuAttrValueList.valueId").size(1000));
		System.out.println(searchSourceBuilder.toString());
		return searchSourceBuilder.toString();
	}


}
