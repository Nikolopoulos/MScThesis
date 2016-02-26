/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//**
 *
 * @author Zolotas
 */
package affinitySupport;

import java.text.ParseException;
import java.util.StringTokenizer;

import com.sun.jna.*;
import com.sun.jna.ptr.LongByReference;
import util.Control;

/**
 * For attaching threads to cores
 *
 * @author cheremin
 * @since 25.10.11, 14:18
 */
//I have no fucking idea how this works to be 100% honest, but it does
//Thanks JNA
public class ThreadAffinity {

    private final Core[] cores;
    private Control c;

    {
        final int coresCount = Runtime.getRuntime().availableProcessors();
        cores = new Core[coresCount];

        for (int i = 0; i < cores.length; i++) {
            cores[i] = new Core(i, c);
        }
    }

    public void setCurrentThreadAffinityMask(final long mask) throws Exception {
        final CLibrary lib = CLibrary.INSTANCE;
        final int cpuMaskSize = Long.SIZE / 8;
        try {
            final int ret = lib.sched_setaffinity(0, cpuMaskSize, new LongByReference(mask));
            if (ret < 0) {
                throw new Exception("sched_setaffinity( 0, (" + cpuMaskSize + ") , &(" + mask + ") ) return " + ret);
            }
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    public void setThreadAffinityMask(final long threadID,
            final long mask) throws Exception {
        final CLibrary lib = CLibrary.INSTANCE;
        final int cpuMaskSize = Long.SIZE / 8;
        try {
            final int ret = lib.sched_setaffinity(
                    (int) threadID,
                    cpuMaskSize,
                    new LongByReference(mask)
            );
            if (ret < 0) {
                throw new Exception("sched_setaffinity( " + threadID + ", (" + cpuMaskSize + ") , &(" + mask + ") ) return " + ret);
            }
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    public Core[] cores() {
        return cores.clone();
    }

    public Core currentCore() throws Exception {
        final int cpuSequence = CLibrary.INSTANCE.sched_getcpu();
        return cores[cpuSequence];
    }

    public void nice(final int increment) throws Exception {
        final CLibrary lib = CLibrary.INSTANCE;
        try {
            final int ret = lib.nice(increment);
            if (ret < 0) {
                throw new Exception("nice( " + increment + " ) return " + ret);
            }
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    private interface CLibrary extends Library {

        public final CLibrary INSTANCE = (CLibrary) Native.loadLibrary("c", CLibrary.class);

        public int nice(final int increment) throws Exception;

        public int sched_setaffinity(final int pid,
                final int cpusetsize,
                final PointerType cpuset) throws Exception;

        public int sched_getcpu() throws Exception;
    }

    public Core[] getCores() {
        return cores;
    }

    public ThreadAffinity(Control c) {
        this.c = c;

    }

    public int[] parseCoresIndexes(final String str,
            final int[] defaults) throws ParseException {
        final StringTokenizer stok = new StringTokenizer(str, ",");
        final int size = stok.countTokens();
        if (size == 0) {
            return defaults;
        }

        final int maxIndex = Runtime.getRuntime().availableProcessors() - 1;
        final int[] indexes = new int[size];
        for (int i = 0; stok.hasMoreTokens(); i++) {
            final String token = stok.nextToken();
            final int index;
            try {
                index = Integer.parseInt(token);
            } catch (NumberFormatException e) {
                throw new ParseException("Can't parse [" + i + "]='" + token + "' as Integer", i);
            }
            if (index > maxIndex || index < 0) {
                throw new ParseException("Core index[" + i + "]=" + index + " is out of bounds [0," + maxIndex + "]", i);
            }
            indexes[i] = index;
        }
        return indexes;
    }

    public Core[] parseCores(final String str,
            final int[] defaults) throws ParseException {
        final int[] indexes = parseCoresIndexes(str, defaults);
        final Core[] cores = new Core[indexes.length];
        for (int i = 0; i < cores.length; i++) {
            cores[i] = cores()[indexes[i]];
        }
        return cores;
    }
}
