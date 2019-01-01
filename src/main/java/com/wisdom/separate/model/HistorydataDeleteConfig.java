package com.wisdom.separate.model;

import java.util.Date;

public class HistorydataDeleteConfig {
    private Integer id;

    private String tableName;

    private Long positionIndex;

    private Date createTime;

    private Date updateTime;

    private Integer count;

    private Short flag;

    private Short dayCount;

    private Long deleteCount;

    private Short priority;

    private Long maxId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Long getPositionIndex() {
		return positionIndex;
	}

	public void setPositionIndex(Long positionIndex) {
		this.positionIndex = positionIndex;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Short getFlag() {
        return flag;
    }

    public void setFlag(Short flag) {
        this.flag = flag;
    }


    public Short getPriority() {
        return priority;
    }

    public void setPriority(Short priority) {
        this.priority = priority;
    }

	public Short getDayCount() {
		return dayCount;
	}

	public void setDayCount(Short dayCount) {
		this.dayCount = dayCount;
	}

	public Long getDeleteCount() {
		return deleteCount;
	}

	public void setDeleteCount(Long deleteCount) {
		this.deleteCount = deleteCount;
	}

	public Long getMaxId() {
		return maxId;
	}

	public void setMaxId(Long maxId) {
		this.maxId = maxId;
	}

}