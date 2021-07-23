package com.mutool.mock.hsf;

import com.taobao.hsf.invocation.Invocation;
import com.taobao.hsf.invocation.InvocationHandler;
import com.taobao.hsf.invocation.RPCResult;
import com.taobao.hsf.invocation.filter.ClientFilter;
import com.taobao.hsf.util.concurrent.ListenableFuture;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/1/30 15:10<br>
 */
public class RPCServerFilter implements ClientFilter {

    @Override
    public ListenableFuture<RPCResult> invoke(InvocationHandler invocationHandler, Invocation invocation) throws Throwable {
        return invocationHandler.invoke(invocation);
    }

    @Override
    public void onResponse(Invocation invocation, RPCResult rpcResult) {
        rpcResult.setAppResponse("this value");
    }

}
