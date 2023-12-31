package com.bxc.service.stats.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bxc.service.stats.rpc.gethelloworld.GetHelloWorldRequest;
import com.bxc.service.stats.rpc.gethelloworld.GetHelloWorldResponse;

import io.grpc.internal.testing.StreamRecorder;

public class GetHelloWorldRpcTest {
  private static final int TIMEOUT_MILLIS = 500;
  private static StatsService service;

  @BeforeClass
  public static void setup() {
    service = new StatsService();
  }

  @Test
  public void getHelloWorldRpc_returnsMessage() throws Exception {
    StreamRecorder<GetHelloWorldResponse> responseObserver = StreamRecorder
        .create();

    service.getHelloWorldRpc(GetHelloWorldRequest.getDefaultInstance(),
        responseObserver);

    GetHelloWorldResponse response = responseObserver.firstValue()
        .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    assertEquals(
        GetHelloWorldResponse.newBuilder().setMessage("Hello world!").build(),
        response);
  }
}
