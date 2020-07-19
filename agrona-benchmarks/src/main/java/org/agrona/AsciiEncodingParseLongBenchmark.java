package org.agrona;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

public class AsciiEncodingParseLongBenchmark
{

    private static final int VALUE_COUNT = 1024 * 1024;
    private static final int VALUE_COUNT_MASK = VALUE_COUNT - 1;

    @State(Scope.Thread)
    // 80% are single digit numbers, 20% are 2-digit numbers to avoid taking the same branch all the time
    public static class MostlySingleDigits
    {
        public final String[] values = new String[VALUE_COUNT];
        public int next;

        @Setup
        public void setUp()
        {
            final int singleDigitLimit = VALUE_COUNT * 80 / 100;
            for (int i = 0; i < singleDigitLimit; i++)
            {
                final long nextValue = ThreadLocalRandom.current().nextLong(10);
                values[i] = String.valueOf(nextValue);
            }
            for (int i = singleDigitLimit; i < VALUE_COUNT; i++)
            {
                final long nextValue = 10 + ThreadLocalRandom.current().nextLong(90);
                values[i] = String.valueOf(nextValue);
            }
            Collections.shuffle(Arrays.asList(values));
        }
    }


    @State(Scope.Thread)
    public static class Millions
    {
        public final String[] values = new String[VALUE_COUNT];
        public int next;

        @Setup
        public void setUp()
        {
            for (int i = 0; i < VALUE_COUNT; i++)
            {
                final long nextValue = 1_000_000 * (1 + ThreadLocalRandom.current().nextLong(50));
                values[i] = String.valueOf(nextValue);
            }
        }
    }


    @State(Scope.Thread)
    public static class RandomNonNegativeValues
    {
        public final String[] values = new String[VALUE_COUNT];
        public int next;

        @Setup
        public void setUp()
        {
            for (int i = 0; i < VALUE_COUNT; i++)
            {
                final long nextValue = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
                values[i] = String.valueOf(nextValue);
            }
        }
    }


    @State(Scope.Thread)
    public static class RandomValues
    {
        public final String[] values = new String[VALUE_COUNT];
        public int next;

        @Setup
        public void setUp()
        {
            for (int i = 0; i < VALUE_COUNT; i++)
            {
                final long nextValue = Long.MIN_VALUE / 2 + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
                values[i] = String.valueOf(nextValue);
            }
        }
    }

    @Benchmark
    public long mostlySingleDigits(final MostlySingleDigits values)
    {
        final String value = values.values[(values.next++) & VALUE_COUNT_MASK];
        return AsciiEncoding.parseLongAscii(value, 0, value.length());
    }

    @Benchmark
    public long millions(final Millions values)
    {
        final String value = values.values[(values.next++) & VALUE_COUNT_MASK];
        return AsciiEncoding.parseLongAscii(value, 0, value.length());
    }

    @Benchmark
    public long randomNonNegativeValues(final RandomNonNegativeValues values)
    {
        final String value = values.values[(values.next++) & VALUE_COUNT_MASK];
        return AsciiEncoding.parseLongAscii(value, 0, value.length());
    }

    @Benchmark
    public long randomValues(final RandomValues values)
    {
        final String value = values.values[(values.next++) & VALUE_COUNT_MASK];
        return AsciiEncoding.parseLongAscii(value, 0, value.length());
    }
}
