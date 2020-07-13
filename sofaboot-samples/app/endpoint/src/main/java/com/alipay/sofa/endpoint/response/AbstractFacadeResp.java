package com.alipay.sofa.endpoint.response;

/**
 * Common response data.
 */
public class AbstractFacadeResp {

    private boolean success = false;

    private String resultMsg;

    private String requestId;

    public AbstractFacadeResp(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
