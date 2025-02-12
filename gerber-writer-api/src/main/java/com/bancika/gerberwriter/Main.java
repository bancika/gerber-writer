package com.bancika.gerberwriter;

import com.bancika.gerberwriter.padmasters.*;
import com.bancika.gerberwriter.path.Path;

import java.io.IOException;

import static com.bancika.gerberwriter.GerberFunctions.*;

public class Main {

    public static void main(String[] args) throws IOException {
//        System.out.println(test2());
        test2();
    }
    
    private static String test1() {
        GenerationSoftware genSoftware = new GenerationSoftware(
                "Test Corp",
                "GerberWriter Test",
                "1.0.0"
        );

        // Create a copper top layer
        DataLayer copperTop = new DataLayer("Copper,L1,Top", false, genSoftware);

        // 1. Add some via pads (non-negative)
        Circle viaPad = new Circle(0.6, VIA_PAD, false);
        copperTop.addPad(viaPad, new Point(0.0, 0.0));
        copperTop.addPad(viaPad, new Point(5.0, 0.0));
        copperTop.addPad(viaPad, new Point(0.0, 5.0));
        copperTop.addPad(viaPad, new Point(5.0, 5.0));

        // 1. Add some via pads (non-negative)
        Circle viaPadHole = new Circle(0.2, "ViaPadHole", true);
        copperTop.addPad(viaPadHole, new Point(0.0, 0.0));
        copperTop.addPad(viaPadHole, new Point(5.0, 0.0));
        copperTop.addPad(viaPadHole, new Point(0.0, 5.0));
        copperTop.addPad(viaPadHole, new Point(5.0, 5.0));

        // 2. Add some SMD pads (non-negative)
        Rectangle smdPad = new Rectangle(2.0, 1.0, SMDPAD_CU_DEF, false);
        copperTop.addPad(smdPad, new Point(2.5, 1.0), 0);
        copperTop.addPad(smdPad, new Point(2.5, 4.0), 90);  // Rotated 90 degrees

        // 3. Add a rounded rectangle pad
        RoundedRectangle roundedPad = new RoundedRectangle(
                3.0,    // x size
                1.5,    // y size
                0.3,    // corner radius
                SMDPAD_CU_DEF,
                false   // non-negative
        );
        copperTop.addPad(roundedPad, new Point(2.5, 2.5), 45);  // Rotated 45 degrees

        // 4. Add some traces
        // Straight trace
        copperTop.addTraceLine(
                new Point(0.0, 0.0),
                new Point(5.0, 0.0),
                0.2,        // width
                CONDUCTOR,
                false      // non-negative
        );

        // Arc trace
        copperTop.addTraceArc(
                new Point(5.0, 0.0),
                new Point(5.0, 5.0),
                new Point(5.0, 2.5),  // center point
                "+",                   // clockwise
                0.2,                  // width
                CONDUCTOR,
                false                 // non-negative
        );

        // 5. Add a thermal pad
        Thermal thermalPad = new Thermal(
                2.0,    // outer diameter
                1.0,    // inner diameter
                0.3,    // gap
                "ThermalPad",
                false   // non-negative
        );
        copperTop.addPad(thermalPad, new Point(2.5, 2.5), 0);

        // 6. Add a custom polygon region
        Path polygonPath = new Path();
        polygonPath.moveTo(new Point(1.0, 1.0));
        polygonPath.lineTo(new Point(2.0, 1.0));
        polygonPath.lineTo(new Point(2.0, 2.0));
        polygonPath.lineTo(new Point(1.0, 2.0));
        polygonPath.lineTo(new Point(1.0, 1.0));  // Close the path
        copperTop.addRegion(polygonPath, CONDUCTOR, false);  // non-negative

        // 7. Add a complex trace path
        Path tracePath = new Path();
        tracePath.moveTo(new Point(0.0, 0.0));
        tracePath.lineTo(new Point(2.0, 0.0));
        tracePath.arcTo(
                new Point(2.0, 2.0),  // end point
                new Point(2.0, 1.0),  // center point
                "+"                   // clockwise
        );
        tracePath.lineTo(new Point(0.0, 2.0));
        copperTop.addTracesPath(tracePath, 0.2, CONDUCTOR, false);  // non-negative

        // 8. Add a negative pad example
        Circle clearancePad = new Circle(1.0, "Clearance", true);  // negative pad
        copperTop.addPad(clearancePad, new Point(3.5, 3.5));
        
        return copperTop.dumpGerberToString();
    }
    
    private static String test2() throws IOException {
        GenerationSoftware genSoftware = new GenerationSoftware(
                "Karel Tavernier",
                "gerber_writer_example.java",
                "2024.03"
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

//        // Footprint of IC16
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

//         Connect pin one of IC16 to copper pour
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
//
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

        top.dumpGerberToFile("/Users/bancika/Downloads/test.gbr");
        return null;
    }
}