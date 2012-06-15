/*
 * Copyright (c) 2006-2012 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume, jcarsique
 */

package org.nuxeo.ecm.core.storage.sql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.Environment;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.services.streaming.FileSource;

/**
 * A simple filesystem-based binary manager. It stores the binaries according to
 * their digest (hash), which means that no transactional behavior needs to be
 * implemented.
 * <p>
 * A garbage collection is needed to purge unused binaries.
 * <p>
 * The format of the <em>binaries</em> directory is:
 * <ul>
 * <li><em>data/</em> hierarchy with the actual binaries in subdirectories,</li>
 * <li><em>tmp/</em> temporary storage during creation,</li>
 * <li><em>config.xml</em> a file containing the configuration used.</li>
 * </ul>
 *
 * @author Florent Guillaume
 */
public class DefaultBinaryManager extends AbstractBinaryManager implements BinaryManagerStreamSupport {

    private static final Log log = LogFactory.getLog(DefaultBinaryManager.class);

    public static final String DEFAULT_PATH = "binaries";

    public static final String DATA = "data";

    public static final String TMP = "tmp";

    public static final String CONFIG_FILE = "config.xml";

    protected File storageDir;

    protected File tmpDir;

    @Override
    public void initialize(RepositoryDescriptor repositoryDescriptor)
            throws IOException {
        String path = repositoryDescriptor.binaryStorePath;
        if (path == null || path.trim().length() == 0) {
            path = DEFAULT_PATH;
        }
        path = Framework.expandVars(path);
        path = path.trim();
        File base;
        if (path.startsWith("/") || path.startsWith("\\")
                || path.contains("://") || path.contains(":\\")) {
            // absolute
            base = new File(path);
        } else {
            // relative
            File home = Environment.getDefault().getData();
            base = new File(home, path);

            // Backward compliance with versions before 5.4 (NXP-5370)
            File oldBase = new File(Framework.getRuntime().getHome().getPath(),
                    path);
            if (oldBase.exists()) {
                log.warn("Old binaries path used (NXP-5370). Please move "
                        + oldBase + " to " + base);
                base = oldBase;
            }
        }

        log.info("Repository '"
                + repositoryDescriptor.name
                + "' using "
                + (this.getClass().equals(DefaultBinaryManager.class) ? ""
                        : (this.getClass().getSimpleName() + " and "))
                + "binary store: " + base);
        storageDir = new File(base, DATA);
        tmpDir = new File(base, TMP);
        storageDir.mkdirs();
        tmpDir.mkdirs();
        descriptor = getDescriptor(new File(base, CONFIG_FILE));
        createGarbageCollector();
    }

    public File getStorageDir() {
        return storageDir;
    }

    @Override
    public Binary getBinary(FileSource source) throws IOException {
        String  digest = storeAndDigest(source);

        File file = getFileForDigest(digest, false);
        /*
         * Now we can build the Binary.
         */
        return getBinaryScrambler().getUnscrambledBinary(file, digest,
                repositoryName);
    }

    public Binary getBinary(InputStream in) throws IOException {
        String digest = storeAndDigest(in);
        File file = getFileForDigest(digest, false);
        /*
         * Now we can build the Binary.
         */
        return getBinaryScrambler().getUnscrambledBinary(file, digest,
                repositoryName);
    }

    @Override
    public Binary getBinary(String digest) {
        File file = getFileForDigest(digest, false);
        if (file == null) {
            // invalid digest
            return null;
        }
        if (!file.exists()) {
            log.warn("cannot fetch content at " + file.getPath()
                    + " (file does not exist), check your configuration");
            return null;
        }
        return getBinaryScrambler().getUnscrambledBinary(file, digest,
                repositoryName);
    }

    /**
     * Gets a file representing the storage for a given digest.
     *
     * @param digest the digest
     * @param createDir {@code true} if the directory containing the file itself
     *            must be created
     * @return the file for this digest
     */
    public File getFileForDigest(String digest, boolean createDir) {
        int depth = descriptor.depth;
        if (digest.length() < 2 * depth) {
            return null;
        }
        StringBuilder buf = new StringBuilder(3 * depth - 1);
        for (int i = 0; i < depth; i++) {
            if (i != 0) {
                buf.append(File.separatorChar);
            }
            buf.append(digest.substring(2 * i, 2 * i + 2));
        }
        File dir = new File(storageDir, buf.toString());
        if (createDir) {
            dir.mkdirs();
        }
        return new File(dir, digest);
    }

    protected String storeAndDigest(FileSource source)  throws IOException {
        File sourceFile = source.getFile();
        InputStream in = source.getStream();
        OutputStream out = new NullOutputStream();
        String digest;
        try {
            digest = storeAndDigest(in, out);
        } finally {
            in.close();
            out.close();
        }
        File  digestFile = getFileForDigest(digest, true);
        if (!sourceFile.renameTo(digestFile)) {
            FileUtils.copy(sourceFile, digestFile);
            sourceFile.delete();
        }
        source.setFile(digestFile);
        return digest;
    }

    protected String storeAndDigest(InputStream in) throws IOException {
        File tmp = File.createTempFile("create_", ".tmp", tmpDir);
        OutputStream out = new FileOutputStream(tmp);
        /*
         * First, write the input stream to a temporary file, while computing a
         * digest.
         */
        try {
            String digest;
            try {
                digest = storeAndDigest(in, out);
            } finally {
                in.close();
                out.close();
            }
            /*
             * Move the tmp file to its destination.
             */
            File file = getFileForDigest(digest, true);
            tmp.renameTo(file); // atomic move, fails if already there
            if (!file.exists()) {
                throw new IOException("Could not create file: " + file);
            }
            return digest;
        } finally {
            tmp.delete();
        }

    }

    protected void createGarbageCollector() {
        garbageCollector = new DefaultBinaryGarbageCollector(this);
    }

    public static class DefaultBinaryGarbageCollector implements
            BinaryGarbageCollector {

        /**
         * Windows FAT filesystems have a time resolution of 2s. Other common
         * filesystems have 1s.
         */
        public static int TIME_RESOLUTION = 2000;

        protected final DefaultBinaryManager binaryManager;

        protected volatile long startTime;

        protected BinaryManagerStatus status;

        public DefaultBinaryGarbageCollector(DefaultBinaryManager binaryManager) {
            this.binaryManager = binaryManager;
        }

        @Override
        public String getId() {
            return binaryManager.getStorageDir().toURI().toString();
        }

        @Override
        public BinaryManagerStatus getStatus() {
            return status;
        }

        @Override
        public boolean isInProgress() {
            // volatile as this is designed to be called from another thread
            return startTime != 0;
        }

        @Override
        public void start() {
            if (startTime != 0) {
                throw new RuntimeException("Alread started");
            }
            startTime = System.currentTimeMillis();
            status = new BinaryManagerStatus();
        }

        @Override
        public void mark(String digest) {
            File file = binaryManager.getFileForDigest(digest, false);
            if (!file.exists()) {
                log.error("Unknown file digest: " + digest);
                return;
            }
            touch(file);
        }

        @Override
        public void stop(boolean delete) {
            if (startTime == 0) {
                throw new RuntimeException("Not started");
            }
            deleteOld(binaryManager.getStorageDir(), startTime
                    - TIME_RESOLUTION, 0, delete);
            status.gcDuration = System.currentTimeMillis() - startTime;
            startTime = 0;
        }

        protected void deleteOld(File file, long minTime, int depth,
                boolean delete) {
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    deleteOld(f, minTime, depth + 1, delete);
                }
                if (depth > 0 && file.list().length == 0) {
                    // empty directory
                    file.delete();
                }
            } else if (file.isFile() && file.canWrite()) {
                long lastModified = file.lastModified();
                long length = file.length();
                if (lastModified == 0) {
                    log.error("Cannot read last modified for file: " + file);
                } else if (lastModified < minTime) {
                    status.sizeBinariesGC += length;
                    status.numBinariesGC++;
                    if (delete && !file.delete()) {
                        log.warn("Cannot gc file: " + file);
                    }
                } else {
                    status.sizeBinaries += length;
                    status.numBinaries++;
                }
            }
        }
    }

    /**
     * Sets the last modification date to now on a file
     *
     * @param file the file
     */
    public static void touch(File file) {
        long time = System.currentTimeMillis();
        if (file.setLastModified(time)) {
            // ok
            return;
        }
        if (!file.canWrite()) {
            // cannot write -> stop won't be able to delete anyway
            return;
        }
        try {
            // Windows: the file may be open for reading
            // workaround found by Thomas Mueller, see JCR-2872
            RandomAccessFile r = new RandomAccessFile(file, "rw");
            try {
                r.setLength(r.length());
            } finally {
                r.close();
            }
        } catch (IOException e) {
            log.error("Cannot set last modified for file: " + file, e);
        }
    }

}
