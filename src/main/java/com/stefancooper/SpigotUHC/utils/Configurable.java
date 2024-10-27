package com.stefancooper.SpigotUHC.utils;

import com.stefancooper.SpigotUHC.enums.ConfigKey;

public record Configurable<T>(ConfigKey key, T value) {}
