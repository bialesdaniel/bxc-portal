package com.bxc.data;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.bxc.data.rpc.DataService;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.protobuf.services.ProtoReflectionService;

public class DataServer {
  private static final Logger logger = Logger
      .getLogger(DataServer.class.getName());

  private final int port;
  private final Server server;

  public DataServer(int port) throws IOException {
    this.port = port;
    server = Grpc
        .newServerBuilderForPort(this.port, InsecureServerCredentials.create())
        .addService(new DataService())
        .addService(ProtoReflectionService.newInstance()).build();
  }

  /** Start serving requests. */
  public void start() throws IOException {
    server.start();
    logger.info("Server started, listening on " + port + ".");
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM
        // shutdown hook.
        System.err.println(
            "*** shutting down gRPC server since JVM is shutting down.");
        try {
          DataServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  /** Stop serving requests and shutdown resources. */
  public void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon
   * threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(System.getenv("PORT"));
    DataServer server = new DataServer(port);
    server.start();
    server.blockUntilShutdown();
  }
}