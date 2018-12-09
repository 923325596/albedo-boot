package com.albedo.java.modules.sys.service;

import com.albedo.java.common.persistence.DynamicSpecifications;
import com.albedo.java.common.persistence.SpecificationDetail;
import com.albedo.java.common.persistence.domain.BaseEntity;
import com.albedo.java.common.persistence.service.TreeVoService;
import com.albedo.java.modules.sys.domain.Dict;
import com.albedo.java.modules.sys.repository.DictRepository;
import com.albedo.java.util.DictUtil;
import com.albedo.java.util.PublicUtil;
import com.albedo.java.util.StringUtil;
import com.albedo.java.util.domain.PageModel;
import com.albedo.java.util.domain.QueryCondition;
import com.albedo.java.vo.base.SelectResult;
import com.albedo.java.vo.sys.DictVo;
import com.albedo.java.vo.sys.query.DictTreeQuery;
import com.albedo.java.vo.sys.query.DictTreeResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing dicts.
 */
@Service
@Transactional
public class DictService extends TreeVoService<DictRepository, Dict, String, DictVo> {
    /**
     * 业务数据
     */
    private static final String DICT_BIZPARENT_ID = "28a368fdbbd44a7a99af28d01b12c089";
    private static final String DICT_SYSPARENT_ID = "5ea249bb780348eb8ea6a0efade684a6";

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<DictTreeResult> findTreeData(DictTreeQuery dictTreeQuery, List<Dict> dictList) {
        String extId = dictTreeQuery != null ? dictTreeQuery.getExtId() : null, all = dictTreeQuery != null ? dictTreeQuery.getAll() : null;
        List<DictTreeResult> mapList = Lists.newArrayList();
        Collections.sort(dictList, Comparator.comparing(Dict::getSort).reversed());
        for (Dict e : dictList) {
            if ((PublicUtil.isEmpty(extId)|| PublicUtil.isEmpty(e.getParentIds()) ||
                (PublicUtil.isNotEmpty(extId) && !extId.equals(e.getId()) && e.getParentIds() != null && e.getParentIds().indexOf("," + extId + ",") == -1))
                && (all != null || (all == null && BaseEntity.FLAG_NORMAL.equals(e.getStatus())))){
                DictTreeResult dictTreeResult = new DictTreeResult();
                dictTreeResult.setId(e.getId());
                dictTreeResult.setPid(PublicUtil.isEmpty(e.getParentId()) ? "0" : e.getParentId());
                dictTreeResult.setName(e.getName());
                dictTreeResult.setValue(e.getId());
                dictTreeResult.setLabel(dictTreeResult.getName());
                mapList.add(dictTreeResult);
            }
        }
        return mapList;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Dict findOne(String id) {
        return repository.findOneById(id);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<Dict> findAll(PageModel<Dict> pm) {
        SpecificationDetail<Dict> spec = DynamicSpecifications.buildSpecification(pm.getQueryConditionJson(),
                QueryCondition.ne(Dict.F_STATUS, Dict.FLAG_DELETE));
        return repository.findAll(spec, pm);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Dict findFristByParentId(String parentId) {
        return repository.findTopByParentIdAndStatusNotOrderBySortDesc(parentId, Dict.FLAG_DELETE);
    }

    public Map<String,List<SelectResult>> findCodes(String codes) {
        return findCodeList(PublicUtil.isNotEmpty(codes) ? Lists.newArrayList(codes.split(StringUtil.SPLIT_DEFAULT)) : null);
    }
    public Map<String,List<SelectResult>> findCodeList(List<String> codeList) {
        Map<String, List<SelectResult>> map = Maps.newHashMap();
        //业务参数
        List<Dict> dictList = DictUtil.getDictList();
        List<Dict> dictCodes = Lists.newArrayList();
        if(PublicUtil.isEmpty(codeList)){
            for(Dict dict : dictList){
                //业务字典
                if(DICT_BIZPARENT_ID.equals(dict.getParentId())||DICT_SYSPARENT_ID.equals(dict.getParentId())){
                    dictCodes.add(dict);
                }
            }
        }else{
            for(String codeItem : codeList){
                for(Dict dict : dictList){
                    //命中的数据字段
                    if(codeItem.equals(dict.getCode())){
                        dictCodes.add(dict);
                        break;
                    }
                }
//                if(Globals.UA_SYS_CITY.equals(codeItem)){
//                    map.put(Globals.UA_SYS_CITY, repository.findCitys());
//                }
            }
        }
        dictCodes.forEach(dict -> map.put(dict.getCode(), getDictList(dictList, dict)));
//        if(!map.containsKey(Globals.UA_SYS_CITY) && PublicUtil.isEmpty(codeList)){
//            map.put(Globals.UA_SYS_CITY, repository.findCitys());
//        }

        return map;
    }

    private List<SelectResult> getDictList(List<Dict> dictList, Dict dict) {
        List<SelectResult> list = Lists.newLinkedList();
        if(PublicUtil.isNotEmpty(dictList)){
            for(Dict item : dictList){
                if(PublicUtil.isNotEmpty(item.getParentId()) && item.getParentId().equals(dict.getId())){
                    list.add(new SelectResult(item.getVal(), item.getName(), item.getVersion()));
                }
            }
        }
        return list;
    }

    public List<Dict> findAllByStatusNotAndIsShowOrderBySortAsc(Integer flagDelete, Integer isShow) {
    return repository.findAllByStatusNotAndIsShowOrderBySortAsc(flagDelete, isShow);
    }
}
