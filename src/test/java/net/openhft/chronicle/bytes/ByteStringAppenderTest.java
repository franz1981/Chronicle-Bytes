/*
 *     Copyright (C) 2015  higherfrequencytrading.com
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.openhft.chronicle.bytes;

import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ByteStringAppenderTest {
    Bytes bytes = Bytes.elasticByteBuffer();

    @Test
    public void testAppend() {
        long expected = 1234;
        bytes.append(expected);

        assertEquals(expected, bytes.parseLong());
    }

    @Test
    public void testAppendWithOffset() {
        bytes.readLimit(20);
        bytes.writeLimit(20);
        for (long expected : new long[]{123456, 12345, 1234, 123, 12, 1, 0}) {
            bytes.append(10, expected, 6);
            assertEquals(expected, bytes.parseLong(10));
        }
    }

    @Test
    public void testAppendWithOffsetNeg() {
        bytes.readLimit(20);
        bytes.writeLimit(20);
        for (long expected : new long[]{-123456, 12345, -1234, 123, -12, 1, 0}) {
            bytes.append(10, expected, 7);
            assertEquals(expected, bytes.parseLong(10));
        }
    }

    @Test
    public void testAppendDouble() throws IOException {
        testAppendDouble0(-6.895305375646115E24);
        Random random = new Random(1);
        for (int i = 0; i < 100000; i++) {
            double d = Math.pow(1e32, random.nextDouble()) / 1e6;
            if (i % 3 == 0) d = -d;
            testAppendDouble0(d);
        }
    }

    private void testAppendDouble0(double d) throws IOException {
        bytes.clear();
        bytes.append(d).append(' ');

        double d2 = bytes.parseDouble();
        assertEquals(d, d2, 0);

/* assumes self terminating.
        bytes.clear();
        bytes.append(d);
        bytes.flip();
        double d3 = bytes.parseDouble();
        Assert.assertEquals(d, d3, 0);
*/
    }

    @Test
    public void testAppendLongDecimal() throws IOException {
        bytes.appendDecimal(128, 0).append('\n');
        bytes.appendDecimal(128, 1).append('\n');
        bytes.appendDecimal(128, 2).append('\n');
        bytes.appendDecimal(128, 3).append('\n');
        bytes.appendDecimal(128, 4).append('\n');

        bytes.appendDecimal(0, 0).append('\n');
        bytes.appendDecimal(0, 1).append('\n');
        bytes.appendDecimal(0, 4).append('\n');
        bytes.appendDecimal(1, 0).append('\n');
        bytes.appendDecimal(1, 1).append('\n');
        bytes.appendDecimal(1, 2).append('\n');
        bytes.appendDecimal(1, 3).append('\n');
        bytes.appendDecimal(1, 4).append('\n');

        assertEquals("128\n" +
                "12.8\n" +
                "1.28\n" +
                "0.128\n" +
                "0.0128\n" +
                "0\n" +
                "0.0\n" +
                "0.0000\n" +
                "1\n" +
                "0.1\n" +
                "0.01\n" +
                "0.001\n" +
                "0.0001\n", bytes.toString());
    }

    @Test
    public void testAppendDoublePrecision() throws IOException {
        bytes.append(1.28, 0).append('\n');
        bytes.append(-1.28, 1).append('\n');
        bytes.append(1.28, 2).append('\n');
        bytes.append(-1.28, 3).append('\n');
        bytes.append(1.28, 4).append('\n');

        bytes.append(0, 0).append('\n');
        bytes.append(-0, 1).append('\n');
        bytes.append(0, 4).append('\n');
        bytes.append(1, 0).append('\n');
        bytes.append(-1.11, 1).append('\n');
        bytes.append(0.111, 2).append('\n');
        bytes.append(1.1, 3).append('\n');
        bytes.append(-0.01111, 4).append('\n');

        assertEquals("1\n" +
                "-1.3\n" +
                "1.28\n" +
                "-1.280\n" +
                "1.2800\n" +
                "0\n" +
                "0.0\n" +
                "0.0000\n" +
                "1\n" +
                "-1.1\n" +
                "0.11\n" +
                "1.100\n" +
                "-0.0111\n", bytes.toString());
    }
}