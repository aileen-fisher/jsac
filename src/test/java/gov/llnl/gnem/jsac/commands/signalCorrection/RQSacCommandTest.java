/*-
 * #%L
 * Java Seismic Analysis Code (JSAC)
 *  LLNL-CODE-855505
 *  This work was performed under the auspices of the U.S. Department of Energy
 *  by Lawrence Livermore National Laboratory under Contract DE-AC52-07NA27344.
 * %%
 * Copyright (C) 2022 - 2023 Lawrence Livermore National Laboratory
 * %%
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
 * #L%
 */
package gov.llnl.gnem.jsac.commands.signalCorrection;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.llnl.gnem.jsac.SacDataModel;
import gov.llnl.gnem.jsac.TestUtil;
import gov.llnl.gnem.jsac.dataAccess.dataObjects.SacTraceData;
import gov.llnl.gnem.jsac.dataAccess.dataObjects.SpectralData;

public class RQSacCommandTest {

    private static final String TEST_FILE_DIRECTORY_1 = "gov/llnl/gnem/jsac/commands/transfer/";
    private static final String TEST_FILE_DIRECTORY = "gov/llnl/gnem/jsac/commands/signalCorrection/";

    public RQSacCommandTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        SacDataModel.getInstance().clear();
    }

    @AfterAll
    protected static void tearDownAfterClass() throws Exception {
        SacDataModel.getInstance().clear();
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of execute method, of class RQSacCommand.
     */
    @Test
    public void testExecute() {
        System.out.println("execute RQ");

        // Load the test file...
        String testSacFile = "fgseis.sac";
        TestUtil.loadTestSacFile(testSacFile, TEST_FILE_DIRECTORY_1, true);

        // Compute to frequency domain
        SacDataModel.getInstance().computeFFT(false, SpectralData.PresentationFormat.AmplitudePhase);

        // Apply RQ with defaults
        RQSacCommand instance = new RQSacCommand();
        String cmd = "RQ";
        String[] tokens = cmd.split("(\\s+)|(\\s*,\\s*)");
        instance.initialize(tokens);
        instance.execute();

        // Convert back to Time Domain
        SacDataModel.getInstance().computeIFFT();

        // Get the result
        List<SacTraceData> traces = SacDataModel.getInstance().getData();
        SacTraceData rqSeis = traces.get(0);

        // Now get result computed with old SAC
        String rqSacFile = "rq.sac";
        TestUtil.loadTestSacFile(rqSacFile, TEST_FILE_DIRECTORY, true);
        traces = SacDataModel.getInstance().getData();
        SacTraceData truth = traces.get(0);
        Assertions.assertArrayEquals(truth.getData(), rqSeis.getData(), 0.001F);

    }

}
