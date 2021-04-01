package com.wj.server.register;

public class ServiceObject {

    private String name;

    private Class<?> clazz;
  
    private Object obj;

    public ServiceObject(String name, Class<?> clazz, Object obj) {
        this.name = name;
        this.clazz = clazz;
        this.obj = obj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
