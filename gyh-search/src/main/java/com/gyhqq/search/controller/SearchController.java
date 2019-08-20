package com.gyhqq.search.controller;

import com.gyhqq.common.vo.PageResult;
import com.gyhqq.search.dto.GoodsDTO;
import com.gyhqq.search.dto.SearchRequest;
import com.gyhqq.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;
    @PostMapping("/page")
    public ResponseEntity<PageResult<GoodsDTO>> search(@RequestBody SearchRequest request){
            return ResponseEntity.ok(searchService.search(request.getKey(),request.getPage()));
    }
}
