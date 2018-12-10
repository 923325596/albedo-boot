/**
 * Copyright &copy; 2015 <a href="http://www.bs-innotech.com/">bs-innotech</a> All rights reserved.
 */
package com.albedo.java.modules.sys.domain;

import com.albedo.java.common.persistence.domain.DataEntity;
import com.albedo.java.common.persistence.domain.DataUserEntity;
import com.albedo.java.common.persistence.pk.IdGen;
import com.albedo.java.util.annotation.DictType;
import com.albedo.java.util.annotation.SearchField;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotBlank;

import javax.persistence.*;
/**
 * 任务调度管理Entity 任务调度
 *
 * @author lj
 * @version 2017-01-23
 */
@Entity
@Table(name = "sys_task_schedule_job_t")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@ToString @Data
public class TaskScheduleJob extends DataUserEntity<String> {

    /**
     * F_NAME name_ : 名称
     */
    public static final String F_NAME = "name";
    /**
     * F_GROUP group_ : 分组
     */
    public static final String F_GROUP = "group";
    /**
     * F_STARTSTATUS start_status : 是否启动
     */
    public static final String F_JOBSTATUS = "jobStatus";
    /**
     * F_CRONEXPRESSION cron_expression : cron表达式
     */
    public static final String F_CRONEXPRESSION = "cronExpression";
    /**
     * F_BEANCLASS bean_class : 调用类名
     */
    public static final String F_BEANCLASS = "beanClass";
    /**
     * F_ISCONCURRENT is_concurrent : 是否当前任务
     */
    public static final String F_ISCONCURRENT = "isConcurrent";
    /**
     * F_SPRINGID spring_id : spring bean
     */
    public static final String F_SPRINGID = "springId";
    /**
     * F_SOURCEID sourceId : 业务编号
     */
    public static final String F_SOURCEID = "sourceId";
    /**
     * F_METHODNAME method_name : 调用方法名
     */
    public static final String F_METHODNAME = "methodName";
    /**
     * F_METHODPARAMS method_params : 调用方法参数 json
     */
    public static final String F_METHODPARAMS = "methodParams";
    private static final long serialVersionUID = 1L;
    // columns START
    /**
     * name 名称
     */
    @Size(max = 255)
    @Column(name = "name_", unique = false, nullable = true, length = 255)
    private String name;
    /**
     * group 分组
     */
    @Size(max = 255)
    @Column(name = "group_", unique = false, nullable = true, length = 255)
    private String group;
    /**
     * jobStatus 任务状态
     */
    @Column(name = "job_status", unique = false, nullable = true)
    @DictType(name = "sys_yes_no")
    private String jobStatus;
    /**
     * cronExpression cron表达式
     */
    @NotBlank
    @Size(max = 255)
    @Column(name = "cron_expression", unique = false, nullable = false, length = 255)
    private String cronExpression;
    /**
     * beanClass 调用类名
     */
    @Size(max = 255)
    @Column(name = "bean_class", unique = false, nullable = true, length = 255)
    private String beanClass;
    /**
     * isConcurrent 是否当前任务
     */
    @Column(name = "is_concurrent", unique = false, nullable = true)
    @DictType(name = "sys_yes_no")
    private Integer isConcurrent;
    /**
     * springId spring bean
     */
    @Size(max = 255)
    @Column(name = "spring_id", unique = false, nullable = true, length = 255)
    private String springId;
    /**
     * sourceId 业务编号
     */
    @Size(max = 32)
    @Column(name = "source_id", unique = false, nullable = true, length = 32)
    private String sourceId;
    /**
     * methodName 调用方法名
     */
    @NotBlank
    @Size(max = 255)
    @Column(name = "method_name", unique = false, nullable = false, length = 255)
    private String methodName;
    @Column(name = "method_params", unique = false, nullable = false, length = 255)
    private String methodParams;

    // columns END
    public TaskScheduleJob() {
    }

    public TaskScheduleJob(String id) {
        this.id = id;
    }

}
