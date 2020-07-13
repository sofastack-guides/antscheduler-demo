package com.alipay.sofa.endpoint.response;

/**
 * Response data wrapper in the format of json.
 */
public class RestSampleFacadeResp<T> extends AbstractFacadeResp {

    private T data;


    public RestSampleFacadeResp() {
        super(true);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RestSampleFacadeResp{" +
                "data=" + data +
                '}';
    }
}
