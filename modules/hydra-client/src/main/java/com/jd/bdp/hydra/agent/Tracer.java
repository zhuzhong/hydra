package com.jd.bdp.hydra.agent;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.jd.bdp.hydra.Annotation;
import com.jd.bdp.hydra.BinaryAnnotation;
import com.jd.bdp.hydra.Endpoint;
import com.jd.bdp.hydra.Span;
import com.jd.bdp.hydra.agent.support.SampleImp;

/**
 * Date: 13-3-19 Time: 下午4:14 系统跟踪类(单例)
 */

public class Tracer {

    private static final Logger logger = LoggerFactory.getLogger(Tracer.class);

    // private static Tracer tracer = null;

    private Sampler sampler = new SampleImp();

    // 传递parentSpan
    private ThreadLocal<Span> spanThreadLocal = new ThreadLocal<Span>();

    // TraceService traceService;

    private Tracer() {
    }

    public void removeParentSpan() {
        spanThreadLocal.remove();
    }

    public Span getParentSpan() {
        return spanThreadLocal.get();
    }

    public void setParentSpan(Span span) {
        spanThreadLocal.set(span);
    }

    // 构件Span，参数通过上游接口传递过来
    public Span genSpan(Long traceId, Long pid, Long id, String spanname, boolean isSample, String serviceId) {
        Span span = new Span();
        span.setId(id);
        span.setParentId(pid);
        span.setSpanName(spanname);
        span.setSample(isSample);
        span.setTraceId(traceId);
        span.setServiceId(serviceId);
        return span;
    }
  
    // 构件rootSpan,是否采样
    public Span newSpan(String spanname, Endpoint endpoint, String serviceId) {
        boolean s = isSample();
        Span span = new Span();
        span.setTraceId(s ? genTracerId() : null);
        span.setId(s ? genSpanId() : null);
        span.setSpanName(spanname);
        span.setServiceId(serviceId);
        span.setSample(s);
        // if (s) {//应用名写入
        // BinaryAnnotation appname = new BinaryAnnotation();
        // appname.setKey("dubbo.applicationName");
        // appname.setValue(transfer.appName().getBytes());
        // appname.setType("string");
        // appname.setHost(endpoint);
        // span.addBinaryAnnotation(appname);
        // }
        return span;
    }

    public Endpoint newEndPoint() {
        return new Endpoint();
    }

    private static class TraceHolder {
        static Tracer instance = new Tracer();
    }

    public static Tracer getTracer() {
        return TraceHolder.instance;
    }

    public void start() throws Exception {
        transfer.start();
    }

    // 启动后台消息发送线程
    public static void startTraceWork() {
        try {
            getTracer().start();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public boolean isSample() {
        return sampler.isSample() && (transfer != null && transfer.isReady());
    }

    public void addBinaryAnntation(BinaryAnnotation b) {
        Span span = spanThreadLocal.get();
        if (span != null) {
            span.addBinaryAnnotation(b);
        }
    }

    /**
     * 主要就是将调用分成四个阶段： cs:客户端发送记录； sr:服务端接收记录; ss:服务端发送记录; cr:客户端接收记录
     * 
     * 
     */
    // 构件cs annotation
    public void clientSendRecord(Span span, Endpoint endpoint, long start, String paras) {
        Annotation annotation = new Annotation();
        annotation.setKey(Annotation.CLIENT_SEND);
        annotation.setHost(endpoint);
        annotation.setTimestamp(start);
        annotation.setValue(paras);// 记录rpc请求方法的参数
        span.addAnnotation(annotation);
    }

    // 构件cr annotation
    public void clientReceiveRecord(Span span, Endpoint endpoint, long end, String paras) {
        Annotation annotation = new Annotation();
        annotation.setKey(Annotation.CLIENT_RECEIVE);
        annotation.setHost(endpoint);
        annotation.setTimestamp(end);
        annotation.setValue(paras);// 记录rpc请求方法的参数
        span.addAnnotation(annotation);
        transfer.syncSend(span);
    }

    // 构件sr annotation
    public void serverReceiveRecord(Span span, Endpoint endpoint, long start, String paras) {
        Annotation annotation = new Annotation();
        annotation.setKey(Annotation.SERVER_RECEIVE);
        annotation.setHost(endpoint);
        annotation.setTimestamp(start);
        annotation.setValue(paras);// 记录rpc请求方法的参数

        span.addAnnotation(annotation);
        spanThreadLocal.set(span);
    }

    // 构件 ss annotation
    public void serverSendRecord(Span span, Endpoint endpoint, long end, String paras) {
        Annotation annotation = new Annotation();
        annotation.setTimestamp(end);
        annotation.setHost(endpoint);
        annotation.setKey(Annotation.SERVER_SEND);
        annotation.setValue(paras);// 记录rpc请求方法的参数
        span.addAnnotation(annotation);
        transfer.syncSend(span);
    }

    public String getServiceId(String name) {
        String id = null;
        if (transfer != null) {
            id = transfer.getServiceId(name);
        }
        return id;
    }

    public Long genTracerId() {
        return transfer.getTraceId();
    }

    public Long genSpanId() {
        return transfer.getSpanId();
    }

    /*
     * public void setTraceService(TraceService traceService) {
     * this.traceService = traceService; }
     */
    private SyncTransfer transfer = null;

    public void setTransfer(SyncTransfer transfer) {
        this.transfer = transfer;
    }
}
