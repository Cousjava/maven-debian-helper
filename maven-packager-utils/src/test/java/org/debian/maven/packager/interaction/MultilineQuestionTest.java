/*
 * Copyright 2013 Emmanuel Bourg
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

package org.debian.maven.packager.interaction;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

public class MultilineQuestionTest extends TestCase {
    
    private String EOL = System.getProperty("line.separator");

    public void testQuestion() throws Exception {
        StringWriter output = new StringWriter();

        MultilineQuestion question = new MultilineQuestion("What's your address?");
        question.setInput(new BufferedReader(
                new StringReader("\nSoftware in the Public Interest, Inc.\n" +
                "P.O. Box 501248\n" +
                "Indianapolis, IN 46250-6248\n" +
                "United States\n\n")));
        question.setOutput(new PrintWriter(output, true));

        String answer = question.ask();

        assertEquals("Question", "What's your address?" + EOL, output.toString());
        assertEquals("Answer", "Software in the Public Interest, Inc.\n" +
                "P.O. Box 501248\n" +
                "Indianapolis, IN 46250-6248\n" + 
                "United States", answer);
    }
}
