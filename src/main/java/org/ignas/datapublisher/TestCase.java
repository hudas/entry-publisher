package org.ignas.datapublisher;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_cases")
public class TestCase {

    @Id
    private Long id;

    private String debtor;

    private String creditor;

    private LocalDateTime time;

    private BigDecimal amount;

    private BigDecimal locationLatitude;

    private BigDecimal locationLongtitude;

    private boolean fraud;

    private boolean processed;

    public TestCase() {
    }

    public Long getId() {
        return id;
    }

    public String getDebtor() {
        return debtor;
    }

    public String getCreditor() {
        return creditor;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getLocationLatitude() {
        return locationLatitude;
    }

    public BigDecimal getLocationLongtitude() {
        return locationLongtitude;
    }

    public boolean isFraud() {
        return fraud;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public void markProcessed() {
        this.processed = true;
    }
}
