package com.wisdom.separate.model;

import java.io.Serializable;
import java.util.Date;

public class MonitorLog implements Serializable {


    private static final long serialVersionUID = -7848644750611797035L;

    private long id;

    private String title;

    private String context;

    private String param;

    private String result;

    private int status;

    private String machineIp;

    private String logType;

    private Date createTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public  enum  LogStatus{

        effective(1),
        invalid(0);

        private int status;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        LogStatus(int i){
            this.status = i;
        }

    }

    public enum LogType{
        EXCEPTION,
        ERROR,
        INFO,
    }
}
