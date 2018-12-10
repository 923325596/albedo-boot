package com.albedo.java.modules.sys.domain;

import com.albedo.java.common.persistence.domain.IdEntity;
import com.albedo.java.util.PublicUtil;
import com.albedo.java.util.annotation.DictType;
import com.albedo.java.util.annotation.SearchField;
import com.albedo.java.util.base.Collections3;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2013 albedo All right reserved Author somewhere Created on 2013-10-23 下午4:32:52
 */
@Entity
@Table(name = "sys_role_t")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ToString
@Data
public class Role extends IdEntity<String> {

    public static final String F_SORT = "sort";
    public static final String F_NAME = "name";
    public static final String F_SYSDATA = "sysData";
    private static final long serialVersionUID = 1L;
    /*** 角色名称 */
    @Column(name = "name_")
    @SearchField
    private String name;
    /*** 名称全拼 */
    @Column(name = "en_")
    @SearchField
    private String en;
    /*** 工作流组用户组类型（security-role：管理员、assignment：可进行任务分配、user：普通用户） */
    @Column(name = "type_")
    private String type;
    /*** 组织ID */
    @Column(name = "org_id")
    private String orgId;

    @ManyToOne
    @JoinColumn(name = "org_id", updatable = false, insertable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(hidden = true)
    private Org org;

    @ManyToOne
    @JoinColumn(name = F_SQL_CREATED_BY, updatable = false, insertable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(hidden = true)
    private User creator;

    /*** 是否系统数据  0 是 1否*/
    @Column(name = "sys_data")
    @DictType(name = "sys_yes_no")
    private Integer sysData;
    /*** 可查看的数据范围 */
    @Column(name = "data_scope")
    @DictType(name = "sys_role_scope")
    private Integer dataScope;
    @Column(name = "sort_")
    private Integer sort;
    /*** 组织机构 */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "sys_role_org_t", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "org_id")})
    @Fetch(FetchMode.SUBSELECT)
    @JSONField(serialize = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ApiModelProperty(hidden = true)
    private Set<Org> orgs = Sets.newHashSet();
    /*** 操作权限 */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "SYS_ROLE_MODULE_T", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "module_id")})
    @Fetch(FetchMode.SUBSELECT)
    @JSONField(serialize = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ApiModelProperty(hidden = true)
    private Set<Module> modules = Sets.newHashSet();

    /*** 拥有用户列表 */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    @Where(clause = "status_ = 0")
    @OrderBy("created_date")
    @Fetch(FetchMode.SUBSELECT)
    @JSONField(serialize = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ApiModelProperty(hidden = true)
    private Set<User> users = Sets.newHashSet();

    @Transient
    @JSONField(serialize = false)
    private List<String> moduleIdList;
    @Transient
    @JSONField(serialize = false)
    private List<String> orgIdList;


    public Role() {
        super();
    }

    public Role(String id) {
        this.setId(id);
    }

    public Role(String id, Set<Module> modules) {
        this.setId(id);
        this.modules = modules;
    }

    public Role(String id, String name) {
        this.setId(id);
        this.name = name;
    }


    public List<String> getOrgIdList() {
        if (PublicUtil.isEmpty(orgIdList) && PublicUtil.isNotEmpty(orgs)) {
            orgIdList = Lists.newArrayList();
            orgs.forEach(o -> {
                if (PublicUtil.isNotEmpty(o)) orgIdList.add(o.getId());
            });
        }
        return orgIdList;
    }

    public void setOrgIdList(List<String> orgIdList) {
        this.orgIdList = orgIdList;
        if (PublicUtil.isNotEmpty(orgIdList)) {
            orgs = Sets.newHashSet();
            orgIdList.forEach(o -> {
                if (PublicUtil.isNotEmpty(o)) {
                    orgs.add(new Org(o));
                }
            });
        }
    }

    public String getOrgIds() {
        return Collections3.convertToString(getOrgIdList(), ",");
    }

    public List<String> getModuleIdList() {
        if (PublicUtil.isEmpty(moduleIdList) && PublicUtil.isNotEmpty(modules)) {
            moduleIdList = Lists.newArrayList();
            modules.forEach(m -> {
                if (PublicUtil.isNotEmpty(m)) moduleIdList.add(m.getId());
            });
        }
        return moduleIdList;
    }

    public void setModuleIdList(List<String> moduleIdList) {
        this.moduleIdList = moduleIdList;
        if (PublicUtil.isNotEmpty(moduleIdList)) {
            modules = Sets.newHashSet();
            moduleIdList.forEach(m -> {
                if (PublicUtil.isNotEmpty(m)) {
                    modules.add(new Module(m));
                }
            });
        }
    }

    public String getModuleIds() {
        return Collections3.convertToString(getModuleIdList(), ",");
    }

}
