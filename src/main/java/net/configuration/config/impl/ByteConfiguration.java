package net.configuration.config.impl;

import net.configuration.config.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ByteConfiguration extends FileConfiguration {


    protected ByteConfiguration(File file) throws IOException {
        super(file);
    }

    @Override
    public boolean save() {
        return false;
    }

    @Override
    public boolean reload() {
        return false;
    }

    @Override
    public boolean hasMember(@NotNull String path) {
        return false;
    }

    @Override
    public @NotNull Optional<Byte> getByte(@NotNull String path) {
        return Optional.empty();
    }

    @Override
    public void setByte(@NotNull String path, byte value) {

    }

    @Override
    public @NotNull Optional<Integer> getInt(@NotNull String path) {
        return Optional.empty();
    }

    @Override
    public void setInt(@NotNull String path, int value) {

    }

    @Override
    public @NotNull Optional<Long> getLong(@NotNull String path) {
        return Optional.empty();
    }

    @Override
    public void setLong(@NotNull String path, long value) {

    }

    @Override
    public @NotNull Optional<Short> getShort(@NotNull String path) {
        return Optional.empty();
    }

    @Override
    public void setShort(@NotNull String path, short value) {

    }

    @Override
    public @NotNull Optional<Float> getFloat(@NotNull String path) {
        return Optional.empty();
    }

    @Override
    public void setFloat(@NotNull String path, float value) {

    }

    @Override
    public @NotNull Optional<Double> getDouble(@NotNull String path) {
        return Optional.empty();
    }

    @Override
    public void setDouble(@NotNull String path, double value) {

    }

    @Override
    public @NotNull Optional<Character> getChar(@NotNull String path) {
        return Optional.empty();
    }

    @Override
    public void setChar(@NotNull String path, char value) {

    }

    @Override
    public @NotNull Optional<Boolean> getBoolean(@NotNull String path) {
        return Optional.empty();
    }

    @Override
    public void setBoolean(@NotNull String path, boolean value) {

    }

    @Override
    public @NotNull Optional<String> getString(@NotNull String path) {
        return Optional.empty();
    }

    @Override
    public void setString(@NotNull String path, String value) {

    }

    @Override
    public @NotNull <T> Optional<List<T>> getList(@NotNull String path, @NotNull Class<T> classOfT) {
        return Optional.empty();
    }

    @Override
    public <T> void setList(@NotNull String path, List<T> list) {

    }

    @Override
    public @NotNull <T> Optional<T> get(@NotNull String path, @NotNull Class<T> classOfT) {
        return Optional.empty();
    }

    @Override
    public <T> void set(@NotNull String path, T value) {

    }
}
