package com.example.kedamall.search;

import com.alibaba.fastjson.JSON;
import com.example.kedamall.search.config.KedamallElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
public class KedamallSearchApplicationTests {

    @Autowired
    RestHighLevelClient restHighLevelClient;


    @Test
    public void searchData() throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices("bank");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
//        sourceBuilder.from();
//        sourceBuilder.size();
//        sourceBuilder.aggregation()
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age.keyword").size(10);
        sourceBuilder.aggregation(ageAgg);


        AvgAggregationBuilder balance = AggregationBuilders.avg("balance");
        sourceBuilder.aggregation(balance);
        System.out.println(sourceBuilder.toString());


        request.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(request, KedamallElasticSearchConfig.COMMON_OPTIONS);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            searchHit.getIndex();
            searchHit.getId();
            String sourceAsString = searchHit.getSourceAsString();

        }
        System.out.println(searchResponse.toString());

    }

    /**
     * 测试存储数据
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        // indexRequest.source("username","zhangsan","age",18,"gender","male");


        User user = new User();
        user.setAge(17);
        user.setGender("male");
        user.setUserName("Jack");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);

        IndexResponse index = restHighLevelClient.index(indexRequest, KedamallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(index);
    }

    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;
    }

    @Test
    public void contextLoads() {
        System.out.println(restHighLevelClient);
    }

}
