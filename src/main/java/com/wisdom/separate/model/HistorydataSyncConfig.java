package com.wisdom.separate.model;

import java.util.Date;

public class HistorydataSyncConfig {
    private Integer id;

    private String tableName;

    private Long positionIndex;

    private Integer count;

    private Date createTime;

    private Date updateTime;

    private Short flag;

    private Short dayCount;

    private Long insertCount;

    private Short priority;

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

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	public Short getFlag() {
		return flag;
	}

	public void setFlag(Short flag) {
		this.flag = flag;
	}

	public Short getDayCount() {
		return dayCount;
	}

	public void setDayCount(Short dayCount) {
		this.dayCount = dayCount;
	}

	public Long getInsertCount() {
		return insertCount;
	}

	public void setInsertCount(Long insertCount) {
		this.insertCount = insertCount;
	}

	public Short getPriority() {
		return priority;
	}

	public void setPriority(Short priority) {
		this.priority = priority;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "WisdomHistorydataSyncConfig [id=" + id + ", tableName=" + tableName + ", positionIndex=" + positionIndex
				+ ", count=" + count + ", createTime=" + createTime + ", updateTime=" + updateTime + ", flag=" + flag
				+ ", dayCount=" + dayCount + ", insertCount=" + insertCount + ", priority=" + priority + "]";
	}

    
}