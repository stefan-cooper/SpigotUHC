package com.stefancooper.EasyUHC.base.records;

import com.stefancooper.EasyUHC.base.ConfigKey;

public record Configurable<T>(ConfigKey key, T value) {}
