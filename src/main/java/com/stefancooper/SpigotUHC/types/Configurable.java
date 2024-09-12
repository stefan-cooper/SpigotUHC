package com.stefancooper.SpigotUHC.types;

import com.stefancooper.SpigotUHC.resources.ConfigKey;

public record Configurable<T>(ConfigKey key, T value) {}
