package com.plunder.plunder.providers;

public class ProviderClientNotReadyException extends Exception {
  public ProviderClientNotReadyException() {
    super("The provider client is not ready");
  }
}
