package com.library.entity;


import javax.persistence.Id;
import java.util.Date;

public class  Borrow{

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBorrowedBookId() {
        return borrowedBookId;
    }

    public void setBorrowedBookId(Integer borrowedBookId) {
        this.borrowedBookId = borrowedBookId;
    }

    public Integer getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(Integer borrowerId) {
        this.borrowerId = borrowerId;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(Date limitTime) {
        this.limitTime = limitTime;
    }

    @Override
    public String toString() {
        return "Borrow{" +
                "id=" + id +
                ", borrowedBookId=" + borrowedBookId +
                ", borrowerId=" + borrowerId +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", limitTime=" + limitTime +
                '}';
    }

    @Id
    private Integer id;
    private Integer borrowedBookId;
    private Integer borrowerId;
    private Date beginTime;
    private Date endTime;
    private Date limitTime;

}