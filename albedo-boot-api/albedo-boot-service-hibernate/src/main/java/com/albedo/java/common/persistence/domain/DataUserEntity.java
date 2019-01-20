package com.albedo.java.common.persistence.domain;

import com.albedo.java.modules.sys.domain.User;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;


@MappedSuperclass
public class DataUserEntity<PK extends Serializable> extends IdEntity<PK> {
    @ManyToOne
    @JoinColumn(name = F_SQL_CREATED_BY, updatable = false, insertable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @JSONField(serialize = false)
    private User creator;
    @ManyToOne
    @JoinColumn(name = F_SQL_LAST_MODIFIED_BY, updatable = false, insertable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    @JSONField(serialize = false)
    private User modifier;

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getModifier() {
        return modifier;
    }

    public void setModifier(User modifier) {
        this.modifier = modifier;
    }

    public DataUserEntity() {
    }
}
