package site.assad.phoenix.dbutil;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.sql.Timestamp;

/**
 * Phoenix 实体
 *
 * @author yulinying
 * @since 2020/8/20
 */
public class Record {

    private String uid;
    private Timestamp punchDate;
    private Integer amount;
    private String username;

    public Record() {
    }

    public Record(String uid, Timestamp punchDate, Integer amount, String username) {
        this.uid = uid;
        this.punchDate = punchDate;
        this.amount = amount;
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Timestamp getPunchDate() {
        return punchDate;
    }

    public void setPunchDate(Timestamp punchDate) {
        this.punchDate = punchDate;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Record{" +
                "uid='" + uid + '\'' +
                ", punchDate=" + punchDate +
                ", amount=" + amount +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        return new EqualsBuilder()
                .append(uid, record.uid)
                .append(punchDate, record.punchDate)
                .append(amount, record.amount)
                .append(username, record.username)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(uid)
                .append(punchDate)
                .append(amount)
                .append(username)
                .toHashCode();
    }
}

