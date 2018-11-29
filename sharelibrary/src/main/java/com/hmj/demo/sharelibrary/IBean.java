package com.hmj.demo.sharelibrary;

public interface IBean {

    String getValue();

    void setValue(String value);

    void setListener(Listener listener);

    interface Listener{
        void onResult(String result);
    }
}
