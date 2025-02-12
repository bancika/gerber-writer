package com.bancika.gerberwriter;

import com.bancika.gerberwriter.padmasters.*;
import com.bancika.gerberwriter.path.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DataLayer {

    private static final double TO_NM = 1_000_000; // convert user units (mm) to gerber coordinates (nm)
    private static final int DECIMALS = 6;  // Max number of decimals in calculated Gerber AD parameters
    private static final double TOLERANCE = 0.5e-3; // Smaller features can be simplified for more robust Gerber file
    
    private final String function;
    private final boolean negative;
    private final List<GraphicsObject> graphicsObjectStream;
    private Point pointMax;
    private int[] integerDigits;
    private GenerationSoftware generationSoftware;

    public DataLayer(String function, boolean negative, GenerationSoftware generationSoftware) {
        this.function = function;
        this.negative = negative;
        this.graphicsObjectStream = new ArrayList<>();
        this.pointMax = new Point(1, 1);
        this.integerDigits = new int[]{0, 0};
        this.generationSoftware = generationSoftware;
    }

    // Add methods
    public void addPad(AbstractPad master, Point position, double angle) {
        validatePadMaster(master);
        graphicsObjectStream.add(new Pad(master, position, angle));
        updatePointMax(position);
    }

    public void addPad(AbstractPad master, Point position) {
        addPad(master, position, 0);
    }

    public void addTraceLine(Point start, Point end, double width, String function, boolean negative) {
        validateTraceParameters(width, function);

        Path line = new Path();
        line.moveTo(start);
        line.lineTo(end);
        graphicsObjectStream.add(new TracesPath(line, width, function, negative));

        updatePointMax(line.getPointMax());
    }

    public void addTraceArc(Point start, Point end, Point center, String orientation,
                            double width, String function, boolean negative) {
        validateTraceParameters(width, function);

        Path arc = new Path();
        arc.moveTo(start);
        arc.arcTo(end, center, orientation);
        graphicsObjectStream.add(new TracesPath(arc, width, function, negative));

        updatePointMax(arc.getPointMax());
    }

    public void addTracesPath(Path path, double width, String function, boolean negative) {
        validateTraceParameters(width, function);
        graphicsObjectStream.add(new TracesPath(path, width, function, negative));
        updatePointMax(path.getPointMax());
    }

    public void addRegion(Path path, String function, boolean negative) {
        if (!path.isContour()) {
            throw new IllegalArgumentException("Some subpaths are not closed");
        }
        graphicsObjectStream.add(new Region(path, function, negative));
        updatePointMax(path.getPointMax());
    }

    /**
     * Validates a pad master object and its parameters
     *
     * @param master The pad master object to validate
     * @throws IllegalArgumentException if the pad master is invalid
     */
    private void validatePadMaster(AbstractPad master) {
        if (master == null) {
            throw new IllegalArgumentException("Pad master cannot be null");
        }

        // Validate specific pad master types
        if (master instanceof Circle) {
            Circle circle = (Circle) master;
            if (circle.getDiameter() <= 0) {
                throw new IllegalArgumentException("Circle diameter must be positive");
            }
            if (circle.getFunction() == null || circle.getFunction().isEmpty()) {
                throw new IllegalArgumentException("Circle function must not be empty");
            }
        }
        else if (master instanceof Rectangle) {
            Rectangle rect = (Rectangle) master;
            if (rect.getXSize() <= 0 || rect.getYSize() <= 0) {
                throw new IllegalArgumentException("Rectangle dimensions must be positive");
            }
            if (rect.getFunction() == null || rect.getFunction().isEmpty()) {
                throw new IllegalArgumentException("Rectangle function must not be empty");
            }
        }
        else if (master instanceof RoundedRectangle) {
            RoundedRectangle rounded = (RoundedRectangle) master;
            if (rounded.getXSize() <= 0 || rounded.getYSize() <= 0) {
                throw new IllegalArgumentException("RoundedRectangle dimensions must be positive");
            }
            if (rounded.getRadius() <= 0) {
                throw new IllegalArgumentException("RoundedRectangle radius must be positive");
            }
            if (rounded.getRadius() * 2 > Math.min(rounded.getXSize(), rounded.getYSize())) {
                throw new IllegalArgumentException("RoundedRectangle radius too large for dimensions");
            }
            if (rounded.getFunction() == null || rounded.getFunction().isEmpty()) {
                throw new IllegalArgumentException("RoundedRectangle function must not be empty");
            }
        }
        else if (master instanceof ChamferedRectangle) {
            ChamferedRectangle chamfered = (ChamferedRectangle) master;
            if (chamfered.getXSize() <= 0 || chamfered.getYSize() <= 0) {
                throw new IllegalArgumentException("ChamferedRectangle dimensions must be positive");
            }
            if (chamfered.getCutoff() <= 0) {
                throw new IllegalArgumentException("ChamferedRectangle cutoff must be positive");
            }
            if (chamfered.getCutoff() * 2 > Math.min(chamfered.getXSize(), chamfered.getYSize())) {
                throw new IllegalArgumentException("ChamferedRectangle cutoff too large for dimensions");
            }
            if (chamfered.getFunction() == null || chamfered.getFunction().isEmpty()) {
                throw new IllegalArgumentException("ChamferedRectangle function must not be empty");
            }
        }
        else if (master instanceof Thermal) {
            Thermal thermal = (Thermal) master;
            if (thermal.getOuterDiameter() <= 0) {
                throw new IllegalArgumentException("Thermal outer diameter must be positive");
            }
            if (thermal.getInnerDiameter() <= 0) {
                throw new IllegalArgumentException("Thermal inner diameter must be positive");
            }
            if (thermal.getGap() <= 0) {
                throw new IllegalArgumentException("Thermal gap must be positive");
            }
            if (thermal.getInnerDiameter() >= thermal.getOuterDiameter()) {
                throw new IllegalArgumentException("Thermal inner diameter must be less than outer diameter");
            }
            if (thermal.getGap() >= thermal.getOuterDiameter()) {
                throw new IllegalArgumentException("Thermal gap must be less than outer diameter");
            }
            if (thermal.getFunction() == null || thermal.getFunction().isEmpty()) {
                throw new IllegalArgumentException("Thermal function must not be empty");
            }
        }
        else if (master instanceof RoundedThermal) {
            RoundedThermal roundedThermal = (RoundedThermal) master;
            if (roundedThermal.getOuterDiameter() <= 0) {
                throw new IllegalArgumentException("RoundedThermal outer diameter must be positive");
            }
            if (roundedThermal.getInnerDiameter() <= 0) {
                throw new IllegalArgumentException("RoundedThermal inner diameter must be positive");
            }
            if (roundedThermal.getGap() <= 0) {
                throw new IllegalArgumentException("RoundedThermal gap must be positive");
            }
            if (roundedThermal.getInnerDiameter() >= roundedThermal.getOuterDiameter()) {
                throw new IllegalArgumentException("RoundedThermal inner diameter must be less than outer diameter");
            }
            if (roundedThermal.getGap() >= roundedThermal.getOuterDiameter()) {
                throw new IllegalArgumentException("RoundedThermal gap must be less than outer diameter");
            }
            if (roundedThermal.getFunction() == null || roundedThermal.getFunction().isEmpty()) {
                throw new IllegalArgumentException("RoundedThermal function must not be empty");
            }
        }
        else if (master instanceof UserPolygon) {
            UserPolygon polygon = (UserPolygon) master;
            if (polygon.getPolygon() == null || polygon.getPolygon().length < 3) {
                throw new IllegalArgumentException("UserPolygon must have at least 3 points");
            }
            if (polygon.getFunction() == null || polygon.getFunction().isEmpty()) {
                throw new IllegalArgumentException("UserPolygon function must not be empty");
            }
            // Validate that the polygon is closed
            Point first = polygon.getPolygon()[0];
            Point last = polygon.getPolygon()[polygon.getPolygon().length - 1];
            if (!first.equals(last)) {
                throw new IllegalArgumentException("UserPolygon must be closed (first point must equal last point)");
            }
        }
        else {
            throw new IllegalArgumentException("Unknown pad master type: " + master.getClass().getName());
        }
    }

    private void validateTraceParameters(double width, String function) {
        if (width < 0) {
            throw new IllegalArgumentException("Width must be >= 0");
        }
        if (function == null) {
            throw new IllegalArgumentException("Function cannot be null");
        }
    }

    private void updatePointMax(Point newPoint) {
        pointMax = new Point(
                Math.max(Math.abs(newPoint.x), pointMax.x),
                Math.max(Math.abs(newPoint.y), pointMax.y)
        );

        // Update integer digits
        integerDigits = new int[]{
                Math.max(1 + (int) Math.log10(pointMax.x), 3),
                Math.max(1 + (int) Math.log10(pointMax.y), 3)
        };
    }

    // Gerber generation
    public void dumpGerberToFile(String filePath) throws IOException {
        Files.write(Paths.get(filePath), dumpGerberToString().getBytes());
    }

    public String dumpGerberToString() {
        return dumpGerberToString(LocalDateTime.now());
    }

    public String dumpGerberToString(LocalDateTime now) {
        StringBuilder commands = new StringBuilder();
        Map<String, Integer> apertures = new HashMap<>();
        Set<String> macros = new HashSet<>();
        Map<UserPolygon, String> polygons = new HashMap<>();
        AtomicInteger generateDcode = new AtomicInteger(10);
        AtomicInteger generatePolygonNumber = new AtomicInteger(1);
        
        GraphicsState state = new GraphicsState();

        // Header
        commands.append(String.format("G04 #@! TF.CreationDate,%s*\n",
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))));
        
        if (!function.isEmpty()) {
            commands.append(String.format("G04 #@! TF.FileFunction,%s*\n", function));
        }

        commands.append(String.format("G04 #@! TF.FilePolarity,%s*\n",
                negative ? "Negative" : "Positive"));

        // Generation software
        if (generationSoftware.getVendor() != null && !generationSoftware.getVendor().isEmpty() &&
                generationSoftware.getApplication() != null && !generationSoftware.getApplication().isEmpty() &&
                generationSoftware.getVersion() != null && !generationSoftware.getVersion().isEmpty()) {

            commands.append(String.format("G04 #@! TF.GenerationSoftware,%s,%s,%s*\n",
                    generationSoftware.getVendor(),
                    generationSoftware.getApplication(),
                    generationSoftware.getVersion()));
        }

        commands.append("%MOMM*%\n");

        // Format specification
        int maxIntegerDigits = Math.max(integerDigits[0], integerDigits[1]);
        commands.append(String.format("%%FSLAX%d6Y%d6*%%\n", maxIntegerDigits, maxIntegerDigits));
        commands.append("G75*\n");
        
        List<String> bodyCommands = new ArrayList<>();
        List<String> adCommands = new ArrayList<>();
        // Process graphics objects
        for (GraphicsObject obj : graphicsObjectStream) {
            if (obj instanceof Pad) {
                processPad((Pad)obj, bodyCommands, adCommands, apertures, polygons, generatePolygonNumber, macros, state, generateDcode);
            } else if (obj instanceof Region) {
                processRegion((Region)obj, bodyCommands, state);
            } else if (obj instanceof TracesPath) {
                processTracesPath((TracesPath)obj, bodyCommands, adCommands, apertures, state, generateDcode);
            }
        }

        // Write macro commands in sorted order for predictable output
        List<String> sortedMacros = new ArrayList<>(macros);
        Collections.sort(sortedMacros);
        for (String macro : sortedMacros) {
            commands.append(macro).append('\n');
        }
        
        // Write aperture definition commands
        for (String adCommand : adCommands) {
            commands.append(adCommand).append('\n');
        }

        // Write body commands (D01/02/03, G01/02/03, G36/G37)
        for (String bodyCommand : bodyCommands) {
            commands.append(bodyCommand).append('\n');
        }
        
        // End of file
        commands.append("M02*\n");

        return commands.toString();
    }

    private void processPad(Pad pad, List<String> bodyCommands, List<String> adCommands, Map<String, Integer> apertures,
                            Map<UserPolygon, String> polygons,  // Added polygons parameter
                            AtomicInteger generatePolygonNumber,  // Added counter parameter
                            Set<String> macros, GraphicsState state, AtomicInteger generateDcode) {
        if (pad.master instanceof Circle) {
            Circle circle = (Circle) pad.master;
            String shape = String.format("Circle,%f", circle.getDiameter());
            String adBody = String.format("C,%f", circle.getDiameter());
            handleFlash(shape, adBody, circle.getFunction(), circle.isNegative(),
                    pad.position, bodyCommands, adCommands, apertures, state, generateDcode);

        } else if (pad.master instanceof Rectangle) {
            Rectangle rect = (Rectangle) pad.master;
            String shape = String.format("Rectangle,%f,%f,%f",
                    rect.getXSize(), rect.getYSize(), pad.angle);

            String adBody;
            if (pad.angle % 180 == 0) {
                adBody = String.format("R,%fX%f", rect.getXSize(), rect.getYSize());
            } else {
                macros.add(GerberMacros.MACRO_RECTANGLE);
                adBody = String.format("Rectangle,%fX%fX%f",
                        rect.getXSize()/2, rect.getYSize()/2, pad.angle);
            }
            handleFlash(shape, adBody, rect.getFunction(), rect.isNegative(),
                    pad.position, bodyCommands, adCommands, apertures, state, generateDcode);

        } else if (pad.master instanceof RoundedRectangle) {
            RoundedRectangle rounded = (RoundedRectangle) pad.master;
            double xSize = rounded.getXSize();
            double ySize = rounded.getYSize();
            double radius = rounded.getRadius();
            double angle = pad.angle;

            String shape = String.format("RoundedRectangle,%f,%f,%f,%f",
                    xSize, ySize, radius, angle);

            String adBody;
            if ((Math.min(xSize, ySize) - 2*radius < TOLERANCE) && (angle % 90 == 0)) {
                // Becomes obround
                adBody = String.format("O,%fX%f", xSize, ySize);
            } else {
                macros.add(GerberMacros.MACRO_ROUNDED_RECTANGLE);
                double xc = xSize/2 - radius;
                double yc = ySize/2 - radius;
                Point centerQ1 = Point.rotate(new Point(+xc, yc), angle);
                Point centerQ2 = Point.rotate(new Point(-xc, yc), angle);

                adBody = String.format("RoundedRectangle,%fX%fX%fX%fX%fX%fX%fX%fX%fX%f",
                        round(xSize/2), round(ySize/2),
                        round(xc), round(yc),
                        round(angle), round(2*radius),
                        round(centerQ1.x), round(centerQ1.y),
                        round(centerQ2.x), round(centerQ2.y));
            }
            handleFlash(shape, adBody, rounded.getFunction(), rounded.isNegative(),
                    pad.position, bodyCommands, adCommands, apertures, state, generateDcode);

        } else if (pad.master instanceof ChamferedRectangle) {
            ChamferedRectangle chamfered = (ChamferedRectangle) pad.master;
            String shape = String.format("ChamferedRectangle,%f,%f,%f,%f",
                    chamfered.getXSize(), chamfered.getYSize(),
                    chamfered.getCutoff(), pad.angle);

            macros.add(GerberMacros.MACRO_CHAMFERED_RECTANGLE);
            String adBody = String.format("ChamferedRectangle,%fX%fX%fX%fX%f",
                    chamfered.getXSize()/2,
                    chamfered.getYSize()/2,
                    chamfered.getXSize()/2 - chamfered.getCutoff(),
                    chamfered.getYSize()/2 - chamfered.getCutoff(),
                    pad.angle);

            handleFlash(shape, adBody, chamfered.getFunction(), chamfered.isNegative(),
                    pad.position, bodyCommands, adCommands, apertures, state, generateDcode);

        } else if (pad.master instanceof Thermal) {
            Thermal thermal = (Thermal) pad.master;
            String shape = String.format("Thermal,%f,%f,%f,%f",
                    thermal.getOuterDiameter(), thermal.getInnerDiameter(),
                    thermal.getGap(), pad.angle);

            macros.add(GerberMacros.MACRO_THERMAL);
            String adBody = String.format("Thermal,%fX%fX%fX%f",
                    thermal.getOuterDiameter(),
                    thermal.getInnerDiameter(),
                    thermal.getGap(),
                    pad.angle);

            handleFlash(shape, adBody, thermal.getFunction(), thermal.isNegative(),
                    pad.position, bodyCommands, adCommands, apertures, state, generateDcode);

        } else if (pad.master instanceof RoundedThermal) {
            RoundedThermal rounded = (RoundedThermal) pad.master;
            String shape = String.format("RoundedThermal,%f,%f,%f,%f",
                    rounded.getOuterDiameter(), rounded.getInnerDiameter(),
                    rounded.getGap(), pad.angle);

            macros.add(GerberMacros.MACRO_ROUNDED_THERMAL);

            // Calculate parameters for the rounded thermal
            double outerDiameter = rounded.getOuterDiameter();
            double innerDiameter = rounded.getInnerDiameter();
            double gapGiven = rounded.getGap();
            double angle = pad.angle;

            // Complex gap and rounding calculations
            RoundedThermalParams params = calculateRoundedThermalParams(
                    outerDiameter, innerDiameter, gapGiven);

            String adBody = String.format("RoundedThermal,%fX%fX%fX%fX%fX%fX%fX%fX%fX%f",
                    round(outerDiameter),
                    round(innerDiameter),
                    round(params.gapPrimitive),
                    round(angle),
                    round(params.roundingDiameter),
                    round(params.centerH.x),
                    round(params.centerH.y),
                    round(params.centerV.x),
                    round(params.centerV.y),
                    round(innerDiameter));

            handleFlash(shape, adBody, rounded.getFunction(), rounded.isNegative(),
                    pad.position, bodyCommands, adCommands, apertures, state, generateDcode);

        } else if (pad.master instanceof RegularPolygon) {
            RegularPolygon polygon = (RegularPolygon) pad.master;
            String shape = String.format("RegularPolygon,%f,%d,%f",
                    polygon.getOuterDiameter(), polygon.getVertices(), pad.angle);

            String adBody = String.format("P,%fX%dX%f",
                    polygon.getOuterDiameter(),
                    polygon.getVertices(),
                    pad.angle);

            handleFlash(shape, adBody, polygon.getFunction(), polygon.isNegative(),
                    pad.position, bodyCommands, adCommands, apertures, state, generateDcode);

        } else if (pad.master instanceof UserPolygon) {
            UserPolygon userPoly = (UserPolygon) pad.master;
            String macroName = polygons.get(userPoly);

            if (macroName == null) {
                macroName = "UserPolygon_" + generatePolygonNumber.getAndIncrement();
                polygons.put(userPoly, macroName);

                // Define macro
                StringBuilder macroDef = new StringBuilder();
                macroDef.append(String.format("%%AM%s*\n", macroName));
                macroDef.append(String.format("4,1,%d,", userPoly.getPolygon().length - 1));

                for (Point point : userPoly.getPolygon()) {
                    macroDef.append(String.format("%f,%f,", point.getX(), point.getY()));
                }

                macroDef.append("$1*\n%");
                macros.add(macroDef.toString());
            }

            String shape = String.format("UserPolygon,%f", pad.angle);
            String adBody = String.format("%s,%f", macroName, pad.angle);

            handleFlash(shape, adBody, userPoly.getFunction(), userPoly.isNegative(),
                    pad.position, bodyCommands, adCommands, apertures, state, generateDcode);
        }
    }

    private RoundedThermalParams calculateRoundedThermalParams(
            double outerDiameter, double innerDiameter, double gapGiven) {

        double gapPrimitive;
        double roundingDiameter;
        Point centerH;
        Point centerV;

        if (gapGiven * Math.sqrt(2) >= (innerDiameter - (1 + Math.sqrt(2)) *
                (outerDiameter - innerDiameter))) {
            // Large gap case
            gapPrimitive = gapGiven + (outerDiameter - innerDiameter)/2;

            double cornerYInner = 0;
            double cornerYOuter = 0;

            while (true) {
                // Calculate y-coordinates where the gap intersects the circles
                // For inner circle:
                // Using right triangle formed by:
                // - Hypotenuse = innerDiameter/2 (radius)
                // - Adjacent = gapPrimitive/2 (half gap)
                // - Opposite = cornerYInner (y-coordinate we want)
                 cornerYInner = innerDiameter *
                        Math.sin(Math.acos(gapPrimitive/innerDiameter))/2;

                // Same calculation for outer circle
                 cornerYOuter = outerDiameter *
                        Math.sin(Math.acos(gapPrimitive/outerDiameter))/2;

                // The rounding diameter is the difference between these y-coordinates
                // This represents how much we need to round the corners
                roundingDiameter = cornerYOuter - cornerYInner;

                // The actual gap will be smaller than gapPrimitive by roundingDiameter
                double gapReal = gapPrimitive - roundingDiameter;

                // Iterate until we get close enough to desired gap
                if (Math.abs(gapGiven - gapReal) < TOLERANCE/10) break;
                gapPrimitive += gapGiven - gapReal;
            }

            // Calculate centers of rounding circles
            double centerY = (cornerYOuter + cornerYInner)/2;  // Midpoint between corners
            centerH = new Point(centerY, gapPrimitive/2);      // Horizontal gap
            centerV = new Point(gapPrimitive/2, centerY);      // Vertical gap

        } else {
            // Small gap case - similar calculations but different geometry
            gapPrimitive = gapGiven + 1.2*(outerDiameter-innerDiameter)/2;

            while (true) {
                double alfaInner = Math.acos(gapPrimitive/innerDiameter);
                double alfaOuter = Math.acos(gapPrimitive/outerDiameter);

                // Calculate intersection points similar to large gap case
                double cornerYInner = innerDiameter * Math.sin(alfaInner)/2;
                double cornerYOuter = outerDiameter * Math.sin(alfaOuter)/2;

                // For small gaps, rounding diameter calculation includes sine adjustment
                roundingDiameter = (cornerYOuter - cornerYInner)/Math.sin(alfaOuter);
                double gapReal = gapPrimitive - roundingDiameter*(1+Math.cos(alfaOuter));

                if (Math.abs(gapGiven - gapReal) < TOLERANCE/10) break;
                gapPrimitive += gapGiven - gapReal;
            }

            // Calculate centers with different geometry for small gaps
            double roundingDistance = outerDiameter - roundingDiameter;
            double alfaOuter = Math.acos(gapPrimitive/outerDiameter);
            centerV = new Point(
                    roundingDistance * Math.cos(alfaOuter)/2,
                    roundingDistance * Math.sin(alfaOuter)/2
            );
            centerH = new Point(
                    roundingDistance * Math.sin(alfaOuter)/2,
                    roundingDistance * Math.cos(alfaOuter)/2
            );
        }

        return new RoundedThermalParams(gapPrimitive, roundingDiameter, centerH, centerV);
    }

    private void handleFlash(String shape, String adBody, String function, boolean negative,
                             Point position, List<String> bodyCommands, List<String> adCommands,
                             Map<String, Integer> apertures, GraphicsState state,
                             AtomicInteger generateDcode) {
        handleLp(negative, bodyCommands, state);
        handleDcode(shape, function, negative, adBody, bodyCommands, adCommands, apertures, state, generateDcode);

        Point gerberPoint = toGerberCoordinates(position);
        bodyCommands.add(String.format("X%dY%dD03*",
                (int)gerberPoint.x, (int)gerberPoint.y));
        state.point = position;
    }

    private void handleDcode(String shape, String function, boolean negative,
                             String adBody, List<String> bodyCommands, List<String> adCommands,
                             Map<String, Integer> apertures, GraphicsState state,
                             AtomicInteger generateDcode) {
        String key = shape + "," + function + "," + negative;
        Integer dcode = apertures.get(key);

        if (dcode == null) {
            dcode = generateDcode.getAndIncrement();
            apertures.put(key, dcode);

            if (!function.isEmpty()) {
                adCommands.add(String.format("G04 #@! TA.AperFunction,%s*", function));
            }
            adCommands.add(String.format("G04 #@! TAShape,%s*", shape));
            adCommands.add(String.format("%%ADD%d%s*%%", dcode, adBody));
            if (!function.isEmpty()) {
                adCommands.add("G04 #@! TD*");
            }
        }

        if (!Objects.equals(state.dcode, dcode)) {
            bodyCommands.add(String.format("D%d*", dcode));
            state.dcode = dcode;
        }
    }

    private static Point toGerberCoordinates(Point point) {
        return new Point(
                point.x * TO_NM,
                point.y * TO_NM
        );
    }

    private double round(double value) {
        return Math.round(value * Math.pow(10, DECIMALS)) / Math.pow(10, DECIMALS);
    }

    private void processRegion(Region region, List<String> bodyCommands, GraphicsState state) {
        handleLp(region.negative, bodyCommands, state);
        if (!region.function.isEmpty()) {
            bodyCommands.add(String.format("G04 #@! TA.AperFunction,%s*", region.function));
        }
        bodyCommands.add("G36*");
        handlePathOperators(region.path, bodyCommands, state, true);
        bodyCommands.add("G37*");
        if (!region.function.isEmpty()) {
            bodyCommands.add("G04 #@! TD*");
        }
    }

    private void processTracesPath(TracesPath tracesPath, List<String> bodyCommands,
                                   List<String> adCommands,
                                   Map<String, Integer> apertures, GraphicsState state,
                                   AtomicInteger generateDcode) {
        handleTraceLpAdDnn(tracesPath, bodyCommands, adCommands, apertures, state, generateDcode);
        handlePathOperators(tracesPath.path, bodyCommands, state, false);
    }

    // Additional helper methods for Gerber command generation
    private void handleLp(boolean negative, List<String> bodyCommands, GraphicsState state) {
        if (state.negative == null || state.negative != negative) {
            bodyCommands.add(String.format("%%LP%s*%%", negative ? "C" : "D"));
            state.negative = negative;
        }
    }
    
    private void handlePathOperators(Path path, List<String> bodyCommands, GraphicsState state, boolean alwaysD02) {
        for (PathOperator operator : path.getOperators()) {
            if (operator instanceof MoveTo) {
                MoveTo moveTo = (MoveTo) operator;

                // For regions (alwaysD02=true) or when current point is null, use D02
                if (alwaysD02) {
                    Point gerberPoint = toGerberCoordinates(moveTo.getTo());
                    bodyCommands.add(String.format("X%dY%dD02*",
                            (int)gerberPoint.x,
                            (int)gerberPoint.y));
                } else {
                    handleD02(bodyCommands, state, moveTo.getTo());
                }
                

                state.point = moveTo.getTo();

            } else if (operator instanceof LineTo) {
                LineTo lineTo = (LineTo) operator;
                if (state.point == null) {
                    throw new IllegalStateException("LineTo without current point");
                }

                handleG0n(bodyCommands, state, "G01*");
                Point gerberPoint = toGerberCoordinates(lineTo.getTo());
                bodyCommands.add(String.format("X%dY%dD01*",
                        (int)gerberPoint.x,
                        (int)gerberPoint.y));

                state.point = lineTo.getTo();

            } else if (operator instanceof ArcTo) {
                ArcTo arcTo = (ArcTo) operator;
                if (state.point == null) {
                    throw new IllegalStateException("ArcTo without current point");
                }

                // Set quadrant mode if needed
                String g0n = arcTo.getOrientation().equals("-") ? "G02*" : "G03*";
                handleG0n(bodyCommands, state, g0n);

                // Convert points to Gerber coordinates
                Point gerberEnd = toGerberCoordinates(arcTo.getTo());
                Point gerberCenter = toGerberCoordinates(arcTo.getCenter());

                // Calculate offsets from current point to center
                Point currentGerber = toGerberCoordinates(state.point);
                int i = (int)(gerberCenter.x - currentGerber.x);
                int j = (int)(gerberCenter.y - currentGerber.y);

                // Output arc command
                bodyCommands.add(String.format("X%dY%dI%dJ%dD01*",
                        (int)gerberEnd.x,
                        (int)gerberEnd.y,
                        i,
                        j));

                state.point = arcTo.getTo();
            }
        }
    }
    
    private static void handleD02(List<String> bodyCommands, GraphicsState state, Point point) {
        if (!point.equals(state.point)) {
            Point gerberPoint = toGerberCoordinates(point);
            bodyCommands.add(String.format("X%dY%dD02*",
                    (int)gerberPoint.x,
                    (int)gerberPoint.y));
            state.point = point;
        }
    }

    private static void handleG0n(List<String> bodyCommands, GraphicsState state, String g0n) {
        if (!g0n.equals(state.g0n)) {
            bodyCommands.add(g0n);
            state.g0n = g0n;
        }
    }
    
    private void handleTraceLpAdDnn(TracesPath tracePath,
                                    List<String> bodyCommands,
                                    List<String> adCommands,
                                    Map<String, Integer> apertures,
                                    GraphicsState state,
                                    AtomicInteger generateDcode) {
        // First handle the Line Polarity
        handleLp(tracePath.negative, bodyCommands, state);

        // Create the shape and aperture definition
        String shape = String.format("Circle,%f", tracePath.width);
        String adBody = String.format("C,%f", tracePath.width);

        // Handle the aperture definition and D-code selection
        handleDcode(
                shape,                  // Shape description
                tracePath.function,     // Function (e.g., "Conductor")
                tracePath.negative,     // Polarity
                adBody,                 // Aperture definition body
                bodyCommands,              // Command accumulator
                adCommands,
                apertures,             // Existing apertures
                state,                 // Graphics state
                generateDcode          // D-code counter
        );
    }
    
    private static class GraphicsState {
        Point point = null;
        Integer dcode = null;
        String g0n = null;
        Boolean negative = null;
    }

    // Graphics objects as inner classes
    private interface GraphicsObject {
    }

    private static class Pad implements GraphicsObject {
        final AbstractPad master;
        final Point position;
        final double angle;

        Pad(AbstractPad master, Point position, double angle) {
            this.master = master;
            this.position = position;
            this.angle = angle;
        }
    }

    private static class Region implements GraphicsObject {
        final Path path;
        final String function;
        final boolean negative;

        Region(Path path, String function, boolean negative) {
            this.path = path;
            this.function = function;
            this.negative = negative;
        }
    }

    private static class TracesPath implements GraphicsObject {
        final Path path;
        final double width;
        final String function;
        final boolean negative;

        TracesPath(Path path, double width, String function, boolean negative) {
            this.path = path;
            this.width = width;
            this.function = function;
            this.negative = negative;
        }
    }

    private static class RoundedThermalParams {
        final double gapPrimitive;
        final double roundingDiameter;
        final Point centerH;
        final Point centerV;

        RoundedThermalParams(double gapPrimitive, double roundingDiameter,
                             Point centerH, Point centerV) {
            this.gapPrimitive = gapPrimitive;
            this.roundingDiameter = roundingDiameter;
            this.centerH = centerH;
            this.centerV = centerV;
        }
    }
}