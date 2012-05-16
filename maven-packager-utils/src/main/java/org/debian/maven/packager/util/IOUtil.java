package org.debian.maven.packager.util;

/*
 * Copyright 2012 Ludovic Claude.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class IOUtil {

    public static String readLine() {
        LineNumberReader consoleReader = new LineNumberReader(new InputStreamReader(System.in));
        try {
            return consoleReader.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void executeProcess(final String[] cmd, final OutputHandler handler) {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            System.out.print("> ");
            for (String arg : cmd) {
                System.out.print(arg + " ");
            }
            System.out.println();
            final Process process = pb.start();
            try {
                ThreadFactory threadFactory = new ThreadFactory() {

                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "Run command " + cmd[0]);
                        t.setDaemon(true);
                        return t;
                    }
                };

                ExecutorService executor = Executors.newSingleThreadExecutor(threadFactory);
                executor.execute(new Runnable() {

                    public void run() {
                        try {
                            InputStreamReader isr = new InputStreamReader(process.getInputStream());
                            BufferedReader br = new BufferedReader(isr);
                            LineNumberReader aptIn = new LineNumberReader(br);
                            String line;
                            while ((line = aptIn.readLine()) != null) {
                                handler.newLine(line);
                            }
                            aptIn.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                process.waitFor();
                executor.awaitTermination(5, TimeUnit.SECONDS);
                if (process.exitValue() != 0) {
                    System.out.println(cmd[0] + " failed to execute successfully");
                    handler.failure();
                }
                process.destroy();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                Thread.interrupted();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            handler.failure();
        }
    }
}