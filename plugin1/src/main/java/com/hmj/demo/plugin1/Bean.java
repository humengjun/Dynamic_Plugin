package com.hmj.demo.plugin1;

import com.hmj.demo.sharelibrary.IBean;

public class Bean implements IBean {
    String value = "This is Plugin1";

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void setListener(Listener listener) {
        listener.onResult("bind success");
    }
}
