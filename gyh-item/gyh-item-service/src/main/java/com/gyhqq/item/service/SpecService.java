package com.gyhqq.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.common.utils.BeanHelper;
import com.gyhqq.item.entity.TbSpecGroup;
import com.gyhqq.item.entity.TbSpecParam;
import com.gyhqq.item.pojo.SpecGroupDTO;
import com.gyhqq.item.pojo.SpecParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SpecService {

    @Autowired
    private TbSpecGroupService tbSpecGroupService;

    @Autowired
    private TbSpecParamService tbSpecParamService;

    public List<SpecGroupDTO> findGroupByCategoryId(Long cid) {
        QueryWrapper<TbSpecGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSpecGroup::getCid, cid);
        List<TbSpecGroup> specGroupList = tbSpecGroupService.list(queryWrapper);
        if (CollectionUtils.isEmpty(specGroupList)) {
            throw new GyhException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(specGroupList, SpecGroupDTO.class);
    }

    public void saveGroups(TbSpecGroup specGroup) {
        boolean save = tbSpecGroupService.save(specGroup);
        if (!save) {
            throw new GyhException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    public List<SpecParamDTO> findParamList(Long gid, Long cid) {
        QueryWrapper<TbSpecParam> queryWrapper = new QueryWrapper<>();
        if (gid != null && gid != 0) {
            queryWrapper.lambda().eq(TbSpecParam::getGroupId, gid);
        }
        if (cid != null && cid != 0) {
            queryWrapper.lambda().eq(TbSpecParam::getCid, cid);
        }
        List<TbSpecParam> specParamList = tbSpecParamService.list(queryWrapper);
        if (CollectionUtils.isEmpty(specParamList)) {
            throw new GyhException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(specParamList, SpecParamDTO.class);
    }

    public void updateGroups(TbSpecGroup specGroup) {
        boolean save = tbSpecGroupService.updateById(specGroup);
        if (!save) {
            throw new GyhException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }

    public void deleteById(Long id) {
        boolean removeById = tbSpecGroupService.removeById(id);
        if (!removeById) {
            throw new GyhException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }

    }

    public void saveParam(TbSpecParam tbSpecParam) {
        boolean save = tbSpecParamService.save(tbSpecParam);
        if (!save) {
            throw new GyhException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    public void updateParam(TbSpecParam tbSpecParam) {
        boolean update = tbSpecParamService.updateById(tbSpecParam);
        if (!update) {
            throw new GyhException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    public void deleteParam(Long id) {
        boolean removeById = tbSpecParamService.removeById(id);
        if (!removeById) {
            throw new GyhException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }
    }
}
