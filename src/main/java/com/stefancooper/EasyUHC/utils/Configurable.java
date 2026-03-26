package com.stefancooper.EasyUHC.utils;

import com.stefancooper.EasyUHC.enums.ConfigKey;

public record Configurable<T>(ConfigKey key, T value) {}
