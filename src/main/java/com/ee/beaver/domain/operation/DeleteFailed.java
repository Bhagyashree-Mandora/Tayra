package com.ee.beaver.domain.operation;

public class DeleteFailed extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DeleteFailed(final String message) {
      super(message);
    }
}