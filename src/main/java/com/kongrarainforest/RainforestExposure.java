package com.kongrarainforest;

/**
 * Interface implemented (via mixin-free duck-typing helper) to store per-player
 * rain exposure. Since we avoid mixins, we use a static tracking map instead.
 */
public interface RainforestExposure {
    int kongra$getRainExposure();
    void kongra$setRainExposure(int value);
}