package org.ignas.datapublisher.dto;

public class MarkDTO {

    private String transactionId;

    public MarkDTO(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
