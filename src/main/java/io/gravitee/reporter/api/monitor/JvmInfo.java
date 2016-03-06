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
package io.gravitee.reporter.api.monitor;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 * @author GraviteeSource Team
 */
public final class JvmInfo {

    public long timestamp = -1;
    public long uptime;
    public Mem mem;
    public Threads threads;
    public GarbageCollectors gc;

    public JvmInfo(long timestamp, long uptime) {
        this.timestamp = timestamp;
        this.uptime = uptime;
    }

    public static class Mem {
        public long heapCommitted;
        public long heapUsed;
        public long heapMax;
        public long nonHeapCommitted;
        public long nonHeapUsed;

        public MemoryPool[] pools = new MemoryPool[0];
    }

    public static class MemoryPool {
        String name;
        long used;
        long max;

        long peakUsed;
        long peakMax;

        public MemoryPool(String name, long used, long max, long peakUsed, long peakMax) {
            this.name = name;
            this.used = used;
            this.max = max;
            this.peakUsed = peakUsed;
            this.peakMax = peakMax;
        }
    }

    public static class Threads {
        public int count;
        public int peakCount;
    }

    public static class GarbageCollectors {
        public GarbageCollector[] collectors;
    }

    public static class GarbageCollector {
        public String name;
        public long collectionCount;
        public long collectionTime;
    }
}
