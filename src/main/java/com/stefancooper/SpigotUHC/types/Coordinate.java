package com.stefancooper.SpigotUHC.types;

public record Coordinate(double x, double z) {

    @Override
    public String toString() {
        return "Coordinate(" + x() + "," + z() + ")";
    }
}

