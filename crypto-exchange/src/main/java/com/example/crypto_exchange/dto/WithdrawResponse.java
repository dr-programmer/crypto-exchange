package com.example.crypto_exchange.dto;

public class WithdrawResponse {
    private String txId;
    private String txHash ;
    private String status;

    public WithdrawResponse(String txId, String txHash, String status) {
        this.txId = txId;
        this.txHash = txHash;
        this.status = status;
    }
    
    public String getTxId() {
        return txId;
    }

    public String getTxHash() {
        return txHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }
}
