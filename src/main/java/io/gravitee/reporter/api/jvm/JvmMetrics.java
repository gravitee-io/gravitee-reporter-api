/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.reporter.api.jvm;

import io.gravitee.reporter.api.AbstractMetrics;
import io.gravitee.reporter.api.metrics.Gauge;
import io.gravitee.reporter.api.metrics.Metric;

import java.util.Map;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 * @author GraviteeSource Team
 */
public final class JvmMetrics extends AbstractMetrics {

    private Gauge<Double> cpuUsage;
    private Map<String, Metric> threadsState;
    private Map<String, Metric> garbageCollector;
    private Map<String, Metric> memoryUsage;

    private JvmMetrics(long timestamp) {
        super(timestamp);
    }

    public Gauge<Double> getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Gauge<Double> cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Map<String, Metric> getThreadsState() {
        return threadsState;
    }

    public void setThreadsState(Map<String, Metric> threadsState) {
        this.threadsState = threadsState;
    }

    public Map<String, Metric> getGarbageCollector() {
        return garbageCollector;
    }

    public void setGarbageCollector(Map<String, Metric> garbageCollector) {
        this.garbageCollector = garbageCollector;
    }

    public Map<String, Metric> getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Map<String, Metric> memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public static Builder on(long timestamp) {
        return new Builder(timestamp);
    }

    public static class Builder {

        private final long timestamp;

        private Builder(long timestamp) {
            this.timestamp = timestamp;
        }

        public JvmMetrics build() {
            return new JvmMetrics(timestamp);
        }
    }
}
