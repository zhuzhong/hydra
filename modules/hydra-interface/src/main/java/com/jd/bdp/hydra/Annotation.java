package com.jd.bdp.hydra;

import java.io.Serializable;

/**
 * Date: 13-3-18 Time: 下午3:36
 */
public class Annotation implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -381720121226343875L;
    public static final String CLIENT_SEND = "cs";
    public static final String CLIENT_RECEIVE = "cr";
    public static final String SERVER_SEND = "ss";
    public static final String SERVER_RECEIVE = "sr";
    
    private String key;
    private String value; //增加参数，记录rpc方法调用的参数
    private Endpoint host;
    private Long timestamp;
    private Integer duration;

   // private String paras;// 增加参数，记录rpc方法调用的参数

   

    public Annotation() {

    }

    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Annotation(Long timestamp, String key, Endpoint host) {
        this.timestamp = timestamp;
        this.key = key;
        this.host = host;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Endpoint getHost() {
        return host;
    }

    public void setHost(Endpoint host) {
        this.host = host;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Annotation{" + "timestamp=" + timestamp + ", key='" + key + '\'' + ", host=" + host + ", duration=" + duration + ", value=" + value+"}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Annotation))
            return false;

        Annotation that = (Annotation) o;

        if (duration != null && !duration.equals(that.duration))
            return false;
        if (!host.equals(that.host))
            return false;
        if (!timestamp.equals(that.timestamp))
            return false;
        if (!key.equals(that.key))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = timestamp.hashCode();
        result = 31 * result + key.hashCode();
        result = 31 * result + host.hashCode();
        result = 31 * result + duration.hashCode();
        return result;
    }
}
