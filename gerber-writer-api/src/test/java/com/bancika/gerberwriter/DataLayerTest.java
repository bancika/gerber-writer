package com.bancika.gerberwriter;

import com.bancika.gerberwriter.padmasters.*;
import com.bancika.gerberwriter.path.Path;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static com.bancika.gerberwriter.GerberFunctions.*;
import static com.bancika.gerberwriter.GerberFunctions.CONDUCTOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DataLayerTest {

    @Test
    void testGenerateCopperLayer() throws IOException {
        
        GenerationSoftware genSoftware = new GenerationSoftware(
                "Bancika",
                "gerber_writer_example.java",
                "1.0.0-alpha"
        );

        // Initialize parameters
        double traceWidth = 0.254;
        Circle viaPad = new Circle(0.508, CONNECTOR_PAD, false);
        Circle viaPadHole = new Circle(0.208, CONNECTOR_PAD, true);

        // Create top copper layer
        DataLayer top = new DataLayer("Copper,L1,Top,Signal", false, genSoftware);

        // Footprint of IC17
        Rectangle IC17_toe = new Rectangle(1.27, 2.54, SMDPAD_CU_DEF, false);
        top.addPad(IC17_toe, new Point(65.094, 47.269), 45);
        top.addPad(IC17_toe, new Point(68.047, 50.267), 45);

        // Connect one pin to via
        top.addTraceLine(
                new Point(65.094, 47.269),
                new Point(65.094 + 1, 47.269 + 1),
                traceWidth,
                CONDUCTOR,
                false
        );
        top.addPad(viaPad, new Point(65.094 + 1, 47.269 + 1));
        top.addPad(viaPadHole, new Point(65.094 + 1, 47.269 + 1));

        // Footprint of IC16
        RoundedRectangle IC16_toe = new RoundedRectangle(1.257, 2.286, 0.254, SMDPAD_CU_DEF, false);
        Point[] footprint = {
                new Point(56.515, 47.879),
                new Point(60.341, 47.879),
                new Point(58.428, 43.700)
        };

        for (Point toeLocation : footprint) {
            top.addPad(IC16_toe, toeLocation);
        }

        // Connect pin 2 to via
        top.addTraceLine(footprint[1], new Point(62.549, 47.879), traceWidth, CONDUCTOR, false);
        top.addTraceLine(
                new Point(62.549, 47.879),
                new Point(64.350, 49.657),
                traceWidth,
                CONDUCTOR,
                false
        );
        top.addPad(viaPad, new Point(64.350, 49.657));
        top.addPad(viaPadHole, new Point(64.350, 49.657));

        // Connect pin 3 to IC17
        Point p1 = new Point(65.000, 43.700);
        Point p2 = new Point(65.000 + 4.8, 43.700 + 4.8);
        Point p3 = new Point(68.047, 50.267);

        Path con3IC17 = new Path();
        con3IC17.moveTo(footprint[2]);
        con3IC17.lineTo(p1);
        con3IC17.lineTo(p2);
        con3IC17.lineTo(p3);
        top.addTracesPath(con3IC17, traceWidth, CONDUCTOR, false);

        // Copper pour, rectangle with one rounded corner
        double xLeft = 55;
        double xRight = 63;
        double yBottom = 50;
        double yTop = 56;
        double radius = 2.2;

        Path pour = new Path();
        pour.moveTo(new Point(xLeft, yBottom));
        pour.lineTo(new Point(xRight - radius, yBottom));
        pour.arcTo(
                new Point(xRight, yBottom + radius),
                new Point(xRight - radius, yBottom + radius),
                "+"
        );
        pour.lineTo(new Point(xRight, yTop));
        pour.lineTo(new Point(xLeft, yTop));
        pour.lineTo(new Point(xLeft, yBottom));
        top.addRegion(pour, CONDUCTOR, false);

        // Thermal relief pad in copper pour
        RoundedThermal thermalPad = new RoundedThermal(1, 0.8, 0.06, THERMAL_RELIEF_PAD, true);
        top.addPad(thermalPad, new Point(xRight - radius, yBottom + radius), 45);

        // Embedded via pad in copper pour
        top.addPad(viaPad, new Point(xLeft + radius, yTop - radius));

        // Connect pin one of IC16 to copper pour
        top.addTraceLine(
                footprint[0],
                new Point(56.515, 47.879 + 2.54),
                traceWidth,
                CONDUCTOR,
                false
        );

        // Connect vias with arcs, parallel
        Point traceStart = new Point(64, 53);
        top.addPad(viaPad, traceStart);

        Path connectionA = new Path();
        connectionA.moveTo(traceStart);
        connectionA.lineTo(new Point(traceStart.x, traceStart.y + 1));
        connectionA.arcTo(
                new Point(traceStart.x + 2, traceStart.y + 3),
                new Point(traceStart.x + 2, traceStart.y + 1),
                "-"
        );
        connectionA.arcTo(
                new Point(traceStart.x + 3, traceStart.y + 4),
                new Point(traceStart.x + 2, traceStart.y + 4),
                "+"
        );
        connectionA.lineTo(new Point(traceStart.x + 3, traceStart.y + 6));
        top.addTracesPath(connectionA, traceWidth, CONDUCTOR, false);
        top.addPad(viaPad, new Point(traceStart.x + 3, traceStart.y + 6));
        top.addPad(viaPadHole, traceStart);
        top.addPad(viaPadHole, new Point(traceStart.x + 3, traceStart.y + 6));

        traceStart = new Point(65, 53);
        top.addPad(viaPad, traceStart);

        Path connectionB = new Path();
        connectionB.moveTo(traceStart);
        connectionB.lineTo(new Point(traceStart.x, traceStart.y + 1));
        connectionB.arcTo(
                new Point(traceStart.x + 1, traceStart.y + 2),
                new Point(traceStart.x + 1, traceStart.y + 1),
                "-"
        );
        connectionB.arcTo(
                new Point(traceStart.x + 3, traceStart.y + 4),
                new Point(traceStart.x + 1, traceStart.y + 4),
                "+"
        );
        connectionB.lineTo(new Point(traceStart.x + 3, traceStart.y + 6));
        top.addTracesPath(connectionB, traceWidth, CONDUCTOR, false);
        top.addPad(viaPad, new Point(traceStart.x + 3, traceStart.y + 6));
        top.addPad(viaPadHole, traceStart);
        top.addPad(viaPadHole, new Point(traceStart.x + 3, traceStart.y + 6));

        String gerber = top.dumpGerberToString(LocalDateTime.MIN);
        String content = new String(Files.readAllBytes(Paths.get("src/test/resources/test1.gbr")))
                .replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
        assertEquals(content, gerber);
//        System.out.println(gerber);
    }
}
