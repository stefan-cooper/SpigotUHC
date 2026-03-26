package com.stefancooper.EasyUHC.types;

public record Coordinate(double x, double z) {

    @Override
    public String toString() {
        return "Coordinate(" + x() + "," + z() + ")";
    }
}

