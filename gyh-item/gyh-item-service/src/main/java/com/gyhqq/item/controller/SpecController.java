package com.gyhqq.item.controller;

import com.gyhqq.item.entity.TbSpecGroup;
import com.gyhqq.item.entity.TbSpecParam;
import com.gyhqq.item.pojo.SpecGroupDTO;
import com.gyhqq.item.pojo.SpecParamDTO;
import com.gyhqq.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecController {

    @Autowired
    private SpecService specService;

    /**
     * 查询规格组信息 根据cid
     * 其中的@RequestParam(name = "id")中的id必须写id,不能写cid,因为接口文档中规定了前端传参为id!
     *
     * @param cid
     * @return
     */
    @GetMapping("/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findGroupByCategoryId(@RequestParam(name = "id") Long cid) {
        return ResponseEntity.ok(specService.findGroupByCategoryId(cid));
    }

    /**
     * 新增规格组信息 @RequestBody-->json格式需要
     *
     * @param specGroup
     * @return
     */
    @PostMapping("/group")
    public ResponseEntity<Void> saveGroups(@RequestBody TbSpecGroup specGroup) {
        specService.saveGroups(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改规格组信息 @RequestBody-->json格式需要
     *
     * @param specGroup
     * @return
     */
    @PutMapping("/group")
    public ResponseEntity<Void> update(@RequestBody TbSpecGroup specGroup) {
        specService.updateGroups(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除规格组信息
     * restful风格中/{id}与@PathVariable结合使用!
     *
     * @param id
     * @return
     */
    @DeleteMapping("/group/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        specService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 查询规格参数信息 和 新增商品中的规格参数回显
     * @param gid
     * @param cid
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> findParamList(@RequestParam(name = "gid", required = false) Long gid,
                                                            @RequestParam(name = "cid", required = false) Long cid,
                                                            @RequestParam(name = "searching", required = false) Boolean searching) {
        return ResponseEntity.ok(specService.findParamList(gid, cid,searching));
    }

    /**
     * 新增规格参数信息 @RequestBody-->json格式需要
     *
     * @param tbSpecParam
     * @return
     */
    @PostMapping("/param")
    public ResponseEntity<Void> saveParam(@RequestBody TbSpecParam tbSpecParam) {
        specService.saveParam(tbSpecParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新规格参数信息 @RequestBody-->json格式需要
     *
     * @param tbSpecParam
     * @return
     */
    @PutMapping("/param")
    public ResponseEntity<Void> updateParam(@RequestBody TbSpecParam tbSpecParam) {
        specService.updateParam(tbSpecParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除规格参数信息
     * restful风格中/{id}与@PathVariable结合使用!
     * @param id
     * @return
     */
    @DeleteMapping("/param/{id}")
    public ResponseEntity<Void> deleteParam(@PathVariable Long id) {
        specService.deleteParam(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据cid 查询 规格分组信息
     * @param cid
     * @return
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findGroupByCid(@RequestParam(name = "id")Long cid){
        return ResponseEntity.ok(specService.findGroupByCategoryId(cid));
    }

}
