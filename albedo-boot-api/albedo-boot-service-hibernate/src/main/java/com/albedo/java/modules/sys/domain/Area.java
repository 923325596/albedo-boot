/**
 * Copyright &copy; 2015 <a href="http://www.bs-innotech.com/">bs-innotech</a> All rights reserved.
 */
package com.albedo.java.modules.sys.domain;

import com.albedo.java.common.persistence.domain.TreeUserEntity;
import com.albedo.java.util.annotation.DictType;
import com.albedo.java.util.annotation.SearchField;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 区域管理Entity 区域管理
 *
 * @author admin
 * @version 2017-01-01
 */
@Entity
@Table(name = "sys_area_t")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ToString @Data
public class Area extends TreeUserEntity<Area> {

    /**
     * F_SHORTNAME short_name  :  区域简称
     */
    public static final String F_SHORTNAME = "shortName";
    /**
     * F_LEVEL level_  :  区域等级
     */
    public static final String F_LEVEL = "level";
    /**
     * F_CODE code_  :  区域编码
     */
    public static final String F_CODE = "code";
    private static final long serialVersionUID = 1L;

    //columns START
    /**
     * shortName 区域简称
     */
    @Size(max = 32)
    @Column(name = "short_name", unique = false, nullable = true, length = 32)
    private String shortName;
    /**
     * level 区域等级
     */
    @Column(name = "level_", unique = false, nullable = true)
    @DictType(name = "sys_area_type")
    private Integer level;
    /**
     * code 区域编码
     */
    @Size(max = 32)
    @Column(name = "code_", unique = true, nullable = true, length = 32)
    @SearchField
    @NotNull
    private String code;

    //columns END
    public Area() {
    }

    public Area(String id) {
        this.id = id;
    }


    /**
     * shortName 区域简称
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * shortName 区域简称
     */
    public void setShortName(String value) {
        this.shortName = value;
    }

    /**
     * level 区域等级
     */
    public Integer getLevel() {
        return this.level;
    }

    /**
     * level 区域等级
     */
    public void setLevel(Integer value) {
        this.level = value;
    }

    /**
     * code 区域编码
     */
    public String getCode() {
        return this.code;
    }

    /**
     * code 区域编码
     */
    public void setCode(String value) {
        this.code = value;
    }

}
