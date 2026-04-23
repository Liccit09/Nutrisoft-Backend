package com.nutrisoft.core.shared.ddd;

public interface Identifier<T> extends ValueObject {

  T value();
}
