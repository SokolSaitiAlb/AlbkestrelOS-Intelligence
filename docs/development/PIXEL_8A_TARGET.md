# Pixel 8a (`akita`) Development Target

## Target profile

- Device: Google Pixel 8a
- Codename: `akita`
- SoC: Google Tensor G3
- RAM: 8 GB
- Storage: 128 GB
- Platform: Android/AOSP-derived ROM

## Engineering constraints

The device is a constrained-memory mobile target. Do not assume that a model with a particular parameter count or quantization will meet latency, memory, thermal, or battery targets.

## Benchmark before selecting a model

Measure:
- cold and warm model load time;
- peak and steady-state resident memory;
- time to first token;
- generation throughput;
- CPU/GPU/NPU utilization where observable;
- energy and thermal impact;
- behavior under memory pressure;
- behavior after process death and model reload.

## Initial model evaluation

Small quantized models may be evaluated as candidates. The 1B–3B / 4-bit range is an experiment, not a requirement or a guarantee of suitability. Select the backend and model only after licensing, Android ABI, operator support, acceleration, and device benchmarks are reviewed.
