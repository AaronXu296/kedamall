package com.example.kedamall.search.service;

import com.example.kedamall.search.vo.SearchParam;
import com.example.kedamall.search.vo.SearchResult;

public interface MallSearchService {
    /**
     *
     * @param searchParam 检索的所有采纳数
     * @return 返回检索的结果
     */
    SearchResult search(SearchParam searchParam);
}
